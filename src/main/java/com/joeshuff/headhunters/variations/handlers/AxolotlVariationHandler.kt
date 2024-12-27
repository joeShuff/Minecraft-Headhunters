package com.joeshuff.headhunters.variations.handlers

import com.joeshuff.headhunters.variations.VariationHandler
import org.bukkit.entity.Axolotl
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack

class AxolotlVariationHandler : VariationHandler() {
    override fun extractVariation(entity: LivingEntity): String {
        if (entity !is Axolotl) throw IllegalArgumentException("Entity is not an axolotl!")
        return entity.variant.name // Returns the axolotl's variant (e.g., "LUCY", "WILD", etc.) as a string
    }

    override fun applyVariationToStack(itemStack: ItemStack, variation: String?): ItemStack {
        return itemStack
    }
}
