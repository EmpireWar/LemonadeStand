package org.empirewar.lemonadestand.discord;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import org.bukkit.OfflinePlayer;
import org.empirewar.lemonadestand.LemonadeStand;
import org.empirewar.lemonadestand.event.KoFiTransactionEvent;
import org.empirewar.lemonadestand.kofi.ShopOrder;

import java.net.MalformedURLException;
import java.util.concurrent.Executors;

public class WebhookSender {

    private static final String WEBHOOK_URL_CONFIG_PATH = "settings.webhook-url";

    private final WebhookClient client;

    public WebhookSender(LemonadeStand plugin) throws MalformedURLException {
        String configuredWebhook = plugin.getConfig().getString(WEBHOOK_URL_CONFIG_PATH);
        // Make sure the URL is not blank and uses https
        if (configuredWebhook == null
                || configuredWebhook.isBlank()
                || !configuredWebhook.startsWith("https")) {
            throw new MalformedURLException("Webhook URL is empty or does not use HTTPS");
        }

        // Using the builder
        WebhookClientBuilder builder = new WebhookClientBuilder(configuredWebhook);
        builder.setThreadFactory(Executors.defaultThreadFactory());
        builder.setWait(true);

        this.client = builder.build();
    }

    public void stop() {
        client.close();
    }

    public void sendWebhook(KoFiTransactionEvent event) {
        final OfflinePlayer player = event.getPlayer();
        final ShopOrder order = event.getShopOrder();

        StringBuilder descriptionBuilder = new StringBuilder();
        if (order.isSubscriptionPayment()) {
            descriptionBuilder.append("This donation is a subscription.");
        }

        if (order.isFirstSubscriptionPayment()) {
            descriptionBuilder.append(" This is their first subscription payment.");
            descriptionBuilder.append("\n");
        }

        if (order.getTierName() != null) {
            descriptionBuilder.append("\n");
            descriptionBuilder.append("Tier: ").append(order.getTierName());
        }

        // Send and log (using embed)
        WebhookEmbed embed = new WebhookEmbedBuilder()
                .setColor(12947200)
                .addField(new WebhookEmbed.EmbedField(true, "Message", order.getMessage() == null ? "" : order.getMessage()))
                .setDescription(descriptionBuilder.toString())
                .setTimestamp(order.getTimestamp())
                .setTitle(new WebhookEmbed.EmbedTitle(player.getName() + " donated " + order.getAmount() + " " + order.getCurrency() + "!", "https://ko-fi.com/empirewar"))
                .setThumbnailUrl("https://mc-heads.net/avatar/" + player.getUniqueId())
                .build();

        client.send(embed).thenAccept((message) -> LemonadeStand.get().getLogger().info("Message with embed has been sent [" + message.getId() + "]"));
    }

}