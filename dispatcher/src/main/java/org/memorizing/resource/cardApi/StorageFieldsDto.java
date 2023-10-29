package org.memorizing.resource.cardApi;

public class StorageFieldsDto {
    private Integer userId;
    private String storageName;

    public StorageFieldsDto(Integer userId, String storageName) {
        this.userId = userId;
        this.storageName = storageName;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
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
