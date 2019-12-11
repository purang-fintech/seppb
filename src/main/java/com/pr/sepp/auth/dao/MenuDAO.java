package com.pr.sepp.auth.dao;

import com.pr.sepp.auth.model.Menu;

import java.util.List;
import java.util.Optional;

public interface MenuDAO {

    List<Menu> findAllMenus();

    void saveMenu(Menu menu);

    void updateMenu(Menu menu);

    void deleteMenu(Integer id);

    Optional<Menu> getMenu(String menuIndex);

    Menu getMenuByParent(Integer parentId);
}
