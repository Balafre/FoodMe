package com.foodme.controller;

import com.foodme.dto.DishDto;
import com.foodme.model.Dish;
import com.foodme.service.DishService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;

@RestController
class DishController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DishController.class);

    @Autowired
    private DishService dishService;

    @Autowired
    private ModelMapper modelMapper;

    @RequestMapping(value = "/menusection/{menuSectionId}/dish", method = RequestMethod.POST)
    public ResponseEntity<Void> createDish(@RequestBody DishDto dishDto,
                                           @PathVariable("menuSectionId") Long menuSectionId,
                                           UriComponentsBuilder ucBuilder) {
        LOGGER.debug("Creating Dish with name: {} for MenuSection with ID#{}", dishDto.getName(), menuSectionId);

        Dish dish = modelMapper.map(dishDto, Dish.class);

//        if (dishService.isDishExist(dish, menuSectionId)) {
//            LOGGER.info("Dish with name: {} for MenuSection with ID#{} already exists",
//                    dishDto.getName(), menuSectionId);
//            return new ResponseEntity<Void>(HttpStatus.CONFLICT);
//        }

        if (dishService.save(dish, menuSectionId) == null) {
            return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/menusection/{msId}/dish/{dId}")
                .buildAndExpand(menuSectionId, dish.getId()).toUri());
        headers.setAccessControlExposeHeaders(Arrays.asList("Location"));

        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/dish/{id}", method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DishDto> updateDish(@PathVariable("id") Long id,
                                           @RequestBody DishDto dishDto) {
        LOGGER.debug("Updating Dish with ID#{}", id);

        if (!dishService.isDishExist(id)) {
            LOGGER.info("Dish with id#{} not found", id);
            return new ResponseEntity<DishDto>(HttpStatus.NOT_FOUND);
        }

        Dish newDish = modelMapper.map(dishDto, Dish.class);
        newDish.setMenuSection(null);
        newDish = dishService.update(newDish);

        return new ResponseEntity<DishDto>(modelMapper.map(newDish, DishDto.class), HttpStatus.OK);
    }

    @RequestMapping(value = "/dish/{id}", method = RequestMethod.DELETE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteDish(@PathVariable("id") Long id) {
        LOGGER.debug("Fetching & Deleting Dish with id#{}", id);

        if (!dishService.isDishExist(id)) {
            LOGGER.info("Unable to delete. Dish with id#{} not found", id);
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }

        dishService.deleteDishById(id);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/dish/{id}", method = RequestMethod.GET)
    public ResponseEntity<DishDto> getDish(@PathVariable("id") Long id) {
        LOGGER.debug("Fetching Dish with id#{}", id);

        Dish dish = dishService.findById(id);
        if (dish == null) {
            LOGGER.info("Unable to fetch Dish with id#{}: not found", id);
            return new ResponseEntity<DishDto>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<DishDto>(modelMapper.map(dish, DishDto.class), HttpStatus.OK);
    }
}
