package gg.tropic.souppvp.leaderboard

import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.tropic.souppvp.TropicSoupPlugin

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

    @Configure
    fun configure()
    {
        // TODO: run aggregation every minute for each lb type
    }
}
