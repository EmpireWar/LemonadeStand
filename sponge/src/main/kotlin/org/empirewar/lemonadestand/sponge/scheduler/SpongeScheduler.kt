package org.empirewar.lemonadestand.sponge.scheduler

import org.empirewar.lemonadestand.scheduler.PlatformScheduler
import org.empirewar.lemonadestand.sponge.LemonadeStandSponge
import org.spongepowered.api.Sponge
import org.spongepowered.api.scheduler.Task

class SpongeScheduler(private val plugin: LemonadeStandSponge): PlatformScheduler() {

    override fun executeOnMain(runnable: Runnable) {
        Sponge.server().scheduler().submit(Task.builder().plugin(plugin.pluginContainer()).execute(runnable).build())
    }
}