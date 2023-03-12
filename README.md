# LemonadeStand
A Minecraft plugin API that integrates with the [Ko-Fi](https://ko-fi.com/) API to allow other plugins to listen to donations.

## Setting up
The webserver by default starts on port 7000.

LemonadeStand will initially load in *development mode*. You can use this for testing (see the 'Validating the plugin works' section). 

For a production environment, you need to provide a valid Ko-Fi verification token.

### Getting a verification token
Go to your **Ko-Fi admin area** and go into the **API section**.

Go to the **webhooks** section and click the **advanced** dropdown. Here you can generate a verification token.

Copy the verification token and put it into the `verification-token` config setting. Restart your server.

#### Setting up the Ko-Fi webhook
In the same section on the Ko-Fi admin area, you'll want to set `Webhook URL` to the webhook endpoint for the plugin, which is `/webhook`.

e.g `127.0.0.1:7000/webhook`.

### Setting up a Discord webhook
If you choose, you can get alerts for donations via a Discord webhook.

To do this, go to your Discord server, hover over a channel, and click on the `Edit Channel` button.

Click on either `Create Webhook` (if the channel has no existing webhooks) or `View Webhooks -> New Webhook`, if it has existing webhooks.

Select the new webhook and click `Copy Webhook URL`. Paste this URL into the `webhook-url` config setting. Restart your server.

### Integrating into the API
You will need to integrate into the LemonadeStand API to give out rewards to your players.

You can use our existing plugin [LemonSqueezer](https://github.com/EmpireWar/LemonSqueezer).

If you opt to create your own plugin, follow the information below.

#### Gradle (kts)
```kotlin
repositories {
    maven("https://repo.convallyria.com/snapshots")
}

dependencies {
    compileOnly("org.empirewar.lemonadestand:LemonadeStand:1.0.0-SNAPSHOT")
}
```

#### Events
Listen to the `KoFiTransactionEvent`.

#### Logging
It is recommended to log rewards to the LemonadeStand transaction logger so that you may easily check if someone received their rewards.

To do this, use `LemonadeStand.get().getTransactionLogger()`.

### Validating the plugin works
We recommend you use [Insomnia](https://insomnia.rest/download) for testing.

Create a new `HTTP Request`, set it to use `Post`, and the URL as the plugin webhook endpoint.

Set the body to `JSON` and paste the following example into it:
```json lines
data = {
    "amount": "20.00",
    "currency": "EUR",
    "email": "jo.example@example.com",
    "from_name": "Notch",
    "is_first_subscription_payment": true,
    "is_public": true,
    "is_subscription_payment": true,
    "kofi_transaction_id": "00000000-1111-2222-3333-444444444444",
    "message": "Notch is based",
    "message_id": "5729c351-d36e-4eaf-b9aa-5b4257ab9e49",
    "shipping": null,
    "shop_items": [],
    "tier_name": "Mojang",
    "timestamp": "2023-02-26T20:43:59Z",
    "type": "Subscription",
    "url": "https://ko-fi.com/Home/CoffeeShop?txid=00000000-1111-2222-3333-444444444444",
    "verification_token": "1862a9e4-b1d8-4c6f-9f45-fd2dbfc2d8e1"
}
```

Then change the `JSON` to `Form URL Encoded`. Make sure in the field key `data` there are no preceding or trailing spaces.

Click send.

You should see something like `[LemonadeStand] Invalid username (not found)! from_name='Notch' message='Notch is based'` in your console.