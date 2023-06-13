package gg.tropic.souppvp.config

/**
 * @author GrowlyX
 * @since 6/12/2023
 */
val config: GameConfig
    get() = GameConfigService.cached()
