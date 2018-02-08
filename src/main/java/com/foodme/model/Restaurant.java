package com.foodme.model;

import com.foodme.enumeration.AddressOption;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(indexes = {@Index(name = "idx_country_city",  columnList="country, city", unique = false)})
public class Restaurant extends BaseAuditEntity {
    @Length(max = 50)
    private String name;

    private String description;

    @Column(name = "restaurant_logo_url")
    private String restaurantLogoUrl;

    private String country;

    private String city;

    private String address;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "fax_number")
    private String faxNumber;

    private String email;

    @Column(name = "official_site")
    private String officialSite;

    @Embedded
    private GeoLocation geoLocation;

    @Column(name = "address_description")
    private String addressDescription;

    @Column(name = "selected_address_option")
    private AddressOption selectedAddressOption;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    private OpeningHours openingHours;

    private String language;

    private String currency;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "restaurant_staff")
    private Set<Account> staff = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY,
            mappedBy = "restaurant", orphanRemoval = true)
    private List<Menu> menus = new ArrayList<>();


    private Restaurant() {
    }

    public Restaurant(String restaurantName) {
        this.name = restaurantName;
    }

    public void setMenus(List<Menu> menus) {
        if(menus == null) {
            return;
        }
        if(this.menus == null) {
            this.menus = menus;
        } else {
            this.menus.clear();
            this.menus.addAll(menus);
        }
    }

    public void deleteMenu(Menu menu) {
        for(Menu tmpMenu : this.getMenus()) {
            if(tmpMenu.equals(menu)) {
                this.getMenus().remove(menu);
                break;
            }
        }
    }
}
