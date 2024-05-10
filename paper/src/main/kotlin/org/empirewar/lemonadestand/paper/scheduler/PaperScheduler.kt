package org.empirewar.lemonadestand.paper.scheduler

import org.bukkit.Bukkit
import org.empirewar.lemonadestand.paper.LemonadeStandPaper
import org.empirewar.lemonadestand.scheduler.PlatformScheduler

class PaperScheduler(private val plugin: LemonadeStandPaper): PlatformScheduler() {

    override fun executeOnMain(runnable: Runnable) {
        Bukkit.getScheduler().runTask(plugin, runnable)
    }

}