package gg.tropic.souppvp.shop

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 7/2/2023
 */
class ShopMenu : Menu("Shop")
{
    override fun getButtons(player: Player) = mutableMapOf<Int, Button>()
}
