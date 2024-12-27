package com.joeshuff.headhunters.variations

import it.unimi.dsi.fastutil.Stack
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack

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
    abstract fun applyVariationToStack(itemStack: ItemStack, variation: String?): ItemStack
}
