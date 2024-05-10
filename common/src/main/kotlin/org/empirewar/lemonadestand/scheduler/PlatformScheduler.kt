package org.empirewar.lemonadestand.scheduler

abstract class PlatformScheduler {

    abstract fun executeOnMain(runnable: Runnable)
}