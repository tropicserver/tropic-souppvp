package gg.tropic.souppvp.command

import gg.scala.commons.acf.ConditionFailedException
import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.issuer.ScalaPlayer
import gg.scala.flavor.inject.Inject
import gg.scala.lemon.player.LemonPlayer
import gg.tropic.souppvp.TropicSoupPlugin
import gg.tropic.souppvp.profile.PlayerState
import gg.tropic.souppvp.profile.coinIcon
import gg.tropic.souppvp.profile.extract
import gg.tropic.souppvp.profile.local.CombatTag
import gg.tropic.souppvp.profile.profile
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemUtils
import org.bukkit.Sound

/**
 * @author GrowlyX
 * @since 6/12/2023
 */
@AutoRegister
object PayCommand : ScalaCommand()
{
    @CommandAlias("pay")
    fun onRepair(player: ScalaPlayer, target: LemonPlayer, amount: Int)
    {
        if (amount <= 0)
        {
            throw ConditionFailedException(
                "You cannot pay a negative amount!"
            )
        }

        if (player.profile.coins < amount)
        {
            throw ConditionFailedException(
                "You do not have ${CC.GOLD}$amount $coinIcon${CC.RED}!"
            )
        }

        target.bukkitPlayer!!.profile.coins += amount
        target.bukkitPlayer!!.profile.save()

        target.bukkitPlayer!!.sendMessage(
            "${CC.GREEN}You received ${CC.GOLD}$amount $coinIcon${CC.GREEN} from ${CC.WHITE}${player.bukkit().name}${CC.GREEN}!"
        )

        target.bukkitPlayer!!.playSound(
            player.bukkit().location,
            Sound.ORB_PICKUP,
            1.0f, 1.0f
        )

        player.profile.coins -= amount
        player.profile.save()

        player.sendMessage(
            "${CC.GREEN}You paid ${CC.WHITE}${target.name} ${CC.GOLD}$amount $coinIcon${CC.GREEN}!"
        )

        player.bukkit().playSound(
            player.bukkit().location,
            Sound.ORB_PICKUP,
            1.0f, 1.0f
        )
    }
}
