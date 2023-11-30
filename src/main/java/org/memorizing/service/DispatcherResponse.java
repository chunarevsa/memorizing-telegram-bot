package org.memorizing.service;

import org.memorizing.model.EStatus;
import org.memorizing.model.menu.MenuFactory;
import org.memorizing.resource.cardApi.TestResultDto;

public class DispatcherResponse {
    private MenuFactory menu;
    private EStatus status;
    private boolean isNeedSendStatus = false;
    private boolean isNeedSendTitle = true;
    private TestResultDto testResult;

    public DispatcherResponse(MenuFactory menu, EStatus status) {
        this.menu = menu;
        this.status = status;
    }

    public DispatcherResponse(MenuFactory menu, EStatus status, boolean isNeedSendStatus) {
        this.menu = menu;
        this.status = status;
        this.isNeedSendStatus = isNeedSendStatus;
    }

    public DispatcherResponse(MenuFactory menu, EStatus status, TestResultDto testResult) {
        this.menu = menu;
        this.status = status;
        this.testResult = testResult;
    }

    public DispatcherResponse() {
    }

    public MenuFactory getMenu() {
        return menu;
    }

    public void setMenu(MenuFactory menu) {
        this.menu = menu;
    }

    public EStatus getStatus() {
        return status;
    }

    public boolean isNeedSendStatus() {
        return isNeedSendStatus;
    }

    public void setNeedSendStatus(boolean needSendStatus) {
        isNeedSendStatus = needSendStatus;
    }

    public void setStatus(EStatus status) {
        this.status = status;
    }

    public boolean isNeedSendTitle() {
        return isNeedSendTitle;
    }

    public void setNeedSendTitle(boolean needSendTitle) {
        isNeedSendTitle = needSendTitle;
    }

    public TestResultDto getTestResult() {
        return testResult;
    }

    public void setTestResult(TestResultDto testResult) {
        this.testResult = testResult;
    }

    @Override
    public String toString() {
        return "DispatcherResponse{" +
                "menu=" + menu +
                ", status=" + status +
                ", isNeedSendStatus=" + isNeedSendStatus +
                ", isNeedSendTitle=" + isNeedSendTitle +
                ", testResult=" + testResult +
                '}';
    }
}
