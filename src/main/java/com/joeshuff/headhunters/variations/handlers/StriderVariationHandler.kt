package com.joeshuff.headhunters.variations.handlers

import com.joeshuff.headhunters.variations.VariationHandler
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Strider
import org.bukkit.inventory.ItemStack

class StriderVariationHandler: VariationHandler() {
    override fun extractVariation(entity: LivingEntity): String? {
        if (entity !is Strider) return null

        return if (entity.isShivering) "shivering" else null
    }

    override fun applyVariationToStack(itemStack: ItemStack, variation: String?): ItemStack {
        return itemStack
    }
}