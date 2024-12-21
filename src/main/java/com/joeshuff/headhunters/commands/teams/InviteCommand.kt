package com.joeshuff.headhunters.commands.teams

import com.joeshuff.headhunters.database.TeamDatabaseHandler
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.Listener

class InviteCommand(private val teamDatabaseHandler: TeamDatabaseHandler) : CommandExecutor, Listener {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage(TextComponent("Only players can invite others!").apply { color = ChatColor.RED })
            return true
        }

        val player = sender

        // Check if the player is already on a team
        val team = teamDatabaseHandler.getTeamForPlayer(player)
        if (team == null) {
            player.sendMessage(TextComponent("You are not on any team!").apply { color = ChatColor.RED })
            return true
        }

        // Ensure an argument (player name) is provided
        if (args.isEmpty()) {
            player.sendMessage(TextComponent("Please specify a player to invite.").apply { color = ChatColor.YELLOW })
            return true
        }

        val invitedPlayerName = args[0]
        val invitedPlayer = player.server.getPlayer(invitedPlayerName)

        if (invitedPlayer == null) {
            player.sendMessage(TextComponent("Player '$invitedPlayerName' is not online.").apply {
                color = ChatColor.RED
            })
            return true
        }

        // Send an invite to the player with a clickable join message
        val inviteMessage = "Click to join team '${team.teamName}'!"
        val joinCommand = "/join ${team.id}"

        // Send message to invited player with color
        invitedPlayer.sendMessage(TextComponent("You have been invited to join ${player.name}'s team!").apply {
            color = ChatColor.GREEN
        })
        invitedPlayer.spigot().sendMessage(
            TextComponent("[$inviteMessage]").apply {
                isUnderlined = true
                color = ChatColor.AQUA
                clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, joinCommand)
            }
        )

        // Send confirmation to the player
        player.sendMessage(TextComponent("You have invited '$invitedPlayerName' to your team.").apply {
            color = ChatColor.GREEN
        })

        return true
    }

}
