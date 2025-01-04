package com.joeshuff.headhunters.database

import org.bukkit.plugin.Plugin
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class DatabaseHandler(private val plugin: Plugin) {

    private val dbUrl = "jdbc:sqlite:${plugin.dataFolder.absolutePath}/headhunters.db"
    private var connection: Connection? = null

    init {
        // Initialize the database and create tables
        initializeDatabase()
    }

    // Initialize the database connection and tables
    private fun initializeDatabase() {
        try {
            // Ensure the plugin's data folder exists
            val dataFolder = plugin.dataFolder
            if (!dataFolder.exists()) {
                if (dataFolder.mkdirs()) {
                    plugin.logger.info("Plugin data folder created successfully.")
                } else {
                    plugin.logger.severe("Failed to create plugin data folder.")
                    return
                }
            }

            if (connection == null || connection!!.isClosed) {
                val newConnection = DriverManager.getConnection(dbUrl)
                newConnection.autoCommit = true

                plugin.logger.info("Database connection established successfully.")
                createTables(newConnection)

                newConnection.close()
            }
        } catch (e: SQLException) {
            plugin.logger.severe("Error initializing database: ${e.message}")
        }
    }

    // Create necessary tables
    private fun createTables(connection: Connection) {
        val tables = mapOf(
            "teams" to """
                CREATE TABLE IF NOT EXISTS teams (
                    ID TEXT PRIMARY KEY NOT NULL,
                    team_name TEXT NOT NULL,
                    shrine_world TEXT,
                    shrine_x INTEGER,
                    shrine_y INTEGER,
                    shrine_z INTEGER
                );
            """,
            "skulls" to """
                CREATE TABLE IF NOT EXISTS skulls (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    team_id TEXT NOT NULL,
                    entity_type TEXT NOT NULL,
                    earned BOOLEAN NOT NULL DEFAULT 0,
                    earned_by TEXT,
                    earned_at INTEGER,
                    collected BOOLEAN NOT NULL DEFAULT 0,
                    earned_variation TEXT DEFAULT NULL,
                    FOREIGN KEY (team_id) REFERENCES teams(ID)
                );
            """,
            "players" to """
                CREATE TABLE IF NOT EXISTS players (
                    player_id TEXT PRIMARY KEY NOT NULL,
                    team_id TEXT NOT NULL,
                    FOREIGN KEY (team_id) REFERENCES teams(ID)
                );
            """
        )

        tables.forEach { (name, query) ->
            try {
                connection.createStatement()?.use { it.executeUpdate(query) }
                plugin.logger.info("Table '$name' ensured successfully.")
            } catch (e: SQLException) {
                plugin.logger.severe("Error creating table '$name': ${e.message}")
            }
        }
    }

    // Provide the single shared database connection
    fun getConnection(): Connection? {
        if (connection == null || connection?.isClosed == true) {
            try {
                val newConnection = DriverManager.getConnection(dbUrl)
                newConnection.autoCommit = false

                connection = newConnection

                plugin.logger.info("Re-established database connection.")
            } catch (e: SQLException) {
                throw IllegalStateException("Could not re-establish database connection: ${e.message}")
            }
        }
        return connection
    }

    // Close the database connection safely
    fun closeConnection() {
        try {
            connection?.close()
            plugin.logger.info("Database connection closed successfully.")
        } catch (e: SQLException) {
            plugin.logger.severe("Error closing database connection: ${e.message}")
        }
    }
}
