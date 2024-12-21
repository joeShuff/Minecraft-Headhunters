package com.joeshuff.headhunters.commands.teams

import com.joeshuff.headhunters.database.TeamDatabaseHandler
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class LeaveTeamCommand(val teamDatabaseHandler: TeamDatabaseHandler) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage(TextComponent("Only players can leave a team!").apply { color = ChatColor.RED })
            return true
        }

        val player = sender

        val success = teamDatabaseHandler.removePlayerFromTeam(player)

        if (success) {
            player.sendMessage(TextComponent("You have successfully left your team.").apply { color = ChatColor.GREEN })
        } else {
            player.sendMessage(TextComponent("You are not currently in any team.").apply { color = ChatColor.RED })
        }

        return true
    }

}
