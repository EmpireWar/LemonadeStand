package org.empirewar.lemonadestand;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.javalin.Javalin;
import org.bukkit.Bukkit;
import org.empirewar.lemonadestand.event.KoFiTransactionEvent;
import org.empirewar.lemonadestand.gson.InstantAdapter;
import org.empirewar.lemonadestand.kofi.ShopOrder;

import java.time.Instant;

public class WebServer {

	private final Gson gson;
	private final Javalin app;

	public WebServer() {
		gson = new GsonBuilder()
				.registerTypeAdapter(Instant.class, new InstantAdapter())
				.create();

		app = Javalin.create().get("/hello", ctx -> ctx.result("Hello World"));

		app.post("/webhook", ctx -> {
			// The Ko-Fi webhook sends the data as an urlencoded string
			final String jsonBody = ctx.formParam("data");
			Bukkit.getLogger().info("Received webhook: " + jsonBody);

			final ShopOrder shopOrder = gson.fromJson(jsonBody, ShopOrder.class);
			if (shopOrder == null) {
				Bukkit.getLogger().warning("Failed to parse webhook");
				ctx.status(400); // return http status 400 Bad Request
				return;
			}

			// Dispatch event in the main thread
			Bukkit.getScheduler().runTask(LemonadeStand.get(), () -> {
				Bukkit.getPluginManager().callEvent(new KoFiTransactionEvent(shopOrder));
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
