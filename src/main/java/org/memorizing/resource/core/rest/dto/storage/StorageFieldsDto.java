package org.memorizing.resource.core.rest.dto.storage;

import java.io.Serializable;

public class StorageFieldsDto implements Serializable {
    private Long userId;
    private String storageName;

    public StorageFieldsDto() {}
    public StorageFieldsDto(Long userId, String storageName) {
        this.userId = userId;
        this.storageName = storageName;
    }
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getStorageName() {
        return storageName;
    }

    public void setStorageName(String storageName) {
        this.storageName = storageName;
    }

    @Override
    public String toString() {
        return "StorageFieldsDto{" +
                "userId=" + userId +
                ", storageName='" + storageName + '\'' +
                '}';
    }
}
