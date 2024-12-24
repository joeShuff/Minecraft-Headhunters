package com.joeshuff.headhunters.util

import org.bukkit.entity.EntityType

fun EntityType.toDisplayString() = toString().lowercase().replace('_', ' ').capitalize()