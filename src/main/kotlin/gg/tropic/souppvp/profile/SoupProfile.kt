package gg.tropic.souppvp.profile

import gg.scala.store.controller.DataStoreObjectControllerCache
import gg.scala.store.storage.storable.IDataStoreObject
import gg.scala.store.storage.type.DataStoreStorageType
import gg.tropic.souppvp.kit.Kit
import gg.tropic.souppvp.profile.bounty.Bounty
import gg.tropic.souppvp.profile.event.PlayerStateChangeEvent
import org.bukkit.Bukkit
import java.util.*

/**
 * @author GrowlyX
 * @since 6/12/2023
 */
data class SoupProfile(
    override val identifier: UUID,
    var kills: Int = 0,
    var killStreak: Int = 0,
    var maxKillStreak: Int = 0,
    var deaths: Int = 0,
    var soupsConsumed: Int = 0,
    var coins: Double = 0.0,
    var experience: Int = 0,
    /**
     * Set of perk class names.
     */
    val ownedPerks: MutableSet<String> = mutableSetOf(),
    val ownedKits: MutableSet<String> = mutableSetOf()
) : IDataStoreObject
{
    var bounty: Bounty? = null
    var previouslyChosenKit: String? = null

    @Transient
    private var backingRepairs: Int? = 0

    var repairs: Int
        get()
        {
            if (backingRepairs == null)
            {
                backingRepairs = 0
            }

            return backingRepairs!!
        }
        set(value)
        {
            backingRepairs = value
        }

    fun owns(kit: Kit) = ownedKits.contains(kit.id)

    @Transient
    internal var backingState = PlayerState.Loading

    var state: PlayerState
        get() = backingState
        set(value)
        {
            val oldValue = backingState
            this.backingState = value

            PlayerStateChangeEvent(
                this@SoupProfile,
                oldValue, value
            ).callEvent()
        }

    private val kdr: Float
        get() = if (deaths == 0)
            kills.toFloat()
        else if (kills == 0)
            -deaths.toFloat()
        else kills / deaths.toFloat()

    val kdrFormat: String
        get() = "%.2f".format(kdr)

    fun player() = Bukkit.getPlayer(identifier)!!

    fun save() = DataStoreObjectControllerCache
        .findNotNull<SoupProfile>()
        .save(this, DataStoreStorageType.MONGO)
}
