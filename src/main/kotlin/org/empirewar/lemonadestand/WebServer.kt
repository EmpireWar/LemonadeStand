package org.empirewar.lemonadestand

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.javalin.Javalin
import io.javalin.http.Context
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.empirewar.lemonadestand.event.KoFiTransactionEvent
import org.empirewar.lemonadestand.gson.InstantAdapter
import org.empirewar.lemonadestand.kofi.ShopOrder
import java.time.Instant

class WebServer(plugin: LemonadeStand) {

    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(Instant::class.java, InstantAdapter())
        .create()

    private val app: Javalin = Javalin.create()

    init {
        val token = plugin.config.getString(VERIFICATION_TOKEN_CONFIG_PATH)
        val isDevelopment = token.isNullOrBlank()
        if (isDevelopment) {
            plugin.getLogger().warning("LemonadeStand has been started without a webhook verification token. It is highly advised to set one in the config.yml file.")
        }

        app.post("/webhook") { ctx: Context ->
            // The Ko-Fi webhook sends the data as an urlencoded string
            val jsonBody = ctx.formParam("data")
            if (isDevelopment) plugin.getLogger().info("Received webhook: $jsonBody")

            // Parse the webhook
            val shopOrder = gson.fromJson(jsonBody, ShopOrder::class.java)
            if (shopOrder == null) {
                plugin.getLogger().warning("Failed to parse webhook")
                ctx.status(400) // Return http status 400 Bad Request (invalid json)
                return@post
            }

            // Verify the webhook
            if (!isDevelopment) {
                if (shopOrder.verificationToken != token) {
                    plugin.getLogger().warning("Invalid verification token")
                    ctx.status(401) // Return http status 401 Unauthorized (invalid token)
                    return@post
                }
            }

            var player: OfflinePlayer?
            player = findPotentialUsername(shopOrder.fromName)

            // If we weren't able to resolve the "from name" as a player, then try the message
            if (player == null) {
                if (shopOrder.message == null) {
                    plugin.getLogger().warning("Missing message in payload: cannot fully process")
                    ctx.status(200) // Not an error, just not a player we can process
                    return@post
                }
                player = findPotentialUsername(shopOrder.message)
            }

            // If neither "from name" nor "message" contained a valid username, fail and return
            if (player == null) {
                plugin.getLogger().warning("Invalid username (not found)! from_name='${shopOrder.fromName}' message='${shopOrder.message}'")
                ctx.status(200) // Not an error, just not a player we can process
                return@post
            }

            // Dispatch event in the main thread
            val finalPlayer: OfflinePlayer = player
            Bukkit.getScheduler().runTask(LemonadeStand.get(), Runnable {
                plugin.transactionLogger.info("Processing order '${shopOrder.kofiTransactionId}' for " + finalPlayer.name + ": ${shopOrder.amount} ${shopOrder.currency}")
                Bukkit.getPluginManager().callEvent(KoFiTransactionEvent(finalPlayer, shopOrder))
            })
            ctx.status(200) // return http status 200 OK
        }
    }

    private fun findPotentialUsername(str: String?): OfflinePlayer? {
        if (str == null) return null

        // Remove all prefixing spaces
        var message = str.replace("^\\s+".toRegex(), "")

        // Remove all characters after a potential space (indicating the end of the username)
        message = message.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]

        // Check if the potential username is >= 3 characters long and <= 16 characters long
        if (message.length < 3 || message.length > 16) {
            LemonadeStand.get().getLogger().warning("Invalid username (too short/long): $message")
            return null
        }

        // Validate the username character set (abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_)
        if (!message.matches("^[a-zA-Z0-9_]+$".toRegex())) {
            LemonadeStand.get().getLogger().warning("Invalid username (char. set): $message")
            return null
        }

        // Get the player from the cache
        return Bukkit.getOfflinePlayerIfCached(message)
    }

    fun start() {
        app.start(LemonadeStand.get().config.getInt("settings.port", 7000))
    }

    fun stop() {
        app.stop()
    }

    companion object {
        private const val VERIFICATION_TOKEN_CONFIG_PATH = "settings.verification-token"
    }
}
