package gg.tropic.souppvp.command

import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.issuer.ScalaPlayer
import gg.tropic.souppvp.leaderboard.LeaderboardService
import gg.tropic.souppvp.leaderboard.LeaderboardType
import gg.tropic.souppvp.profile.coinIcon
import gg.tropic.souppvp.profile.profile
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.math.Numbers

/**
 * @author GrowlyX
 * @since 6/13/2023
 */
@AutoRegister
object BalanceCommand : ScalaCommand()
{
    @CommandAlias("bal|balance")
    fun onBalance(player: ScalaPlayer) =
        with(player.profile) {
            player.sendMessage("${CC.SEC}Balance: ${CC.PRI}${
                Numbers.format(coins)
            } $coinIcon")
        }

    @CommandAlias("baltop|balancetop")
    fun onBalanceTop(player: ScalaPlayer)
    {
        player.sendMessage(
            "${CC.B_GREEN}Top 10 balances:",
            *LeaderboardService
                .mapToFormattedLeaderboards(LeaderboardType.Balances)
                .toTypedArray()
        )
    }
}
