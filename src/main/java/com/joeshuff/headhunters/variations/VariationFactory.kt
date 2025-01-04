package com.joeshuff.headhunters.variations

import com.joeshuff.headhunters.variations.handlers.*
import org.bukkit.entity.Axolotl
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType

object VariationFactory {
    private val extractors: Map<EntityType, VariationHandler> = mapOf(
        EntityType.SHEEP to SheepVariationHandler(),
        EntityType.CAT to CatVariationHandler(),
        EntityType.PLAYER to PlayerVariationHandler(),
        EntityType.AXOLOTL to AxolotlVariationHandler(),
        EntityType.VILLAGER to VillagerVariationHandler(),
        EntityType.ZOMBIE_VILLAGER to ZombieVillagerVariationHandler(),
        EntityType.FOX to FoxVariationHandler(),
        EntityType.STRIDER to StriderVariationHandler(),
        EntityType.HORSE to HorseVariationHandler()
    )

    fun getHandler(entityType: EntityType): VariationHandler? {
        return extractors[entityType]
    }
}