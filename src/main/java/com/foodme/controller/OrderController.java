package com.foodme.controller;

import com.foodme.annotation.CurrentAccount;
import com.foodme.dto.DishDto;
import com.foodme.dto.GeneralOrderDto;
import com.foodme.dto.RestaurantDto;
import com.foodme.model.*;
import com.foodme.repository.DishRepository;
import com.foodme.service.OrderService;
import com.foodme.enumeration.OrderStatus;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.Type;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static com.foodme.util.StreamUtils.streamOf;
import static java.util.Collections.singletonList;

@RestController
@Slf4j
@RequestMapping("order")
class OrderController {

    private final OrderService orderService;
    private final DishRepository dishRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public OrderController(ModelMapper modelMapper, OrderService orderService, DishRepository dishRepository) {
        this.modelMapper = modelMapper;
        this.orderService = orderService;
        this.dishRepository = dishRepository;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Void> create(@RequestBody GeneralOrderDto orderDto,
                                       UriComponentsBuilder ucBuilder,
                                       @CurrentAccount Account account) {
        log.debug("Creating general order to address {} for Account#{}", orderDto.getPickUpAddress(), account.getId());

        GeneralOrder order = modelMapper.map(orderDto, GeneralOrder.class);
        order.setStatus(OrderStatus.CREATED);
        streamOf(order.getOrderItems()).forEach(item -> item.setOrder(order));

        if (orderService.save(order, account) == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/order/{id}").buildAndExpand(order.getId()).toUri());
        headers.setAccessControlExposeHeaders(singletonList("Location"));
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GeneralOrderDto> update(@PathVariable("id") Long id,
                                                  UriComponentsBuilder ucBuilder,
                                                  @RequestBody GeneralOrderDto orderDto) {
        log.debug("Updating Order with ID#{}", id);

        if (!orderService.exists(id)) {
            log.info("Order with id#{} not found", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (!id.equals(orderDto.getId())) {
            log.info("Order id#{} does not correspond to url id", orderDto.getId(), id);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        GeneralOrder order = modelMapper.map(orderDto, GeneralOrder.class);

        streamOf(order.getOrderItems()).forEach(item -> item.setOrder(order));

        orderService.save(order);// fixme: duplicate key value violates unique constraint "order_item_dishes_pkey"
                                 // Подробности: Key (order_item_id, dish_id)=(1, 1) already exists.

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/order/{id}").buildAndExpand(order.getId()).toUri());
        headers.setAccessControlExposeHeaders(singletonList("Location"));
        return new ResponseEntity<>(modelMapper.map(order, GeneralOrderDto.class), headers, HttpStatus.OK);
    }

    @RequestMapping(value = "{orderId}/add-dish/{dishId}", method = RequestMethod.POST)
    public ResponseEntity<RestaurantDto> addDish(@PathVariable Long orderId,
                                                 @PathVariable Long dishId) {
        log.debug("Adding Dish with ID#{} to Order with ID#{}", dishId, orderId);

        if (!dishRepository.exists(dishId)) {
            log.info("Dish with id#{} not found", dishId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        final Dish dish = dishRepository.findOne(dishId);
        final Optional<GeneralOrder> orderOpt = orderService.get(orderId);
        if (!orderOpt.isPresent()) {
            log.info("Order with id#{} not found", orderId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        final GeneralOrder order = orderOpt.get();
        final Restaurant dishRestaurant = dish.getMenuSection().getMenu().getRestaurant();

        final Optional<OrderItem> orderItemOptional = streamOf(order.getOrderItems())
                .filter(item -> Objects.equals(item.getRestaurant().getId(), dishRestaurant.getId()))
                .findFirst();

        if (orderItemOptional.isPresent()) {
            orderItemOptional.get().getOrderedDishes().add(dish);
        } else {
            final OrderItem orderItem = OrderItem.builder()
                    .restaurant(dishRestaurant)
                    .order(order)
                    .dishes(new HashSet<>(singletonList(dish)))
                    .build();
            order.getOrderItems().add(orderItem);
        }

        orderService.save(order);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "{orderId}/remove-dish/{dishId}", method = RequestMethod.POST)
    public ResponseEntity<RestaurantDto> removeDish(@PathVariable Long orderId,
                                                    @PathVariable Long dishId) {
        log.debug("Removing Dish with ID#{} to Order with ID#{}", dishId, orderId);

        if (!dishRepository.exists(dishId)) {
            log.info("Dish with id#{} not found", dishId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        final Optional<GeneralOrder> orderOpt = orderService.get(orderId);
        if (!orderOpt.isPresent()) {
            log.info("Order with id#{} not found", orderId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        streamOf(orderOpt.get().getOrderItems())
                .flatMap(item -> streamOf(item.getOrderedDishes()).map(dish -> new SimpleImmutableEntry<>(item, dish)))
                .filter(e -> e.getValue().getId().equals(dishId))
                .findFirst()
                .ifPresent(e -> e.getKey().getOrderedDishes().remove(e.getValue()));

        orderService.save(orderOpt.get());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        log.debug("Fetching & Deleting Order with id#{}", id);

        if (!orderService.exists(id)) {
            log.info("Unable to delete. Order with id#{} not found", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        orderService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GeneralOrderDto> get(@PathVariable("id") Long id, UriComponentsBuilder ucBuilder) {
        log.debug("Fetching Order with id#{}", id);

        Optional<GeneralOrder> orderOpt = orderService.get(id);
        if (!orderOpt.isPresent()) {
            log.info("Unable to fetch Order with id#{}: not found", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        final GeneralOrder order = orderOpt.get();

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/order/{id}").buildAndExpand(order.getId()).toUri());
        headers.setAccessControlExposeHeaders(singletonList("Location"));
        return new ResponseEntity<>(modelMapper.map(order, GeneralOrderDto.class), headers, HttpStatus.OK);
    }

    @RequestMapping(value = "{id}/dishes", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Set<DishDto>> getDishes(@PathVariable("id") Long id) {
        log.debug("Fetching Dishes for Order with id#{}", id);

        Optional<GeneralOrder> orderOpt = orderService.get(id);
        if (!orderOpt.isPresent()) {
            log.info("Unable to fetch Order with id#{}: not found", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        final Set<Dish> dishes = streamOf(orderOpt.get().getOrderItems()).flatMap(item -> streamOf(item.getOrderedDishes())).toSet();
        Type listType = new TypeToken<Set<DishDto>>() {}.getType();
        final Set<DishDto> dtos = modelMapper.map(dishes, listType);
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @RequestMapping(value = "{id}/dispatch", method = RequestMethod.POST)
    public ResponseEntity<Void> dispatch(@PathVariable("id") Long id) {
        log.debug("Dispatch Order with id#{}", id);

        Optional<GeneralOrder> orderOpt = orderService.get(id);
        if (!orderOpt.isPresent()) {
            log.info("Unable to fetch Order with id#{}: not found", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        final GeneralOrder order = orderOpt.get();
        order.setStatus(OrderStatus.DISPATCHED);
        orderService.save(order);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
