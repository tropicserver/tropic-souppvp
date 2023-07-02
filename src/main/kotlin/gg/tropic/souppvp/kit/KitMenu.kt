package gg.tropic.souppvp.kit

import gg.tropic.souppvp.config.config
import gg.tropic.souppvp.listener.ListenerService.plugin
import gg.tropic.souppvp.profile.coinIcon
import gg.tropic.souppvp.profile.profile
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue

/**
 * @author GrowlyX
 * @since 6/13/2023
 */
class KitMenu : PaginatedMenu()
{
    companion object
    {
        val slots = mutableListOf(
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            19 + 9, 20 + 9, 21 + 9, 22 + 9, 23 + 9, 24 + 9, 25 + 9,
        )
    }

    init
    {
        placeholdBorders = true
    }

    override fun getAllPagesButtons(player: Player) =
        mutableMapOf<Int, Button>().apply {
            val profile = player.profile

            config.kits
                .filter { it.value.enabled }
                .values
                .sortedBy { it.position }
                .forEach {
                    this[size] = ItemBuilder
                        .copyOf(it.item)
                        .name("${CC.GREEN}${it.displayName}")
                        .setLore(
                            it.description
                                .map { line -> "${CC.WHITE}$line" }
                        )
                        .apply {
                            if (it.cost > 0.0 && !profile.owns(it))
                            {
                                addToLore(
                                    "",
                                    "${CC.GRAY}Price: ${CC.GOLD}${it.cost} $coinIcon",
                                    "",
                                    if (profile.coins < it.cost)
                                        "${CC.RED}You're too broke to purchase this!"
                                    else
                                        "${CC.GREEN}Click to purchase!"
                                )
                            } else
                            {
                                addToLore(
                                    "",
                                    "${CC.GREEN}Click to equip!"
                                )
                            }
                        }
                        .toButton { _, _ ->
                            if (profile.owns(it) || it.cost == 0.0)
                            {
                                profile.previouslyChosenKit = it.id
                                profile.save()

                                it.applyTo(player)

                                player.setMetadata(
                                    "kit-applied",
                                    FixedMetadataValue(plugin, "")
                                )

                                player.sendMessage("${CC.GREEN}You have selected the ${CC.PRI}${it.displayName}${CC.GREEN} kit!")
                                player.closeInventory()
                            } else
                            {
                                if (profile.coins < it.cost)
                                {
                                    player.sendMessage("${CC.RED}You're too broke to purchase this!")
                                    return@toButton
                                }

                                ConfirmMenu(
                                    title = "Purchase kit: ${it.displayName}",
                                    extraInfo = emptyList(),
                                    confirm = true
                                ) { confirmed ->
                                    if (confirmed)
                                    {
                                        profile.coins -= it.cost
                                        profile.ownedKits += it.id
                                        profile.save()

                                        player.playSound(player.location, Sound.NOTE_BASS, 1.0f, 1.5f)
                                        player.sendMessage("${CC.GREEN}You purchased the kit: ${CC.WHITE}${it.displayName}")

                                        openMenu(player)
                                    } else
                                    {
                                        openMenu(player)
                                    }
                                }.openMenu(player)
                            }
                        }
                }
        }

    override fun size(buttons: Map<Int, Button>) = 45
    override fun getMaxItemsPerPage(player: Player) = 21

    override fun getAllPagesButtonSlots() = slots
    override fun getPrePaginatedTitle(player: Player) = "Select a kit"
}
