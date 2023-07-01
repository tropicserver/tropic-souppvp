package gg.tropic.souppvp.config

import gg.tropic.souppvp.kit.Kit
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.cuboid.Cuboid
import org.bukkit.Bukkit
import org.bukkit.Location

/**
 * @author GrowlyX
 * @since 6/12/2023
 */
class GameConfig(
    val kits: MutableMap<String, Kit> = mutableMapOf(
        "default" to Kit(id = "default")
    ),
    var launchpad: Launchpads = Launchpads(),
    var spawnZone: List<LocalZone> = mutableListOf(
        LocalZone(
            zoneMin = Location(
                Bukkit.getWorlds()[0],
                -27.5, 64.0, -21.5
            ),
            zoneMax = Location(
                Bukkit.getWorlds()[0],
                -2.5, 90.0, -45.5
            )
        )
    ),
    var defaultKit: String? = null,
    var spawn: Location = Location(
        Bukkit.getWorlds()[0],
        -15.5, 78.0, -33.5
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

data class LocalZone(
    val zoneMin: Location,
    val zoneMax: Location
)
{
    @Transient
    private var backingCuboid: Cuboid? = null

    val cuboid: Cuboid
        get() = if (backingCuboid == null)
        {
            backingCuboid = Cuboid(zoneMin, zoneMax)
            backingCuboid!!
        } else
            backingCuboid!!
}
