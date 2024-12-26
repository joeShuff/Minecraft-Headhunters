package com.joeshuff.headhunters.listeners

import com.joeshuff.headhunters.HeadHuntersPlugin
import com.joeshuff.headhunters.database.SkullDatabaseHandler
import com.joeshuff.headhunters.database.TeamDatabaseHandler
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.PlayerDeathEvent

class SkullEarnedListener(
    private val plugin: HeadHuntersPlugin,
    private val teamDatabaseHandler: TeamDatabaseHandler,
    private val skullDatabaseHandler: SkullDatabaseHandler
) : Listener, Stoppable {

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    private fun playerKilledEntity(killer: Player, entityType: EntityType, killedExtraDetails: String?) {
        val playerTeam = teamDatabaseHandler.getTeamForPlayer(killer) ?: return  // Check if the player is on a team

        if (skullDatabaseHandler.isSkullEarned(playerTeam.id, entityType)) {
            return  // EntityType is already earned for this team
        }

        // Mark the skull as earned
        val markedAsEarned = skullDatabaseHandler.markSkullEarned(
            teamId = playerTeam.id,
            player = killer,
            entityType = entityType,
            killedInfo = killedExtraDetails
        )

        if (markedAsEarned) {
            teamDatabaseHandler.getPlayerGuidsForTeam(playerTeam.id).forEach {
                Bukkit.getPlayer(it)?.let {
                    it.sendMessage("${ChatColor.GREEN}Your team has earned the ${ChatColor.GOLD}${entityType.name}${ChatColor.GREEN} skull, thanks to ${killer.name}!")
                }
            }
        } else {
            plugin.logger.warning("Failed to mark skull as earned for ${playerTeam.id} and entity type ${entityType.name}.")
        }
    }

    @EventHandler
    fun onPlayerKill(event: PlayerDeathEvent) {
        val victim = event.entity
        val killer = victim.killer ?: return // Only award skulls if a player killed them

        playerKilledEntity(killer, EntityType.PLAYER, victim.uniqueId.toString())
    }

    @EventHandler
    fun onEntityKill(event: EntityDeathEvent) {
        val entity = event.entity
        val killer = entity.killer ?: return  // Only handle kills by players

        playerKilledEntity(killer, entity.type, getExtraDetailsFromEntityType(entity))
    }

    private fun getExtraDetailsFromEntityType(entity: LivingEntity): String? {
        return when (entity.type) {
            EntityType.CAT -> null
            else -> null
        }
    }

    override fun stop() {
        HandlerList.unregisterAll(this)
    }
}
