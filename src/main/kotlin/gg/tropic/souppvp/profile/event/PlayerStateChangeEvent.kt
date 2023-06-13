package gg.tropic.souppvp.profile.event

import gg.scala.commons.event.StatelessEvent
import gg.tropic.souppvp.profile.PlayerState
import gg.tropic.souppvp.profile.SoupProfile

/**
 * @author GrowlyX
 * @since 6/13/2023
 */
class PlayerStateChangeEvent(
    val profile: SoupProfile,
    val from: PlayerState,
    val to: PlayerState
) : StatelessEvent()
