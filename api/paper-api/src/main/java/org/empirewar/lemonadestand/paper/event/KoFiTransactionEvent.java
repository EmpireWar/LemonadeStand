package org.empirewar.lemonadestand.paper.event;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.empirewar.lemonadestand.kofi.ShopOrder;
import org.jetbrains.annotations.NotNull;

public final class KoFiTransactionEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final OfflinePlayer player;
    private final ShopOrder shopOrder;

    public KoFiTransactionEvent(OfflinePlayer player, ShopOrder shopOrder) {
        this.player = player;
        this.shopOrder = shopOrder;
    }

    public OfflinePlayer getPlayer() {
        return player;
    }

    public ShopOrder getShopOrder() {
        return shopOrder;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
