package com.joeshuff.headhunters.util

import com.destroystokyo.paper.profile.ProfileProperty
import org.bukkit.Bukkit
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.util.*

fun ItemStack.applyTexture(
    texture: String
) {
    val skullMeta = itemMeta as? SkullMeta?: return
    val uuid = UUID.randomUUID()
    val profile = Bukkit.createProfile(uuid, uuid.toString().substring(0, 16))

    // Apply the texture using the Property class from authlib
    profile.setProperty(ProfileProperty("textures", texture))
    skullMeta.playerProfile = profile

    itemMeta = skullMeta
}