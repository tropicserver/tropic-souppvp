package gg.tropic.souppvp.feature

import gg.scala.commons.annotations.plugin.SoftDependency
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.flavor.service.ignore.IgnoreAutoScan
import gg.tropic.game.extensions.cosmetics.CosmeticLocalConfig

/**
 * @author GrowlyX
 * @since 4/26/2022
 */
@Service
@IgnoreAutoScan
@SoftDependency("CoreGameExtensions")
object CoreGameExtensionsFeature
{
    @Configure
    fun configure()
    {
        CosmeticLocalConfig.enableCosmeticResources = false
    }
}
