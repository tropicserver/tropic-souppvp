package gg.tropic.souppvp.profile

import gg.scala.commons.persist.ProfileOrchestrator
import gg.scala.flavor.service.Service
import gg.tropic.souppvp.profile.local.CombatTag
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

    override fun preLogout(profile: SoupProfile)
    {
        profile.player()
            .extract<CombatTag>("combat")
            ?.apply {
                profile.apply {
                    deaths += 1
                    coins -= 100

                    if (killStreak > 0)
                    {
                        killStreak = 0
                    }
                }

                terminable.closeAndReportException()
            }
    }

    override fun postLoad(uniqueId: UUID)
    {
        val profile = find(uniqueId)
            ?: return

        // to prevent issues w/ transient types
        profile.backingState = PlayerState.Loading
    }
}
