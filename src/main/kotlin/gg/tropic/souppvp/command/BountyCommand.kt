package gg.tropic.souppvp.command

import gg.scala.commons.acf.CommandHelp
import gg.scala.commons.acf.ConditionFailedException
import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.acf.annotation.CommandCompletion
import gg.scala.commons.acf.annotation.Default
import gg.scala.commons.acf.annotation.Description
import gg.scala.commons.acf.annotation.HelpCommand
import gg.scala.commons.acf.annotation.Subcommand
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.issuer.ScalaPlayer
import gg.scala.lemon.player.LemonPlayer
import gg.scala.lemon.util.QuickAccess.username
import gg.tropic.souppvp.profile.bounty.Bounty
import gg.tropic.souppvp.profile.coinIcon
import gg.tropic.souppvp.profile.profile
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.math.Numbers
import org.bukkit.Bukkit

/**
 * @author GrowlyX
 * @since 6/13/2023
 */
@AutoRegister
@CommandAlias("bounty")
object BountyCommand : ScalaCommand()
{
    @HelpCommand
    fun onHelp(help: CommandHelp)
    {
        help.showHelp()
    }

    @Default
    @CommandCompletion("@players")
    fun onView(sender: ScalaPlayer, player: LemonPlayer)
    {
        val profile = player.bukkitPlayer!!.profile

        if (profile.bounty == null)
        {
            throw ConditionFailedException(
                "${CC.YELLOW}${player.bukkitPlayer!!.name}${CC.RED} does not have a bounty!"
            )
        }

        sender.sendMessage(
            "${CC.GREEN}${player.bukkitPlayer!!.name}'s bounty:",
            "${CC.SEC}Amount: ${CC.GOLD}${
                Numbers.format(profile.bounty!!.amount)
            } $coinIcon",
            "${CC.SEC}Contributors:",
            *profile.bounty!!.contributors
                .map { "${CC.GRAY} - ${it.username()}" }
                .toTypedArray()
        )
    }

    @Subcommand("set")
    @CommandCompletion("@players")
    @Description("Place a bounty on a player.")
    fun onPlace(sender: ScalaPlayer, player: LemonPlayer, amount: Double)
    {
        val profile = sender.profile
        val targetProfile = player.bukkitPlayer!!.profile

        if (profile.identifier == targetProfile.identifier)
        {
            throw ConditionFailedException(
                "You cannot place a bounty on yourself!"
            )
        }

        if (amount < 0)
        {
            throw ConditionFailedException(
                "Your bounty must be a positive number!"
            )
        }

        if (profile.coins < amount)
        {
            throw ConditionFailedException(
                "You don't have enough funds to place a bounty of $amount!"
            )
        }

        if (targetProfile.bounty == null)
        {
            val bounty = Bounty(
                amount = amount,
                contributors = mutableSetOf(sender.uniqueId)
            )

            targetProfile.bounty = bounty
            targetProfile.save()

            sender.sendMessage("${CC.SEC}You placed a bounty of ${CC.GOLD}${
                Numbers.format(amount)
            } $coinIcon${CC.SEC} on ${CC.GREEN}${
                player.bukkitPlayer!!.name
            }${CC.SEC}!")

            Bukkit.broadcastMessage(
                "${CC.SEC}A bounty of ${CC.GOLD}${
                    Numbers.format(amount)
                } $coinIcon${CC.SEC} has been placed on ${CC.GREEN}${
                    player.bukkitPlayer!!.name
                }${CC.SEC}!"
            )
        } else
        {
            targetProfile.bounty!!.apply {
                this.amount += amount
                this.contributors.add(sender.uniqueId)
            }
            targetProfile.save()

            sender.sendMessage("${CC.SEC}You upped the bounty for ${CC.PRI}${
                player.bukkitPlayer!!.name
            }${CC.SEC} to ${CC.GOLD}${
                Numbers.format(targetProfile.bounty!!.amount)
            } $coinIcon ${CC.GRAY}(+$amount)${CC.SEC}!")

            Bukkit.broadcastMessage(
                "${CC.GREEN}${sender.bukkit().name}${CC.SEC} upped the bounty for ${CC.PRI}${
                    player.bukkitPlayer!!.name
                }${CC.SEC} to ${CC.GOLD}${
                    Numbers.format(targetProfile.bounty!!.amount)
                } $coinIcon ${CC.GRAY}(+$amount)${CC.SEC}!"
            )
        }
    }
}
