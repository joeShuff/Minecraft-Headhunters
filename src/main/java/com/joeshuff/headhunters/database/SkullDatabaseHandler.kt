package com.joeshuff.headhunters.database

import com.joeshuff.headhunters.HeadHuntersPlugin
import com.joeshuff.headhunters.data.models.SkullData
import org.bukkit.entity.Player
import org.bukkit.entity.EntityType
import java.sql.SQLException
import java.sql.Timestamp
import java.util.Collections.emptyList

class SkullDatabaseHandler(private val plugin: HeadHuntersPlugin, private val dbHandler: DatabaseHandler) {

    // Seed the skulls for a team (add all entity types to track)
    fun seedTeam(teamId: String): Boolean {
        val connection = dbHandler.getConnection() ?: return false
        val entityTypes = EntityType.values().filter { it.isAlive }
        val insertQuery = """
            INSERT INTO skulls (team_id, entity_type, earned, collected)
            VALUES (?, ?, false, false)
        """
        try {
            val statement = connection.prepareStatement(insertQuery)
            connection.autoCommit = false // Ensure it's done in a single transaction

            entityTypes.forEach { entityType ->
                statement.setString(1, teamId)
                statement.setString(2, entityType.name)
                statement.addBatch()
            }

            val affectedRows = statement.executeBatch()
            connection.commit()
            return affectedRows.sum() > 0
        } catch (e: SQLException) {
            plugin.logger.severe("Error seeding team skulls: ${e.message}")
        }
        return false
    }

    // Mark a skull as earned, setting the earned flag and earned_by/earned_at fields
    fun markSkullEarned(teamId: String, player: Player, entityType: EntityType): Boolean {
        val connection = dbHandler.getConnection() ?: return false
        val updateQuery = """
            UPDATE skulls
            SET earned = true, earned_by = ?, earned_at = ?
            WHERE team_id = ? AND entity_type = ? AND earned = false
        """
        try {
            val statement = connection.prepareStatement(updateQuery)
            statement.setString(1, player.uniqueId.toString())
            statement.setLong(2, System.currentTimeMillis())
            statement.setString(3, teamId)
            statement.setString(4, entityType.name)

            val rowsAffected = statement.executeUpdate()
            return rowsAffected > 0
        } catch (e: SQLException) {
            plugin.logger.severe("Error marking skull as earned: ${e.message}")
        }
        return false
    }

    // Mark a skull as collected
    fun markSkullCollected(teamId: String, entityType: EntityType): Boolean {
        val connection = dbHandler.getConnection() ?: return false
        val updateQuery = """
            UPDATE skulls
            SET collected = true
            WHERE team_id = ? AND entity_type = ? AND collected = false
        """
        try {
            val statement = connection.prepareStatement(updateQuery)
            statement.setString(1, teamId)
            statement.setString(2, entityType.name)

            val rowsAffected = statement.executeUpdate()
            return rowsAffected > 0
        } catch (e: SQLException) {
            plugin.logger.severe("Error marking skull as collected: ${e.message}")
        }
        return false
    }

    // Check if the skull has already been earned for the team
    fun isSkullEarned(teamId: String, entityType: EntityType): Boolean {
        val connection = dbHandler.getConnection() ?: return true // Assume earned if DB connection fails
        val query = """
        SELECT earned FROM skulls
        WHERE team_id = ? AND entity_type = ?
    """
        try {
            val statement = connection.prepareStatement(query)
            statement.setString(1, teamId)
            statement.setString(2, entityType.name)
            val resultSet = statement.executeQuery()

            // If no record exists for the entity type, consider it as earned
            if (!resultSet.next()) {
                return true
            }

            // Return whether the skull is marked as earned
            return resultSet.getBoolean("earned")
        } catch (e: SQLException) {
            plugin.logger.severe("Error checking if skull is earned: ${e.message}")
        }
        return true // Assume earned on error to prevent unintended behavior
    }

    // Check if the skull has already been collected for the team
    fun isSkullCollected(teamId: String, entityType: EntityType): Boolean {
        val connection = dbHandler.getConnection() ?: return false
        val query = """
            SELECT collected FROM skulls
            WHERE team_id = ? AND entity_type = ? AND collected = true
        """
        try {
            val statement = connection.prepareStatement(query)
            statement.setString(1, teamId)
            statement.setString(2, entityType.name)
            val resultSet = statement.executeQuery()

            return resultSet.next()
        } catch (e: SQLException) {
            plugin.logger.severe("Error checking if skull is collected: ${e.message}")
        }
        return false
    }

    // Get the skull data for a team (returns a list of all skull records for the team)
    fun getSkullData(teamId: String): List<SkullData> {
        val connection = dbHandler.getConnection() ?: return emptyList()
        val query = """
            SELECT id, team_id, entity_type, earned, earned_by, earned_at, collected
            FROM skulls
            WHERE team_id = ?
        """
        val skullDataList = mutableListOf<SkullData>()
        try {
            val statement = connection.prepareStatement(query)
            statement.setString(1, teamId)
            val resultSet = statement.executeQuery()

            while (resultSet.next()) {
                val skullData = SkullData(
                    id = resultSet.getInt("id"),
                    teamId = resultSet.getString("team_id"),
                    entityType = resultSet.getString("entity_type"),
                    earned = resultSet.getBoolean("earned"),
                    earnedBy = resultSet.getString("earned_by"),
                    earnedAt = resultSet.getLong("earned_at"),
                    collected = resultSet.getBoolean("collected")
                )
                skullDataList.add(skullData)
            }
        } catch (e: SQLException) {
            plugin.logger.severe("Error retrieving skull data: ${e.message}")
        }
        return skullDataList
    }

    fun getEarnedButNotCollectedSkulls(teamId: String): List<SkullData> {
        val connection = dbHandler.getConnection() ?: return emptyList()
        val query = """
        SELECT id, team_id, entity_type, earned, earned_by, earned_at, collected
        FROM skulls
        WHERE team_id = ? AND earned = true AND collected = false
    """
        val skullDataList = mutableListOf<SkullData>()
        try {
            val statement = connection.prepareStatement(query)
            statement.setString(1, teamId)
            val resultSet = statement.executeQuery()

            while (resultSet.next()) {
                val skullData = SkullData(
                    id = resultSet.getInt("id"),
                    teamId = resultSet.getString("team_id"),
                    entityType = resultSet.getString("entity_type"),
                    earned = resultSet.getBoolean("earned"),
                    earnedBy = resultSet.getString("earned_by"),
                    earnedAt = resultSet.getLong("earned_at"),
                    collected = resultSet.getBoolean("collected")
                )
                skullDataList.add(skullData)
            }
        } catch (e: SQLException) {
            plugin.logger.severe("Error retrieving earned but not collected skulls: ${e.message}")
        }
        return skullDataList
    }

}