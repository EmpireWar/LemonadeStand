package org.empirewar.lemonadestand;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.empirewar.lemonadestand.discord.WebhookSender;
import org.empirewar.lemonadestand.event.KoFiTransactionEvent;
import org.empirewar.lemonadestand.logging.PrettyFormatter;

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
			File logsFolder = new File(getDataFolder() + File.separator + "logs");
			if (!logsFolder.exists()) {
				logsFolder.mkdirs();
			}

			File logFile = new File(logsFolder + File.separator + "transactions.log");
			if (!logFile.exists()) {
				logFile.createNewFile();
			}

			transactionLogger = Logger.getLogger("TransactionLogger");
			FileHandler fileHandler = new FileHandler(logFile.getPath(), true);
			fileHandler.setFormatter(new PrettyFormatter());
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