package gg.tropic.souppvp.command

import gg.scala.commons.acf.ConditionFailedException
import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.acf.annotation.CommandPermission
import gg.scala.commons.acf.annotation.Flags
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.lemon.filter.ChatMessageFilterHandler
import gg.tropic.souppvp.listener.ListenerService
import gg.tropic.souppvp.profile.PlayerState
import gg.tropic.souppvp.profile.extract
import gg.tropic.souppvp.profile.local.ItemRenameCooldown
import gg.tropic.souppvp.profile.profile
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue
import java.util.concurrent.TimeUnit

/**
 * @author GrowlyX
 * @since 6/12/2023
 */
@AutoRegister
object RenameCommand : ScalaCommand()
{
    @CommandAlias("rename-sword")
    @CommandPermission("souppvp.donator")
    fun onRepair(
        @Flags("itemheld") player: Player,
        display: String
    )
    {
        if (player.profile.state != PlayerState.Warzone)
        {
            throw ConditionFailedException(
                "You must be fighting to use this command!"
            )
        }

        if (display.length > 20)
        {
            throw ConditionFailedException(
                "Your dick is too long!"
            )
        }

        if (ChatMessageFilterHandler.handleMessageFilter(player, display, false))
        {
            throw ConditionFailedException(
                "You cannot have profanity in your sword name!"
            )
        }

        player
            .extract<ItemRenameCooldown>("rename")
            ?.apply {
                throw ConditionFailedException(
                    "You are on cooldown! Try again in ${CC.BOLD}${expectedEndFormat}s${CC.RED}."
                )
            }

        val itemInHand = ItemBuilder
            .copyOf(player.itemInHand)
            .name(display)
            .build()

        player.itemInHand = itemInHand
        player.updateInventory()

        player.sendMessage(
            "${CC.GREEN}You've renamed your sword to: ${CC.WHITE}$display${CC.GREEN}!"
        )

        player.setMetadata(
            "rename",
            FixedMetadataValue(
                ListenerService.plugin,
                ItemRenameCooldown(
                    expectedEnd = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5L)
                )
            )
        )

        player.playSound(
            player.location,
            Sound.ORB_PICKUP,
            1.0f, 1.0f
        )
    }
}
