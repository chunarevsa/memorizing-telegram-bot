package org.memorizing.service;

import org.memorizing.model.ERegularMessages;
import org.memorizing.model.menu.MenuFactory;

public class DispatcherResponse {
    private MenuFactory menu;
    private ERegularMessages status;
    private boolean isNeedSendStatus = false;
    private boolean isNeedSendTitle = true;

    public DispatcherResponse(MenuFactory menu, ERegularMessages status) {
        this.menu = menu;
        this.status = status;
    }

    public DispatcherResponse(MenuFactory menu, ERegularMessages status, boolean isNeedSendStatus) {
        this.menu = menu;
        this.status = status;
        this.isNeedSendStatus = isNeedSendStatus;
    }

    public MenuFactory getMenu() {
        return menu;
    }

    public void setMenu(MenuFactory menu) {
        this.menu = menu;
    }

    public ERegularMessages getStatus() {
        return status;
    }

    public boolean isNeedSendStatus() {
        return isNeedSendStatus;
    }

    public void setNeedSendStatus(boolean needSendStatus) {
        isNeedSendStatus = needSendStatus;
    }

    public void setStatus(ERegularMessages status) {
        this.status = status;
    }

    public boolean isNeedSendTitle() {
        return isNeedSendTitle;
    }

    public void setNeedSendTitle(boolean needSendTitle) {
        isNeedSendTitle = needSendTitle;
    }

    @Override
    public String toString() {
        return "DispatcherResponse{" +
                "menu=" + menu +
                ", status=" + status +
                ", isNeedSendStatus=" + isNeedSendStatus +
                ", isNeedSendTitle=" + isNeedSendTitle +
                '}';
    }
}
