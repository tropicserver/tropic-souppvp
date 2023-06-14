package gg.tropic.souppvp.kit

import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect

/**
 * @author GrowlyX
 * @since 6/12/2023
 */
data class Kit(
    val id: String,
    var displayName: String = id
        .lowercase()
        .capitalize(),
    val item: ItemStack = ItemBuilder
        .of(Material.PAPER)
        .build(),
    val description: MutableList<String> = mutableListOf(
        "Default kit description."
    ),
    val position: Int = 0,
    val cost: Double = 0.0,
    val armor: MutableList<ItemStack> = mutableListOf(),
    /***
     * Abilities are parsed and removed on kit content update. These
     * ability slots are set when the kit is applied to the player.
     */
    val contents: MutableList<ItemStack> = mutableListOf(),
    val abilitySlots: MutableMap<Int, String> = mutableMapOf(),
    val potionEffects: MutableList<PotionEffect> = mutableListOf()
)
{
    fun applyTo(player: Player)
    {

    }
}
