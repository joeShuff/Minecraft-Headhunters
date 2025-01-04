package com.joeshuff.headhunters.variations.handlers

import com.joeshuff.headhunters.variations.VariationHandler
import org.bukkit.entity.Axolotl
import org.bukkit.entity.LivingEntity

class AxolotlVariationHandler : VariationHandler() {
    override fun extractVariation(entity: LivingEntity): String? {
        if (entity !is Axolotl) return null
        return entity.variant.name.lowercase() // Returns the axolotl's variant (e.g., "LUCY", "WILD", etc.) as a string
    }
}
