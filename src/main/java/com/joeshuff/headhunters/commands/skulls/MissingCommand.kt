package com.joeshuff.headhunters.commands.skulls

import com.joeshuff.headhunters.database.SkullDatabaseHandler
import com.joeshuff.headhunters.database.TeamDatabaseHandler
import org.bukkit.command.CommandExecutor
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class MissingCommand(
    private val teamDatabaseHandler: TeamDatabaseHandler,
    private val skullDatabaseHandler: SkullDatabaseHandler
) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage(TextComponent("Only players can use this command.").apply { color = ChatColor.RED })
            return true
        }

        // Get the player's team
        val team = teamDatabaseHandler.getTeamForPlayer(sender)
        if (team == null) {
            sender.sendMessage(TextComponent("You must be part of a team to use this command.").apply {
                color = ChatColor.RED
            })
            return true
        }

        val skullData = skullDatabaseHandler.getSkullData(team.id)

        // Separate skulls into two categories
        val awaitingCollection = skullData.filter { it.earned && !it.collected }.map { it.entityType }
        val notYetEarned = skullData.filterNot { it.earned }.map { it.entityType }

        // Build the message
        val message = StringBuilder()
        message.append("${ChatColor.GOLD}Your team's performance:${ChatColor.RESET}\n")

        if (awaitingCollection.isNotEmpty()) {
            message.append("${ChatColor.GREEN}Awaiting Collection:${ChatColor.RESET}\n")
            awaitingCollection.forEach { entityType ->
                message.append("- ${ChatColor.YELLOW}${entityType.lowercase().replace('_', ' ').capitalize()}\n")
            }
        } else {
            message.append("${ChatColor.GREEN}Awaiting Collection:${ChatColor.GRAY} None\n")
        }

        if (notYetEarned.isNotEmpty()) {
            message.append("${ChatColor.RED}Not yet earned:${ChatColor.RESET}\n")
            notYetEarned.forEach { entityType ->
                message.append("- ${ChatColor.YELLOW}${entityType.lowercase().replace('_', ' ').capitalize()}\n")
            }
        } else {
            message.append("${ChatColor.RED}Not yet earned:${ChatColor.GRAY} None\n")
        }

        // Send the message to the player
        sender.sendMessage(message.toString())

        return true
    }
}