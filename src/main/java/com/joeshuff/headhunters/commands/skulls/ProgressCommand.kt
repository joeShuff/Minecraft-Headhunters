package com.joeshuff.headhunters.commands.skulls

import com.joeshuff.headhunters.database.SkullDatabaseHandler
import com.joeshuff.headhunters.database.TeamDatabaseHandler
import net.md_5.bungee.api.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class ProgressCommand(
    private val teamDatabaseHandler: TeamDatabaseHandler,
    private val skullDatabaseHandler: SkullDatabaseHandler
): CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (sender !is Player) {
            sender.sendMessage("${ChatColor.RED}Only players can use this command.")
            return true
        }

        // Fetch all teams
        val playerTeam = teamDatabaseHandler.getTeamForPlayer(sender)
        if (playerTeam == null) {
            sender.sendMessage("${ChatColor.RED}You are not on a team.")
            return true
        }

        val skullData = skullDatabaseHandler.getSkullData(playerTeam.id)
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
        val message = StringBuilder()
        message.append("${ChatColor.YELLOW}${playerTeam.teamName}:${ChatColor.RESET} $progressPercentage% ")
        message.append("[$progressBar${ChatColor.RESET}]\n")

        sender.sendMessage(message.toString())

        return true
    }
}