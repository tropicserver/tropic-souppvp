package gg.tropic.souppvp.command

import gg.scala.commons.acf.ConditionFailedException
import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.issuer.ScalaPlayer
import gg.tropic.souppvp.kit.KitMenu
import gg.tropic.souppvp.profile.PlayerState
import gg.tropic.souppvp.profile.profile

/**
 * @author GrowlyX
 * @since 6/13/2023
 */
@AutoRegister
object KitCommand : ScalaCommand()
{
    @CommandAlias("kit|kits")
    fun onKit(player: ScalaPlayer)
    {
        if (player.profile.state != PlayerState.Spawn)
        {
            throw ConditionFailedException(
                "You can only run this command at spawn!"
            )
        }

        KitMenu().openMenu(player.bukkit())
    }
}
