package util

import org.bukkit.Location

fun Location.toDisplay(): String {
    return "x: ${this.blockX}, y: ${this.blockY}, z: ${this.blockZ} in ${this.world.name}"
}