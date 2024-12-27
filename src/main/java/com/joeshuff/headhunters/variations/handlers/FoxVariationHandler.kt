package com.joeshuff.headhunters.variations.handlers

import com.joeshuff.headhunters.variations.VariationHandler
import org.bukkit.entity.Fox
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack

class FoxVariationHandler: VariationHandler() {
    override fun extractVariation(entity: LivingEntity): String? {
        if (entity !is Fox) return null

        val type = entity.foxType.name.lowercase()

        return type
    }

    override fun applyVariationToStack(itemStack: ItemStack, variation: String?): ItemStack {
        return itemStack
    }
}