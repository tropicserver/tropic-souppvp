package gg.tropic.souppvp.scoreboard

import gg.scala.flavor.service.Service
import gg.tropic.souppvp.listener.ListenerService
import gg.tropic.souppvp.profile.coinIcon
import gg.tropic.souppvp.profile.extract
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
        lines += "${CC.WHITE}Kills: ${CC.GOLD}${profile.kills}"
        lines += "${CC.WHITE}Deaths: ${CC.GOLD}${profile.deaths}"
        lines += "${CC.WHITE}Coins: ${CC.GOLD}${Numbers.format(profile.coins)} $coinIcon"

        profile.bounty
            ?.apply {
                lines += ""
                lines += "${CC.GREEN}Bounty:"
                lines += "${CC.WHITE}Contributors: ${CC.GREEN}${contributors.size}"
                lines += "${CC.WHITE}Total: ${CC.GOLD}$amount $coinIcon"
            }

        player
            .extract<ListenerService.CombatTag>("combat")
            ?.apply {
                lines += ""
                lines += "${CC.RED}Combat Tag:"
                lines += "${CC.WHITE}Ends: ${CC.RED}${
                    "%.2f".format((expectedEnd - System.currentTimeMillis()) / 1000.0f)
                }s"
            }

        lines += ""
        lines += "${CC.GRAY}tropic.gg" + "          "  + CC.GRAY + "      "  + CC.GRAY
    }

    override fun getTitle(player: Player) = "${CC.B_PRI}SoupPvP"
}
