package com.joeshuff.headhunters.util

import org.bukkit.Bukkit
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.entity.Player
import java.util.UUID

class BossBarManager {
    private val playerBossBars = mutableMapOf<UUID, BossBar>()

    fun updateBossBar(player: Player, title: String, progress: Double) {
        val bossBar = playerBossBars.computeIfAbsent(player.uniqueId) {
            Bukkit.createBossBar(title, BarColor.GREEN, BarStyle.SEGMENTED_20)
        }

        bossBar.setTitle(title)
        bossBar.progress = progress
        bossBar.addPlayer(player)
    }

    fun removeBossBar(player: Player) {
        playerBossBars[player.uniqueId]?.removePlayer(player)
        playerBossBars.remove(player.uniqueId)
    }

    fun clearAll() {
        playerBossBars.values.forEach { it.removeAll() }
        playerBossBars.clear()
    }
}
