package com.joeshuff.headhunters

import com.joeshuff.headhunters.commands.teams.*
import com.joeshuff.headhunters.database.DatabaseHandler
import com.joeshuff.headhunters.database.ShrineDatabaseHandler
import com.joeshuff.headhunters.database.SkullDatabaseHandler
import com.joeshuff.headhunters.database.TeamDatabaseHandler
import com.joeshuff.headhunters.listeners.PlayerListener
import com.joeshuff.headhunters.listeners.SkullEarnedListener
import com.joeshuff.headhunters.listeners.Stoppable
import com.joeshuff.headhunters.regions.RegionManager
import org.bukkit.plugin.java.JavaPlugin

class HeadHuntersPlugin : JavaPlugin() {

    private val stoppables = arrayListOf<Stoppable>()

    private val dbHandler = DatabaseHandler(this)

    private val regionManager = RegionManager()
    private val skullController = SkullController(this)

    private val shrineDatabaseHandler = ShrineDatabaseHandler(this, dbHandler, regionManager)
    private val teamDatabaseHandler = TeamDatabaseHandler(this, dbHandler)
    private val skullDatabaseHandler = SkullDatabaseHandler(this, dbHandler)

    override fun onEnable() {
        registerCommands()
        registerEventListeners()

        saveResource("head_data.json", true)
        saveDefaultConfig()

        logger.info("====================")
        logger.info("HEAD HUNTER PLUGIN LOADED")
        logger.info("====================")
    }

    override fun onDisable() {
        // Plugin shutdown logic
        stoppables.forEach { it.stop() }
        dbHandler.closeConnection()
    }

    private fun registerCommands() {
        getCommand("setshrine")?.setExecutor(SetShrineCommand(shrineDatabaseHandler, teamDatabaseHandler))

        getCommand("createteam")?.setExecutor(CreateTeamCommand(teamDatabaseHandler, skullDatabaseHandler))

        getCommand("invite")?.setExecutor(InviteCommand(teamDatabaseHandler))

        getCommand("join")?.setExecutor(JoinCommand(teamDatabaseHandler))

        getCommand("leaveteam")?.setExecutor(LeaveTeamCommand(teamDatabaseHandler))
//
//        getCommand("resummon")?.setExecutor(ResummonCommand())
//
//        getCommand("progress")?.setExecutor(ProgressCommand())
//
//        getCommand("missing")?.setExecutor(MissingCommand())
//
//        getCommand("globalprogress")?.setExecutor(GlobalProgressCommand())
//
//        getCommand("disablebossbar")?.setExecutor(DisableBossBarCommand())

    }

    private fun registerEventListeners() {
        stoppables.add(SkullEarnedListener(this, teamDatabaseHandler, skullDatabaseHandler))
        stoppables.add(PlayerListener(this, regionManager, teamDatabaseHandler, skullDatabaseHandler, skullController))
    }
}