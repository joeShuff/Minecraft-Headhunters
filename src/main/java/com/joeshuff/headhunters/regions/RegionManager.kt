package com.joeshuff.headhunters.regions

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerMoveEvent
import java.util.*

data class Region(val regionId: String, var location: Location, var radius: Double)

class RegionManager {
    // A map to store regions by their ID, with each region holding a Location and radius
    private val regions = mutableMapOf<String, Region>()

    private val playerRegions = mutableMapOf<UUID, String?>()

    fun upsertRegion(regionId: String, location: Location, radius: Double): Boolean {
        val region = regions[regionId]
        return if (region != null) {
            // Update the existing region's properties
            region.location = location
            region.radius = radius
            true // Successfully updated the region
        } else {
            // Add a new region if it doesn't exist
            regions[regionId] = Region(regionId, location, radius)
            true // Successfully added the region
        }
    }

    // Check if a player is within any region
    fun getRegionPlayerIsIn(player: Player): String? {
        for (region in regions.values) {
            if (region.location.distance(player.location) <= region.radius) {
                return region.regionId
            }
        }
        return null
    }

    fun playerMoveEvent(event: PlayerMoveEvent) {
        val player = event.player
        val newRegionId = getRegionPlayerIsIn(player)

        // Check if the region has changed
        val previousRegionId = playerRegions[player.uniqueId]

        if (newRegionId != previousRegionId) {
            // Fire the leave event for the old region
            previousRegionId?.let {
                Bukkit.getServer().pluginManager.callEvent(PlayerLeaveRegionEvent(player, it))
            }

            // Fire the enter event for the new region
            newRegionId?.let {
                Bukkit.getServer().pluginManager.callEvent(PlayerEnterRegionEvent(player, it))
            }

            // Update the player's current region
            playerRegions[player.uniqueId] = newRegionId
        }
    }
}

open class PlayerRegionEvent(val player: Player, val regionId: String) : Event() {
    companion object {
        private val handlerList = HandlerList()

        // Optionally, you can provide the HandlerList for your event class as a static member
        // This is required by the Bukkit event system to register and listen to your event.
        @JvmStatic
        fun getHandlerList(): HandlerList {
            return handlerList
        }
    }

    override fun getHandlers(): HandlerList {
        return handlerList
    }
}

// Event triggered when a player enters a region
class PlayerEnterRegionEvent(player: Player, regionId: String) : PlayerRegionEvent(player, regionId)

// Event triggered when a player leaves a region
class PlayerLeaveRegionEvent(player: Player, regionId: String) : PlayerRegionEvent(player, regionId)