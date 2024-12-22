package com.joeshuff.headhunters.timers

import com.joeshuff.headhunters.database.TeamDatabaseHandler
import com.joeshuff.headhunters.database.SkullDatabaseHandler
import com.joeshuff.headhunters.util.BossBarManager
import org.bukkit.Bukkit
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
                    "Team Progress: ${(progress * 100).toInt()}%",
                    progress
                )
            } else {
                bossBarManager.removeBossBar(player)
            }
        }
    }
}
