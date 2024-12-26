package com.joeshuff.headhunters.database

import com.joeshuff.headhunters.HeadHuntersPlugin
import com.joeshuff.headhunters.data.models.Team
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import java.sql.SQLException
import java.util.*

class TeamDatabaseHandler(val plugin: HeadHuntersPlugin, val dbHandler: DatabaseHandler) {

    // Create a new team with a given team name and add the player to that team
    fun createTeam(player: Player, teamName: String): Boolean {
        val connection = dbHandler.getConnection() ?: return false

        // Generate a unique ID for the new team
        val teamId = UUID.randomUUID().toString()

        // Insert the new team into the teams table
        val createTeamQuery = """
            INSERT INTO teams (ID, team_name, shrine_world, shrine_x, shrine_y, shrine_z)
            VALUES (?, ?, ?, ?, ?, ?)
        """
        try {
            val statement = connection.prepareStatement(createTeamQuery)
            statement.setString(1, teamId)
            statement.setString(2, teamName)
            statement.setString(3, null) // shrine_world (null initially)
            statement.setInt(4, 0) // shrine_x (default to 0)
            statement.setInt(5, 0) // shrine_y (default to 0)
            statement.setInt(6, 0) // shrine_z (default to 0)

            val rowsAffected = statement.executeUpdate()
            if (rowsAffected > 0) {
                // Team creation succeeded, now add the player to the team
                return addPlayerToTeam(player, teamId)
            }
        } catch (e: SQLException) {
            plugin.logger.severe("Error creating team: ${e.message}")
        }
        return false
    }

    // Get the team object for a player (by player UUID)
    fun getTeamForPlayer(player: Player): Team? {
        val connection = dbHandler.getConnection() ?: return null
        val query = """
            SELECT t.ID, t.team_name, t.shrine_world, t.shrine_x, t.shrine_y, t.shrine_z
            FROM teams t
            INNER JOIN players p ON p.team_id = t.ID
            WHERE p.player_id = ?
        """
        try {
            val statement = connection.prepareStatement(query)
            statement.setString(1, player.uniqueId.toString())
            val resultSet = statement.executeQuery()

            if (resultSet.next()) {
                val teamId = resultSet.getString("ID")
                val teamName = resultSet.getString("team_name")
                val shrineWorldName = resultSet.getString("shrine_world")
                val shrineX = resultSet.getInt("shrine_x")
                val shrineY = resultSet.getInt("shrine_y")
                val shrineZ = resultSet.getInt("shrine_z")

                // Convert shrine information to a Location, if available
                val shrineLocation = if (shrineWorldName != null) {
                    val world = Bukkit.getWorld(shrineWorldName)
                    world?.let { Location(it, shrineX.toDouble(), shrineY.toDouble(), shrineZ.toDouble()) }
                } else {
                    null
                }

                return Team(teamId, teamName, shrineLocation)
            }
        } catch (e: SQLException) {
            plugin.logger.severe("Error getting team for player: ${e.message}")
        }
        return null
    }

    // Get all player GUIDs for a given team ID
    fun getPlayerGuidsForTeam(teamId: String): List<UUID> {
        val connection = dbHandler.getConnection() ?: return emptyList()
        val query = "SELECT player_id FROM players WHERE team_id = ?"
        val playerGuids = mutableListOf<UUID>()

        try {
            val statement = connection.prepareStatement(query)
            statement.setString(1, teamId)
            val resultSet = statement.executeQuery()

            while (resultSet.next()) {
                val playerGuid = UUID.fromString(resultSet.getString("player_id"))
                playerGuids.add(playerGuid)
            }
        } catch (e: SQLException) {
            plugin.logger.severe("Error getting player GUIDs for team: ${e.message}")
        }

        return playerGuids
    }

    // Add a player to a team
    fun addPlayerToTeam(player: Player, teamId: String): Boolean {
        val connection = dbHandler.getConnection() ?: return false
        val query = "INSERT OR REPLACE INTO players (player_id, team_id) VALUES (?, ?)"
        try {
            val statement = connection.prepareStatement(query)
            statement.setString(1, player.uniqueId.toString())
            statement.setString(2, teamId)

            val rowsAffected = statement.executeUpdate()
            return rowsAffected > 0
        } catch (e: SQLException) {
            plugin.logger.severe("Error adding player to team: ${e.message}")
        }
        return false
    }

    // Remove a player from a team
    fun removePlayerFromTeam(player: Player): Boolean {
        val connection = dbHandler.getConnection() ?: return false
        val query = "DELETE FROM players WHERE player_id = ?"
        try {
            val statement = connection.prepareStatement(query)
            statement.setString(1, player.uniqueId.toString())

            val rowsAffected = statement.executeUpdate()
            return rowsAffected > 0
        } catch (e: SQLException) {
            plugin.logger.severe("Error removing player from team: ${e.message}")
        }
        return false
    }

    fun getTeamById(teamId: String): Team? {
        val sql = """
            SELECT t.ID, t.team_name, t.shrine_world, t.shrine_x, t.shrine_y, t.shrine_z
            FROM teams t
            WHERE t.ID = ?
        """

        val connection = dbHandler.getConnection() ?: return null

        try {
            val statement = connection.prepareStatement(sql)
            statement.setString(1, teamId)
            val resultSet = statement.executeQuery()

            if (resultSet.next()) {
                val teamId = resultSet.getString("ID")
                val teamName = resultSet.getString("team_name")
                val shrineWorldName = resultSet.getString("shrine_world")
                val shrineX = resultSet.getInt("shrine_x")
                val shrineY = resultSet.getInt("shrine_y")
                val shrineZ = resultSet.getInt("shrine_z")

                // Convert shrine information to a Location, if available
                val shrineLocation = if (shrineWorldName != null) {
                    val world = Bukkit.getWorld(shrineWorldName)
                    world?.let { Location(it, shrineX.toDouble(), shrineY.toDouble(), shrineZ.toDouble()) }
                } else {
                    null
                }

                return Team(teamId, teamName, shrineLocation)
            }
        } catch (e: SQLException) {
            plugin.logger.severe("Error getting team by ID: ${e.message}")
        }
        return null
    }

    fun getTeamByName(queryTeamName: String): Team? {
        val sql = """
            SELECT t.ID, t.team_name, t.shrine_world, t.shrine_x, t.shrine_y, t.shrine_z
            FROM teams t
            INNER JOIN players p ON p.team_id = t.ID
            WHERE t.team_name = ?
        """

        val connection = dbHandler.getConnection() ?: return null

        try {
            val statement = connection.prepareStatement(sql)
            statement.setString(1, queryTeamName)
            val resultSet = statement.executeQuery()

            if (resultSet.next()) {
                val teamId = resultSet.getString("ID")
                val teamName = resultSet.getString("team_name")
                val shrineWorldName = resultSet.getString("shrine_world")
                val shrineX = resultSet.getInt("shrine_x")
                val shrineY = resultSet.getInt("shrine_y")
                val shrineZ = resultSet.getInt("shrine_z")

                // Convert shrine information to a Location, if available
                val shrineLocation = if (shrineWorldName != null) {
                    val world = Bukkit.getWorld(shrineWorldName)
                    world?.let { Location(it, shrineX.toDouble(), shrineY.toDouble(), shrineZ.toDouble()) }
                } else {
                    null
                }

                return Team(teamId, teamName, shrineLocation)
            }
        } catch (e: SQLException) {
            plugin.logger.severe("Error getting team by ID: ${e.message}")
        }
        return null
    }

    fun getAllTeams(): List<Team> {
        val connection = dbHandler.getConnection() ?: return emptyList()
        val query = """
        SELECT ID, team_name, shrine_world, shrine_x, shrine_y, shrine_z
        FROM teams
    """
        val teams = mutableListOf<Team>()

        try {
            val statement = connection.prepareStatement(query)
            val resultSet = statement.executeQuery()

            while (resultSet.next()) {
                val teamId = resultSet.getString("ID")
                val teamName = resultSet.getString("team_name")
                val shrineWorldName = resultSet.getString("shrine_world")
                val shrineX = resultSet.getInt("shrine_x")
                val shrineY = resultSet.getInt("shrine_y")
                val shrineZ = resultSet.getInt("shrine_z")

                // Convert shrine information to a Location, if available
                val shrineLocation = if (shrineWorldName != null) {
                    val world = Bukkit.getWorld(shrineWorldName)
                    world?.let { Location(it, shrineX.toDouble(), shrineY.toDouble(), shrineZ.toDouble()) }
                } else {
                    null
                }

                teams.add(Team(teamId, teamName, shrineLocation))
            }
        } catch (e: SQLException) {
            plugin.logger.severe("Error retrieving all teams: ${e.message}")
        }
        return teams
    }

}
