package gg.tropic.souppvp.profile.bounty

import java.util.UUID

/**
 * @author GrowlyX
 * @since 6/12/2023
 */
data class Bounty(
    var amount: Int,
    val contributors: MutableList<UUID> = mutableListOf()
)
