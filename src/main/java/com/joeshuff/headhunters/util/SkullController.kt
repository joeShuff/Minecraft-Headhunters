package com.joeshuff.headhunters.util

import com.joeshuff.headhunters.HeadHuntersPlugin
import com.joeshuff.headhunters.data.models.SkullDBData
import com.joeshuff.headhunters.data.models.SkullSourceData
import com.joeshuff.headhunters.database.SkullDatabaseHandler
import com.joeshuff.headhunters.variations.VariationFactory
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.EntityType
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.persistence.PersistentDataType
import java.util.*

class SkullController(
    val plugin: HeadHuntersPlugin,
    val skullDatabaseHandler: SkullDatabaseHandler
) {

    val skullTextures = mutableMapOf<EntityType, SkullSourceData>()

    init {
        loadSkullData()
    }

    private fun loadSkullData() {
        skullDatabaseHandler.getRawSkullData().forEach { skullData ->
            val entityType = EntityType.fromName(skullData.entityType) ?: return@forEach
            skullTextures[entityType] = skullData
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

    fun getSkullItemStack(
        entityType: EntityType,
        earnedPlayerUUID: String?,
        earnedVariation: String? = null
    ): ItemStack? {
        val skullMaterial = getSkullTypeVanilla(entityType)

        var skull = ItemStack(skullMaterial) // Create a player skull item

        var skullMeta = skull.itemMeta as SkullMeta // Get the meta for the skull

        // Set the display name
        val displayName = "${ChatColor.DARK_GREEN}${entityType.toDisplayString()} Skull"
        skullMeta.setDisplayName(displayName)
        skull.itemMeta = skullMeta

        if (skullMaterial == Material.PLAYER_HEAD) {
            skullTextures[entityType]?.let { rawData ->
                VariationFactory.getHandler(entityType)?.applyVariationToStack(skull, earnedVariation, rawData)
                    ?.let {
                        skull = it
                    } ?: run {
                    getSkullTextureForEntityType(entityType)?.let { skull.applyTexture(it) }
                }
            } ?: {
                plugin.logger.severe("No raw skull data for $entityType")
            }
        }

        skullMeta = skull.itemMeta as SkullMeta // Get the meta for the skull

        val lore = mutableListOf<String>()
        val description = if (earnedPlayerUUID != null) {
            val playerName = plugin.server.getOfflinePlayer(UUID.fromString(earnedPlayerUUID)).name ?: "Unknown Player"
            "${ChatColor.GOLD}Headhunted bravely by ${ChatColor.AQUA}$playerName"
        } else {
            "${ChatColor.GOLD}Headhunted bravely"
        }
        lore.add(description)

        skullMeta.lore = lore

        // Add PersistentDataContainer for persistence
        val dataContainer = skullMeta.persistentDataContainer
        val key = NamespacedKey(plugin, "skull_description")
        dataContainer.set(key, PersistentDataType.STRING, description)

        skull.itemMeta = skullMeta // Set the updated meta back to the skull item

        return skull
    }

    fun spawnSkullAtLocation(itemStack: ItemStack, location: Location) {
        // Spawn the skull at the location
        val droppedItem = location.world?.dropItem(location, itemStack)

        droppedItem?.apply {
            isGlowing = true
            isPersistent = true // Prevent item from despawning
            fireTicks = 0 // Ensure the item isn't burning
            isInvulnerable = true // Make it invulnerable to fire damage
        }
    }

    // Method to spawn a skull at a specific location matching the EntityType
    fun spawnSkullForEntityType(location: Location, skullDBData: SkullDBData): Boolean {
        val entityType = EntityType.fromName(skullDBData.entityType) ?: return false
        val skullItemStack = getSkullItemStack(entityType, skullDBData.earnedBy, skullDBData.earnedVariation)

        skullItemStack?.let {
            spawnSkullAtLocation(skullItemStack, location)
            return true
        } ?: return false
    }
}