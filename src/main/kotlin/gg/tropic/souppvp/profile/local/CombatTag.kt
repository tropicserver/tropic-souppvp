package gg.tropic.souppvp.profile.local

import me.lucko.helper.terminable.composite.CompositeTerminable

/**
 * @author GrowlyX
 * @since 6/13/2023
 */
data class CombatTag(
    val terminable: CompositeTerminable,
    val expectedEnd: Long
)
{
    val expectedEndFormat: String
        get() = "%.2f".format(
            (expectedEnd - System.currentTimeMillis()) / 1000.0f
        )
}
