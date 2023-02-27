package org.empirewar.lemonadestand.event;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.empirewar.lemonadestand.kofi.ShopOrder;

public class KoFiTransactionEvent extends Event {

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

	@Override
	public HandlerList getHandlers() {
		return HANDLER_LIST;
	}

	public static HandlerList getHandlerList() {
		return HANDLER_LIST;
	}

}