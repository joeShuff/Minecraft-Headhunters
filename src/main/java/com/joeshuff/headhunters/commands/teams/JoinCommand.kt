package com.joeshuff.headhunters.commands.teams

import com.joeshuff.headhunters.database.TeamDatabaseHandler
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class JoinCommand(private val teamDatabaseHandler: TeamDatabaseHandler) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Only players can join teams!")
            return true
        }

        val player = sender

        // Ensure the player provides a team GUID
        if (args.isEmpty()) {
            player.sendMessage("Please provide the team GUID.")
            return true
        }

        val teamGuid = args[0]

        val team = teamDatabaseHandler.getTeamById(teamGuid)
        if (team == null) {
            player.sendMessage("The team with GUID '$teamGuid' does not exist.")
            return true
        }

        // Add player to the team
        teamDatabaseHandler.addPlayerToTeam(player, team.id)

        player.sendMessage("You have successfully joined the team '${team.teamName}'.")
        return true
    }
}
