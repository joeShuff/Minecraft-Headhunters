package com.joeshuff.headhunters.variations.handlers

import com.joeshuff.headhunters.data.models.SkullSourceData
import com.joeshuff.headhunters.variations.VariationHandler
import org.bukkit.entity.Cat
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack

class CatVariationHandler: VariationHandler() {
    override fun extractVariation(entity: LivingEntity): String? {
        if (entity !is Cat) return null
        return entity.catType.key.key // Returns the cat type as a string
    }
}