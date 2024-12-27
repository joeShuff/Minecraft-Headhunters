package com.joeshuff.headhunters.variations.handlers

import com.joeshuff.headhunters.variations.VariationHandler
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.util.*

class PlayerVariationHandler: VariationHandler() {
    override fun extractVariation(entity: LivingEntity): String? {
        if (entity !is Player) return null
        return entity.uniqueId.toString()
    }

    override fun applyVariationToStack(itemStack: ItemStack, variation: String?): ItemStack {
        val skullMeta = (itemStack.itemMeta as? SkullMeta)?: return itemStack

        if (variation == null) return itemStack

        val player = Bukkit.getOfflinePlayer(UUID.fromString(variation))
        skullMeta.owningPlayer = player

        // Set the display name
        val displayName = "${ChatColor.DARK_GREEN}${player.name}'s Skull"
        skullMeta.setDisplayName(displayName)
        itemStack.itemMeta = skullMeta

        return itemStack
    }
}