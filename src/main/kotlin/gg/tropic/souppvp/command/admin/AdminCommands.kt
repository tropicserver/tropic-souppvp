package gg.tropic.souppvp.command.admin

import gg.scala.commons.acf.CommandHelp
import gg.scala.commons.acf.ConditionFailedException
import gg.scala.commons.acf.annotation.*
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.annotations.commands.customizer.CommandManagerCustomizer
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.command.ScalaCommandManager
import gg.scala.commons.issuer.ScalaPlayer
import gg.scala.flavor.inject.Inject
import gg.scala.lemon.player.LemonPlayer
import gg.tropic.souppvp.TropicSoupPlugin
import gg.tropic.souppvp.config.LocalZone
import gg.tropic.souppvp.config.config
import gg.tropic.souppvp.kit.Kit
import gg.tropic.souppvp.kit.ability.AbilityService
import gg.tropic.souppvp.profile.coinIcon
import gg.tropic.souppvp.profile.profile
import net.evilblock.cubed.menu.menus.TextEditorMenu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.prompt.InputPrompt
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

/**
 * @author GrowlyX
 * @since 6/12/2023
 */
@AutoRegister
@CommandAlias("soupadmin")
@CommandPermission("soup.command.admin")
object AdminCommands : ScalaCommand()
{
    @Inject
    lateinit var plugin: TropicSoupPlugin

    private val potionEffectRegistry = """
            SPEED
            SLOW
            FAST_DIGGING
            SLOW_DIGGING
            INCREASE_DAMAGE
            HEAL
            HARM
            JUMP
            CONFUSION
            REGENERATION
            DAMAGE_RESISTANCE
            FIRE_RESISTANCE
            WATER_BREATHING
            INVISIBILITY
            BLINDNESS
            NIGHT_VISION
            HUNGER
            WEAKNESS
            POISON
            WITHER
            HEALTH_BOOST
            ABSORPTION 
            SATURATION 
        """.trimIndent()
        .split("\n")
        .associateWith { PotionEffectType.getByName(it) }

    @CommandManagerCustomizer
    fun customizer(
        manager: ScalaCommandManager
    )
    {
        manager.commandCompletions
            .registerAsyncCompletion(
                "kits"
            ) {
                config.kits.keys
            }

        manager.commandCompletions
            .registerAsyncCompletion(
                "effects"
            ) {
                potionEffectRegistry.keys
            }

        manager.commandContexts
            .registerContext(
                PotionEffectType::class.java
            ) {
                val arg = it.popFirstArg()

                potionEffectRegistry[arg]
                    ?: throw ConditionFailedException(
                        "No potion effect with the ID $arg exists."
                    )
            }

        manager.commandContexts
            .registerContext(
                Kit::class.java
            ) {
                val arg = it.popFirstArg()

                config.kits[arg.lowercase()]
                    ?: throw ConditionFailedException(
                        "No kit with the ID $arg exists."
                    )
            }
    }

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
                            spawnZone += LocalZone(
                                zoneMin = minimum,
                                zoneMax = maximum
                            )
                            pushUpdates()

                            player.sendMessage("${CC.GREEN}Updated the spawn zone! You now have ${spawnZone.size} zones.")
                        }
                        .start(player.bukkit())
                }
                .start(player.bukkit())
        }

    @Subcommand("kit create")
    fun onKitCreate(player: ScalaPlayer, @Single id: String) =
        with(config) {
            if (config.kits[id.lowercase()] != null)
            {
                throw ConditionFailedException(
                    "A kit with that id already exists."
                )
            }

            config.kits[id.lowercase()] = Kit(id.lowercase())
            pushUpdates()

            player.sendMessage(
                "${CC.GREEN}Added new kit: ${CC.WHITE}$id${CC.GREEN}."
            )
        }

    @Subcommand("kit aggregate-all-abilities")
    fun onAggAbilities(player: ScalaPlayer)
    {
        config.kits
            .flatMap { it.value.abilitySlots.values }
            .forEach {
                player.sendMessage(it)
            }
    }

    @Subcommand("kit delete")
    fun onKitCreate(player: ScalaPlayer, kit: Kit) =
        with(config) {
            config.kits.remove(kit.id.lowercase())
            pushUpdates()

            player.sendMessage(
                "${CC.GREEN}Deleted kit: ${CC.WHITE}${kit.id}${CC.GREEN}."
            )
        }

    @Subcommand("ability give")
    fun onAbilityGive(player: ScalaPlayer, ability: String)
    {
        player.bukkit().inventory.addItem(
            AbilityService.buildAbilityItem(ability)
        )
    }

    @Subcommand("kit display")
    @CommandCompletion("@kits")
    fun onKitDisplay(player: ScalaPlayer, kit: Kit, displayName: String) =
        with(config) {
            kit.displayName = displayName
            pushUpdates()

            player.sendMessage(
                "${CC.GREEN}Display is now: ${CC.WHITE}$displayName${CC.GREEN}."
            )
        }

    @Subcommand("kit abilities")
    @CommandCompletion("@kits")
    fun onKitAbilities(player: ScalaPlayer, kit: Kit) =
        with(config) {
            player.sendMessage(
                "${CC.GREEN}abilties : ${
                    kit.abilitySlots.values.joinToString(", ")}"
            )
        }

    @Subcommand("kit cost")
    @CommandCompletion("@kits")
    fun onKitCost(player: ScalaPlayer, kit: Kit, cost: Double) =
        with(config) {
            kit.cost = cost
            pushUpdates()

            player.sendMessage(
                "${CC.GREEN}Cost is now: ${CC.WHITE}$cost${CC.GREEN}."
            )
        }

    @Subcommand("kit positionweight")
    @CommandCompletion("@kits")
    fun onKitPositionWeight(player: ScalaPlayer, kit: Kit, position: Int) =
        with(config) {
            kit.position = position
            pushUpdates()

            player.sendMessage(
                "${CC.GREEN}Position is now: ${CC.WHITE}$position{CC.GREEN}."
            )
        }

    @Subcommand("kit getinventory")
    @CommandCompletion("@kits")
    fun onKitGetInventory(player: ScalaPlayer, kit: Kit) =
        with(config) {
            val bukkit = player.bukkit()
            bukkit.inventory.contents = kit.contents
            bukkit.inventory.armorContents = kit.armor

            kit.abilitySlots
                .forEach { (t, u) ->
                    bukkit.inventory.setItem(
                        t,
                        AbilityService.buildAbilityItem(u)
                    )
                }

            bukkit.updateInventory()
        }

    @Subcommand("kit setinventory")
    @CommandCompletion("@kits")
    fun onKitSetInventory(player: ScalaPlayer, kit: Kit) =
        with(config) {
            val bukkit = player.bukkit()
            kit.contents = bukkit.inventory.contents
                .filterNot {
                    it == null || AbilityService.isAbilityItem(it)
                }
                .toTypedArray()

            kit.armor = bukkit.inventory.armorContents

            kit.abilitySlots.clear()

            bukkit.inventory.contents
                .filterNotNull()
                .forEachIndexed { index, itemStack ->
                    if (AbilityService.isAbilityItem(itemStack))
                    {
                        kit.abilitySlots[index] = AbilityService
                            .exportAbilityFromItem(itemStack)
                    }
                }

            player.sendMessage("${CC.GREEN}Set inventory")
            pushUpdates()
        }

    @Subcommand("kit item")
    @CommandCompletion("@kits")
    fun onKitItem(player: ScalaPlayer, kit: Kit) =
        with(config) {
            if (player.bukkit().itemInHand == null)
            {
                throw ConditionFailedException("Have an item in your hand!")
            }

            kit.item = player.bukkit().itemInHand
            pushUpdates()

            player.sendMessage(
                "${CC.GREEN}Item is now from your inventory."
            )
        }

    @Subcommand("kit toggle")
    @CommandCompletion("@kits")
    fun onKitToggle(player: ScalaPlayer, kit: Kit) =
        with(config) {
            kit.enabled = !kit.enabled
            pushUpdates()

            player.sendMessage(
                "${CC.GREEN}Toggled is now: ${kit.enabled}."
            )
        }

    @CommandCompletion("@kits")
    @Subcommand("kit potioneffect remove")
    fun onPotionEffectRemove(player: ScalaPlayer, kit: Kit, type: PotionEffectType) =
        with(config) {
            kit.potionEffects
                .removeIf {
                    it.type == type
                }
            pushUpdates()

            player.sendMessage(
                "${CC.GREEN}Removed type: ${type.name}."
            )
        }

    @CommandCompletion("@kits")
    @Subcommand("kit potioneffect add")
    fun onPotionEffectAdd(player: ScalaPlayer, kit: Kit, type: PotionEffectType, duration: Int, amplifier: Int) =
        with(config) {
            if (kit.potionEffects.any { it.type == type })
            {
                throw ConditionFailedException("potion effect already exists!")
            }

            kit.potionEffects.add(
                PotionEffect(type, duration, amplifier)
            )
            pushUpdates()

            player.sendMessage(
                "${CC.GREEN}Added type: ${type.name}."
            )
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

    @Subcommand("builder build-mode")
    fun onBuildMode(player: ScalaPlayer)
    {
        if (player.bukkit().hasMetadata("builder"))
        {
            player.bukkit()
                .removeMetadata(
                    "builder", plugin
                )

            player.sendMessage("${CC.RED}You've exited build mode!")
            return
        }

        player.bukkit().setMetadata(
            "builder",
            FixedMetadataValue(plugin, "builder")
        )

        player.sendMessage("${CC.GREEN}You've entered build mode!")
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
