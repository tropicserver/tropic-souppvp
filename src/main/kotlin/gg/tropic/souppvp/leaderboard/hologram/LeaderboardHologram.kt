package gg.tropic.souppvp.leaderboard.hologram

import gg.tropic.souppvp.leaderboard.LeaderboardService
import gg.tropic.souppvp.leaderboard.LeaderboardType
import net.evilblock.cubed.entity.EntityHandler
import net.evilblock.cubed.entity.hologram.updating.FormatUpdatingHologramEntity
import net.evilblock.cubed.util.CC
import org.bukkit.Location

/**
 * @author GrowlyX
 * @since 6/29/2023
 */
class LeaderboardHologram(
    val type: LeaderboardType,
    location: Location
) : FormatUpdatingHologramEntity(
    "meow", location
)
{
    override fun getTickInterval() = 60 * 20L

    fun configure()
    {
        initializeData()
        EntityHandler.trackEntity(this)
    }

    override fun getNewLines() = listOf(
        "${CC.B_GREEN}Top 10 ${type.display}:",
        *LeaderboardService
            .mapToFormattedLeaderboards(type)
            .toTypedArray()
    )
}
