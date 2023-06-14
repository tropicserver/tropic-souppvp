package gg.tropic.souppvp.kit

import gg.tropic.souppvp.config.config
import gg.tropic.souppvp.profile.coinIcon
import gg.tropic.souppvp.profile.profile
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.entity.Player

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
            19, 20, 21, 22, 23, 24, 25
        )
    }

    init
    {
        placeholdBorders = true
    }

    override fun getAllPagesButtons(player: Player) =
        mutableMapOf<Int, Button>().apply {
            val profile = player.profile
            config.kits.values.forEach {
                this[size] = ItemBuilder
                    .copyOf(it.item)
                    .name("${CC.GREEN}${it.displayName}")
                    .setLore(
                        it.description
                            .map { line -> "${CC.WHITE}$line" }
                    )
                    .apply {
                        if (!profile.owns(it))
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
                        if (profile.owns(it))
                        {
                            profile.previouslyChosenKit = it.id
                            player.closeInventory()
                        } else
                        {
                            if (profile.coins < it.cost)
                            {
                                player.sendMessage("${CC.RED}You're too broke to purchase this!")
                                return@toButton
                            }

                            profile.coins -= it.cost
                            profile.ownedKits += it.id
                            profile.save()

                            player.sendMessage("${CC.GREEN}You purchased the kit: ${CC.WHITE}${it.displayName}")
                            openMenu(player)
                        }
                    }
            }
        }

    override fun getAllPagesButtonSlots() = slots
    override fun getPrePaginatedTitle(player: Player) = "Select a kit"
}
