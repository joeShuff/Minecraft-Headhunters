package com.joeshuff.headhunters.commands.skulls

import com.joeshuff.headhunters.database.SkullDatabaseHandler
import com.joeshuff.headhunters.database.TeamDatabaseHandler
import net.md_5.bungee.api.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class GlobalProgressCommand(
    private val teamDatabaseHandler: TeamDatabaseHandler,
    private val skullDatabaseHandler: SkullDatabaseHandler
): CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("${ChatColor.RED}Only players can use this command.")
            return true
        }

        // Fetch all teams
        val allTeams = teamDatabaseHandler.getAllTeams()
        if (allTeams.isEmpty()) {
            sender.sendMessage("${ChatColor.RED}No teams found.")
            return true
        }

        // Build progress information for each team
        val message = StringBuilder()
        message.append("${ChatColor.GOLD}Global Progress:${ChatColor.RESET}\n")

        for (team in allTeams) {
            val skullData = skullDatabaseHandler.getSkullData(team.id)
            val totalSkulls = skullData.size
            val earnedSkulls = skullData.count { it.earned }

            val progressPercentage = if (totalSkulls > 0) (earnedSkulls * 100) / totalSkulls else 0

            // Build the progress bar
            val progressBarLength = 20
            val filledLength = (progressPercentage * progressBarLength) / 100
            val progressBar = buildString {
                append("${ChatColor.GREEN}")
                append("█".repeat(filledLength))
                append("${ChatColor.RED}")
                append("█".repeat(progressBarLength - filledLength))
            }

            // Add team progress to the message
            message.append("${ChatColor.YELLOW}${team.teamName}:${ChatColor.RESET} $progressPercentage% ")
            message.append("[$progressBar${ChatColor.RESET}]\n")
        }

        // Send the message to the player
        sender.sendMessage(message.toString())

        return true
    }


}