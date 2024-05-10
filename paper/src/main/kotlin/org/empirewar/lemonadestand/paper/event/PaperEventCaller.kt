package org.empirewar.lemonadestand.paper.event

import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.empirewar.lemonadestand.LemonadeStand
import org.empirewar.lemonadestand.event.EventCaller
import org.empirewar.lemonadestand.kofi.ShopOrder

class PaperEventCaller(plugin: LemonadeStand<OfflinePlayer>) : EventCaller<OfflinePlayer>(plugin) {

    override fun callTransactionEvent(player: OfflinePlayer, shopOrder: ShopOrder) {
        Bukkit.getPluginManager().callEvent(KoFiTransactionEvent(player, shopOrder))
    }
}