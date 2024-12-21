package com.joeshuff.headhunters.commands.teams

import com.joeshuff.headhunters.database.TeamDatabaseHandler
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class LeaveTeamCommand(val teamDatabaseHandler: TeamDatabaseHandler) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Only players can leave a team!")
            return true
        }

        val player = sender

        val success = teamDatabaseHandler.removePlayerFromTeam(player)

        if (success) {
            player.sendMessage("§aYou have successfully left your team.")
        } else {
            player.sendMessage("§cYou are not currently in any team.")
        }

        return true
    }
}
