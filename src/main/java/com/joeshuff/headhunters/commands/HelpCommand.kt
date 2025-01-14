package com.joeshuff.headhunters.commands

import com.joeshuff.headhunters.util.sendHelpMessage
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class HelpCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return true

        sender.sendHelpMessage()
        return true
    }
}