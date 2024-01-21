package com.imoxion.sensems.web.database.domain;

import org.apache.ibatis.type.Alias;

@Alias("actionMenu")
public class ImbActionMenu {

    private String menu_key;
    private String menu;

    public String getMenu_key() { return menu_key; }

    public void setMenu_key(String menu_key) { this.menu_key = menu_key; }

    public String getMenu() {
        return menu;
    }

    public void setMenu(String menu) { this.menu = menu; }


}
