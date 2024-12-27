package com.joeshuff.headhunters.variations.handlers

import com.joeshuff.headhunters.variations.VariationHandler
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.ZombieVillager
import org.bukkit.inventory.ItemStack

class ZombieVillagerVariationHandler: VariationHandler() {
    override fun extractVariation(entity: LivingEntity): String? {
        if (entity !is ZombieVillager) return null

        // Extract the profession of the zombie villager
        val profession = entity.villagerProfession.key.key

        // Get biome from the entity's location
        val biome = entity.villagerType.key.key

        // For now not going to bother with biomes because 7 biomes and 15 professions. 105 variations is too much.
        //        return "${biome}_$profession"

        return profession
    }

    override fun applyVariationToStack(itemStack: ItemStack, variation: String?): ItemStack {
        return itemStack
    }
}