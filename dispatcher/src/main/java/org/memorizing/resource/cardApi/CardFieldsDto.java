package org.memorizing.resource.cardApi;

import org.memorizing.service.IMappable;

public class CardFieldsDto implements IMappable {
    private Integer cardStockId;
    private String cardKey;
    private String cardValue;

    public CardFieldsDto() {
    }

    public CardFieldsDto(Integer cardStockId, String cardKey, String cardValue) {
        this.cardStockId = cardStockId;
        this.cardKey = cardKey;
        this.cardValue = cardValue;
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

    @Override
    public String toString() {
        return "CardFieldsDto{" +
                "cardStockId=" + cardStockId +
                ", cardKey='" + cardKey + '\'' +
                ", cardValue='" + cardValue + '\'' +
                '}';
    }
}
