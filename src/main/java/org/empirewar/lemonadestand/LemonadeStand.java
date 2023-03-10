package org.empirewar.lemonadestand;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.empirewar.lemonadestand.discord.WebhookSender;
import org.empirewar.lemonadestand.event.KoFiTransactionEvent;

import java.io.File;
import java.net.MalformedURLException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public final class LemonadeStand extends JavaPlugin implements Listener {

	private static LemonadeStand INSTANCE;

	private Logger transactionLogger;

	private WebServer webServer;
	private WebhookSender webhookSender;

	@Override
	public void onEnable() {
		// Plugin startup logic
		INSTANCE = this;

		try {
			transactionLogger = Logger.getLogger("TransactionLogger");
			FileHandler fileHandler = new FileHandler(getDataFolder() + File.separator + "logs" + File.separator + "transactions.log", true);
			transactionLogger.addHandler(fileHandler);
			transactionLogger.setUseParentHandlers(false);
		} catch (Exception e) {
			e.printStackTrace();
		}

		saveDefaultConfig();

		getLogger().info("Starting web server...");
		webServer = new WebServer(this);
		webServer.start();
		getLogger().info("Web server started!");

		try {
			webhookSender = new WebhookSender(this);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

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

	public Logger getTransactionLogger() {
		return transactionLogger;
	}

	@EventHandler
	public void onWebHookReceive(KoFiTransactionEvent event) {
		getLogger().info("Sending webhook...");
		if (webhookSender != null) {
			webhookSender.sendWebhook(event);
		}
	}

}