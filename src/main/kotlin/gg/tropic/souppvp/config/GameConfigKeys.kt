package gg.tropic.souppvp.config

import gg.scala.commons.persist.datasync.DataSyncKeys

/**
 * @author GrowlyX
 * @since 6/12/2023
 */
object GameConfigKeys : DataSyncKeys
{
    private const val NAMESPACE = "soup"

    override fun store() = keyOf(NAMESPACE, "configuration")
    override fun sync() = keyOf(NAMESPACE, "update")
}
