package com.joeshuff.headhunters.commands.teams

import com.joeshuff.headhunters.database.SkullDatabaseHandler
import com.joeshuff.headhunters.database.TeamDatabaseHandler
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CreateTeamCommand(
    private val teamDatabaseHandler: TeamDatabaseHandler,
    private val skullDatabaseHandler: SkullDatabaseHandler
) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage(TextComponent("Only players can create a team!").apply { color = ChatColor.RED })
            return true
        }

        val player = sender

        // Check if the player is already on a team
        val existingTeam = teamDatabaseHandler.getTeamForPlayer(player)
        if (existingTeam != null) {
            player.sendMessage(TextComponent("You are already on a team! Leave your current team before creating a new one.").apply {
                color = ChatColor.RED
            })
            return true
        }

        // Ensure that the player provides a team name
        if (args.isEmpty()) {
            player.sendMessage(TextComponent("Please provide a team name!").apply { color = ChatColor.YELLOW })
            return true
        }

        val teamName = args.joinToString(" ")

        // Attempt to create the team and add the player
        val teamCreated = teamDatabaseHandler.createTeam(player, teamName)
        if (teamCreated) {
            val team = teamDatabaseHandler.getTeamForPlayer(player)
            team?.id?.let {
                skullDatabaseHandler.seedTeam(it)
                player.sendMessage(TextComponent("You have successfully created the team '$teamName' and have been added to it.").apply {
                    color = ChatColor.GREEN
                })
            }
        } else {
            player.sendMessage(TextComponent("There was an error creating your team.").apply { color = ChatColor.RED })
        }

        return true
    }

}