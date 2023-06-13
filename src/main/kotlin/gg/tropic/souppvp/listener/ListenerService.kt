package gg.tropic.souppvp.listener

import gg.scala.commons.annotations.Listeners
import gg.scala.flavor.inject.Inject
import gg.tropic.souppvp.TropicSoupPlugin
import gg.tropic.souppvp.config.config
import gg.tropic.souppvp.profile.PlayerState
import gg.tropic.souppvp.profile.event.PlayerStateChangeEvent
import gg.tropic.souppvp.profile.profile
import gg.tropic.souppvp.profile.refresh
import me.lucko.helper.Events
import me.lucko.helper.Schedulers
import me.lucko.helper.terminable.composite.CompositeTerminable
import net.evilblock.cubed.util.CC
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.metadata.FixedMetadataValue
import java.util.concurrent.TimeUnit

/**
 * @author GrowlyX
 * @since 6/13/2023
 */
@Listeners
object ListenerService : Listener
{
    @Inject
    lateinit var plugin: TropicSoupPlugin

    @EventHandler
    fun PlayerJoinEvent.on()
    {
        config.loginMessage
            .forEach(player::sendMessage)

        player.profile.state = PlayerState.Spawn
    }

    @EventHandler
    fun EntityDamageByEntityEvent.on()
    {
        if (damager !is Player)
        {
            return
        }

        val player = entity
        if (player is Player)
        {
            startCombatTag(player)
            startCombatTag(damager as Player)
        }
    }

    data class CombatTag(
        val terminable: CompositeTerminable,
        val expectedEnd: Long
    )

    private fun startCombatTag(player: Player)
    {
        if (player.hasMetadata("combat"))
        {
            return
        }

        val terminable = CompositeTerminable.create()
        terminable.with {
            player
                .removeMetadata(
                    "combat", plugin
                )
        }

        player.setMetadata(
            "combat",
            FixedMetadataValue(plugin, CombatTag(
                terminable = terminable,
                expectedEnd = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(15L)
            ))
        )
        player.sendMessage("${CC.RED}You have been combat-tagged!")

        Events
            .subscribe(PlayerQuitEvent::class.java)
            .handler {
                // TODO: logged out while in combat?
                terminable.closeAndReportException()
            }
            .bindWith(terminable)

        Schedulers
            .async()
            .runLater({
                player.sendMessage("${CC.GRAY}You are no longer in combat!")
            }, 15L, TimeUnit.SECONDS)
            .bindWith(terminable)
    }

    @EventHandler
    fun PlayerDeathEvent.on()
    {
        deathMessage =
            "${CC.RED}${entity.name} was killed by ${entity.killer?.name}!"

        entity.profile.apply {
            deaths += 1
            save()

            state = PlayerState.Spawn
        }

        entity.killer?.apply {
            profile.kills += 1
            profile.save()
        }
    }

    @EventHandler
    fun PlayerStateChangeEvent.toSpawn()
    {
        if (to != PlayerState.Spawn)
        {
            return
        }

        profile.player().refresh(GameMode.ADVENTURE)
        profile.player().teleport(config.spawn)
    }
}
