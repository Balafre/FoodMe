package com.foodme.controller;

import com.foodme.dto.MenuSectionDto;
import com.foodme.model.Menu;
import com.foodme.model.MenuSection;
import com.foodme.service.MenuSectionService;
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

import static com.foodme.util.StreamUtils.streamOf;

@RestController
class MenuSectionController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MenuSectionController.class);

    @Autowired
    private MenuSectionService menuSectionService;

    @Autowired
    private ModelMapper modelMapper;

    @RequestMapping(value = "/menu/{menuId}/menusection", method = RequestMethod.POST)
    public ResponseEntity<Void> createMenuSection(@RequestBody MenuSectionDto menuSectionDto,
                                                  @PathVariable("menuId") Long menuId,
                                                  UriComponentsBuilder ucBuilder) {
        LOGGER.debug("Creating MenuSection with name: \"{}\" for Menu with id#{}", menuSectionDto.getName(), menuId);

        MenuSection menuSection = modelMapper.map(menuSectionDto, MenuSection.class);

//        if (menuSectionService.isMenuSectionExist(menuSection, menuId)) {
//            LOGGER.info("MenuSection with name: {} for Menu with ID#{} already exists", menuSectionDto.getName(), menuId);
//            return new ResponseEntity<Void>(HttpStatus.CONFLICT);
//        }

        fixMenuSectionRelationships(menuSection);

        if (menuSectionService.save(menuSection, menuId) == null) {
            return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/menu/{mId}/menusection/{msId}")
                .buildAndExpand(menuId, menuSection.getId()).toUri());
        headers.setAccessControlExposeHeaders(Arrays.asList("Location"));

        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/menusection/{id}", method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MenuSectionDto> updateMenuSection(@PathVariable("id") Long id,
                                                         @RequestBody MenuSectionDto menuSectionDto) {
        LOGGER.debug("Updating MenuSection with id#{}", id);

        if (!menuSectionService.isMenuSectionExist(id)) {
            LOGGER.info("MenuSection with id#{} not found", id);
            return new ResponseEntity<MenuSectionDto>(HttpStatus.NOT_FOUND);
        }

        MenuSection newMenuSection = modelMapper.map(menuSectionDto, MenuSection.class);
        newMenuSection.setMenu(null); //mapper adds some wrong menu reference, so let's get rid of it
        fixMenuSectionRelationships(newMenuSection);

        newMenuSection = menuSectionService.update(newMenuSection);

        return new ResponseEntity<MenuSectionDto>(
                modelMapper.map(newMenuSection, MenuSectionDto.class), HttpStatus.OK);
    }

    @RequestMapping(value = "/menusection/{id}", method = RequestMethod.DELETE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteMenuSelection(@PathVariable("id") Long id) {
        LOGGER.debug("Fetching & Deleting MenuSection with id#{}", id);

        if (!menuSectionService.isMenuSectionExist(id)) {
            LOGGER.info("Unable to delete. MenuSection with id#{} not found", id);
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }

        menuSectionService.deleteMenuSectionById(id);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/menusection/{id}", method = RequestMethod.GET)
    public ResponseEntity<MenuSectionDto> getMenuSection(@PathVariable("id") Long id) {
        LOGGER.debug("Fetching MenuSection with id#{}", id);

        MenuSection menuSection = menuSectionService.findById(id);
        if (menuSection == null) {
            LOGGER.info("Unable to fetch MenuSection with id#{}: not found", id);
            return new ResponseEntity<MenuSectionDto>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<MenuSectionDto>(modelMapper.map(menuSection, MenuSectionDto.class), HttpStatus.OK);
    }

    private void fixMenuSectionRelationships(MenuSection menuSection) {
        streamOf(menuSection.getDishes()).forEach(dish -> dish.setMenuSection(menuSection));
    }
}
