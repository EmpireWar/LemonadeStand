package org.empirewar.lemonadestand.kofi;

import com.google.gson.annotations.SerializedName;

public final class ShopItem {

	@SerializedName("direct_link_code")
	private String directLinkCode;

	@SerializedName("variation_name")
	private String variationName;

	@SerializedName("quantity")
	private int quantity;

	public String getDirectLinkCode() {
		return directLinkCode;
	}

	public String getVariationName() {
		return variationName;
	}

	public int getQuantity() {
		return quantity;
	}

}
