package org.empirewar.lemonadestand.sponge.event

import org.empirewar.lemonadestand.LemonadeStand
import org.empirewar.lemonadestand.event.EventCaller
import org.empirewar.lemonadestand.kofi.ShopOrder
import org.spongepowered.api.Sponge
import org.spongepowered.api.entity.living.player.User

class SpongeEventCaller(plugin: LemonadeStand<User>) : EventCaller<User>(plugin) {

    override fun callTransactionEvent(player: User, shopOrder: ShopOrder) {
        Sponge.eventManager().post(
            KoFiTransactionEvent(
                player,
                shopOrder
            )
        )
    }
}