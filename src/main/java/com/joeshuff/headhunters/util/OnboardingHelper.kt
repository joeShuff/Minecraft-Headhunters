package com.joeshuff.headhunters.util

import net.md_5.bungee.api.ChatColor
import org.bukkit.entity.Player

fun Player.sendHelpMessage() {

    fun separatorMessage() {
        sendMessage("${ChatColor.YELLOW}====")
    }

    fun headerMessage(title: String) {
        sendMessage("${ChatColor.BLUE}$title")
    }

    sendMessage("${ChatColor.GOLD}${ChatColor.BOLD}Headhunters Plugin Commands:")

    if (isOp) {
        headerMessage("Admin Commands")
        sendMessage("${ChatColor.AQUA}/teams ${ChatColor.WHITE}- List all the teams and their IDs for use with other commands")
        sendMessage("${ChatColor.AQUA}/join <team_guid> <playername> ${ChatColor.WHITE}- Add a specific player to a specific team")
        sendMessage("${ChatColor.AQUA}/earn <team_guid> <mob type> <variation name> ${ChatColor.WHITE}- Mark a skull for a team as earned.")
    }

    headerMessage("Team Commands")
    sendMessage("${ChatColor.AQUA}/createteam <team_name> ${ChatColor.WHITE}- Create a new team with the given name.")
    sendMessage("${ChatColor.AQUA}/invite <playername> ${ChatColor.WHITE}- Invite a player to your team.")
    sendMessage("${ChatColor.AQUA}/setshrine ${ChatColor.WHITE}- Set your team's shrine location.")
    sendMessage("${ChatColor.AQUA}/resummon ${ChatColor.WHITE}- Resummon a collected mob head at the shrine.")
    sendMessage("${ChatColor.AQUA}/leave ${ChatColor.WHITE}- Leave your team.")
    separatorMessage()
    headerMessage("Progress Commands")
    sendMessage("${ChatColor.AQUA}/progress ${ChatColor.WHITE}- View your team's progress.")
    sendMessage("${ChatColor.AQUA}/globalprogress ${ChatColor.WHITE}- View the progress of all teams.")
    sendMessage("${ChatColor.AQUA}/missing ${ChatColor.WHITE}- View the list of mobs whose heads are yet to be collected.")
    separatorMessage()
    headerMessage("Help Commands")
    sendMessage("${ChatColor.AQUA}/headhuntershelp ${ChatColor.WHITE}- Show this help menu.")
    sendMessage("${ChatColor.GRAY}Type ${ChatColor.YELLOW}/help <command name> ${ChatColor.GRAY}for more details about a specific command.")
}