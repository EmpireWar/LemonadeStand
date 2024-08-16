package org.empirewar.lemonadestand.discord

import club.minnced.discord.webhook.WebhookClient
import club.minnced.discord.webhook.WebhookClientBuilder
import club.minnced.discord.webhook.receive.ReadonlyMessage
import club.minnced.discord.webhook.send.WebhookEmbed.EmbedField
import club.minnced.discord.webhook.send.WebhookEmbed.EmbedTitle
import club.minnced.discord.webhook.send.WebhookEmbedBuilder
import org.empirewar.lemonadestand.LemonadeStand
import org.empirewar.lemonadestand.kofi.ShopOrder
import java.net.MalformedURLException
import java.util.UUID
import java.util.concurrent.Executors

class WebhookSender(private val plugin: LemonadeStand<*>) {

    private val client: WebhookClient

    init {
        val configuredWebhook = plugin.config().node(WEBHOOK_URL_CONFIG_PATH).getString("")
        // Make sure the URL is not blank and uses https
        if (configuredWebhook.isNullOrBlank()
            || !configuredWebhook.startsWith("https")
        ) {
            throw MalformedURLException("Webhook URL is empty or does not use HTTPS")
        }

        // Using the builder
        val builder = WebhookClientBuilder(configuredWebhook)
        builder.setThreadFactory(Executors.defaultThreadFactory())
        builder.setWait(true)
        client = builder.build()
    }

    fun stop() {
        client.close()
    }

    fun sendEmbed(playerName: String, playerId: UUID, order: ShopOrder) {
        val descriptionBuilder = StringBuilder()
        if (order.isSubscriptionPayment) {
            descriptionBuilder.append("This donation is a subscription.")
        }

        if (order.isFirstSubscriptionPayment) {
            descriptionBuilder.append(" This is their first subscription payment.")
            descriptionBuilder.append("\n")
        }

        if (order.tierName != null) {
            descriptionBuilder.append("\n")
            descriptionBuilder.append("Tier: ").append(order.tierName)
        }

        // Send and log (using embed)
        val embed = WebhookEmbedBuilder()
            .setColor(12947200)
            .addField(EmbedField(true, "Message", order.message ?: ""))
            .setDescription(descriptionBuilder.toString())
            .setTimestamp(order.timestamp)
            .setTitle(
                EmbedTitle(
                    "$playerName donated ${order.amount} ${order.currency}!",
                    plugin.config().node("settings", "kofi-url").getString("")
                )
            )
            .setThumbnailUrl("https://mc-heads.net/avatar/$playerId")
            .build()

        client.send(embed).thenAccept { message: ReadonlyMessage ->
            plugin.logger().info("Message with embed has been sent [${message.id}]")
        }
    }

    companion object {
        private val WEBHOOK_URL_CONFIG_PATH = listOf("settings", "webhook-url")
    }
}
