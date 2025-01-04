package com.joeshuff.headhunters.listeners

import com.joeshuff.headhunters.HeadHuntersPlugin
import com.joeshuff.headhunters.util.SkullController
import org.bukkit.Material
import org.bukkit.block.data.type.CreakingHeart
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

class CreakingKillListener(
    plugin: HeadHuntersPlugin,
    private val skullController: SkullController
) : Listener, Stoppable {

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val block = event.block
        val blockData = block.blockData

        if (block.type == Material.CREAKING_HEART && blockData is CreakingHeart) {
            if (blockData.isActive) {
                skullController.onSkullEarn(
                    EntityType.CREAKING,
                    event.player
                )
            }
        }
    }

    override fun stop() {
        HandlerList.unregisterAll(this)
    }
}