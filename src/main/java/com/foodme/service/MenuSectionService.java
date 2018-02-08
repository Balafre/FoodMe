package com.foodme.service;

import com.foodme.model.Menu;
import com.foodme.model.MenuSection;
import com.foodme.repository.MenuRepository;
import com.foodme.repository.MenuSectionRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MenuSectionService {

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private MenuSectionRepository menuSectionRepository;

    @Transactional
    public MenuSection update(MenuSection menuSection) {
        if (StringUtils.isBlank(menuSection.getName())) {
            return null;
        }
        if (menuSection.getMenu() == null) {
            menuSection.setMenu(menuRepository.findOneByMenuSectionsId(menuSection.getId()));
        }
        menuSection = menuSectionRepository.saveAndFlush(menuSection);
        return menuSection;
    }

    @Transactional
    public MenuSection save(MenuSection menuSection, Long menuId) {
        Menu menu = menuRepository.findOne(menuId);
        if (menu == null) {
            return null;
        }
        menuSection.setMenu(menu);
        return update(menuSection);
    }

    public MenuSection findById(Long id) {
        return menuSectionRepository.findOne(id);
    }

    public boolean isMenuSectionExist(MenuSection menuSection, Long menuId) {
        return menuSectionRepository.findOneByNameIgnoreCaseAndMenuId(menuSection.getName(), menuId) != null;
    }

    public boolean isMenuSectionExist(Long menuSectionId) {
        return menuSectionRepository.exists(menuSectionId);
    }

    @Transactional
    public void deleteMenuSectionById(Long id) {
        MenuSection section = menuSectionRepository.findOne(id);
        Menu menu = section.getMenu();
        menu.deleteSection(section);

        menuRepository.save(menu);
    }
}
