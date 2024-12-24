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
): TabExecutor {
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): List<String>? {
        if (sender !is Player || args.size != 1) return null

        if (!sender.isOp) return null

        val team = teamDatabaseHandler.getTeamForPlayer(sender) ?: return emptyList()

        val allHeads = skullDatabaseHandler.getSkullData(team.id)
//        return allHeads.filter { it.earned }
//            .map { it.entityType.name }
//            .filter { it.startsWith(args[0], ignoreCase = true) }

        return emptyList()
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player || !sender.isOp) {
            sender.sendMessage("${ChatColor.RED}You do not have permission to use this command.")
            return true
        }

        // Validate number of arguments
        if (args.isEmpty()) {
            sender.sendMessage("${ChatColor.GOLD}Please provide an entity type.")
            return false
        }

        // Get the entity type (first argument)
        val entityType = try {
            EntityType.valueOf(args[0].toUpperCase())
        } catch (e: IllegalArgumentException) {
            sender.sendMessage("${ChatColor.RED}Invalid entity type. Please provide a valid entity type.")
            return true
        }

        // Get the team name (remaining arguments)
        val teamName =
            if (args.size > 1) {
                args.drop(1).joinToString(" ")
            } else {
                val team = teamDatabaseHandler.getTeamForPlayer(sender)
                team?.teamName
            }

        if (teamName == null) {
            sender.sendMessage("${ChatColor.RED}You must be on a team, or provide a valid team name.")
            return true
        }

        // Retrieve the team by name
        val teamByName = teamDatabaseHandler.getTeamByName(teamName)

        if (teamByName == null) {
            sender.sendMessage("${ChatColor.RED}Team '$teamName' does not exist.")
            return true
        }

        // Mark the skull as earned for the team
        val success = skullDatabaseHandler.markSkullEarned(teamName, sender, entityType)

        if (success) {
            sender.sendMessage("${ChatColor.GREEN}The ${entityType.name} skull has been marked as earned for team '$teamName'.")
        } else {
            sender.sendMessage("${ChatColor.RED}Failed to mark the ${entityType.name} skull as earned for team '$teamName'.")
        }

        return true
    }

}