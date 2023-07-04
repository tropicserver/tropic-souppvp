package gg.tropic.souppvp.profile

import gg.scala.commons.issuer.ScalaPlayer
import org.bukkit.GameMode
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 6/12/2023
 */
const val coinIcon = "⛀"

val Player.profile: SoupProfile
    get() = SoupProfileService.find(this)!!

val ScalaPlayer.profile: SoupProfile
    get() = SoupProfileService.find(bukkit())!!

inline fun <reified T> Entity.extract(metadata: String) =
    getMetadata(metadata).firstOrNull()?.value() as T?

fun Player.refresh(gameMode: GameMode = GameMode.ADVENTURE)
{
    health = maxHealth
    foodLevel = 20
    saturation = 12.8f
    maximumNoDamageTicks = 20
    fireTicks = 0
    fallDistance = 0.0f
    level = 0
    exp = 0.0f
    walkSpeed = 0.2f
    inventory.heldItemSlot = 0

    isFlying = false
    allowFlight = false

    inventory.clear()
    inventory.armorContents = null

    closeInventory()
    updateInventory()

    this.gameMode = gameMode

    for (potionEffect in activePotionEffects)
    {
        removePotionEffect(potionEffect.type)
    }
}
