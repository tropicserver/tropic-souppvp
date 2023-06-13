package gg.tropic.souppvp.profile

import gg.scala.commons.persist.ProfileOrchestrator
import gg.scala.flavor.service.Service
import java.util.*

/**
 * @author GrowlyX
 * @since 6/12/2023
 */
@Service
object SoupProfileService : ProfileOrchestrator<SoupProfile>()
{
    override fun new(uniqueId: UUID) = SoupProfile(uniqueId)
    override fun type() = SoupProfile::class

    override fun postLoad(uniqueId: UUID)
    {
        val profile = find(uniqueId)
            ?: return

        // to prevent issues w/ transient types
        profile.backingState = PlayerState.Loading
    }
}
