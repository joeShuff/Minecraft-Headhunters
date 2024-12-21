package com.joeshuff.headhunters.commands.teams

import com.joeshuff.headhunters.database.ShrineDatabaseHandler
import com.joeshuff.headhunters.database.TeamDatabaseHandler
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import com.joeshuff.headhunters.util.toDisplay
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.TextComponent

class SetShrineCommand(
    private val shrineDatabaseHandler: ShrineDatabaseHandler,
    private val teamDatabaseHandler: TeamDatabaseHandler
) : CommandExecutor {


    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage(TextComponent("Only players can use this command.").apply { color = ChatColor.RED })
            return true
        }

        val player = sender
        val team = teamDatabaseHandler.getTeamForPlayer(player)
        if (team == null) {
            player.sendMessage(TextComponent("You must be part of a team to set a shrine.").apply {
                color = ChatColor.RED
            })
            return true
        }

        val currentShrine = shrineDatabaseHandler.getShrine(team.id)
        if (currentShrine != null && args.firstOrNull() != "confirm") {
            player.sendMessage(TextComponent("Your team already has a shrine at ${currentShrine.toDisplay()}. Use /setshrine confirm to overwrite it.").apply {
                color = ChatColor.YELLOW
            })
            return true
        }

        val location = player.location
        shrineDatabaseHandler.setShrine(team.id, location)

        for (playerId in teamDatabaseHandler.getPlayerGuidsForTeam(team.id)) {
            Bukkit.getPlayer(playerId)?.let {
                it.sendMessage(TextComponent("Shrine set at ${location.blockX}, ${location.blockY}, ${location.blockZ} for team ${team.teamName}.").apply {
                    color = ChatColor.GREEN
                })
            }
        }
        return true
    }

}
