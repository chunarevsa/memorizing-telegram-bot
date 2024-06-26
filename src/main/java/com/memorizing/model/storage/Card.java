package com.memorizing.model.storage;

import com.memorizing.service.IMappable;

import java.io.Serializable;

public class Card implements IMappable, Serializable {
    private Integer id;
    private Integer cardStockId;
    private String cardKey;
    private String cardValue;
    private Integer pointFromKey;
    private Integer pointToKey;
    private String statusFromKey;
    private String statusToKey;

    public Card() {
    }

    public Card(Integer cardStockId, String cardKey, String cardValue) {
        this.cardStockId = cardStockId;
        this.cardKey = cardKey;
        this.cardValue = cardValue;
    }

    public Card(Integer id, Integer cardStockId, String cardKey, String cardValue, int pointFromKey, int pointToKey, String statusFromKey, String statusToKey) {
        this.id = id;
        this.cardStockId = cardStockId;
        this.cardKey = cardKey;
        this.cardValue = cardValue;
        this.pointFromKey = pointFromKey;
        this.pointToKey = pointToKey;
        this.statusFromKey = statusFromKey;
        this.statusToKey = statusToKey;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getPointFromKey() {
        return pointFromKey;
    }

    public void setPointFromKey(int pointFromKey) {
        this.pointFromKey = pointFromKey;
    }

    public int getPointToKey() {
        return pointToKey;
    }

    public void setPointToKey(int pointToKey) {
        this.pointToKey = pointToKey;
    }

    public String getStatusFromKey() {
        return statusFromKey;
    }

    public void setStatusFromKey(String statusFromKey) {
        this.statusFromKey = statusFromKey;
    }

    public String getStatusToKey() {
        return statusToKey;
    }

    public void setStatusToKey(String statusToKey) {
        this.statusToKey = statusToKey;
    }

    @Override
    public String toString() {
        return "Card{" +
                "id=" + id +
                ", cardStockId=" + cardStockId +
                ", cardKey='" + cardKey + '\'' +
                ", cardValue='" + cardValue + '\'' +
                ", pointFromKey=" + pointFromKey +
                ", pointToKey=" + pointToKey +
                ", statusFromKey='" + statusFromKey + '\'' +
                ", statusToKey='" + statusToKey + '\'' +
                '}';
    }
}
