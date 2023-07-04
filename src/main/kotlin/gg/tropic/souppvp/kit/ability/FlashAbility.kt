package gg.tropic.souppvp.kit.ability

import gg.scala.flavor.service.Service
import gg.tropic.souppvp.config.config
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.time.Duration

/**
 * @author GrowlyX
 * @since 7/3/2023
 */
@Service
object FlashAbility : Ability()
{
    override val id = "flash"
    override val item = ItemBuilder
        .of(Material.REDSTONE_TORCH_ON)
        .name("${CC.RED}Flash")
        .addToLore(
            "${CC.GREEN}Right-Click: ${CC.GRAY}Teleport to another location!"
        )

    override val cooldown = Duration.ofSeconds(25L)!!

    override fun use(player: Player, item: ItemStack): Boolean
    {
        val blockLoc: Location
        val blocks = player.getLastTwoTargetBlocks(null as HashSet<Byte?>?, 50)
        blockLoc = if (blocks.size > 1 && blocks[1].type == Material.AIR)
        {
            val maxLocation: Location = player.location.add(player.location.direction.multiply(50))
            player.world.getHighestBlockAt(maxLocation).location
        } else
        {
            blocks[0].location
        }

        val playerLoc = player.location
        val distance = playerLoc.distance(blockLoc)

        if (distance > 2)
        {
            val loc = blockLoc.add(0.5, 1.5, 0.5)

            loc.pitch = playerLoc.pitch
            loc.yaw = playerLoc.yaw

            if (loc.blockY >= 150 || config.spawnZone
                    .any {
                        it.cuboid.contains(loc)
                    })
            {
                player.sendMessage(ChatColor.RED.toString() + "You can't teleport here!")
                return false
            }

            player.eject()
            player.teleport(loc)

            playerLoc.world.playSound(playerLoc, Sound.ENDERMAN_TELEPORT, 1f, 1.2f)
            playerLoc.world.playSound(loc, Sound.ENDERMAN_TELEPORT, 1f, 1.2f)
            playerLoc.world.playEffect(loc, Effect.ENDER_SIGNAL, 1)

            loc.world.playEffect(loc, Effect.ENDER_SIGNAL, 1)

            player.addPotionEffect(PotionEffect(PotionEffectType.WEAKNESS, (distance / 2 * 20).toInt(), 1), true)

            if (player.fallDistance > 10)
            {
                // half the damage if teleporting down from high places
                player.fallDistance = player.fallDistance / 2
            }
            return true
        } else
        {
            player.sendMessage(ChatColor.RED.toString() + "You can't teleport this close!")
            return false
        }
    }
}
