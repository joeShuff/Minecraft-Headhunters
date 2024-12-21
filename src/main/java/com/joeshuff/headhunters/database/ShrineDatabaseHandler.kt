package com.joeshuff.headhunters.database

import com.joeshuff.headhunters.regions.RegionManager
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.plugin.Plugin
import java.sql.SQLException

class ShrineDatabaseHandler(
    private val plugin: Plugin,
    private val dbHandler: DatabaseHandler,
    private val regionManager: RegionManager
) {

    init {
        //Initialise all shrines into memory
        getAllShrines().forEach {
            regionManager.upsertRegion(it.first, it.second, getShrineRadius())
        }
    }

    private fun getShrineRadius(): Double {
        // Retrieve the shrine radius from the config
        val radius = plugin.config.getDouble("shrine-radius", 15.0)  // Default value is 10.0 if not set
        return radius
    }

    // Get the shrine location for a team
    fun getShrine(teamId: String): Location? {
        val connection = dbHandler.getConnection() ?: return null
        val query = "SELECT shrine_world, shrine_x, shrine_y, shrine_z FROM teams WHERE ID = ?"
        try {
            val statement = connection.prepareStatement(query)
            statement.setString(1, teamId)
            val resultSet = statement.executeQuery()

            if (resultSet.next()) {
                val shrineWorldName = resultSet.getString("shrine_world") ?: ""
                val shrineX = resultSet.getInt("shrine_x")
                val shrineY = resultSet.getInt("shrine_y")
                val shrineZ = resultSet.getInt("shrine_z")

                // If world is found, return the location, else return null
                val world = Bukkit.getWorld(shrineWorldName)
                return world?.let { Location(it, shrineX.toDouble(), shrineY.toDouble(), shrineZ.toDouble()) }
            }
        } catch (e: SQLException) {
            plugin.logger.severe("Error getting shrine data: ${e.message}")
        }
        return null
    }

    fun getAllShrines(): List<Pair<String, Location>> {
        val connection = dbHandler.getConnection() ?: return emptyList()
        val query = """
        SELECT ID, shrine_x, shrine_y, shrine_z
        FROM teams
    """
        val shrines = mutableListOf<Pair<String, Location?>>()

        try {
            val statement = connection.prepareStatement(query)
            val resultSet = statement.executeQuery()

            while (resultSet.next()) {
                val teamId = resultSet.getString("ID")
                val shrineX = resultSet.getInt("shrine_x")
                val shrineY = resultSet.getInt("shrine_y")
                val shrineZ = resultSet.getInt("shrine_z")
                val shrineLocation = if (shrineX != 0 && shrineY != 0 && shrineZ != 0) {
                    Location(null, shrineX.toDouble(), shrineY.toDouble(), shrineZ.toDouble())
                } else {
                    null
                }
                shrines.add(Pair(teamId, shrineLocation))
            }
        } catch (e: SQLException) {
            plugin.logger.severe("Error retrieving shrines from the database: ${e.message}")
        }

        return shrines.mapNotNull { shrine ->
            shrine.second?.let {
                Pair(shrine.first, it)
            }
        }
    }

    // Set the shrine location for a team
    fun setShrine(teamId: String, location: Location): Boolean {
        val connection = dbHandler.getConnection() ?: return false
        val query = """
            UPDATE teams 
            SET shrine_world = ?, shrine_x = ?, shrine_y = ?, shrine_z = ? 
            WHERE ID = ?
        """
        try {
            val statement = connection.prepareStatement(query)
            statement.setString(1, location.world?.name)
            statement.setInt(2, location.blockX)
            statement.setInt(3, location.blockY)
            statement.setInt(4, location.blockZ)
            statement.setString(5, teamId)

            // Check if the update was successful
            val rowsAffected = statement.executeUpdate()
            val success = rowsAffected > 0

            if (success) {
                regionManager.upsertRegion(teamId, location, getShrineRadius())
            }
        } catch (e: SQLException) {
            plugin.logger.severe("Error setting shrine data: ${e.message}")
        }
        return false
    }

}