package com.joeshuff.headhunters.listeners

import com.joeshuff.headhunters.HeadHuntersPlugin
import com.joeshuff.headhunters.database.SkullDatabaseHandler
import com.joeshuff.headhunters.database.TeamDatabaseHandler
import com.joeshuff.headhunters.util.SkullController
import com.joeshuff.headhunters.variations.VariationFactory
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.PlayerDeathEvent

class SkullEarnedListener(
    private val plugin: HeadHuntersPlugin,
    private val skullController: SkullController
) : Listener, Stoppable {

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    private fun playerKilledEntity(killer: Player, entity: LivingEntity) {
        val entityType = entity.type

        val variationInfo = VariationFactory.getHandler(entityType)?.extractVariation(entity)

        skullController.onSkullEarn(
            entityType,
            killer,
            variationInfo
        )
    }

    @EventHandler
    fun onPlayerKill(event: PlayerDeathEvent) {
        val victim = event.entity
        val killer = victim.killer ?: return // Only award skulls if a player killed them

        playerKilledEntity(killer, victim)
    }

    @EventHandler
    fun onEntityKill(event: EntityDeathEvent) {
        val entity = event.entity
        val killer = entity.killer ?: return  // Only handle kills by players

        playerKilledEntity(killer, entity)
    }

    override fun stop() {
        HandlerList.unregisterAll(this)
    }
}
