package org.empirewar.lemonadestand;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.javalin.Javalin;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.empirewar.lemonadestand.event.KoFiTransactionEvent;
import org.empirewar.lemonadestand.gson.InstantAdapter;
import org.empirewar.lemonadestand.kofi.ShopOrder;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.Instant;

public class WebServer {

	private final Gson gson;
	private final Javalin app;

	public WebServer(LemonadeStand plugin) {
		gson = new GsonBuilder()
				.registerTypeAdapter(Instant.class, new InstantAdapter())
				.create();

		app = Javalin.create().get("/hello", ctx -> ctx.result("Hello World"));

		String key = plugin.getConfig().getString("store.key", "");
		String path = "/webhook";
		if (!key.isEmpty()) {
			path += "/" + key;
		} else {
			plugin.getLogger().warning("LemonadeStand is running in a development environment (no webhook key).");
		}

		app.post(path, ctx -> {
			// The Ko-Fi webhook sends the data as an urlencoded string
			final String jsonBody = ctx.formParam("data");
			Bukkit.getLogger().info("Received webhook: " + jsonBody);

			final ShopOrder shopOrder = gson.fromJson(jsonBody, ShopOrder.class);
			if (shopOrder == null) {
				Bukkit.getLogger().warning("Failed to parse webhook");
				ctx.status(400); // return http status 400 Bad Request
				return;
			}

			File root = new File(plugin.getDataFolder() + "/logs");
			if (!root.exists()) {
				root.mkdirs();
			}

			File file = new File(root + "/transactions.log");
			if (!file.exists()) {
				file.createNewFile();
			}

			String logInfo = shopOrder.getTimestamp() + ", " + shopOrder.getKofiTransactionId() + ", " + shopOrder.getMessage();
			Files.writeString(file.toPath(), logInfo, StandardCharsets.UTF_8, StandardOpenOption.APPEND);

			if (shopOrder.getMessage() == null) {
				Bukkit.getLogger().warning("Missing message in payload: cannot fully process");
				ctx.status(200);
				return;
			}

			// Remove all prefixing spaces
			String message = shopOrder.getMessage().replaceAll("^\\s+", "");

			// Remove all characters after a potential space (indicating the end of the username)
			message = message.split(" ")[0];

			// Check if the potential username is >= 3 characters long and <= 16 characters long
			if (message.length() < 3 || message.length() > 16) {
				Bukkit.getLogger().warning("Invalid username (too long): " + message);
				ctx.status(200);
				return;
			}

			// Validate the username character set (abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_)
			if (!message.matches("^[a-zA-Z0-9_]+$")) {
				Bukkit.getLogger().warning("Invalid username (characters): " + message);
				ctx.status(200);
				return;
			}

			// Get the player from the cache
			final OfflinePlayer player = Bukkit.getOfflinePlayerIfCached(message);
			if (player == null) {
				Bukkit.getLogger().warning("Invalid username (not found): " + message);
				ctx.status(200);
				return;
			}

			// Dispatch event in the main thread
			Bukkit.getScheduler().runTask(LemonadeStand.get(), () -> {
				Bukkit.getPluginManager().callEvent(new KoFiTransactionEvent(player, shopOrder));
			});

			ctx.status(200); // return http status 200 OK
		});
	}

	public void start() {
		app.start(7000);
	}

	public void stop() {
		app.stop();
	}

}
