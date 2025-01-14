package com.joeshuff.headhunters.listeners

import com.joeshuff.headhunters.HeadHuntersPlugin
import org.bukkit.entity.EntityType
import org.bukkit.entity.Horse
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent

class HorseSpawnListener(val plugin: HeadHuntersPlugin) : Listener, Stoppable {

    private var chanceForSkeleton: Double = 0.15
    private var chanceForZombie: Double = 0.15

    init {
        chanceForZombie = plugin.config.getDouble("zombie-horse-chance")
        chanceForSkeleton = plugin.config.getDouble("skeleton-horse-chance")

        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    fun onHorseSpawn(event: CreatureSpawnEvent) {
        val entity = event.entity


        // Check if the spawned entity is a horse
        if (entity.type == EntityType.HORSE && entity is Horse) {
            val randomValue = Math.random()

            plugin.logger.info("Horse spawned value is $randomValue")

            // Determine replacement based on configured chances
            when {
                randomValue <= chanceForSkeleton -> {
                    event.isCancelled = true
                    entity.world.spawnEntity(entity.location, EntityType.SKELETON_HORSE)
                }
                randomValue <= chanceForSkeleton + chanceForZombie -> {
                    event.isCancelled = true
                    entity.world.spawnEntity(entity.location, EntityType.ZOMBIE_HORSE)
                }
            }
        }
    }

    override fun stop() {
        HandlerList.unregisterAll(this)
    }

}