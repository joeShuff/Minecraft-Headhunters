package com.joeshuff.headhunters.variations.handlers

import com.joeshuff.headhunters.variations.VariationHandler
import org.bukkit.entity.Horse
import org.bukkit.entity.LivingEntity

class HorseVariationHandler : VariationHandler() {
    override fun extractVariation(entity: LivingEntity): String? {
        if (entity !is Horse) return null
        return entity.color.name.lowercase() // Returns the horse's colour
    }
}