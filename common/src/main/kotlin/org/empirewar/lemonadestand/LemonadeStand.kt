package org.empirewar.lemonadestand

import org.empirewar.lemonadestand.event.EventCaller
import org.empirewar.lemonadestand.scheduler.PlatformScheduler
import org.spongepowered.configurate.ConfigurationNode
import java.util.logging.Logger

interface LemonadeStand<P> {

    fun getOfflinePlayer(username: String): P?

    fun getUsername(player: P): String

    fun eventCaller(): EventCaller<P>

    fun scheduler(): PlatformScheduler

    fun config(): ConfigurationNode

    fun logger(): Logger

    fun transactionLogger(): Logger
}