package com.joeshuff.headhunters.util

import com.destroystokyo.paper.profile.ProfileProperty
import com.google.gson.Gson
import com.joeshuff.headhunters.HeadHuntersPlugin
import com.joeshuff.headhunters.data.models.SkullSourceData
import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import org.bukkit.*
import org.bukkit.entity.EntityType
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.persistence.PersistentDataType
import java.io.InputStreamReader
import java.util.*

class SkullController(val plugin: HeadHuntersPlugin) {

    val skullTextures = mutableMapOf<EntityType, SkullSourceData>()

    init {
        loadSkullData()
    }

    private fun loadSkullData() {
        try {
            val resourceStream = plugin.getResource("skull_data.json")
            val reader = InputStreamReader(resourceStream)
            val skullDataList = Gson().fromJson(reader, Array<SkullSourceData>::class.java)
            skullDataList.forEach { skullData ->
                val entityType = EntityType.fromName(skullData.entityType) ?: return@forEach
                skullTextures[entityType] = skullData
            }
        } catch (e: Exception) {
            plugin.logger.severe("Error loading skull data: ${e.message}")
        }
    }

    // Get the skull texture for an EntityType
    fun getSkullTextureForEntityType(entityType: EntityType): String? {
        val skullData = skullTextures[entityType]
        return skullData?.skullTexture
    }

    fun getSkullTypeVanilla(entityType: EntityType): Material {
        return when (entityType) {
            EntityType.SKELETON -> Material.SKELETON_SKULL
            EntityType.WITHER_SKELETON -> Material.WITHER_SKELETON_SKULL
            EntityType.ZOMBIE -> Material.ZOMBIE_HEAD
            EntityType.CREEPER -> Material.CREEPER_HEAD
            EntityType.ENDER_DRAGON -> Material.DRAGON_HEAD
            EntityType.PIGLIN -> Material.PIGLIN_HEAD
            else -> Material.PLAYER_HEAD
        }
    }

    // Method to spawn a skull at a specific location matching the EntityType
    fun spawnSkullAtLocation(location: Location, entityType: EntityType, earnedPlayerUUID: String?): Boolean {
        val skullTexture = getSkullTextureForEntityType(entityType)
        val skullMaterial = getSkullTypeVanilla(entityType)

        val skull = ItemStack(skullMaterial) // Create a player skull item

        val skullMeta = skull.itemMeta as SkullMeta // Get the meta for the skull

        if (skullMaterial == Material.PLAYER_HEAD) {
            if (skullTexture == null) {
                plugin.logger.severe("No texture data for $entityType")
                return false
            }

            val uuid = UUID.randomUUID()
            val profile = Bukkit.createProfile(uuid, uuid.toString().substring(0, 16))
            plugin.logger.info("texture for $entityType is $skullTexture")

            // Apply the texture using the Property class from authlib
            profile.setProperty(ProfileProperty("textures", skullTexture))
            skullMeta.playerProfile = profile
        }

        val lore = mutableListOf<String>()
        val description = if (earnedPlayerUUID != null) {
            val playerName = plugin.server.getOfflinePlayer(UUID.fromString(earnedPlayerUUID)).name ?: "Unknown Player"
            "${ChatColor.GOLD}Headhunted bravely by ${ChatColor.AQUA}$playerName"
        } else {
            "${ChatColor.GOLD}Headhunted bravely"
        }
        lore.add(description)

        // Set the display name
        val displayName = "${ChatColor.DARK_GREEN}${entityType.toDisplayString()} Skull"
        skullMeta.setDisplayName(displayName)

        skullMeta.lore = lore

        // Add PersistentDataContainer for persistence
        val dataContainer = skullMeta.persistentDataContainer
        val key = NamespacedKey(plugin, "skull_description")
        dataContainer.set(key, PersistentDataType.STRING, description)

        skull.itemMeta = skullMeta // Set the updated meta back to the skull item

        // Spawn the skull at the location
        location.world?.dropItem(location, skull)
        return true
    }
}