package gg.tropic.souppvp

import gg.scala.commons.ExtendedScalaPlugin
import gg.scala.commons.annotations.container.ContainerEnable
import gg.scala.commons.core.plugin.Plugin
import gg.scala.commons.core.plugin.PluginAuthor
import gg.scala.commons.core.plugin.PluginDependency
import gg.scala.commons.core.plugin.PluginWebsite
import gg.scala.commons.preconfigure.PreConfigureSubTypeProcessor
import gg.tropic.souppvp.kit.ability.Ability
import gg.tropic.souppvp.kit.ability.AbilityService
import org.bukkit.Bukkit
import org.bukkit.entity.Entity

/**
 * @author GrowlyX
 * @since 6/12/2023
 */
@Plugin(
    name = "SoupPvP",
    version = "%remote%/%branch%/%id%"
)

@PluginAuthor("Scala")
@PluginWebsite("https://scala.gg")

@PluginDependency("Lemon")
@PluginDependency("scala-commons")

@PluginDependency("ScBasics", soft = true)
@PluginDependency("cloudsync", soft = true)
class TropicSoupPlugin : ExtendedScalaPlugin()
{
    @ContainerEnable
    fun containerEnable()
    {
        Bukkit.getWorlds().forEach {
            it.entities.forEach(Entity::remove)
        }

        PreConfigureSubTypeProcessor
            .register<Ability> {
                AbilityService.mappings[it.id] = it
            }
    }
}
