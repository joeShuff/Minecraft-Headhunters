package com.joeshuff.headhunters

import com.joeshuff.headhunters.commands.skulls.*
import com.joeshuff.headhunters.commands.teams.*
import com.joeshuff.headhunters.database.DatabaseHandler
import com.joeshuff.headhunters.database.ShrineDatabaseHandler
import com.joeshuff.headhunters.database.SkullDatabaseHandler
import com.joeshuff.headhunters.database.TeamDatabaseHandler
import com.joeshuff.headhunters.listeners.PlayerListener
import com.joeshuff.headhunters.listeners.SkullEarnedListener
import com.joeshuff.headhunters.listeners.Stoppable
import com.joeshuff.headhunters.regions.RegionManager
import com.joeshuff.headhunters.timers.TeamProgressTask
import com.joeshuff.headhunters.util.BossBarManager
import com.joeshuff.headhunters.util.SkullController
import org.bukkit.plugin.java.JavaPlugin

class HeadHuntersPlugin : JavaPlugin() {

    private val stoppables = arrayListOf<Stoppable>()

    private val regionManager by lazy { RegionManager() }

    private val dbHandler by lazy { DatabaseHandler(this) }

    private val shrineDatabaseHandler by lazy { ShrineDatabaseHandler(this, dbHandler, regionManager) }
    private val teamDatabaseHandler by lazy { TeamDatabaseHandler(this, dbHandler) }
    private val skullDatabaseHandler by lazy { SkullDatabaseHandler(this, dbHandler) }

    private val skullController by lazy { SkullController(this, skullDatabaseHandler) }

    private val bossBarManager = BossBarManager()

    override fun onEnable() {
        saveResource("skull_data.json", true)
        saveDefaultConfig()

        registerCommands()
        registerEventListeners()
        setupTimers()

        shrineDatabaseHandler.initShrines()

        logger.info("====================")
        logger.info("HEAD HUNTER PLUGIN LOADED")
        logger.info("====================")
    }

    override fun onDisable() {
        // Plugin shutdown logic
        stoppables.forEach { it.stop() }
        dbHandler.closeConnection()
        bossBarManager.clearAll()
    }

    private fun setupTimers() {
        TeamProgressTask(teamDatabaseHandler, skullDatabaseHandler, bossBarManager)
            .runTaskTimer(this, 0L, 20L) // Runs every second
    }

    private fun registerCommands() {
        getCommand("setshrine")?.setExecutor(SetShrineCommand(shrineDatabaseHandler, teamDatabaseHandler))

        getCommand("createteam")?.setExecutor(CreateTeamCommand(teamDatabaseHandler, skullDatabaseHandler))

        getCommand("invite")?.setExecutor(InviteCommand(teamDatabaseHandler))

        getCommand("join")?.setExecutor(JoinCommand(teamDatabaseHandler))

        getCommand("teams")?.setExecutor(ListTeamsCommand(teamDatabaseHandler))

        getCommand("leaveteam")?.setExecutor(LeaveTeamCommand(teamDatabaseHandler))

        getCommand("resummon")?.setExecutor(
            ResummonCommand(
                this,
                teamDatabaseHandler,
                skullDatabaseHandler,
                skullController
            )
        )

        getCommand("progress")?.setExecutor(ProgressCommand(teamDatabaseHandler, skullDatabaseHandler))

        getCommand("missing")?.setExecutor(MissingCommand(teamDatabaseHandler, skullDatabaseHandler))

        getCommand("globalprogress")?.setExecutor(GlobalProgressCommand(teamDatabaseHandler, skullDatabaseHandler))

        getCommand("earn")?.setExecutor(EarnCommand(teamDatabaseHandler, skullDatabaseHandler))
    }

    private fun registerEventListeners() {
        stoppables.add(SkullEarnedListener(this, teamDatabaseHandler, skullDatabaseHandler))
        stoppables.add(PlayerListener(this, regionManager, teamDatabaseHandler, skullDatabaseHandler, skullController))
    }
}