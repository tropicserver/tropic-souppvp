package gg.tropic.souppvp.command.admin

import gg.scala.commons.acf.CommandHelp
import gg.scala.commons.acf.annotation.*
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.issuer.ScalaPlayer
import gg.scala.lemon.player.LemonPlayer
import gg.tropic.souppvp.config.LocalZone
import gg.tropic.souppvp.config.config
import gg.tropic.souppvp.profile.coinIcon
import gg.tropic.souppvp.profile.profile
import net.evilblock.cubed.menu.menus.TextEditorMenu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.prompt.InputPrompt
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

    @Subcommand("player add-balance")
    @Description("Add balance to a player.")
    @CommandCompletion("@players")
    fun onAddBalance(player: ScalaPlayer, target: LemonPlayer, amount: Double)
    {
        target.bukkitPlayer!!.profile
            .apply {
                this.coins += amount
                player.sendMessage(
                    "${CC.SEC}Increased balance by ${CC.GOLD}$amount $coinIcon${CC.SEC}."
                )
            }
    }

    @Subcommand("spawn zone")
    fun onSpawnZone(player: ScalaPlayer) =
        with(config) {
            InputPrompt()
                .withText("Type when you get to the minimum")
                .acceptInput { _, _ ->
                    val minimum = player.bukkit().location

                    InputPrompt()
                        .withText("Type when you get to the maximum")
                        .acceptInput { _, _ ->
                            val maximum = player.bukkit().location
                            spawnZone = LocalZone(
                                zoneMin = minimum,
                                zoneMax = maximum
                            )
                            pushUpdates()

                            player.sendMessage("${CC.GREEN}Updated the spawn zone!")
                        }
                        .start(player.bukkit())
                }
                .start(player.bukkit())
        }

    @Subcommand("launchpad velocity")
    fun onLaunchpadVelocity(player: ScalaPlayer, velocity: Double) =
        with(config) {
            with(launchpad) {
                this.velocity = velocity
            }
            pushUpdates()

            player.sendMessage(
                "${CC.GREEN}Launchpad velocity: ${CC.WHITE}$velocity${CC.GREEN}."
            )
        }

    @Subcommand("launchpad y-multiplier")
    fun onLaunchpadYMultiplier(player: ScalaPlayer, yMultiplier: Double) =
        with(config) {
            with(launchpad) {
                this.yMultiplier = yMultiplier
            }
            pushUpdates()

            player.sendMessage(
                "${CC.GREEN}Launchpad Y multiplier: ${CC.WHITE}$yMultiplier${CC.GREEN}."
            )
        }

    @Subcommand("spawn set")
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
