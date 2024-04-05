package com.memorizing.resource.core.rest.dto.cardStock;

import java.io.Serializable;

public class CardStockFieldsDto implements Serializable {
    private Integer storageId;
    private String cardStockName;
    private String description;
    private String keyType;
    private String valueType;
    private Integer maxPoint;
    private Boolean testModeIsAvailable;
    private Boolean onlyFromKey;
    public CardStockFieldsDto() {}
    public CardStockFieldsDto(Integer storageId, String cardStockName, String description, String keyType, String valueType, Integer maxPoint, Boolean testModeIsAvailable, Boolean onlyFromKey) {
        this.storageId = storageId;
        this.cardStockName = cardStockName;
        this.description = description;
        this.keyType = keyType;
        this.valueType = valueType;
        this.maxPoint = maxPoint;
        this.testModeIsAvailable = testModeIsAvailable;
        this.onlyFromKey = onlyFromKey;
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

    public Integer getMaxPoint() {
        return maxPoint;
    }

    public void setMaxPoint(Integer maxPoint) {
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
        return "CardStockFieldsDto{" +
                "storageId=" + storageId +
                ", name='" + cardStockName + '\'' +
                ", description='" + description + '\'' +
                ", keyType='" + keyType + '\'' +
                ", valueType='" + valueType + '\'' +
                ", maxPoint=" + maxPoint +
                ", testModeIsAvailable=" + testModeIsAvailable +
                ", onlyFromKey=" + onlyFromKey +
                '}';
    }
}

