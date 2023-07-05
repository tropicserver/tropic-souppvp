package gg.tropic.souppvp.command

import gg.scala.commons.acf.ConditionFailedException
import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.acf.annotation.CommandPermission
import gg.scala.commons.acf.annotation.Conditions
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.issuer.ScalaPlayer
import gg.scala.flavor.inject.Inject
import gg.scala.lemon.filter.ChatMessageFilterHandler
import gg.tropic.souppvp.TropicSoupPlugin
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.Sound

/**
 * @author GrowlyX
 * @since 6/12/2023
 */
@AutoRegister
object RenameCommand : ScalaCommand()
{
    @Inject
    lateinit var plugin: TropicSoupPlugin

    @CommandAlias("rename-sword")
    @CommandPermission("souppvp.donator")
    fun onRepair(
        @Conditions("cooldown:duration=5,unit=MINUTES") player: ScalaPlayer,
        display: String
    )
    {
        if (!player.bukkit().hasMetadata("kit-applied"))
        {
            throw ConditionFailedException(
                "You must have a kit equipped to use this command!"
            )
        }

        if (display.length > 20)
        {
            throw ConditionFailedException(
                "Your display name is too long! Please enter a name less than 20 characters."
            )
        }

        if (ChatMessageFilterHandler.handleMessageFilter(player.bukkit(), display, false))
        {
            throw ConditionFailedException(
                "You cannot have profanity in your sword name!"
            )
        }

        val itemInHand = ItemBuilder
            .copyOf(player.bukkit().itemInHand)
            .name(display)
            .build()

        player.bukkit().itemInHand = itemInHand
        player.bukkit().updateInventory()

        player.sendMessage(
            "${CC.GREEN}You've renamed your sword to: ${CC.WHITE}$display${CC.GREEN}!"
        )

        player.bukkit().playSound(
            player.bukkit().location,
            Sound.ORB_PICKUP,
            1.0f, 1.0f
        )
    }
}
