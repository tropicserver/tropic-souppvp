package gg.tropic.souppvp.kit.ability

import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

/**
 * @author GrowlyX
 * @since 6/14/2023
 */
object AbilityService
{
    val mappings = mutableMapOf<String, Ability>()
    val abilityMetaKey = "${CC.WHITE}[ability]"

    fun buildAbilityItem(name: String) = ItemBuilder
        .of(Material.MONSTER_EGG)
        .name(abilityMetaKey)
        .addToLore(name)
        .build()

    fun isAbilityItem(itemStack: ItemStack) =
        itemStack.itemMeta.displayName == abilityMetaKey

    fun exportAbilityFromItem(item: ItemStack) =
        item.itemMeta.lore.first()
}
