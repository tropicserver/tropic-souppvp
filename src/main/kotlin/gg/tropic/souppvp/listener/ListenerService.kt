package gg.tropic.souppvp.listener

import com.google.common.cache.CacheBuilder
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.tropic.souppvp.TropicSoupPlugin
import gg.tropic.souppvp.config.config
import gg.tropic.souppvp.kit.KitMenu
import gg.tropic.souppvp.profile.*
import gg.tropic.souppvp.profile.event.PlayerStateChangeEvent
import gg.tropic.souppvp.profile.local.CombatTag
import gg.tropic.souppvp.profile.local.RefillStationCooldown
import me.lucko.helper.Events
import me.lucko.helper.Schedulers
import me.lucko.helper.event.filter.EventFilters
import me.lucko.helper.terminable.composite.CompositeTerminable
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.math.Numbers
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.Sign
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.*
import org.bukkit.inventory.ItemStack
import org.bukkit.metadata.FixedMetadataValue
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * @author GrowlyX
 * @since 6/13/2023
 */
@Service
object ListenerService : Listener
{
    @Inject
    lateinit var plugin: TropicSoupPlugin

    private val abilityMeta = "${CC.WHITE}[ability]"

    private val soup = ItemStack(Material.MUSHROOM_SOUP)
    private val inventoryContents = mutableListOf<ItemStack>()
        .apply {
            repeat(27) {
                add(soup)
            }
        }

    private val hotbarMappings = mutableMapOf(
        ItemBuilder
            .of(Material.FIREBALL)
            .name("${CC.GREEN}Select a Kit")
            .build()
            to
            ({ player: Player ->
                KitMenu().openMenu(player)
            } to 0)
    )

    private val fallDamageInvincibilityCache = CacheBuilder
        .newBuilder()
        .expireAfterWrite(5L, TimeUnit.SECONDS)
        .build<UUID, UUID>()

    @Configure
    fun configure()
    {
        plugin.server.pluginManager
            .registerEvents(this, plugin)

        listOf(
            BlockBreakEvent::class.java,
            BlockPlaceEvent::class.java,
            FoodLevelChangeEvent::class.java,
            PlayerBedEnterEvent::class.java,
            PlayerPortalEvent::class.java,
            PlayerFishEvent::class.java,
            PlayerBucketFillEvent::class.java,
            PlayerBucketEmptyEvent::class.java,
            BlockExplodeEvent::class.java
        ).forEach {
            Events
                .subscribe(it)
                .handler { event ->
                    event.isCancelled = true
                }
                .bindWith(plugin)
        }

        var timer = 180
        val triggerSeconds = listOf(30, 5, 4, 3, 2, 1)

        Schedulers
            .async()
            .runRepeating({ _ ->
                if (timer <= 0)
                {
                    Tasks.sync {
                        Bukkit.getWorlds()
                            .forEach {
                                it.entities
                                    .filterIsInstance<Item>()
                                    .forEach { entity ->
                                        entity.remove()
                                    }
                            }

                        Bukkit.broadcastMessage(
                            "${CC.RED}[Server] ${CC.GRAY}Ground items have been cleared!"
                        )
                    }
                    timer = 180
                    return@runRepeating
                }

                if (timer in triggerSeconds)
                {
                    Bukkit.broadcastMessage(
                        "${CC.RED}[Server] ${CC.GRAY}Ground items will be cleared in ${CC.WHITE}$timer${CC.GRAY} seconds."
                    )
                }

                timer -= 1
            }, 0L, 20L)
    }

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

        if ((damager as Player).profile.state == PlayerState.Spawn)
        {
            isCancelled = true
            return
        }

        val player = entity
        if (player is Player)
        {
            startCombatTag(player)
            startCombatTag(damager as Player)
        }
    }

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
            FixedMetadataValue(
                plugin, CombatTag(
                    terminable = terminable,
                    expectedEnd = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(15L)
                )
            )
        )
        player.sendMessage("${CC.RED}You are now combat-tagged!")

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
                terminable.closeAndReportException()
            }, 15L, TimeUnit.SECONDS)
            .bindWith(terminable)
    }

    @EventHandler
    fun PlayerMoveEvent.on()
    {
        if (
            !EventFilters
                .ignoreSameBlockAndY<PlayerMoveEvent>()
                .test(this)
        )
        {
            return
        }

        val block = to.block
            .getRelative(BlockFace.DOWN)

        if (block.type == Material.SPONGE)
        {
            val vector = player.location.direction
                .multiply(config.launchpad.velocity)
                .setY(config.launchpad.yMultiplier)

            player.velocity = vector
        }
    }

    @EventHandler
    fun PlayerMoveEvent.onVisitSpawn()
    {
        if (
            !EventFilters
                .ignoreSameBlock<PlayerMoveEvent>()
                .test(this)
        )
        {
            return
        }

        handleSpawnZone(
            from = from.block,
            to = to.block,
            player = player
        )
    }

    @EventHandler
    fun PlayerTeleportEvent.onVisitSpawn()
    {
        handleSpawnZone(
            from = from.block,
            to = to.block,
            player = player
        )
    }

    private fun handleSpawnZone(
        from: Block,
        to: Block,
        player: Player
    )
    {
        if (
            config.spawnZone.cuboid.contains(from) &&
            !config.spawnZone.cuboid.contains(to)
        )
        {
            player.profile.state = PlayerState.Warzone
            player.sendMessage("going to warzone")
            return
        }

        // TODO: player tps in? player enderpeals in?
        if (
            !config.spawnZone.cuboid.contains(from) &&
            config.spawnZone.cuboid.contains(to)
        )
        {
            player.profile.state = PlayerState.Spawn
            player.sendMessage("going to spawn")
            return
        }
    }

    @EventHandler
    fun PlayerDeathEvent.on()
    {
        drops.removeIf {
            it.type != Material.MUSHROOM_SOUP
        }

        deathMessage =
            "${CC.GREEN}${entity.name}${CC.GRAY} was killed${
                if (entity.killer != null) " by ${CC.GREEN}${entity.killer?.name}" else ""
            }${CC.GRAY}!"

        entity.profile.apply {
            deaths += 1

            if (killStreak > 0)
            {
                killStreak = 0
                entity.sendMessage("${CC.RED}You lost your streak of $killStreak kills!")
            }

            save()

            state = PlayerState.Spawn

            Tasks.delayed(1L) {
                entity.teleport(config.spawn)
            }
        }

        entity.extract<CombatTag>("combat")
            ?.apply {
                terminable.closeAndReportException()
            }

        entity.killer?.apply {
            profile.kills += 1
            profile.killStreak += 1

            profile.coins += 13
            profile.experience += 3

            sendMessage(arrayOf(
                "${CC.GOLD}+13 coins (killing a player)",
                "${CC.GREEN}+3 exp (killing a player)"
            ))

            if (profile.maxKillStreak < profile.killStreak)
            {
                profile.maxKillStreak = profile.killStreak
            }

            profile.save()

            if (entity.profile.bounty != null)
            {
                entity.profile.bounty!!
                    .apply {
                        profile.coins += this.amount

                        sendMessage(
                            "${CC.SEC}You claimed a bounty on ${CC.GREEN}${entity.name}${CC.SEC} worth ${CC.GOLD}${
                                Numbers.format(amount)
                            } $coinIcon${CC.SEC}."
                        )

                        Bukkit.broadcastMessage(
                            "${CC.DARK_AQUA}$name${CC.SEC} claimed the bounty on ${CC.GREEN}${entity.name}${CC.SEC} worth ${CC.GOLD}${
                                Numbers.format(amount)
                            } $coinIcon${CC.SEC}."
                        )
                    }

                entity.profile.bounty = null
                entity.profile.save()
            }
        }
    }

    @EventHandler
    fun PlayerDropItemEvent.on()
    {
        if (player.profile.state == PlayerState.Spawn)
        {
            isCancelled = true
            return
        }

        if (itemDrop.itemStack.type == Material.BOWL)
        {
            Schedulers
                .sync()
                .runLater({
                    itemDrop.remove()
                }, 1L)
            return
        }

        if (itemDrop.itemStack.type.name.contains("SWORD"))
        {
            isCancelled = true
            return
        }

        val lore = itemDrop.itemStack.itemMeta.lore

        if (lore.isNotEmpty() && lore.last() == abilityMeta)
        {
            isCancelled = true
            return
        }
    }

    @EventHandler
    fun PlayerStateChangeEvent.toSpawn()
    {
        if (to != PlayerState.Spawn)
        {
            return
        }

        val player = profile.player()
        player.refresh(GameMode.ADVENTURE)

        hotbarMappings.entries
            .forEach {
                player.inventory.setItem(it.value.second, it.key)
            }

        player.updateInventory()

        if (from == PlayerState.Loading)
        {
            player.teleport(config.spawn)
        }
    }

    @EventHandler
    fun EntityDamageEvent.onFall()
    {
        if (
            entity is Player &&
            (entity as Player).profile.state == PlayerState.Spawn
        )
        {
            isCancelled = true
            return
        }

        if (cause == EntityDamageEvent.DamageCause.DROWNING)
        {
            isCancelled = true
            return
        }

        if (
            entity is Player &&
            cause == EntityDamageEvent.DamageCause.FALL &&
            fallDamageInvincibilityCache.getIfPresent(entity.uniqueId) != null
        )
        {
            isCancelled = true
        }
    }

    @EventHandler
    fun PlayerStateChangeEvent.toWarZone()
    {
        if (to != PlayerState.Warzone)
        {
            return
        }

        profile.player().refresh(GameMode.SURVIVAL)
        profile.previouslyChosenKit
            ?.apply {
                val kit = config.kits[this]
                    ?: return@apply

                kit.applyTo(profile.player())
            }

        fallDamageInvincibilityCache.put(
            profile.identifier, profile.identifier
        )
    }

    @EventHandler
    fun PlayerInteractEvent.on()
    {
        // TODO: fix block interaction but no duplication
        if (action == Action.RIGHT_CLICK_AIR)
        {
            if (player.profile.state == PlayerState.Spawn)
            {
                hotbarMappings.entries
                    .firstOrNull {
                        it.key.isSimilar(item)
                    }
                    ?.value?.first?.invoke(player)
                return
            }

            if (player.itemInHand.type == Material.MUSHROOM_SOUP && player.health < 19.5)
            {
                player.health = (player.health + 7.0).coerceAtMost(20.0)
                player.itemInHand.type = Material.BOWL
                player.updateInventory()
                return
            }
        }

        if (action == Action.RIGHT_CLICK_BLOCK)
        {
            if (clickedBlock.type == Material.WALL_SIGN)
            {
                val sign = clickedBlock.state as Sign

                if (sign.getLine(0).isNotEmpty())
                {
                    player.extract<RefillStationCooldown>("refill")
                        ?.apply {
                            player.sendMessage(
                                "${CC.RED}You're on cooldown. You can refill again in ${expectedEndFormat}s!"
                            )
                            return
                        }

                    val terminable = CompositeTerminable.create()
                    terminable.with {
                        player
                            .removeMetadata(
                                "refill", plugin
                            )
                    }

                    player.setMetadata(
                        "refill",
                        FixedMetadataValue(
                            plugin,
                            RefillStationCooldown(
                                terminable = terminable,
                                expectedEnd = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(30L)
                            )
                        )
                    )

                    Events
                        .subscribe(PlayerQuitEvent::class.java)
                        .handler {
                            terminable.closeAndReportException()
                        }
                        .bindWith(terminable)

                    Schedulers
                        .async()
                        .runLater({
                            terminable.closeAndReportException()
                        }, 30L, TimeUnit.SECONDS)
                        .bindWith(terminable)

                    val inventory = Bukkit
                        .createInventory(
                            player,
                            InventoryType.CHEST,
                            "Refill your inventory..."
                        )

                    inventory.contents = inventoryContents
                        .toTypedArray()
                    player.openInventory(inventory)
                }
            }
        }
    }
}
