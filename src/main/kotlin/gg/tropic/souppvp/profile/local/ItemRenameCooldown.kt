package gg.tropic.souppvp.profile.local

import net.evilblock.cubed.util.time.TimeUtil

/**
 * @author GrowlyX
 * @since 6/13/2023
 */
data class ItemRenameCooldown(
    val expectedEnd: Long
)
{
    val expectedEndFormat: String
        get() = TimeUtil.formatIntoAbbreviatedString(
            ((expectedEnd - System.currentTimeMillis()) / 1000).toInt()
        )
}
