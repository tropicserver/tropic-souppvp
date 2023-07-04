package gg.tropic.souppvp.achievement

import gg.scala.achievements.plugin.model.goals.ttl.TTLRespectingGoalDataCollector
import org.joda.time.DateTime

/**
 * @author GrowlyX
 * @since 5/22/2023
 */
class TTLKillDataCollector(
    override val goalId: String,
    override var value: Int = 0,
    override var lastTtlHeartbeat: Long = DateTime
        .now()
        .millis
) : TTLRespectingGoalDataCollector<Int>
{
    override fun defaultValueOf() = 0
    override fun getAbstractType() = TTLKillDataCollector::class.java

    override fun nextExpectedTtlRefresh(): DateTime =
        DateTime(lastTtlHeartbeat)
            .plusDays(1)
}
