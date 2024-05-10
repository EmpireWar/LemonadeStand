package org.empirewar.lemonadestand.event

import org.empirewar.lemonadestand.LemonadeStand
import org.empirewar.lemonadestand.kofi.ShopOrder

abstract class EventCaller<P>(private var plugin: LemonadeStand<P>) {

    abstract fun callTransactionEvent(player: P, shopOrder: ShopOrder)
}