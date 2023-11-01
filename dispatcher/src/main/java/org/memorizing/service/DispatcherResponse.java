package org.memorizing.service;

import org.memorizing.model.ResponseStatus;
import org.memorizing.model.menu.MenuFactory;

public class DispatcherResponse {
    private MenuFactory menu;
    private ResponseStatus status;

    public DispatcherResponse(MenuFactory menu, ResponseStatus status) {
        this.menu = menu;
        this.status = status;
    }

    public MenuFactory getMenu() {
        return menu;
    }

    public void setMenu(MenuFactory menu) {
        this.menu = menu;
    }

    public ResponseStatus getStatus() {
        return status;
    }

    public void setStatus(ResponseStatus status) {
        this.status = status;
    }
}
