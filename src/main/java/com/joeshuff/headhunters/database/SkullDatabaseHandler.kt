package com.joeshuff.headhunters.database

import com.google.gson.Gson
import com.joeshuff.headhunters.HeadHuntersPlugin
import com.joeshuff.headhunters.data.models.SkullDBData
import com.joeshuff.headhunters.data.models.SkullSourceData
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import java.io.File
import java.sql.SQLException


class SkullDatabaseHandler(private val plugin: HeadHuntersPlugin, private val dbHandler: DatabaseHandler) {

    fun getRawSkullData(): List<SkullSourceData> {
        val skullDataFile = File(plugin.dataFolder, "skull_data.json")

        if (!skullDataFile.exists()) {
            plugin.logger.severe("Skull data file not found at ${skullDataFile.path}. Ensure it exists and is properly formatted.")
            return emptyList()
        }

        val skullData: List<SkullSourceData> = try {
            val gson = Gson()
            skullDataFile.bufferedReader().use { reader ->
                gson.fromJson(reader, Array<SkullSourceData>::class.java).toList()
            }
        } catch (e: Exception) {
            plugin.logger.severe("Error reading skull_data.json: ${e.message}")
            return emptyList()
        }

        return skullData
    }

    // Seed the skulls for a team (add all entity types to track)
    fun seedTeam(teamId: String): Boolean {
        val connection = dbHandler.getConnection() ?: return false

        val skullData = getRawSkullData()

        val insertQuery = """
            INSERT INTO skulls (team_id, entity_type, earned, collected)
            VALUES (?, ?, false, false)
        """
        try {
            val statement = connection.prepareStatement(insertQuery)
            connection.autoCommit = false // Ensure it's done in a single transaction

            skullData.forEach { skull ->
                statement.setString(1, teamId)
                statement.setString(2, skull.entityType)
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

    fun markSkullEarned(teamId: String, player: Player, entityType: EntityType, earnedVariation: String? = null): Boolean {
        val connection = dbHandler.getConnection() ?: return false
        val updateQuery = """
        UPDATE skulls
        SET earned = true, earned_by = ?, earned_at = ?, earned_variation = ?
        WHERE team_id = ? AND entity_type = ? AND earned = false
    """
        try {
            val statement = connection.prepareStatement(updateQuery)
            statement.setString(1, player.uniqueId.toString()) // Set the player who earned the skull
            statement.setLong(2, System.currentTimeMillis()) // Set the time when the skull was earned
            statement.setString(3, earnedVariation) // Set the variation that was killed
            statement.setString(4, teamId) // Set the team ID
            statement.setString(5, entityType.name) // Set the entity type

            val rowsAffected = statement.executeUpdate()
            connection.commit()

            return rowsAffected > 0
        } catch (e: SQLException) {
            plugin.logger.severe("Error marking skull as earned: ${e.message}")
        }
        return false
    }

    // Mark a skull as collected
    fun markSkullCollected(teamId: String, entityType: String): Boolean {
        val connection = dbHandler.getConnection() ?: return false
        val updateQuery = """
            UPDATE skulls
            SET collected = true
            WHERE team_id = ? AND entity_type = ? AND collected = false
        """
        try {
            val statement = connection.prepareStatement(updateQuery)
            statement.setString(1, teamId)
            statement.setString(2, entityType)

            val rowsAffected = statement.executeUpdate()
            connection.commit()

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
    fun getSkullData(teamId: String, extendVariations: Boolean = false): List<SkullDBData> {
        val connection = dbHandler.getConnection() ?: return emptyList()
        val query = """
            SELECT id, team_id, entity_type, earned, earned_by, earned_at, collected, earned_variation
            FROM skulls
            WHERE team_id = ?
        """
        val skullDataList = mutableListOf<SkullDBData>()
        try {
            val statement = connection.prepareStatement(query)
            statement.setString(1, teamId)
            val resultSet = statement.executeQuery()

            while (resultSet.next()) {
                val entityType = resultSet.getString("entity_type")

                val skullData = SkullDBData(
                    id = resultSet.getInt("id"),
                    teamId = resultSet.getString("team_id"),
                    entityType = entityType,
                    earned = resultSet.getBoolean("earned"),
                    earnedBy = resultSet.getString("earned_by"),
                    earnedAt = resultSet.getLong("earned_at"),
                    collected = resultSet.getBoolean("collected"),
                    earnedVariation = resultSet.getString("earned_variation")
                )

                if (extendVariations) {
                    getRawSkullData().find { it.entityType == entityType }?.let {
                        if (it.variations.isEmpty()) {
                            skullDataList.add(skullData)
                        } else {
                            it.variations.forEach {
                                skullDataList.add(skullData.copy(earnedVariation = it.id))
                            }
                        }
                    }
                } else {
                    skullDataList.add(skullData)
                }
            }
        } catch (e: SQLException) {
            plugin.logger.severe("Error retrieving skull data: ${e.message}")
        }
        return skullDataList
    }

    fun getEarnedButNotCollectedSkulls(teamId: String): List<SkullDBData> {
        val connection = dbHandler.getConnection() ?: return emptyList()
        val query = """
        SELECT id, team_id, entity_type, earned, earned_by, earned_at, collected, earned_variation
        FROM skulls
        WHERE team_id = ? AND earned = true AND collected = false
    """
        val skullDataList = mutableListOf<SkullDBData>()
        try {
            val statement = connection.prepareStatement(query)
            statement.setString(1, teamId)
            val resultSet = statement.executeQuery()

            while (resultSet.next()) {
                val skullData = SkullDBData(
                    id = resultSet.getInt("id"),
                    teamId = resultSet.getString("team_id"),
                    entityType = resultSet.getString("entity_type"),
                    earned = resultSet.getBoolean("earned"),
                    earnedBy = resultSet.getString("earned_by"),
                    earnedAt = resultSet.getLong("earned_at"),
                    collected = resultSet.getBoolean("collected"),
                    earnedVariation = resultSet.getString("earned_variation")
                )
                skullDataList.add(skullData)
            }
        } catch (e: SQLException) {
            plugin.logger.severe("Error retrieving earned but not collected skulls: ${e.message}")
        }
        return skullDataList
    }

}