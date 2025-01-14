package com.joeshuff.headhunters.listeners

import com.joeshuff.headhunters.HeadHuntersPlugin
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.world.ChunkPopulateEvent

class HorseSpawnListener(val plugin: HeadHuntersPlugin) : Listener, Stoppable {

    private var chanceForSkeleton: Double = 0.15
    private var chanceForZombie: Double = 0.15

    init {
        chanceForZombie = plugin.config.getDouble("zombie-horse-chance")
        chanceForSkeleton = plugin.config.getDouble("skeleton-horse-chance")

        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    private fun handleHorseSpawn(location: Location): Boolean {
        val randomValue = Math.random()

        // Determine replacement based on configured chances
        when {
            randomValue <= chanceForSkeleton -> {
                location.world.spawnEntity(location, EntityType.SKELETON_HORSE)
                return true
            }

            randomValue <= chanceForSkeleton + chanceForZombie -> {
                location.world.spawnEntity(location, EntityType.ZOMBIE_HORSE)
                return true
            }
        }

        return false
    }

    @EventHandler
    fun onChunkGenerate(event: ChunkPopulateEvent) {
        val entitiesInChunk = event.chunk.entities

        entitiesInChunk.forEach {
            if (it.type == EntityType.HORSE) {
                val replacedHorse = handleHorseSpawn(it.location)
                if (replacedHorse) {
                    it.remove()
                }
            }
        }
    }

    @EventHandler
    fun onHorseSpawn(event: EntitySpawnEvent) {
        val entity = event.entity


        // Check if the spawned entity is a horse
        if (entity.type == EntityType.HORSE) {
            val replacedHorse = handleHorseSpawn(entity.location)

            if (replacedHorse) {
                event.isCancelled = true
            }
        }
    }

    override fun stop() {
        HandlerList.unregisterAll(this)
    }

}