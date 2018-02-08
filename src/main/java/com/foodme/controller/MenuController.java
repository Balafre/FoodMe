package com.foodme.controller;

import com.foodme.dto.MenuDto;
import com.foodme.model.Menu;
import com.foodme.service.MenuService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;

import static com.foodme.util.StreamUtils.streamOf;

@RestController
class MenuController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MenuController.class);

    @Autowired
    private MenuService menuService;

    @Autowired
    private ModelMapper modelMapper;

    @RequestMapping(value = "/restaurant/{restId}/menu", method = RequestMethod.POST)
    public ResponseEntity<Void> createMenu(@RequestBody MenuDto menuDto, @PathVariable("restId") Long restId,
                                           UriComponentsBuilder ucBuilder) {
        LOGGER.debug("Creating Menu with name: {} for Restaurant with ID#{}", menuDto.getName(), restId);

        Menu menu = modelMapper.map(menuDto, Menu.class);

//        if (menuService.isMenuExist(menu, restId)) {
//            LOGGER.info("Menu with name: {} for Restaurant with ID#{} already exists",  menuDto.getName(), restId);
//            return new ResponseEntity<Void>(HttpStatus.CONFLICT);
//        }

        fixMenuRelationships(menu);

        if (menuService.save(menu, restId) == null) {
            return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/restaurant/{rId}/menu/{mId}")
                .buildAndExpand(restId, menu.getId()).toUri());
        headers.setAccessControlExposeHeaders(Arrays.asList("Location"));
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/menu/{id}", method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MenuDto> updateMenu(@PathVariable("id") long id,
                                              @RequestBody MenuDto menuDto) {
        LOGGER.debug("Updating Menu with ID#{}", id);

        if (!menuService.isMenuExist(id)) {
            LOGGER.info("Menu with id#{} not found", id);
            return new ResponseEntity<MenuDto>(HttpStatus.NOT_FOUND);
        }

        Menu newMenu = modelMapper.map(menuDto, Menu.class);
        fixMenuRelationships(newMenu);
        newMenu = menuService.update(newMenu);

        return new ResponseEntity<MenuDto>(modelMapper.map(newMenu, MenuDto.class), HttpStatus.OK);
    }

    @RequestMapping(value = "/menu/{id}", method = RequestMethod.DELETE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteMenu(@PathVariable("id") Long id) {
        LOGGER.debug("Fetching & Deleting Menu with id#{}", id);

        if (!menuService.isMenuExist(id)) {
            LOGGER.info("Unable to delete. Menu with id#{} not found", id);
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }

        menuService.deleteMenuById(id);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/menu/{id}", method = RequestMethod.GET)
    public ResponseEntity<MenuDto> getMenu(@PathVariable("id") Long id) {
        LOGGER.debug("Fetching Menu with id#{}", id);

        Menu menu = menuService.findById(id);
        if (menu == null) {
            LOGGER.info("Unable to fetch Menu with id#{}: not found", id);
            return new ResponseEntity<MenuDto>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<MenuDto>(modelMapper.map(menu, MenuDto.class), HttpStatus.OK);
    }

    private void fixMenuRelationships(Menu menu) {
        streamOf(menu.getMenuSections()).forEach(menuSection -> {
            menuSection.setMenu(menu);
            streamOf(menuSection.getDishes()).forEach(dish -> dish.setMenuSection(menuSection));
        });
    }
}
