package gg.tropic.souppvp.leaderboard

import gg.scala.cache.uuid.ScalaStoreUuidCache
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.lemon.util.QuickAccess.username
import gg.scala.store.controller.DataStoreObjectControllerCache
import gg.tropic.souppvp.TropicSoupPlugin
import gg.tropic.souppvp.profile.SoupProfile
import net.evilblock.cubed.serializers.Serializers
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.math.Numbers

/**
 * @author GrowlyX
 * @since 6/13/2023
 */
@Service
object LeaderboardService
{
    @Inject
    lateinit var plugin: TropicSoupPlugin

    private val mappings =
        mutableMapOf<LeaderboardType, List<LeaderboardResult>>()

    fun resultsFor(type: LeaderboardType) =
        mappings[type] ?: listOf()

    private val format = CC.GREEN + "#%s. " + CC.WHITE + "%s " + CC.GRAY + "❘" + CC.GREEN + " %s"

    fun mapToFormattedLeaderboards(type: LeaderboardType) =
        resultsFor(type)
            .mapIndexed { index, result ->
                format.format(
                    index + 1,
                    ScalaStoreUuidCache.username(result._id),
                    Numbers.format(result.value)
                )
            }

    @Configure
    fun configure()
    {
        Tasks.asyncTimer(0L, 20L * 60L) {
            val profile =
                DataStoreObjectControllerCache
                    .findNotNull<SoupProfile>()

            LeaderboardType
                .values()
                .forEach {
                    mappings[it] = profile.mongo()
                        .aggregate(it.aggregate)
                        .toList()
                        .map { bson ->
                            Serializers
                                .gson
                                .fromJson(
                                    bson.toJson(),
                                    LeaderboardResult::class.java
                                )
                        }
                }
        }
    }
}
