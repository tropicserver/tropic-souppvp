package gg.tropic.souppvp.kit.ability

import gg.scala.flavor.service.Service
import me.lucko.helper.Schedulers
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.time.Duration
import java.util.concurrent.TimeUnit

/**
 * @author GrowlyX
 * @since 6/14/2023
 */
@Service
object IglooCreatorAbility : Ability()
{
    private val contextualBaseBlockPoints = mutableListOf(
        // right side
        3 to 1,
        3 to 0,
        3 to -1,
        // left side
        -3 to 1,
        -3 to 0,
        -3 to -1,
        // top side
        1 to 3,
        0 to 3,
        -1 to 3,
        // bottom side
        1 to -3,
        0 to -3,
        -1 to -3,
        // right top corner
        2 to 2,
        // left top corner
        -2 to 2,
        // right bottom corner
        2 to -2,
        // left bottom corner
        -2 to -2
    )

    private val contextualLevel1BlockPoints = mutableListOf(
        // right side
        2 to 1,
        2 to 0,
        2 to -1,
        // left side
        -2 to 1,
        -2 to 0,
        -2 to -1,
        // top side
        1 to 2,
        0 to 2,
        -1 to 2,
        // bottom side
        1 to -2,
        0 to -2,
        -1 to -2,
    )

    private val contextualLevel2BlockPoints = mutableListOf(
        -1 to 0,
        0 to 0,
        1 to 0,
        -1 to 1,
        0 to 1,
        1 to 1,
        -1 to -1,
        0 to -1,
        1 to -1
    )

    override val id = "igloo"
    override val item = ItemBuilder
        .of(Material.PACKED_ICE)
        .name("${CC.AQUA}Igloo Creator")
        .addToLore(
            "${CC.GREEN}Right-Click: ${CC.GRAY}Creates an ice house around you!"
        )

    override val cooldown = Duration.ofSeconds(30L)!!

    override fun use(player: Player, item: ItemStack)
    {
        val trackedBlocks = mutableListOf<Block>()
        val location = player.location

        fun Location.transformTo(point: Pair<Int, Int>, yMod: Int) =
            player.world
                .getBlockAt(
                    location.x.toInt() + point.first,
                    y.toInt() + yMod,
                    location.z.toInt() + point.second
                )
                .apply {
                    if (this.type == Material.AIR)
                    {
                        type = Material.PACKED_ICE
                        trackedBlocks += this
                    }
                }

        contextualBaseBlockPoints
            .forEach {
                for (i in 0..2)
                    location.transformTo(it, i)
            }

        contextualLevel1BlockPoints
            .forEach {
                location.transformTo(it, 3)
            }

        contextualLevel2BlockPoints
            .forEach {
                location.transformTo(it, 4)
            }

        Schedulers
            .sync()
            .runLater({
                trackedBlocks.forEach {
                    it.type = Material.AIR
                }
            }, 15L, TimeUnit.SECONDS)
    }
}
