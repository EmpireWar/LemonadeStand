package org.empirewar.lemonadestand.kofi;

import com.google.gson.annotations.SerializedName;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public final class ShopOrder {

	@SerializedName("verification_token")
	private UUID verificationToken;

	@SerializedName("message_id")
	private UUID messageId;

	@SerializedName("timestamp")
	private Instant timestamp;

	@SerializedName("type")
	private String type;

	@SerializedName("is_public")
	private boolean isPublic;

	@SerializedName("from_name")
	private String fromName;

	@SerializedName("message")
	private String message;

	@SerializedName("amount")
	private double amount;

	@SerializedName("url")
	private String url;

	@SerializedName("email")
	private String email;

	@SerializedName("currency")
	private String currency;

	@SerializedName("is_subscription_payment")
	private boolean isSubscriptionPayment;

	@SerializedName("is_first_subscription_payment")
	private boolean isFirstSubscriptionPayment;

	@SerializedName("kofi_transaction_id")
	private UUID kofiTransactionId;

	@SerializedName("shop_items")
	private List<ShopItem> shopItems;

	@SerializedName("tier_name")
	private String tierName;

	@SerializedName("shipping")
	private Shipping shipping;

	public UUID getVerificationToken() {
		return verificationToken;
	}

	public UUID getMessageId() {
		return messageId;
	}

	public Instant getTimestamp() {
		return timestamp;
	}

	public String getType() {
		return type;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public String getFromName() {
		return fromName;
	}

	public String getMessage() {
		return message;
	}

	public double getAmount() {
		return amount;
	}

	public String getUrl() {
		return url;
	}

	public String getEmail() {
		return email;
	}

	public String getCurrency() {
		return currency;
	}

	public boolean isSubscriptionPayment() {
		return isSubscriptionPayment;
	}

	public boolean isFirstSubscriptionPayment() {
		return isFirstSubscriptionPayment;
	}

	public UUID getKofiTransactionId() {
		return kofiTransactionId;
	}

	public List<ShopItem> getShopItems() {
		return shopItems;
	}

	public String getTierName() {
		return tierName;
	}

	public Shipping getShipping() {
		return shipping;
	}

}