package com.joeshuff.headhunters

import com.google.gson.Gson
import com.mojang.authlib.minecraft.MinecraftProfileTexture
import com.mojang.authlib.properties.Property
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.profile.PlayerProfile
import org.bukkit.profile.PlayerTextures
import java.io.InputStreamReader
import java.lang.reflect.Field

class SkullController(val plugin: HeadHuntersPlugin) {

    data class SkullData(
        val entityType: String,
        val name: String,
        val skullTexture: String
    )

    val skullTextures = mutableMapOf<EntityType, SkullData>()

    init {
        loadSkullData()
    }

    private fun loadSkullData() {
        try {
            val resourceStream = plugin.getResource("skull_data.json")
            val reader = InputStreamReader(resourceStream)
            val skullDataList = Gson().fromJson(reader, Array<SkullData>::class.java)
            skullDataList.forEach { skullData ->
                val entityType = EntityType.fromName(skullData.entityType)?: return@forEach
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

    // Method to spawn a skull at a specific location matching the EntityType
    fun spawnSkullAtLocation(location: Location, entityType: EntityType) {
        val skullTexture = getSkullTextureForEntityType(entityType)
        if (skullTexture != null) {
            val skull = ItemStack(Material.PLAYER_HEAD) // Create a player skull item
            val skullMeta = skull.itemMeta as SkullMeta // Get the meta for the skull

            val prof = plugin.server.createProfile(entityType.name)
//            val text = PlayerTextures(skullTexture, emptyMap())

//            prof.setTextures(text)

                        // Apply the texture using the Property class from authlib
//            val property = Property("textures", skullTexture)
//            skullMeta.setOwnerProfile(com.mojang.authlib.GameProfile(java.util.UUID.randomUUID(), entityType.name).apply {
//                properties.put("textures", property)
//            })

            skull.itemMeta = skullMeta // Set the updated meta back to the skull item

            // Spawn the skull at the location
            location.world?.dropItem(location, skull)
        } else {
            // Log an error if the texture was not found
            println("No skull texture found for entity: $entityType")
        }
    }
}