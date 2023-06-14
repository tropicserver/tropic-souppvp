package gg.tropic.souppvp.kit.ability

import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
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

    val deployed by lazy {
        item
            .addToLore(
                "",
                "${CC.WHITE}[ability]"
            )
            .build()
    }

    abstract fun use(
        player: Player, item: ItemStack
    )
}
