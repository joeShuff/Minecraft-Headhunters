package com.joeshuff.headhunters.listeners

import com.joeshuff.headhunters.HeadHuntersPlugin
import com.joeshuff.headhunters.util.SkullController
import com.joeshuff.headhunters.database.SkullDatabaseHandler
import com.joeshuff.headhunters.database.TeamDatabaseHandler
import com.joeshuff.headhunters.regions.PlayerEnterRegionEvent
import com.joeshuff.headhunters.regions.PlayerLeaveRegionEvent
import com.joeshuff.headhunters.regions.RegionManager
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

class PlayerListener(
    private val plugin: HeadHuntersPlugin,
    private val regionManager: RegionManager,
    private val teamDatabaseHandler: TeamDatabaseHandler,
    private val skullDatabaseHandler: SkullDatabaseHandler,
    private val skullController: SkullController
): Listener, Stoppable {

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    override fun stop() {
        HandlerList.unregisterAll(this)
    }

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        regionManager.playerMoveEvent(event)
    }

    @EventHandler
    fun onPlayerLeaveRegion(event: PlayerLeaveRegionEvent) {
        event.player.sendMessage("You have left region ${event.regionId}")
    }

    @EventHandler
    fun onPlayerEnterRegion(event: PlayerEnterRegionEvent) {
        val playerTeam = teamDatabaseHandler.getTeamForPlayer(event.player)?: return

        event.player.sendMessage("You have entered region ${event.regionId}")

        //Check that player entered their own teams region
        if (playerTeam.id == event.regionId) {
            val uncollectedSkulls = skullDatabaseHandler.getEarnedButNotCollectedSkulls(playerTeam.id)

            uncollectedSkulls.forEach { uncollectedSkull ->
                val entityType = EntityType.fromName(uncollectedSkull.entityType)?: return@forEach
                val shrineLoc = playerTeam.shrineLocation?: return@forEach

                skullController.spawnSkullAtLocation(shrineLoc, entityType)
                skullDatabaseHandler.markSkullCollected(playerTeam.id, entityType)
            }
        }
    }
}