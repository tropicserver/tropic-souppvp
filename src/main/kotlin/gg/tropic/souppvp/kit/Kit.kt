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
class Kit(
    val id: String,
    var enabled: Boolean = true,
    var displayName: String = id
        .lowercase()
        .capitalize(),
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
    companion object
    {
        fun buildAbilityItem(name: String) = ItemBuilder
            .of(Material.EGG)
            .name("[ability]")
            .addToLore(name)
            .build()

        fun isAbilityItem(itemStack: ItemStack) =
            itemStack.itemMeta.displayName == "[ability]"

        fun exportAbilityFromItem(item: ItemStack) =
            item.itemMeta.lore.first()
    }

    fun applyTo(player: Player)
    {
        player.inventory.contents = contents
        player.inventory.armorContents = armor

        potionEffects.forEach {
            player.addPotionEffect(it, true)
        }

        // TODO: actually apply it
        /*abilitySlots
            .forEach { (t, u) ->
                player.inventory.setItem(
                    t,
                    buildAbilityItem(u)
                )
            }*/

        player.updateInventory()
    }
}
