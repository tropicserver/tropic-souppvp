package gg.tropic.souppvp.profile.local

/**
 * @author GrowlyX
 * @since 6/13/2023
 */
data class ItemRenameCooldown(
    val expectedEnd: Long
)
{
    val expectedEndFormat: String
        get() = "%.2f".format(
            (expectedEnd - System.currentTimeMillis()) / 1000.0f
        )
}
