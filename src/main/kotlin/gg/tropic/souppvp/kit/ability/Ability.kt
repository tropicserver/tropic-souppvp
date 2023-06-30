package gg.tropic.souppvp.kit.ability

import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.ItemUtils
import net.evilblock.cubed.util.nms.NBTUtil
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.time.Duration

/**
 * @author GrowlyX
 * @since 6/14/2023
 */
sealed class Ability
{
    abstract val id: String
    abstract val item: ItemBuilder
    abstract val cooldown: Duration

    // TODO: NBT instead?
    val deployed by lazy {
        ItemUtils.addToItemTag(
            item.build(),
            "ability", id,
            false
        )
    }

    abstract fun use(
        player: Player, item: ItemStack
    )
}
