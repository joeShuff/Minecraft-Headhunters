package com.joeshuff.headhunters.commands.teams

import com.joeshuff.headhunters.database.ShrineDatabaseHandler
import com.joeshuff.headhunters.database.TeamDatabaseHandler
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import util.toDisplay

class SetShrineCommand(private val shrineDatabaseHandler: ShrineDatabaseHandler,
                       private val teamDatabaseHandler: TeamDatabaseHandler) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Only players can use this command.")
            return true
        }

        val player = sender
        val team = teamDatabaseHandler.getTeamForPlayer(player)
        if (team == null) {
            player.sendMessage("You must be part of a team to set a shrine.")
            return true
        }

        val currentShrine = shrineDatabaseHandler.getShrine(team.id)
        if (currentShrine != null && args.firstOrNull() != "confirm") {
            player.sendMessage("Your team already has a shrine at ${currentShrine.toDisplay()}. Use /setshrine confirm to overwrite it.")
            return true
        }

        val location = player.location
        shrineDatabaseHandler.setShrine(team.id, location)

        for (playerId in teamDatabaseHandler.getPlayerGuidsForTeam(team.id)) {
            Bukkit.getPlayer(playerId)?.let {
                it.sendMessage("Shrine set at ${location.blockX}, ${location.blockY}, ${location.blockZ} for team ${team.teamName}.")
            }
        }
        return true
    }
}
