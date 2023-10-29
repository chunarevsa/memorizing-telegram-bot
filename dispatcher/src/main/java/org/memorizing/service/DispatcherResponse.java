package org.memorizing.service;

import org.memorizing.model.Constants;
import org.memorizing.model.menu.MenuFactory;

public class DispatcherResponse {
    private MenuFactory menu;
    private Constants status;

    public DispatcherResponse(MenuFactory menu, Constants status) {
        this.menu = menu;
        this.status = status;
    }

    public MenuFactory getMenu() {
        return menu;
    }

    public void setMenu(MenuFactory menu) {
        this.menu = menu;
    }

    public Constants getStatus() {
        return status;
    }

    public void setStatus(Constants status) {
        this.status = status;
    }
}
