package com.joeshuff.headhunters.data.models

data class SkullTrackingData(
        val id: Int,
        val teamId: String,
        val entityType: String, // String representation of the EntityType
        val earned: Boolean,    // Whether the skull has been earned
        val earnedBy: String?,  // UUID of the player who earned the skull
        val earnedAt: Long?,    // Timestamp of when it was earned
        val collected: Boolean  // Whether the skull has been collected
)

data class SkullSourceData(
        val entityType: String,
        val skullTexture: String,
        val variations: List<SkullSourceVariation>
)

data class SkullSourceVariation(
        val variationName: String,
        val variationTexture: String,
        val variationNbtData: String
)