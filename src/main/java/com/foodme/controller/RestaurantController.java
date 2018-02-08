package com.foodme.controller;

import com.foodme.dto.MenuDto;
import com.foodme.dto.RestaurantDto;
import com.foodme.model.Dish;
import com.foodme.model.Menu;
import com.foodme.model.MenuSection;
import com.foodme.model.Restaurant;
import com.foodme.service.RestaurantService;
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
import java.util.List;

import static com.foodme.util.StreamUtils.streamOf;
import static java.util.Collections.singletonList;

@RestController
@Slf4j
class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private ModelMapper modelMapper;

    @RequestMapping(value = "account/{accId}/restaurant", method = RequestMethod.POST)
    public ResponseEntity<Void> createRestaurant(@PathVariable("accId") Long accId,
                                                 @RequestBody RestaurantDto restaurantDto,
                                                 UriComponentsBuilder ucBuilder) {
        log.debug("Creating Restaurant with name \"{}\" for Account#{}", restaurantDto.getName(), accId);

        Restaurant restaurant = modelMapper.map(restaurantDto, Restaurant.class);

        if (restaurantService.isRestaurantExist(restaurant)) {
            log.info("Restaurant already exists: {}", restaurant);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        fixRestaurantRelationships(restaurant);

        if (restaurantService.save(restaurant, accId) == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/restaurant/{id}").buildAndExpand(restaurant.getId()).toUri());
        headers.setAccessControlExposeHeaders(singletonList("Location"));
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    private void fixRestaurantRelationships(Restaurant restaurant) {
        streamOf(restaurant.getMenus()).forEach(menu -> {
            menu.setRestaurant(restaurant);
            streamOf(menu.getMenuSections()).forEach(menuSection -> {
                menuSection.setMenu(menu);
                streamOf(menuSection.getDishes()).forEach(dish -> dish.setMenuSection(menuSection));
            });
        });
    }

    @RequestMapping(value = "/restaurant/{id}", method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestaurantDto> updateRestaurant(@PathVariable("id") Long id,
                                                          @RequestBody RestaurantDto restaurant) {
        log.debug("Updating Restaurant with ID#{}", id);

        if (!restaurantService.isRestaurantExist(id)) {
            log.info("Restaurant with id#{} not found", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Restaurant restaurantEntity = modelMapper.map(restaurant, Restaurant.class);
        fixRestaurantRelationships(restaurantEntity);

        restaurantEntity = restaurantService.save(restaurantEntity);
        return new ResponseEntity<>(restaurant, HttpStatus.OK);
    }

    @RequestMapping(value = "/restaurant/{id}", method = RequestMethod.DELETE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable("id") Long id) {
        log.debug("Fetching & Deleting Restaurant with id#{}", id);

        if (!restaurantService.isRestaurantExist(id)) {
            log.info("Unable to delete. Restaurant with id#{} not found", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        restaurantService.deleteRestaurantById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/restaurant/{id}",
            method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestaurantDto> getRestaurant(@PathVariable("id") Long id, UriComponentsBuilder ucBuilder) {
        log.debug("Fetching Restaurant with id#{}", id);

        Restaurant restaurant = restaurantService.findById(id);
        if (restaurant == null) {
            log.info("Unable to fetch Restaurant with id#{}: not found", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/restaurant/{id}").buildAndExpand(restaurant.getId()).toUri());
        headers.setAccessControlExposeHeaders(singletonList("Location"));
        return new ResponseEntity<>(
                modelMapper.map(restaurant, RestaurantDto.class), headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/restaurant/{id}/menus",
            method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MenuDto>> getMenusByRestaurant(@PathVariable("id") Long id) {
        log.debug("Fetching all menus for the Restaurant with id#{}", id);

        if (!restaurantService.isRestaurantExist(id)) {
            log.info("Unable to fetch Restaurant with id#{}: not found", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<Menu> results = restaurantService.findAllMenusByRestaurant(id);

        if (results == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        Type targetListType = new TypeToken<List<MenuDto>>() {
        }.getType();
        List<MenuDto> convertedResult = modelMapper.map(results, targetListType);

        log.debug("Menus found for the restaurant#{}: {}", id, results.size());

        return new ResponseEntity<>(convertedResult, HttpStatus.OK);
    }

    @RequestMapping(value = "/restaurant/find",
            method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<RestaurantDto>> findRestaurants(@RequestParam("country") String country,
                                                               @RequestParam("city") String city,
                                                               @RequestParam("latitude") double latitude,
                                                               @RequestParam("longitude") double longitude,
                                                               @RequestParam("range") double range) {

        log.debug("Trying to look for restaurants in {},{} within the range of {} from point {} : {}",
                city, country, range, latitude, longitude);

        List<Restaurant> results =
                restaurantService.findRestaurantsWithinRange(country, city, latitude, longitude, range);

        if (results == null) {
            return new ResponseEntity<List<RestaurantDto>>(HttpStatus.NO_CONTENT);
        }

        Type targetListType = new TypeToken<List<RestaurantDto>>() {
        }.getType();
        List<RestaurantDto> convertedResult = modelMapper.map(results, targetListType);

        log.debug("Restaurants found for {}, {}: {}", country, city, results.size());

        return new ResponseEntity<List<RestaurantDto>>(convertedResult, HttpStatus.OK);
    }
}
