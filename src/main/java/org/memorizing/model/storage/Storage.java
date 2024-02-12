package org.memorizing.model.storage;

import org.memorizing.service.IMappable;

import java.io.Serializable;

public class Storage implements IMappable, Serializable {
    private Integer id;
    private Long userId;
    private String storageName;

    public Storage() {
    }

    public Storage(Integer id, Long userId, String storageName) {
        this.id = id;
        this.userId = userId;
        this.storageName = storageName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
        return "StorageDto{" +
                "id=" + id +
                ", userId=" + userId +
                ", storageName='" + storageName + '\'' +
                '}';
    }
}
