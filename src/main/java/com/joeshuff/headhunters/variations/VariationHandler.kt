package com.joeshuff.headhunters.variations

import com.destroystokyo.paper.profile.ProfileProperty
import com.joeshuff.headhunters.data.models.SkullSourceData
import com.joeshuff.headhunters.util.applyTexture
import com.joeshuff.headhunters.util.toDisplayString
import it.unimi.dsi.fastutil.Stack
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.util.*

abstract class VariationHandler {
    /**
     * Extracts a string representation of the entity's variation data.
     * @param entity The entity whose variation is to be extracted.
     * @return A string representing the entity's variation.
     */
    abstract fun extractVariation(entity: LivingEntity): String?

    /**
     * Applies variation data to an ItemStack.
     * @param itemStack The ItemStack to which the variation will be applied.
     * @param variation The string representation of the variation to apply.
     */
    open fun applyVariationToStack(itemStack: ItemStack, variation: String?, rawSkullSourceData: SkullSourceData): ItemStack {
        var texture = rawSkullSourceData.skullTexture
        var skullName = EntityType.fromName(rawSkullSourceData.entityType)?.toDisplayString()

        rawSkullSourceData.variations.firstOrNull { it.id == variation }?.let {
            texture = it.variationTexture
            skullName = it.variationName
        }

        if (texture.isNullOrEmpty()) {
            println("No texture data for $variation of ${this::class.simpleName}")
            return itemStack
        }

        itemStack.applyTexture(texture)

        val skullMeta = itemStack.itemMeta as? SkullMeta ?: return itemStack

        val displayName = "${ChatColor.DARK_GREEN}${skullName} Skull"
        skullMeta.setDisplayName(displayName)

        itemStack.itemMeta = skullMeta

        return itemStack
    }
}
