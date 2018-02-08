package com.foodme.util;

import com.foodme.dto.RestaurantDto;
import com.foodme.model.*;
import com.foodme.enumeration.DayOfWeek;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.internal.mapper.ObjectMapperType;
import com.jayway.restassured.response.Response;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

public class RestaurantTestHelper {
    private static ModelMapper modelMapper = new ModelMapper();

    private SignUpResult auth;
    private Response restResponse;

    private RestaurantTestHelper(SignUpResult auth, Response restResponse) {
        this.auth = auth;
        this.restResponse = restResponse;
    }

    public static RestaurantTestHelper generate(SignUpResult auth) {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        final Restaurant restaurant = new Restaurant("Rest_" + uuid);

        final OpeningHours openingHours = new OpeningHours();
        openingHours.setOpen24_7(true);
        openingHours.setOpenedDaily(new DailyInterval("09:00", "23:00"));
        openingHours.setDailyScheduleList(Arrays.asList(
                new DailySchedule(DayOfWeek.MO, Arrays.asList(
                        new DailyInterval("09:00", "12:00"),
                        new DailyInterval("13:00", "22:00"))),
                new DailySchedule(DayOfWeek.TU, new DailyInterval("09:00", "23:00")),
                new DailySchedule(DayOfWeek.WD, new DailyInterval("09:00", "23:00")),
                new DailySchedule(DayOfWeek.TH, new DailyInterval("09:00", "23:00")),
                new DailySchedule(DayOfWeek.FR, new DailyInterval("09:00", "23:00")),
                new DailySchedule(DayOfWeek.SA, new DailyInterval("09:00", "20:00")))
        );

        final Menu menu1 = new Menu("Menu1", "Description 1");
        final MenuSection section1 = new MenuSection("Soups", "Menu section containing soups");
        final MenuSection section2 = new MenuSection("Side Dishes", "Menu section containing side dishes");
        final Dish dish1 = new Dish("French Onion Soup", "good French Onion Soup");
        final Dish dish2 = new Dish("Clam Chowder", "good Clam Chowder");
        final Dish dish3 = new Dish("Olivier salad", "good Olivier salad");
        final Dish dish4 = new Dish("Greek salad", "good Greek salad");

        section1.setDishes(Arrays.asList(dish1, dish2));
        section2.setDishes(Arrays.asList(dish3, dish4));
        menu1.setMenuSections(Arrays.asList(section1, section2));
        restaurant.setMenus(Collections.singletonList(menu1));
        restaurant.setOpeningHours(openingHours);

        final Response restResponse = RestAssured.given()
                .header("Authorization", "Bearer " + auth.getAccessToken())
                .header("Content-Type", "application/json")
                .body(modelMapper.map(restaurant, RestaurantDto.class), ObjectMapperType.JACKSON_1)
                .post("http://localhost:9999/account/" + auth.getAccountId() + "/restaurant");

        return new RestaurantTestHelper(auth, restResponse);
    }

    public String path() {
        return restResponse.getHeader("Location");
    }

    public static RestaurantTestHelper getByPath(SignUpResult auth, String path) {
        Response restResponse = RestAssured.given()
                .header("Authorization", "Bearer " + auth.getAccessToken())
                .get(path);
        return new RestaurantTestHelper(auth, restResponse);
    }

    public RestaurantDto entity() {
        if (restResponse.getContentType().isEmpty()) load();
        return restResponse.as(RestaurantDto.class);
    }

    public RestaurantTestHelper load() {
        restResponse = RestAssured.given()
                .header("Authorization", "Bearer " + auth.getAccessToken())
                .get(path());
        return this;
    }

    public RestaurantTestHelper update(RestaurantDto restaurant) {
        restResponse = RestAssured.given()
                .header("Authorization", "Bearer " + auth.getAccessToken())
                .header("Content-Type", "application/json")
                .body(restaurant, ObjectMapperType.JACKSON_1)
                .put(path());
        return this;
    }

    public Response response() {
        return restResponse;
    }

    public RestaurantMenuTestHelper getMenus() {
        final Response response = RestAssured.given()
                .header("Authorization", "Bearer " + auth.getAccessToken())
                .get(path() + "/menus");
        return new RestaurantMenuTestHelper(response);
    }

    public static class RestaurantMenuTestHelper {
        private Response response;

        private RestaurantMenuTestHelper(Response response) {
            this.response = response;
        }
    }
}
