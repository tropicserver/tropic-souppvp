package gg.tropic.souppvp.scoreboard

import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.lemon.LemonConstants
import gg.scala.lemon.util.QuickAccess.username
import gg.tropic.souppvp.leaderboard.LeaderboardService
import gg.tropic.souppvp.leaderboard.LeaderboardType
import gg.tropic.souppvp.profile.coinIcon
import gg.tropic.souppvp.profile.extract
import gg.tropic.souppvp.profile.local.CombatTag
import gg.tropic.souppvp.profile.profile
import me.lucko.helper.Schedulers
import net.evilblock.cubed.scoreboard.ScoreboardAdapter
import net.evilblock.cubed.scoreboard.ScoreboardAdapterRegister
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.math.Numbers
import org.bukkit.entity.Player
import java.util.*

/**
 * @author GrowlyX
 * @since 6/12/2023
 */
@Service
@ScoreboardAdapterRegister
object SoupScoreboardAdapter : ScoreboardAdapter()
{
    private var state = false

    @Configure
    fun configure()
    {
        Schedulers
            .async()
            .runRepeating({ _ ->
                state = !state
            }, 0L, 5 * 20L)
    }

    override fun getLines(
        lines: LinkedList<String>,
        player: Player
    )
    {
        val profile = player.profile

        if (state)
        {
            lines += ""
            lines += "${CC.WHITE}Kills: ${CC.PRI}${profile.kills}"
            lines += "${CC.WHITE}Deaths: ${CC.PRI}${profile.deaths}"
            lines += "${CC.WHITE}KDR: ${CC.PRI}${profile.kdrFormat}"
            lines += "${CC.WHITE}Kill streak: ${CC.PRI}${profile.killStreak} ${CC.GRAY}(${profile.maxKillStreak})"

            profile.bounty
                ?.apply {
                    lines += ""
                    lines += "${CC.GREEN}Bounty:"
                    lines += "${CC.WHITE}Contributors: ${CC.GREEN}${contributors.size}"
                    lines += "${CC.WHITE}Total: ${CC.GOLD}$amount $coinIcon"
                }
                ?: run {
                    lines += ""
                    lines += "${CC.WHITE}Coins: ${CC.GOLD}${Numbers.format(profile.coins)} $coinIcon"
                    lines += "${CC.WHITE}XP: ${CC.GREEN}${Numbers.format(profile.experience)}"
                }
        } else
        {
            lines += "${CC.D_AQUA}Top 5 kills:"

            LeaderboardService
                .resultsFor(LeaderboardType.Kills)
                .take(5)
                .forEach {
                    lines += " ${CC.WHITE}${it._id.username()}: ${CC.AQUA}${it.value}"
                }
        }

        player
            .extract<CombatTag>("combat")
            ?.apply {
                lines += ""
                lines += "${CC.RED}Combat Tag: ${CC.WHITE}${expectedEndFormatSpecific}s"
            }

        lines += ""
        lines += "${CC.GRAY}${LemonConstants.WEB_LINK}" + "          "  + CC.GRAY + "      "  + CC.GRAY
    }

    override fun getTitle(player: Player) = "${CC.B_PRI}SoupPvP"
}
