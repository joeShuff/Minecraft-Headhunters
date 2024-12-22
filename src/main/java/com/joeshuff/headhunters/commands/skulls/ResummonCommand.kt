package com.joeshuff.headhunters.commands.skulls

import com.joeshuff.headhunters.util.SkullController
import com.joeshuff.headhunters.database.SkullDatabaseHandler
import com.joeshuff.headhunters.database.TeamDatabaseHandler
import net.md_5.bungee.api.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player

class ResummonCommand(
    private val teamDatabaseHandler: TeamDatabaseHandler,
    private val skullDatabaseHandler: SkullDatabaseHandler,
    private val skullController: SkullController
): TabExecutor {
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): List<String>? {
        if (sender !is Player || args.size != 1) return null

        val team = teamDatabaseHandler.getTeamForPlayer(sender) ?: return emptyList()

        val allHeads = skullDatabaseHandler.getSkullData(team.id)
        return allHeads.filter { it.earned }
            .map { it.entityType }
            .filter { it.startsWith(args[0], ignoreCase = true) }
    }


    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("${ChatColor.RED}Only players can use this command.")
            return true
        }

        val team = teamDatabaseHandler.getTeamForPlayer(sender)
        if (team == null) {
            sender.sendMessage("{${ChatColor.RED}You must be part of a team to resummon skulls.")
            return true
        }

        if (args.isEmpty()) {
            sender.sendMessage("${ChatColor.GOLD}Usage: /resummon <skull_name>")
            return true
        }

        val skullName = args[0]
        val allHeads = skullDatabaseHandler.getSkullData(team.id)
        val entityType = EntityType.fromName(skullName)

        if (entityType == null) {
            sender.sendMessage("${ChatColor.RED}Unknown entity type for name $skullName")
            return true
        }

        val earnedHead = allHeads.find { it.entityType.equals(skullName, ignoreCase = true) && it.earned }

        if (earnedHead == null) {
            sender.sendMessage("Your team has not earned the skull for '$skullName' or it doesn't exist.")
            return true
        }

        // Resummon the skull
        val shrineLocation = team.shrineLocation
        if (shrineLocation == null) {
            sender.sendMessage("Your team does not have a shrine set. Cannot resummon skull.")
            return true
        }

        skullController.spawnSkullAtLocation(shrineLocation, entityType)
        sender.sendMessage("§aThe skull for '${earnedHead.entityType}' has been resummoned at your team's shrine.")
        return true
    }
}