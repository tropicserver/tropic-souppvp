package gg.tropic.souppvp.scoreboard

import gg.scala.flavor.service.Service
import gg.scala.lemon.LemonConstants
import gg.tropic.souppvp.listener.ListenerService
import gg.tropic.souppvp.profile.coinIcon
import gg.tropic.souppvp.profile.extract
import gg.tropic.souppvp.profile.local.CombatTag
import gg.tropic.souppvp.profile.profile
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
    override fun getLines(
        lines: LinkedList<String>,
        player: Player
    )
    {
        val profile = player.profile
        lines += ""
        lines += "${CC.WHITE}Kills: ${CC.PRI}${profile.kills}"
        lines += "${CC.WHITE}Deaths: ${CC.PRI}${profile.deaths}"
        lines += "${CC.WHITE}KDR: ${CC.PRI}${profile.kdrFormat}"
        lines += "${CC.WHITE}Kill streak: ${CC.PRI}${profile.killStreak} ${CC.GRAY}(${profile.maxKillStreak})"
        lines += ""
        lines += "${CC.WHITE}Coins: ${CC.GOLD}${Numbers.format(profile.coins)} $coinIcon"

        profile.bounty
            ?.apply {
                lines += ""
                lines += "${CC.GREEN}Bounty:"
                lines += "${CC.WHITE}Contributors: ${CC.GREEN}${contributors.size}"
                lines += "${CC.WHITE}Total: ${CC.GOLD}$amount $coinIcon"
            }

        player
            .extract<CombatTag>("combat")
            ?.apply {
                lines += ""
                lines += "${CC.RED}Combat Tag: ${CC.WHITE}${expectedEndFormat}s"
            }

        lines += ""
        lines += "${CC.GRAY}${LemonConstants.WEB_LINK}" + "          "  + CC.GRAY + "      "  + CC.GRAY
    }

    override fun getTitle(player: Player) = "${CC.B_PRI}SoupPvP"
}
