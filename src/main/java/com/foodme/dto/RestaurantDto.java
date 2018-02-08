package com.foodme.dto;

import com.foodme.model.GeoLocation;
import com.foodme.model.OpeningHours;
import com.foodme.enumeration.AddressOption;
import lombok.Data;

import java.util.List;

@Data
public class RestaurantDto {
    private Long id;

    private String name;

    private String description;

    private String restaurantLogoUrl;

    private String country;

    private String city;

    private String address;

    private String postalCode;

    private String phoneNumber;

    private String faxNumber;

    private String email;

    private String officialSite;

    private List<AccountDto> staff;

    private List<MenuDto> menus;

    private GeoLocation geoLocation;

    private String addressDescription;

    private AddressOption selectedAddressOption;

    private OpeningHours openingHours;

    private String language;

    private String currency;
}
