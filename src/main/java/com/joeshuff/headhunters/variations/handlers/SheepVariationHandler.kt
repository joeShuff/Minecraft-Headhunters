package com.joeshuff.headhunters.variations.handlers

import com.joeshuff.headhunters.data.models.SkullSourceData
import com.joeshuff.headhunters.variations.VariationHandler
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Sheep
import org.bukkit.inventory.ItemStack

class SheepVariationHandler: VariationHandler() {
    override fun extractVariation(entity: LivingEntity): String? {
        if (entity !is Sheep) return null

        if (entity.customName().toString() == "_jeb") return "jeb"

        return entity.color?.name?.lowercase() // Returns the color of the sheep as a string
    }
}