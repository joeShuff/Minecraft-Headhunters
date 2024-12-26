package com.joeshuff.headhunters.commands.teams

import com.joeshuff.headhunters.database.TeamDatabaseHandler
import net.md_5.bungee.api.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class ListTeamsCommand(
    private val teamDatabaseHandler: TeamDatabaseHandler
) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("${ChatColor.RED}Only players or the console can use this command.")
            return true
        }

        if (!sender.isOp) {
            sender.sendMessage("${ChatColor.RED}Only admins can use this command.")
            return true
        }

        val teams = teamDatabaseHandler.getAllTeams()
        if (teams.isEmpty()) {
            sender.sendMessage("${ChatColor.RED}No teams have been created yet.")
            return true
        }

        sender.sendMessage("${ChatColor.GOLD}Teams and their IDs:${ChatColor.RESET}")
        for (team in teams) {
            sender.sendMessage("- ${ChatColor.GREEN}${team.teamName} ${ChatColor.AQUA}(ID: ${team.id})${ChatColor.RESET}")
        }

        return true
    }
}