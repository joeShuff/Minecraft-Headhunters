package com.joeshuff.headhunters.listeners

import com.joeshuff.headhunters.HeadHuntersPlugin
import com.joeshuff.headhunters.database.SkullDatabaseHandler
import com.joeshuff.headhunters.database.TeamDatabaseHandler
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent

class SkullEarnedListener(
    private val plugin: HeadHuntersPlugin,
    private val teamDatabaseHandler: TeamDatabaseHandler,
    private val skullDatabaseHandler: SkullDatabaseHandler
) : Listener, Stoppable {

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    fun onEntityKill(event: EntityDeathEvent) {
        val entity = event.entity
        val killer = entity.killer ?: return  // Only handle kills by players
        val playerTeam = teamDatabaseHandler.getTeamForPlayer(killer) ?: return  // Check if the player is on a team

        val entityType = entity.type
        if (skullDatabaseHandler.isSkullEarned(playerTeam.id, entityType)) {
            return  // EntityType is already earned for this team
        }

        // Mark the skull as earned
        val markedAsEarned = skullDatabaseHandler.markSkullEarned(
            teamId = playerTeam.id,
            player = killer,
            entityType = entityType
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

    override fun stop() {
        HandlerList.unregisterAll(this)
    }
}
