package gg.tropic.souppvp.leaderboard

/**
 * @author GrowlyX
 * @since 6/13/2023
 */
enum class LeaderboardType(
    val field: String
)
{
    Kills("kills"),
    Deaths("deaths"),
    Balances("coins"),
    Consumed_Soup("soupsConsumed");

    /**
     * This is sorta ghetto but too lazy right now
     */
    val display: String = name
        .replace("_", " ")
}
