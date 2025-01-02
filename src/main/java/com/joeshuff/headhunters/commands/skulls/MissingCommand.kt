package com.joeshuff.headhunters.commands.skulls

import com.joeshuff.headhunters.database.SkullDatabaseHandler
import com.joeshuff.headhunters.database.TeamDatabaseHandler
import com.joeshuff.headhunters.util.toDisplayString
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player

class MissingCommand(
    private val teamDatabaseHandler: TeamDatabaseHandler,
    private val skullDatabaseHandler: SkullDatabaseHandler
) : TabExecutor {
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): MutableList<String> {
        val rawSkullData = skullDatabaseHandler.getRawSkullData()
        return rawSkullData.mapNotNull { it.category }.distinct().toMutableList()
    }

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

        val rawSkullData = skullDatabaseHandler.getRawSkullData()

        val category = args.getOrNull(0)
        val filteredSkullData = skullData.filter { skull ->
            val rawData = rawSkullData.find { it.entityType == skull.entityType }

            if (category.isNullOrEmpty()) return@filter true

            return@filter rawData?.category == category
        }

        // Separate skulls into two categories
        val awaitingCollection = filteredSkullData
            .filter { it.earned && !it.collected }.map { it.entityType }
        val notYetEarned = filteredSkullData.filterNot { it.earned }.map { it.entityType }

        // Build the message
        val message = StringBuilder()
        if (category != null) {
            message.append("${ChatColor.GOLD}Your team's performance ($category category): ${ChatColor.RESET}\n")
        } else {
            message.append("${ChatColor.GOLD}Your team's performance:${ChatColor.RESET}\n")
        }


        if (awaitingCollection.isNotEmpty()) {
            message.append("${ChatColor.GREEN}Awaiting Collection:${ChatColor.RESET}")
            awaitingCollection.mapNotNull { EntityType.fromName(it) }.forEach { entityType ->
                message.append("\n- ${ChatColor.YELLOW}${entityType.toDisplayString() + ChatColor.RESET}")
            }
        } else {
            message.append("${ChatColor.GREEN}Awaiting Collection:${ChatColor.GRAY} None")
        }

        if (notYetEarned.isNotEmpty()) {
            message.append("\n${ChatColor.RED}Not yet earned:${ChatColor.RESET}")
            notYetEarned.mapNotNull { EntityType.fromName(it) }.forEach { entityType ->
                message.append("\n- ${ChatColor.YELLOW}${entityType.toDisplayString() + ChatColor.RESET}")
            }
        } else {
            message.append("\n${ChatColor.RED}Not yet earned:${ChatColor.GRAY} None")
        }

        // Send the message to the player
        sender.sendMessage(message.toString())

        return true
    }
}