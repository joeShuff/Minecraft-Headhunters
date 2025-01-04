package com.joeshuff.headhunters.timers

import com.joeshuff.headhunters.HeadHuntersPlugin
import com.joeshuff.headhunters.data.models.SkullDBData
import com.joeshuff.headhunters.database.SkullDatabaseHandler
import com.joeshuff.headhunters.util.SkullController
import org.bukkit.Color
import org.bukkit.FireworkEffect
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.Firework
import org.bukkit.scheduler.BukkitRunnable
import kotlin.math.max

class SkullSpawnTimer(
    val plugin: HeadHuntersPlugin,
    val skulls: List<SkullDBData>,
    val skullDatabaseHandler: SkullDatabaseHandler,
    val skullController: SkullController,
    val location: Location
) : BukkitRunnable() {

    init {
        runTaskTimer(plugin, 0L, 20L)

        skulls.forEach {
            skullDatabaseHandler.markSkullCollected(it.teamId, it.entityType)
        }
    }

    var localSkulls = skulls

    override fun run() {
        val thisSkull = localSkulls.take(1).firstOrNull()
        localSkulls = localSkulls.takeLast(max(localSkulls.size - 1, 0))

        if (thisSkull == null) {
            this.cancel()
            return
        }

        val summonSkull = skullController.spawnSkullForEntityType(location, thisSkull)

        //If successfully spawned
        if (summonSkull) {
            spawnFireworkAtLocation(location)
        }
    }

    private fun spawnFireworkAtLocation(location: Location) {
        val world = location.world ?: return

        world.spawnParticle(Particle.FIREWORK, location, 100, 1.0, 1.0, 1.0, 0.01)
        world.spawnParticle(Particle.DUST, location, 50, 1.0, 1.0, 1.0, 0.01, Particle.DustOptions(Color.ORANGE, 1.0f))
        world.playSound(location, Sound.BLOCK_NOTE_BLOCK_CHIME, 1.0f, 2.0f) // High-pitched chime sound
    }
}