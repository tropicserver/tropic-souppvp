package gg.tropic.souppvp.config

import gg.tropic.souppvp.kit.Kit
import net.evilblock.cubed.util.CC
import org.bukkit.Bukkit
import org.bukkit.Location

/**
 * @author GrowlyX
 * @since 6/12/2023
 */
class GameConfig(
    val kits: Map<String, Kit> = mutableMapOf(
        "default" to Kit(id = "default")
    ),
    var launchpad: Launchpads = Launchpads(),
    var spawn: Location = Location(
        Bukkit.getWorlds()[0],
        0.0, 70.0, 0.0
    ),
    var loginMessage: MutableList<String> = mutableListOf(
        "${CC.GRAY}${CC.STRIKE_THROUGH}----------------------------------------",
        "${CC.GOLD}Welcome to soup",
        "${CC.WHITE}hope you have fun bestie",
        "${CC.WHITE}I Luv my Gf",
        "${CC.GRAY}${CC.STRIKE_THROUGH}----------------------------------------"
    )
)
{
    fun pushUpdates() = GameConfigService.sync(this)
}

data class Launchpads(
    var velocity: Double = 3.5,
    var yMultiplier: Double = 1.05
)
