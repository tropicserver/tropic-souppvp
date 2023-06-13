package gg.tropic.souppvp.command.admin

import gg.scala.commons.acf.CommandHelp
import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.acf.annotation.CommandPermission
import gg.scala.commons.acf.annotation.Default
import gg.scala.commons.acf.annotation.Description
import gg.scala.commons.acf.annotation.HelpCommand
import gg.scala.commons.acf.annotation.Subcommand
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.issuer.ScalaPlayer
import gg.tropic.souppvp.config.config
import net.evilblock.cubed.menu.menus.TextEditorMenu
import net.evilblock.cubed.util.CC
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 6/12/2023
 */
@AutoRegister
@CommandAlias("soupadmin")
@CommandPermission("soup.command.admin")
object AdminCommand : ScalaCommand()
{
    @Default
    @HelpCommand
    fun onHelp(help: CommandHelp)
    {
        help.showHelp()
    }

    @Subcommand("set-spawn")
    @Description("Set the server's spawn location.")
    fun onSetSpawn(player: ScalaPlayer) =
        with(config) {
            spawn = player.bukkit().location
            pushUpdates()

            player.sendMessage(
                "${CC.GREEN}You set the server's spawn location."
            )
        }

    @Subcommand("edit-login-message")
    @Description("Set the server's login message.")
    fun onEditLoginMessage(player: ScalaPlayer) =
        with(config) {
            player.sendMessage("${CC.GREEN}Editing login message...")

            object : TextEditorMenu(loginMessage)
            {
                override fun getPrePaginatedTitle(player: Player) =
                    "Edit login message"

                override fun onClose(player: Player)
                {
                }

                override fun onSave(player: Player, list: List<String>)
                {
                    loginMessage = list.toMutableList()
                    pushUpdates()
                }
            }.openMenu(player.bukkit())
        }
}
