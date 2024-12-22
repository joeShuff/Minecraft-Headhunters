package com.joeshuff.headhunters.util

import org.bukkit.Bukkit
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.entity.Player

class BossBarManager {
    private val playerBossBars = mutableMapOf<Player, BossBar>()

    fun updateBossBar(player: Player, title: String, progress: Double) {
        val bossBar = playerBossBars.computeIfAbsent(player) {
            Bukkit.createBossBar(title, BarColor.GREEN, BarStyle.SEGMENTED_10)
        }

        bossBar.setTitle(title)
        bossBar.progress = progress
        if (!bossBar.players.contains(player)) {
            bossBar.addPlayer(player)
        }
    }

    fun removeBossBar(player: Player) {
        playerBossBars[player]?.removePlayer(player)
        playerBossBars.remove(player)
    }

    fun clearAll() {
        playerBossBars.values.forEach { it.removeAll() }
        playerBossBars.clear()
    }
}
