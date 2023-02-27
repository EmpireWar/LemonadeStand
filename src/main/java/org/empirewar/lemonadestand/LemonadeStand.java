package org.empirewar.lemonadestand;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.empirewar.lemonadestand.event.KoFiTransactionEvent;

public final class LemonadeStand extends JavaPlugin implements Listener {

	private static LemonadeStand INSTANCE;

	private WebServer webServer;

	@Override
	public void onEnable() {
		// Plugin startup logic
		INSTANCE = this;

		logger.info("Doing crazy shit now");

		webServer = new WebServer();
		webServer.start();

		logger.info("Oki doki");

		getServer().getPluginManager().registerEvents(this, this);

	}

	@Override
	public void onDisable() {
		// Plugin shutdown logic

		webServer.stop();
	}

	public static LemonadeStand get() {
		return INSTANCE;
	}

	@EventHandler
	public void onWebHookReceive(KoFiTransactionEvent event) {
		logger.info("Received order: " + event.getShopOrder().getKofiTransactionId().toString() + " for " + event.getPlayer().getName());
	}

}