package gg.tropic.souppvp.command

import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.issuer.ScalaPlayer
import gg.tropic.souppvp.qna.QnAMenu

/**
 * @author GrowlyX
 * @since 7/1/2023
 */
@AutoRegister
object QnACommand : ScalaCommand()
{
    @CommandAlias("qna")
    fun onQnA(player: ScalaPlayer)
    {
        QnAMenu.qna(player.bukkit())
    }
}
