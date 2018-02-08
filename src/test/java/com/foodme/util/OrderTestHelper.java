package com.foodme.util;

import com.foodme.dto.DishDto;
import com.foodme.dto.GeneralOrderDto;
import com.foodme.model.GeneralOrder;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.internal.mapper.ObjectMapperType;
import com.jayway.restassured.response.Response;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static java.time.Instant.now;

public class OrderTestHelper {
    private static ModelMapper modelMapper = new ModelMapper();
    private final SignUpResult auth;
    public Response orderResponse;
    private static final String ORDER_PATH = "http://localhost:9999/order";
    private final GeneralOrder order;
    public Response dishResponse;
    public Response dishesResponse;
    public Response dispatchResponse;

    private OrderTestHelper(SignUpResult auth, Response orderResponse) {
        this.auth = auth;
        this.orderResponse = orderResponse;
        order = orderResponse.as(GeneralOrder.class);
    }

    public static OrderTestHelper generate(SignUpResult auth) {
        GeneralOrder order = new GeneralOrder();
        order.setPickUpAddress("Brooklyn, 601 Surf Ave #12");
        order.setPickUpDate(Date.from(now()));

        Response orderResponse = RestAssured.given()
                .header("Authorization", "Bearer " + auth.getAccessToken())
                .header("Content-Type", "application/json")
                .body(modelMapper.map(order, GeneralOrderDto.class), ObjectMapperType.JACKSON_1)
                .post(ORDER_PATH);

        orderResponse = RestAssured.given()
                .header("Authorization", "Bearer " + auth.getAccessToken())
                .get(orderResponse.getHeader("Location"));

        return new OrderTestHelper(auth, orderResponse);
    }

    public OrderTestHelper get() {
        orderResponse = RestAssured.given()
                .header("Authorization", "Bearer " + auth.getAccessToken())
                .get(path());
        return this;
    }

    public String path() {
        return orderResponse.getHeader("Location");
    }

    public OrderTestHelper addDish(Long dishId) {
        dishResponse = RestAssured.given()
                .header("Authorization", "Bearer " + auth.getAccessToken())
                .post(ORDER_PATH + "/" + order.getId() + "/add-dish/" + dishId);
        return this;
    }

    public OrderTestHelper removeDish(Long dishId) {
        dishResponse = RestAssured.given()
                .header("Authorization", "Bearer " + auth.getAccessToken())
                .post(ORDER_PATH + "/" + order.getId() + "/remove-dish/" + dishId);
        return this;
    }

    public static OrderTestHelper get(SignUpResult auth, Long id) {
        Response orderResponse = RestAssured.given()
                .header("Authorization", "Bearer " + auth.getAccessToken())
                .get(ORDER_PATH + "/" + id);
        return new OrderTestHelper(auth, orderResponse);
    }

    public GeneralOrder entity() {
        return orderResponse.as(GeneralOrder.class);
    }

    public List<DishDto> getDishes() {
        dishesResponse = RestAssured.given()
                .header("Authorization", "Bearer " + auth.getAccessToken())
                .get(path() + "/dishes");
        return Arrays.asList(dishesResponse.as(DishDto[].class));
    }

    public OrderTestHelper dispatch() {
        dispatchResponse = RestAssured.given()
                .header("Authorization", "Bearer " + auth.getAccessToken())
                .post(path() + "/dispatch");
        return this;
    }

    public OrderTestHelper update(GeneralOrder order) {
        orderResponse = RestAssured.given()
                .header("Authorization", "Bearer " + auth.getAccessToken())
                .header("Content-Type", "application/json")
                .body(modelMapper.map(order, GeneralOrderDto.class), ObjectMapperType.JACKSON_1)
                .put(path());
        return null;
    }
}
