package gg.tropic.souppvp.command

import gg.scala.commons.acf.ConditionFailedException
import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.acf.annotation.CommandCompletion
import gg.scala.commons.acf.annotation.Optional
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.issuer.ScalaPlayer
import gg.scala.lemon.player.wrapper.AsyncLemonPlayer
import gg.scala.store.controller.DataStoreObjectControllerCache
import gg.scala.store.storage.type.DataStoreStorageType
import gg.tropic.souppvp.profile.SoupProfile
import gg.tropic.souppvp.profile.coinIcon
import gg.tropic.souppvp.profile.profile
import net.evilblock.cubed.util.CC
import java.util.concurrent.CompletableFuture

/**
 * @author GrowlyX
 * @since 6/13/2023
 */
@AutoRegister
object StatisticsCommand : ScalaCommand()
{
    @CommandCompletion("@players")
    @CommandAlias("stats|statistics")
    fun onBalance(
        player: ScalaPlayer,
        @Optional target: AsyncLemonPlayer?
    ): CompletableFuture<Void>
    {
        if (target == null)
        {
            player.profile.sendStatsForProfile(player)
            return CompletableFuture.completedFuture(null)
        }

        return target
            .validatePlayers(player.bukkit(), false) {
                val profile = DataStoreObjectControllerCache
                    .findNotNull<SoupProfile>()
                    .load(it.uniqueId, DataStoreStorageType.MONGO)
                    .join()
                    ?: throw ConditionFailedException(
                        "${CC.YELLOW}${it.name}${CC.RED} has not yet joined our Soup server."
                    )

                player.sendMessage("${CC.GREEN}Stats for ${it.name}:")
                profile.sendStatsForProfile(player)
            }
    }

    private fun SoupProfile.sendStatsForProfile(player: ScalaPlayer) =
        player.sendMessage(
            "${CC.SEC}Kills: ${CC.GREEN}$kills",
            "${CC.SEC}Deaths: ${CC.RED}$deaths",
            "${CC.SEC}Consumed soups: ${CC.AQUA}$soupsConsumed",
            "${CC.SEC}Balance: ${CC.GOLD}$coins $coinIcon"
        )
}
