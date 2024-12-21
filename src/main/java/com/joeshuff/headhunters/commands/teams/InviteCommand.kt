package com.joeshuff.headhunters.commands.teams

import com.joeshuff.headhunters.database.TeamDatabaseHandler
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.Listener

class InviteCommand(private val teamDatabaseHandler: TeamDatabaseHandler) : CommandExecutor, Listener {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Only players can invite others!")
            return true
        }

        val player = sender

        // Check if the player is already on a team
        val team = teamDatabaseHandler.getTeamForPlayer(player)
        if (team == null) {
            player.sendMessage("You are not on any team!")
            return true
        }

        // Ensure an argument (player name) is provided
        if (args.isEmpty()) {
            player.sendMessage("Please specify a player to invite.")
            return true
        }

        val invitedPlayerName = args[0]
        val invitedPlayer = player.server.getPlayer(invitedPlayerName)

        if (invitedPlayer == null) {
            player.sendMessage("Player '$invitedPlayerName' is not online.")
            return true
        }

        // Send an invite to the player with a clickable join message
        val inviteMessage = "Click to join team '${team.teamName}'!"
        val joinCommand = "/join ${team.id}"

        invitedPlayer.sendMessage("You have been invited to join ${player.name}'s team!")
        invitedPlayer.spigot().sendMessage(
            TextComponent("[$inviteMessage]").apply {
                isUnderlined = true
                clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, joinCommand)
            }
        )

        player.sendMessage("You have invited '$invitedPlayerName' to your team.")

        return true
    }
}
