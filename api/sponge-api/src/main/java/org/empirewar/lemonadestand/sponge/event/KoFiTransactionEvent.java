package org.empirewar.lemonadestand.sponge.event;

import org.empirewar.lemonadestand.kofi.ShopOrder;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.EventContext;
import org.spongepowered.api.event.impl.AbstractEvent;

public final class KoFiTransactionEvent extends AbstractEvent {

    private final User player;
    private final ShopOrder shopOrder;
    private final Cause cause;

    public KoFiTransactionEvent(User player, ShopOrder shopOrder) {
        this.player = player;
        this.shopOrder = shopOrder;
        this.cause = Cause.of(EventContext.empty(), player);
    }

    public User getPlayer() {
        return player;
    }

    public ShopOrder getShopOrder() {
        return shopOrder;
    }

    @Override
    public Cause cause() {
        return cause;
    }
}
