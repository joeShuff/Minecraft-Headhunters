package com.joeshuff.headhunters.listeners

import com.joeshuff.headhunters.HeadHuntersPlugin
import com.joeshuff.headhunters.database.TeamDatabaseHandler
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.server.ServerListPingEvent

class ServerPingListener(
    plugin: HeadHuntersPlugin,
    private val teamDatabaseHandler: TeamDatabaseHandler
): Listener, Stoppable {

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    override fun stop() {
        HandlerList.unregisterAll(this)
    }

    @EventHandler
    fun onServerListPing(event: ServerListPingEvent) {
        // Retrieve the current number of teams
        val teamCount = teamDatabaseHandler.getAllTeams().size

        // Build the MOTD as a text component
        val motd = Component.text("Headhunters: ", NamedTextColor.GOLD)
            .append(Component.text("$teamCount", NamedTextColor.AQUA, TextDecoration.BOLD))
            .append(Component.text(" Teams Hunting!", NamedTextColor.YELLOW))

        // Set the MOTD
        event.motd(Component.text().append(motd).build())
    }

}