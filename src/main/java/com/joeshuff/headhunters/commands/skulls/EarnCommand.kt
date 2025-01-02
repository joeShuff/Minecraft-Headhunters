package com.joeshuff.headhunters.commands.skulls

import com.joeshuff.headhunters.database.SkullDatabaseHandler
import com.joeshuff.headhunters.database.TeamDatabaseHandler
import net.md_5.bungee.api.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player

class EarnCommand(
    private val teamDatabaseHandler: TeamDatabaseHandler,
    private val skullDatabaseHandler: SkullDatabaseHandler
) : TabExecutor {
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String> {
        if (sender !is Player) return mutableListOf()

        if (!sender.isOp) return mutableListOf()

        return when (args.size) {
            1 -> {
                val allTeams = teamDatabaseHandler.getAllTeams()

                return allTeams.map { it.id }.toMutableList()
            }

            2 -> {
                val argOne = args.getOrNull(0)?: return mutableListOf()

                val team = teamDatabaseHandler.getTeamById(argOne)
                    ?: return skullDatabaseHandler.getRawSkullData().map { it.entityType }.toMutableList()

                val allHeads = skullDatabaseHandler.getSkullData(team.id)
                return allHeads.filter { !it.earned }
                    .map { it.entityType }
                    .filter { it.startsWith(args[0], ignoreCase = true) }
                    .toMutableList()
            }

            else -> mutableListOf()
        }
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player || !sender.isOp) {
            sender.sendMessage("${ChatColor.RED}You do not have permission to use this command.")
            return true
        }

        // Validate number of arguments
        if (args.isEmpty()) {
            sender.sendMessage("${ChatColor.GOLD}Please provide a team and an entity type.")
            return true
        }

        // Get the team name
        val teamId = args.getOrNull(0)

        if (teamId == null) {
            sender.sendMessage("${ChatColor.RED}You must provide a valid team ID.")
            return true
        }

        // Get the entity type
        val entityTypeInput = args.getOrNull(1)

        if (entityTypeInput == null) {
            sender.sendMessage("${ChatColor.RED}You must provide an entity type.")
            return true
        }

        val entityType = try {
            EntityType.valueOf(entityTypeInput.toUpperCase())
        } catch (e: IllegalArgumentException) {
            sender.sendMessage("${ChatColor.RED}Invalid entity type. Please provide a valid entity type.")
            return true
        }

        // Retrieve the team by ID
        val team = teamDatabaseHandler.getTeamById(teamId)

        if (team == null) {
            sender.sendMessage("${ChatColor.RED}Team '${teamId}' does not exist.")
            return true
        }

        val providedVariation =
            if (args.size > 2) {
                args.getOrNull(2)
            } else null

        // Mark the skull as earned for the team
        val success = skullDatabaseHandler.markSkullEarned(team.id, sender, entityType)

        if (success) {
            sender.sendMessage("${ChatColor.GREEN}The ${entityType.name} skull has been marked as earned for team '${team.teamName}'.")
        } else {
            sender.sendMessage("${ChatColor.RED}Failed to mark the ${entityType.name} skull as earned for team '${team.teamName}'.")
        }

        return true
    }

}