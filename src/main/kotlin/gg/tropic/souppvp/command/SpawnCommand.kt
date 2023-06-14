package gg.tropic.souppvp.command

import gg.scala.commons.acf.ConditionFailedException
import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.issuer.ScalaPlayer
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import gg.tropic.souppvp.TropicSoupPlugin
import gg.tropic.souppvp.config.config
import gg.tropic.souppvp.profile.PlayerState
import gg.tropic.souppvp.profile.extract
import gg.tropic.souppvp.profile.local.CombatTag
import gg.tropic.souppvp.profile.profile
import me.lucko.helper.Events
import me.lucko.helper.Schedulers
import me.lucko.helper.event.filter.EventFilters
import me.lucko.helper.terminable.composite.CompositeTerminable
import net.evilblock.cubed.util.CC
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.metadata.FixedMetadataValue

/**
 * @author GrowlyX
 * @since 6/12/2023
 */
@AutoRegister
object SpawnCommand : ScalaCommand()
{
    @Inject
    lateinit var plugin: TropicSoupPlugin

    @Configure
    fun configure()
    {
        Events
            .subscribe(PlayerQuitEvent::class.java)
            .handler {
                it.player
                    .extract<CompositeTerminable>("spawn")
                    ?.apply {
                        closeAndReportException()
                    }
            }
            .bindWith(plugin)
    }

    @CommandAlias("spawn")
    fun onSpawn(player: ScalaPlayer)
    {
        if (player.profile.state == PlayerState.Spawn)
        {
            throw ConditionFailedException(
                "You are already at spawn!"
            )
        }

        if (player.bukkit().hasMetadata("spawn"))
        {
            throw ConditionFailedException(
                "You're already returning to spawn!"
            )
        }

        player.bukkit()
            .extract<CombatTag>("combat")
            ?.apply {
                throw ConditionFailedException(
                    "You are combat-tagged! Try again in $expectedEndFormat."
                )
            }

        val composite = CompositeTerminable.create()
        composite.with {
            player.bukkit()
                .removeMetadata(
                    "spawn", plugin
                )
        }

        player.bukkit().setMetadata(
            "spawn",
            FixedMetadataValue(plugin, composite)
        )

        var count = 3

        Schedulers
            .sync()
            .runRepeating({ _ ->
                if (count <= 0)
                {
                    player.bukkit().teleport(config.spawn)

                    player.sendMessage("${CC.B_GREEN}You've been teleported to spawn!")
                    composite.closeAndReportException()
                    return@runRepeating
                }

                player.sendMessage("${CC.GRAY}Teleporting in ${count--}...")
            }, 0L, 20L)
            .bindWith(composite)

        Events
            .subscribe(PlayerMoveEvent::class.java)
            .filter {
                EventFilters
                    .ignoreSameBlockAndY<PlayerMoveEvent>()
                    .test(it)
            }
            .handler {
                composite.closeAndReportException()
                player.sendMessage("${CC.RED}You are no longer returning to spawn!")
            }
            .bindWith(composite)
    }
}
