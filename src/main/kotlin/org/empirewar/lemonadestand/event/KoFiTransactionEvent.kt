package org.empirewar.lemonadestand.event

import org.bukkit.OfflinePlayer
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.empirewar.lemonadestand.kofi.ShopOrder

class KoFiTransactionEvent(val player: OfflinePlayer, val shopOrder: ShopOrder) : Event() {

    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        val handlerList = HandlerList()
    }
}
