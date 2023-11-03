package org.memorizing.service;

import org.memorizing.model.RegularMessages;
import org.memorizing.model.menu.MenuFactory;

public class DispatcherResponse {
    private MenuFactory menu;
    private RegularMessages status;

    public DispatcherResponse(MenuFactory menu, RegularMessages status) {
        this.menu = menu;
        this.status = status;
    }

    public MenuFactory getMenu() {
        return menu;
    }

    public void setMenu(MenuFactory menu) {
        this.menu = menu;
    }

    public RegularMessages getStatus() {
        return status;
    }

    public void setStatus(RegularMessages status) {
        this.status = status;
    }
}
