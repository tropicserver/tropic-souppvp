package gg.tropic.souppvp.achievement

import com.cryptomorin.xseries.XMaterial
import gg.scala.achievements.plugin.ScalaAchievementsPlugin
import gg.scala.achievements.plugin.model.*
import gg.scala.achievements.plugin.model.goals.AchievementGoal
import gg.scala.achievements.plugin.profile.AchievementProfile
import gg.scala.achievements.plugin.service.AchievementService
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * @author GrowlyX
 * @since 7/4/2023
 */
@Service
object AchievementService
{
    @Configure
    fun configure()
    {
        ScalaAchievementsPlugin.gamemode = GameMode(
            "SoupPvP", XMaterial.MUSHROOM_STEW
        )

        AchievementService
            .registerAchievement(
                Achievement(
                    id = "soup:daily:kills",
                    name = "Kill Master",
                    stages = linkedSetOf(
                        15.buildStageForKills(),
                        50.buildStageForKills(),
                        100.buildStageForKills(),
                        150.buildStageForKills()
                    ),
                    rewardsPlayer = true,
                    visibleWithinMenu = true,
                    category = AchievementCategory.GameSpecific
                )
            )
    }
}

fun Int.buildStageForKills() = AchievementStage(
    description = listOf(
        "Get ${this@buildStageForKills} daily kills."
    ),
    goals = listOf(
        object : AchievementGoal<Int>
        {
            override val description = "Get ${this@buildStageForKills} daily kills."
            override val id = "dailykills"
            override val requirement = this@buildStageForKills

            override fun createDataCollector(profile: AchievementProfile) =
                TTLKillDataCollector(id)

            override fun matchesRequirement(value: Any?) =
                ((value as Int?) ?: 0) >= this@buildStageForKills
        }
    ),
    rewards = listOf(
        object : AchievementReward
        {
            override val description = "1 egg"

            override fun applyTo(player: Player)
            {
                player.inventory.addItem(ItemStack(Material.EGG))
            }
        }
    )
)
