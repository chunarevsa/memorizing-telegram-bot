package org.memorizing.service;

import org.memorizing.model.EStatus;
import org.memorizing.model.menu.Menu;
import org.memorizing.model.storage.TestResult;

public class DispatcherResponse {
    private Menu menu;
    private EStatus status;
    private boolean isNeedSendStatus = false;
    private boolean isNeedSendTitle = true;
    private TestResult testResult;

    public DispatcherResponse(Menu menu, EStatus status) {
        this.menu = menu;
        this.status = status;
    }

    public DispatcherResponse(Menu menu, EStatus status, boolean isNeedSendStatus) {
        this.menu = menu;
        this.status = status;
        this.isNeedSendStatus = isNeedSendStatus;
    }

    public DispatcherResponse(Menu menu, EStatus status, TestResult testResult) {
        this.menu = menu;
        this.status = status;
        this.testResult = testResult;
    }

    public DispatcherResponse() {
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
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

    public TestResult getTestResult() {
        return testResult;
    }

    public void setTestResult(TestResult testResult) {
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
