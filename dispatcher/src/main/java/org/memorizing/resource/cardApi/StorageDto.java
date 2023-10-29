package org.memorizing.resource.cardApi;

// TODO: add it to separated module
public class StorageDto {
    private Integer id;
    private Integer userId;
    private String storageName;

    public StorageDto(Integer id, Integer userId, String storageName) {
        this.id = id;
        this.userId = userId;
        this.storageName = storageName;
    }

    public StorageDto() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
        return "StorageDto{" +
                "id=" + id +
                ", userId=" + userId +
                ", storageName='" + storageName + '\'' +
                '}';
    }
}
