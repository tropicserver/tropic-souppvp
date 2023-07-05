package gg.tropic.souppvp.kit

import gg.tropic.souppvp.kit.ability.AbilityService
import gg.tropic.souppvp.listener.ListenerService
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.potion.PotionEffect
import java.util.*

/**
 * @author GrowlyX
 * @since 6/12/2023
 */
class Kit(
    val id: String,
    var enabled: Boolean = true,
    var displayName: String = id
        .lowercase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
    var item: ItemStack = ItemBuilder
        .of(Material.PAPER)
        .build(),
    val description: MutableList<String> = mutableListOf(
        "Default kit description."
    ),
    var position: Int = 0,
    var cost: Double = 0.0,
    var armor: Array<ItemStack> = arrayOf(),
    /***
     * Abilities are parsed and removed on kit content update. These
     * ability slots are set when the kit is applied to the player.
     */
    var contents: Array<ItemStack> = arrayOf(),
    val abilitySlots: MutableMap<Int, String> = mutableMapOf(),
    val potionEffects: MutableList<PotionEffect> = mutableListOf()
)
{
    fun applyTo(player: Player)
    {
        player.inventory.contents = contents
        player.inventory.armorContents = armor

        potionEffects.forEach {
            player.addPotionEffect(it, true)
        }

        abilitySlots
            .forEach { (t, u) ->
                val mapping = AbilityService.mappings[u]
                    ?: return@forEach

                player.inventory.setItem(
                    t,
                    mapping.deployed
                )
            }

        if (37 - contents.size > 0)
        {
            repeat(37 - contents.size) {
                player.inventory.addItem(
                    ItemStack(Material.MUSHROOM_SOUP)
                )
            }
        }

        player.updateInventory()
        player.setMetadata(
            "kit-applied",
            FixedMetadataValue(ListenerService.plugin, "")
        )
    }
}
