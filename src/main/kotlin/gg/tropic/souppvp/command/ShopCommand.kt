package gg.tropic.souppvp.command

import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.issuer.ScalaPlayer
import gg.tropic.souppvp.shop.ShopMenu

/**
 * @author GrowlyX
 * @since 7/1/2023
 */
@AutoRegister
object ShopCommand : ScalaCommand()
{
    @CommandAlias("shop")
    fun onQnA(player: ScalaPlayer)
    {
        ShopMenu().openMenu(player.bukkit())
    }
}
