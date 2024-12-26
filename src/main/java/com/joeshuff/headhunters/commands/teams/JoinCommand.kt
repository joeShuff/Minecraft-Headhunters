package com.joeshuff.headhunters.commands.teams

import com.joeshuff.headhunters.database.TeamDatabaseHandler
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

class JoinCommand(private val teamDatabaseHandler: TeamDatabaseHandler) : TabExecutor {

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): MutableList<String> {
        if (sender is Player && !sender.isOp) return mutableListOf()

        return when (args.size) {
            1 -> {
                // First argument: Prepopulate with team GUIDs
                teamDatabaseHandler.getAllTeams().map { it.id }.filter { it.startsWith(args[0]) }.toMutableList()
            }

            2 -> {
                // Second argument: Prepopulate with online player names
                Bukkit.getOnlinePlayers().map { it.name }.filter { it.startsWith(args[1], ignoreCase = true) }
                    .toMutableList()
            }

            else -> mutableListOf()
        }
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage(TextComponent("Only players can join teams!").apply { color = ChatColor.RED })
            return true
        }

        // Ensure the player provides a team GUID
        if (args.isEmpty()) {
            sender.sendMessage(TextComponent("Please provide the team GUID.").apply { color = ChatColor.YELLOW })
            return true
        }

        val teamGuid = args[0]

        val hasProvidedPlayer = args.size > 1
        val searchedPlayerName = args.getOrNull(1)

        var player: Player = sender

        searchedPlayerName?.let {
            val searchedPlayer = Bukkit.getPlayer(searchedPlayerName)

            if (searchedPlayer == null) {
                sender.sendMessage("${ChatColor.RED}Cannot find player ${ChatColor.DARK_RED}${searchedPlayerName}")
                return true
            } else {
                player = searchedPlayer
            }
        }

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

        if (hasProvidedPlayer) {
            sender.sendMessage("${ChatColor.GREEN}You have added ${ChatColor.AQUA}${searchedPlayerName}${ChatColor.GREEN} to the team ${ChatColor.AQUA}${team.teamName}")
        }

        return true
    }

}
