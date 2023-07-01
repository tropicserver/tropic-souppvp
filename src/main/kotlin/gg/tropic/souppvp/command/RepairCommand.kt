package gg.tropic.souppvp.command

import gg.scala.commons.acf.ConditionFailedException
import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.issuer.ScalaPlayer
import gg.scala.flavor.inject.Inject
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
object RepairCommand : ScalaCommand()
{
    @CommandAlias("repair")
    fun onRepair(player: ScalaPlayer)
    {
        if (player.profile.state != PlayerState.Warzone)
        {
            throw ConditionFailedException(
                "You must be fighting to use this command!"
            )
        }

        val price = (350 + (++player.profile.repairs * 150))
            .coerceAtMost(1000)

        if (player.profile.coins < price)
        {
            throw ConditionFailedException(
                "You must have at least ${CC.GOLD}$price $coinIcon${CC.RED} to repair your items."
            )
        }

        // TODO: remove maybe?
        player.bukkit()
            .extract<CombatTag>("combat")
            ?.apply {
                throw ConditionFailedException(
                    "You are combat-tagged! Try again in ${CC.BOLD}${expectedEndFormat}s${CC.RED}."
                )
            }

        player.profile.coins -= price
        player.profile.save()k

        player.bukkit().inventory
            .apply {
                armorContents
                    .filterNotNull()
                    .forEach {
                        it.durability = 0
                    }

                contents
                    .filterNotNull()
                    .filter {
                        !ItemUtils.itemTagHasKey(it, "ability")
                    }
                    .forEach {
                        it.durability = 0
                    }
            }

        player.sendMessage(
            "${CC.GREEN}You've repaired your items!"
        )

        player.bukkit().playSound(
            player.bukkit().location,
            Sound.ORB_PICKUP,
            1.0f, 1.0f
        )
    }
}
