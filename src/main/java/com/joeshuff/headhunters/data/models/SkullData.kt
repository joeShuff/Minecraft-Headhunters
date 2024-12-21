package com.joeshuff.headhunters.data.models

data class SkullData(
        val id: Int,
        val teamId: String,
        val entityType: String, // String representation of the EntityType
        val earned: Boolean,    // Whether the skull has been earned
        val earnedBy: String?,  // UUID of the player who earned the skull
        val earnedAt: Long?,    // Timestamp of when it was earned
        val collected: Boolean  // Whether the skull has been collected
)