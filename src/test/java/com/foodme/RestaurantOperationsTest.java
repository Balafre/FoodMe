package com.foodme;

import com.foodme.dto.RestaurantDto;
import com.foodme.model.DailyInterval;
import com.foodme.util.AbstractIntegrationTest;
import com.foodme.util.RestaurantTestHelper;
import com.foodme.util.SignUpResult;
import com.foodme.util.TestUtil;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import static com.foodme.util.RestaurantTestHelper.generate;
import static com.foodme.util.RestaurantTestHelper.getByPath;
import static org.junit.Assert.*;

public class RestaurantOperationsTest extends AbstractIntegrationTest {
    private static SignUpResult signUpResult;


    @BeforeClass
    public static void createAccountAndSignIn() {
        signUpResult = TestUtil.createAccountAndSignIn();
    }

    @Test
    public void givenRestaurant_whenUseFoodmeClient_thenCreatedForSuccessfulRestaurantCreation() {
        RestaurantTestHelper restHelper = generate(signUpResult);

        final String restaurantGetPath = restHelper.path();
        assertEquals(HttpStatus.CREATED.value(), restHelper.response().getStatusCode());
        assertTrue(restaurantGetPath.matches(TestUtil.RESTAURANT_PATH_RETURNED_REGEXP));

        final RestaurantDto restaurant = getByPath(signUpResult, restaurantGetPath).entity();

        assertNotNull(restaurant.getMenus());
        assertEquals(1, restaurant.getMenus().size());
        assertEquals(2, restaurant.getMenus().get(0).getMenuSections().size());
        assertEquals(2, restaurant.getMenus().get(0).getMenuSections().get(0).getDishes().size());
        assertEquals(6, restaurant.getOpeningHours().getDailyScheduleList().size());
    }

    @Test
    public void givenRestaurant_whenUseFoodmeClient_thenOkForSuccessfulRestaurantUpdate() {
        RestaurantTestHelper restHelper = generate(signUpResult);

        final String restaurantGetPath = restHelper.path();

        restHelper = getByPath(signUpResult, restaurantGetPath);

        RestaurantDto restaurant = restHelper.entity();

        restaurant.setCity("SomeCity");
        restaurant.getOpeningHours().setOpenedDaily(new DailyInterval("08:00", "20:00"));

        restHelper.update(restaurant);

        assertEquals(HttpStatus.OK.value(), restHelper.response().getStatusCode());

        restHelper = getByPath(signUpResult, restaurantGetPath);

        restaurant = restHelper.entity();

        assertEquals("SomeCity", restaurant.getCity());
        assertEquals("08:00", restaurant.getOpeningHours().getOpenedDaily().getFromTime());
        assertEquals("20:00", restaurant.getOpeningHours().getOpenedDaily().getTillTime());
    }
}


