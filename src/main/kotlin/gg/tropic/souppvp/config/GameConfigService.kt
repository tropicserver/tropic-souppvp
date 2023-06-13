package gg.tropic.souppvp.config

import gg.scala.commons.persist.datasync.DataSyncService
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Service
import gg.tropic.souppvp.TropicSoupPlugin

/**
 * @author GrowlyX
 * @since 6/12/2023
 */
@Service
object GameConfigService : DataSyncService<GameConfig>()
{
    @Inject
    lateinit var plugin: TropicSoupPlugin

    override fun keys() = GameConfigKeys
    override fun type() = GameConfig::class.java
}
