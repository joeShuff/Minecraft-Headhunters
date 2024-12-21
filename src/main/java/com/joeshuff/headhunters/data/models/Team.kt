package com.joeshuff.headhunters.data.models

import org.bukkit.Location


data class Team(
        val id: String,
        val teamName: String,
        val shrineLocation: Location? // nullable in case there is no shrine set
)