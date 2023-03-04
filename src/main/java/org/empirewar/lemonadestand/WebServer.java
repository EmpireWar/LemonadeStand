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

	private static final String VERIFICATION_TOKEN_CONFIG_PATH = "settings.verification-token";

	private final Gson gson;
	private final Javalin app;

	public WebServer(LemonadeStand plugin) {
		gson = new GsonBuilder()
				.registerTypeAdapter(Instant.class, new InstantAdapter())
				.create();

		app = Javalin.create().get("/hello", ctx -> ctx.result("Hello World"));

		final String token = plugin.getConfig().getString(VERIFICATION_TOKEN_CONFIG_PATH);
		if (token == null || token.isBlank()) {
			plugin.getLogger().warning("LemonadeStand has been started without a webhook verification token. It is highly advised to set one in the config.yml file.");
		}

		app.post("/webhook", ctx -> {
			// The Ko-Fi webhook sends the data as an urlencoded string
			final String jsonBody = ctx.formParam("data");
			Bukkit.getLogger().info("Received webhook: " + jsonBody);

			// Parse the webhook
			final ShopOrder shopOrder = gson.fromJson(jsonBody, ShopOrder.class);
			if (shopOrder == null) {
				Bukkit.getLogger().warning("Failed to parse webhook");
				ctx.status(400); // return http status 400 Bad Request
				return;
			}

			final File root = new File(plugin.getDataFolder() + File.separator + "logs");
			if (!root.exists()) {
				root.mkdirs();
			}

			final File file = new File(root + File.separator + "transactions.log");
			if (!file.exists()) {
				file.createNewFile();
			}

			// todo: handle errors better (or at all)
			final String logInfo = "[" + shopOrder.getTimestamp() + "] " + shopOrder.getKofiTransactionId() + ": " + shopOrder.getMessage();
			Files.writeString(file.toPath(), logInfo + "\n", StandardCharsets.UTF_8, StandardOpenOption.APPEND);

			// Verify the webhook
			if (token != null && !token.isBlank()) {
				if (!shopOrder.getVerificationToken().equals(token)) {
					Bukkit.getLogger().warning("Invalid verification token");
					ctx.status(401); // return http status 401 Unauthorized
					return;
				}
			}

			OfflinePlayer player;
			player = findPotentialUsername(shopOrder.getFromName());
			// If we weren't able to resolve the from name as a player, then try the message
			if (player == null) {
				if (shopOrder.getMessage() == null) {
					Bukkit.getLogger().warning("Missing message in payload: cannot fully process");
					ctx.status(200);
					return;
				}

				player = findPotentialUsername(shopOrder.getMessage());
			}

			if (player == null) {
				Bukkit.getLogger().warning("Invalid username (not found): " + shopOrder.getMessage());
				ctx.status(200);
				return;
			}

			// Dispatch event in the main thread
			final OfflinePlayer finalPlayer = player;
			Bukkit.getScheduler().runTask(LemonadeStand.get(), () -> {
				Bukkit.getPluginManager().callEvent(new KoFiTransactionEvent(finalPlayer, shopOrder));
			});

			ctx.status(200); // return http status 200 OK
		});
	}

	private OfflinePlayer findPotentialUsername(String str) {
		// Remove all prefixing spaces
		String message = str.replaceAll("^\\s+", "");

		// Remove all characters after a potential space (indicating the end of the username)
		message = message.split(" ")[0];

		// Check if the potential username is >= 3 characters long and <= 16 characters long
		if (message.length() < 3 || message.length() > 16) {
			LemonadeStand.get().getLogger().warning("Invalid username (too long): " + message);
			return null;
		}

		// Validate the username character set (abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_)
		if (!message.matches("^[a-zA-Z0-9_]+$")) {
			LemonadeStand.get().getLogger().warning("Invalid username (characters): " + message);
			return null;
		}

		// Get the player from the cache
		return Bukkit.getOfflinePlayerIfCached(message);
	}

	public void start() {
		app.start(7000);
	}

	public void stop() {
		app.stop();
	}

}