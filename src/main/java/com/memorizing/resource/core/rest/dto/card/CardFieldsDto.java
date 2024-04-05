package com.memorizing.resource.core.rest.dto.card;

import java.io.Serializable;
public class CardFieldsDto implements Serializable {
    private Integer cardStockId;
    private String cardKey;
    private String cardValue;
    private Boolean onlyFromKey;
    public CardFieldsDto() {}

    public CardFieldsDto(Integer cardStockId, String cardKey, String cardValue, Boolean onlyFromKey) {
        this.cardStockId = cardStockId;
        this.cardKey = cardKey;
        this.cardValue = cardValue;
        this.onlyFromKey = onlyFromKey;
    }

    public Integer getCardStockId() {
        return cardStockId;
    }

    public void setCardStockId(Integer cardStockId) {
        this.cardStockId = cardStockId;
    }

    public String getCardKey() {
        return cardKey;
    }

    public void setCardKey(String cardKey) {
        this.cardKey = cardKey;
    }

    public String getCardValue() {
        return cardValue;
    }

    public void setCardValue(String cardValue) {
        this.cardValue = cardValue;
    }

    public Boolean getOnlyFromKey() {
        return onlyFromKey;
    }

    public void setOnlyFromKey(Boolean onlyFromKey) {
        this.onlyFromKey = onlyFromKey;
    }

    @Override
    public String toString() {
        return "CardFieldsDto{" +
                "cardStockId=" + cardStockId +
                ", cardKey='" + cardKey + '\'' +
                ", cardValue='" + cardValue + '\'' +
                ", onlyFromKey=" + onlyFromKey +
                '}';
    }
}
