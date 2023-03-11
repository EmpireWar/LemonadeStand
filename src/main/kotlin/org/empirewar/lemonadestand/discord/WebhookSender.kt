package org.empirewar.lemonadestand.discord

import club.minnced.discord.webhook.WebhookClient
import club.minnced.discord.webhook.WebhookClientBuilder
import club.minnced.discord.webhook.receive.ReadonlyMessage
import club.minnced.discord.webhook.send.WebhookEmbed.EmbedField
import club.minnced.discord.webhook.send.WebhookEmbed.EmbedTitle
import club.minnced.discord.webhook.send.WebhookEmbedBuilder
import org.empirewar.lemonadestand.LemonadeStand
import org.empirewar.lemonadestand.event.KoFiTransactionEvent
import java.net.MalformedURLException
import java.util.concurrent.Executors

class WebhookSender(plugin: LemonadeStand) {

    private val client: WebhookClient

    init {
        val configuredWebhook = plugin.config.getString(WEBHOOK_URL_CONFIG_PATH)
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

    fun sendWebhook(event: KoFiTransactionEvent) {
        val player = event.player
        val order = event.shopOrder

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
                    player.name + " donated " + order.amount + " " + order.currency + "!",
                    "https://ko-fi.com/empirewar"
                )
            )
            .setThumbnailUrl("https://mc-heads.net/avatar/" + player.uniqueId)
            .build()

        client.send(embed).thenAccept { message: ReadonlyMessage ->
            LemonadeStand.get().getLogger().info("Message with embed has been sent [" + message.id + "]")
        }
    }

    companion object {
        private const val WEBHOOK_URL_CONFIG_PATH = "settings.webhook-url"
    }
}