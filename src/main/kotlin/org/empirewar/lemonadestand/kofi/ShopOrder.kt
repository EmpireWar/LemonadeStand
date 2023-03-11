package org.empirewar.lemonadestand.kofi

import com.google.gson.annotations.SerializedName
import java.time.Instant
import java.util.UUID

class ShopOrder {

    @SerializedName("verification_token")
    val verificationToken: String? = null

    @SerializedName("message_id")
    val messageId: UUID? = null

    @SerializedName("timestamp")
    val timestamp: Instant? = null

    @SerializedName("type")
    val type: String? = null

    @SerializedName("is_public")
    val isPublic = false

    @SerializedName("from_name")
    val fromName: String? = null

    @SerializedName("message")
    val message: String? = null

    @SerializedName("amount")
    val amount = 0.0

    @SerializedName("url")
    val url: String? = null

    @SerializedName("email")
    val email: String? = null

    @SerializedName("currency")
    val currency: String? = null

    @SerializedName("is_subscription_payment")
    val isSubscriptionPayment = false

    @SerializedName("is_first_subscription_payment")
    val isFirstSubscriptionPayment = false

    @SerializedName("kofi_transaction_id")
    val kofiTransactionId: UUID? = null

    @SerializedName("shop_items")
    val shopItems: List<ShopItem>? = null

    @SerializedName("tier_name")
    val tierName: String? = null

    @SerializedName("shipping")
    val shipping: Shipping? = null
}