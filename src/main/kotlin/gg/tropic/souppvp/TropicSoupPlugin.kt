package gg.tropic.souppvp

import gg.scala.commons.ExtendedScalaPlugin
import gg.scala.commons.core.plugin.Plugin
import gg.scala.commons.core.plugin.PluginAuthor
import gg.scala.commons.core.plugin.PluginDependency
import gg.scala.commons.core.plugin.PluginWebsite

/**
 * @author GrowlyX
 * @since 6/12/2023
 */
@Plugin(
    name = "slinky",
    version = "%remote%/%branch%/%id%"
)

@PluginAuthor("Scala")
@PluginWebsite("https://scala.gg")

@PluginDependency("Lemon")
@PluginDependency("scala-commons")

@PluginDependency("ScBasics", soft = true)
@PluginDependency("cloudsync", soft = true)
class TropicSoupPlugin : ExtendedScalaPlugin()
