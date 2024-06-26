package com.memorizing.model.storage;

import com.memorizing.service.IMappable;

import java.io.Serializable;

public class CardStock implements IMappable, Serializable {
    private Integer id;
    private Integer storageId;
    private String cardStockName;
    private String description;
    private String keyType;
    private String valueType;
    private Integer maxPoint;
    private Boolean testModeIsAvailable;
    private Boolean onlyFromKey;
    public CardStock() {}
    public CardStock(Integer id, Integer storageId, String cardStockName, String description, String keyType, String valueType, int maxPoint, Boolean testModeIsAvailable, Boolean onlyFromKey) {
        this.id = id;
        this.storageId = storageId;
        this.cardStockName = cardStockName;
        this.description = description;
        this.keyType = keyType;
        this.valueType = valueType;
        this.maxPoint = maxPoint;
        this.testModeIsAvailable = testModeIsAvailable;
        this.onlyFromKey = onlyFromKey;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getStorageId() {
        return storageId;
    }

    public void setStorageId(Integer storageId) {
        this.storageId = storageId;
    }

    public String getCardStockName() {
        return cardStockName;
    }

    public void setCardStockName(String cardStockName) {
        this.cardStockName = cardStockName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getKeyType() {
        return keyType;
    }

    public void setKeyType(String keyType) {
        this.keyType = keyType;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public int getMaxPoint() {
        return maxPoint;
    }

    public void setMaxPoint(int maxPoint) {
        this.maxPoint = maxPoint;
    }

    public Boolean getTestModeIsAvailable() {
        return testModeIsAvailable;
    }

    public void setTestModeIsAvailable(Boolean testModeIsAvailable) {
        this.testModeIsAvailable = testModeIsAvailable;
    }

    public Boolean getOnlyFromKey() {
        return onlyFromKey;
    }

    public void setOnlyFromKey(Boolean onlyFromKey) {
        this.onlyFromKey = onlyFromKey;
    }

    @Override
    public String toString() {
        return "CardStock{" +
                "id=" + id +
                ", storageId=" + storageId +
                ", cardStockName='" + cardStockName + '\'' +
                ", description='" + description + '\'' +
                ", keyType='" + keyType + '\'' +
                ", valueType='" + valueType + '\'' +
                ", maxPoint=" + maxPoint +
                ", testModeIsAvailable=" + testModeIsAvailable +
                ", onlyFromKey=" + onlyFromKey +
                '}';
    }
}

