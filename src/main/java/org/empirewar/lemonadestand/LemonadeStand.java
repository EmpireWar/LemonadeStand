package org.empirewar.lemonadestand;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.empirewar.lemonadestand.discord.WebhookSender;
import org.empirewar.lemonadestand.event.KoFiTransactionEvent;

import java.net.MalformedURLException;

public final class LemonadeStand extends JavaPlugin implements Listener {

	private static LemonadeStand INSTANCE;

	private WebServer webServer;
	private WebhookSender webhookSender;

	@Override
	public void onEnable() {
		// Plugin startup logic
		INSTANCE = this;

		logger.info("Doing crazy shit now");

		saveDefaultConfig();

		webServer = new WebServer(this);
		webServer.start();

		try {
			webhookSender = new WebhookSender(this);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		logger.info("Oki doki");

		getServer().getPluginManager().registerEvents(this, this);

	}

	@Override
	public void onDisable() {
		// Plugin shutdown logic

		webServer.stop();
		webhookSender.stop();
	}

	public static LemonadeStand get() {
		return INSTANCE;
	}

	@EventHandler
	public void onWebHookReceive(KoFiTransactionEvent event) {
		logger.info("Received order: " + event.getShopOrder().getKofiTransactionId().toString() + " for " + event.getPlayer().getName());
		if (webhookSender != null) {
			webhookSender.sendWebhook(event);
		}
	}

}