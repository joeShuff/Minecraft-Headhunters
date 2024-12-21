package com.joeshuff.headhunters.commands.teams

import com.joeshuff.headhunters.database.TeamDatabaseHandler
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class JoinCommand(private val teamDatabaseHandler: TeamDatabaseHandler) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage(TextComponent("Only players can join teams!").apply { color = ChatColor.RED })
            return true
        }

        val player = sender

        // Ensure the player provides a team GUID
        if (args.isEmpty()) {
            player.sendMessage(TextComponent("Please provide the team GUID.").apply { color = ChatColor.YELLOW })
            return true
        }

        val teamGuid = args[0]

        val team = teamDatabaseHandler.getTeamById(teamGuid)
        if (team == null) {
            player.sendMessage(TextComponent("The team with GUID '$teamGuid' does not exist.").apply {
                color = ChatColor.RED
            })
            return true
        }

        // Add player to the team
        teamDatabaseHandler.addPlayerToTeam(player, team.id)

        player.sendMessage(TextComponent("You have successfully joined the team '${team.teamName}'.").apply {
            color = ChatColor.GREEN
        })
        return true
    }

}
