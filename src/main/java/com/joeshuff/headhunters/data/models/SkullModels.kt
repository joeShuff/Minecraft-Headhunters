package com.joeshuff.headhunters.data.models

import com.google.gson.annotations.SerializedName

data class SkullDBData(
        val id: Int,
        val teamId: String,
        val entityType: String, // String representation of the EntityType
        val earned: Boolean,    // Whether the skull has been earned
        val earnedBy: String?,  // UUID of the player who earned the skull
        val earnedAt: Long?,    // Timestamp of when it was earned
        val collected: Boolean,  // Whether the skull has been collected
        val earnedVariation: String? //Info about the entity that got killed
)

data class SkullSourceData(
        val entityType: String,
        val skullTexture: String,
        val variations: List<SkullSourceVariation>,
        val category: String?
)

data class SkullSourceVariation(
        @SerializedName("name") val variationName: String,
        @SerializedName("skullTexture") val variationTexture: String,
        @SerializedName("id") val id: String
)