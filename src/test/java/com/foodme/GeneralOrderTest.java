package com.foodme;

import com.foodme.dto.DishDto;
import com.foodme.dto.RestaurantDto;
import com.foodme.enumeration.OrderStatus;
import com.foodme.model.OrderItem;
import com.foodme.util.*;
import org.junit.Test;

import java.util.List;

import static com.foodme.util.StreamUtils.streamOf;
import static com.foodme.util.TestUtil.assertResponseOk;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GeneralOrderTest extends AbstractIntegrationTest {

    @Test
    public void givenUser_whenFillOrder_thenCreatedForSuccessfulOrderCreation() {
        final SignUpResult auth = TestUtil.createAccountAndSignIn();

        // STEP 1. Choose a Restaurant
        RestaurantDto restaurant = RestaurantTestHelper.generate(auth).entity();

        // STEP 2. Choose a Dish
        final DishDto dish = restaurant.getMenus().get(0).getMenuSections().get(0).getDishes().get(0);

        // STEP 3. Create new Order
        OrderTestHelper orderHelper = OrderTestHelper.generate(auth);
        assertResponseOk(orderHelper.orderResponse);
        assertEquals(OrderStatus.CREATED, orderHelper.entity().getStatus());

        // STEP 4. Add Dish to Order
        orderHelper.addDish(dish.getId());
        assertResponseOk(orderHelper.dishResponse);

        // check get dishes
        List<DishDto> dishes = orderHelper.getDishes();
        assertResponseOk(orderHelper.dishesResponse);
        assertEquals(1, dishes.size());
        assertEquals(dish.getId(), dishes.get(0).getId());

        // check get order with dishes
        orderHelper = orderHelper.get();
        assertResponseOk(orderHelper.orderResponse);
        assertEquals(dish.getId(), orderHelper.entity().getOrderItems().iterator().next()
                .getOrderedDishes().iterator().next().getId());

        // STEP 5. Update Order
        // fixme: face a problem in com.foodme.controller.OrderController.update()
        /*final GeneralOrder order = orderHelper.entity();
        final Date pickUpDate = Date.from(LocalDate.now().plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC));
        order.setPickUpDate(pickUpDate);
        order.setPickUpAddress("New Address");

        orderHelper = orderHelper.update(order);
        assertResponseOk(orderHelper.orderResponse);
        assertEquals(pickUpDate, orderHelper.entity().getPickUpDate());
        assertEquals("New Address", orderHelper.entity().getPickUpAddress());
        assertEquals(dish.getId(), orderHelper.entity().getOrderItems().iterator().next()
                .getOrderedDishes().iterator().next().getId());*/

        // STEP 6. Remove Dish from Order
        orderHelper.removeDish(dish.getId());
        assertResponseOk(orderHelper.dishResponse);

        dishes = orderHelper.getDishes();
        assertResponseOk(orderHelper.dishesResponse);
        assertTrue(dishes.isEmpty());

        // check get order without dishes
        orderHelper = orderHelper.get();
        assertResponseOk(orderHelper.orderResponse);
        assertTrue(streamOf(orderHelper.entity().getOrderItems()).toFlatList(OrderItem::getOrderedDishes).isEmpty());

        // STEP 7. Dispatch Order
        orderHelper.dispatch();
        assertResponseOk(orderHelper.dispatchResponse);

        orderHelper.get();
        assertEquals(OrderStatus.DISPATCHED, orderHelper.entity().getStatus());
    }

}
