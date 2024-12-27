package com.joeshuff.headhunters.variations.handlers

import com.joeshuff.headhunters.variations.VariationHandler
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Villager
import org.bukkit.inventory.ItemStack

class VillagerVariationHandler : VariationHandler() {
    override fun extractVariation(entity: LivingEntity): String? {
        if (entity !is Villager) return null

        // Extract profession and biome of the villager
        val profession = entity.profession.key.key
        val biome = entity.villagerType.key.key

        // For now not going to bother with biomes because 7 biomes and 15 professions. 105 variations is too much.
        //        return "${biome}_$profession"

        return profession
    }

    override fun applyVariationToStack(itemStack: ItemStack, variation: String?): ItemStack {
        return itemStack
    }
}
