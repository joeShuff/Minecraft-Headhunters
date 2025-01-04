package com.joeshuff.headhunters.timers

import com.joeshuff.headhunters.database.TeamDatabaseHandler
import com.joeshuff.headhunters.database.SkullDatabaseHandler
import com.joeshuff.headhunters.util.BossBarManager
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

class TeamProgressTask(
    private val teamDbHandler: TeamDatabaseHandler,
    private val skullDbHandler: SkullDatabaseHandler,
    private val bossBarManager: BossBarManager
) : BukkitRunnable() {

    override fun run() {
        Bukkit.getOnlinePlayers().forEach { player ->
            val team = teamDbHandler.getTeamForPlayer(player)

            if (team != null) {
                val allSkulls = skullDbHandler.getSkullData(team.id)
                val earnedSkulls = skullDbHandler.getSkullData(team.id).count { it.earned }
                val progress = if (allSkulls.isEmpty()) 0.0 else earnedSkulls.toDouble() / allSkulls.size

                bossBarManager.updateBossBar(
                    player,
                    "${team.teamName} Progress: ${(progress * 100).toInt()}%",
                    progress
                )
            } else {
                bossBarManager.removeBossBar(player)
                sendJoinTeamPrompt(player)
            }
        }
    }

    private fun sendJoinTeamPrompt(player: Player) {
        val actionBarMessage = Component.text("/createteam <team name>")
            .color(NamedTextColor.DARK_GREEN)
            .append(Component.text(" or ask for an invite!")
                .color(NamedTextColor.YELLOW)
                .decorate(TextDecoration.ITALIC))

        // Send the action bar message to the player
        player.sendActionBar(actionBarMessage)
    }
}
