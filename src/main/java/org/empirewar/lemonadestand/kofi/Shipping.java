package org.empirewar.lemonadestand.kofi;

import com.google.gson.annotations.SerializedName;

public final class Shipping {

	@SerializedName("full_name")
	private String fullName;

	@SerializedName("street_address")
	private String streetAddress;

	@SerializedName("city")
	private String city;

	@SerializedName("state_or_province")
	private String stateOrProvince;

	@SerializedName("postal_code")
	private String postalCode;

	@SerializedName("country")
	private String country;

	@SerializedName("country_code")
	private String countryCode;

	@SerializedName("telephone")
	private String telephone;

	public String getFullName() {
		return fullName;
	}

	public String getStreetAddress() {
		return streetAddress;
	}

	public String getCity() {
		return city;
	}

	public String getStateOrProvince() {
		return stateOrProvince;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public String getCountry() {
		return country;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public String getTelephone() {
		return telephone;
	}

}