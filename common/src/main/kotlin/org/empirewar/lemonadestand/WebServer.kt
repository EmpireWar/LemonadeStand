package org.empirewar.lemonadestand

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.javalin.Javalin
import io.javalin.http.Context
import org.empirewar.lemonadestand.gson.InstantAdapter
import org.empirewar.lemonadestand.kofi.ShopOrder
import java.time.Instant

class WebServer<P>(private val plugin: LemonadeStand<P>) {

    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(Instant::class.java, InstantAdapter())
        .create()

    private val app: Javalin = Javalin.create { config ->
        config.showJavalinBanner = false
    }

    init {
        val token = plugin.config().node(VERIFICATION_TOKEN_CONFIG_PATH).string
        val isDevelopment = token.isNullOrBlank()
        if (isDevelopment) {
            plugin.logger().warn("LemonadeStand has been started without a webhook verification token. It is highly advised to set one in the config.yml file.")
        }

        app.post("/webhook") { ctx: Context ->
            // The Ko-Fi webhook sends the data as an urlencoded string
            val jsonBody = ctx.formParam("data")
            if (isDevelopment) plugin.logger().info("Received webhook: $jsonBody")

            // Parse the webhook
            val shopOrder = gson.fromJson(jsonBody, ShopOrder::class.java)
            if (shopOrder == null) {
                plugin.logger().warn("Failed to parse webhook")
                ctx.status(400) // Return http status 400 Bad Request (invalid json)
                return@post
            }

            // Verify the webhook
            if (!isDevelopment) {
                if (shopOrder.verificationToken != token) {
                    plugin.logger().warn("Invalid verification token")
                    ctx.status(401) // Return http status 401 Unauthorized (invalid token)
                    return@post
                }
            }

            var player: P?
            player = findPotentialUsername(shopOrder.fromName)

            // If we weren't able to resolve the "from name" as a player, then try the message
            if (player == null) {
                if (shopOrder.message == null) {
                    plugin.logger().warn("Missing message in payload: cannot fully process")
                    ctx.status(200) // Not an error, just not a player we can process
                    return@post
                }
                player = findPotentialUsername(shopOrder.message)
            }

            // If neither "from name" nor "message" contained a valid username, fail and return
            if (player == null) {
                plugin.logger().warn("Invalid username (not found)! from_name='${shopOrder.fromName}' message='${shopOrder.message}'")
                ctx.status(200) // Not an error, just not a player we can process
                return@post
            }

            // Dispatch event in the main thread
            plugin.scheduler().executeOnMain {
                plugin.transactionLogger()
                    .info("Processing order '${shopOrder.kofiTransactionId}' for " + plugin.getUsername(player) + ": ${shopOrder.amount} ${shopOrder.currency}")
                plugin.eventCaller().callTransactionEvent(player, shopOrder)
            }
            ctx.status(200) // return http status 200 OK
        }
    }

    private fun findPotentialUsername(str: String?): P? {
        if (str.isNullOrEmpty()) return null

        // Remove all prefixing spaces
        var message = str.replace("^\\s+".toRegex(), "")

        // Remove all characters after a potential space (indicating the end of the username)
        message = message.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]

        // Check if the potential username is >= 3 characters long and <= 16 characters long
        if (message.length < 3 || message.length > 16) {
            plugin.logger().warn("Invalid username (too short/long): $message")
            return null
        }

        // Validate the username character set (abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_)
        if (!message.matches("^[a-zA-Z0-9_]+$".toRegex())) {
            plugin.logger().warn("Invalid username (char. set): $message")
            return null
        }

        // Get the player from the cache
        return plugin.getOfflinePlayer(message)
    }

    fun start() {
        app.start(plugin.config().node("settings", "port").getInt(7000))
    }

    fun stop() {
        app.stop()
    }

    companion object {
        private val VERIFICATION_TOKEN_CONFIG_PATH = listOf("settings", "verification-token")
    }
}
