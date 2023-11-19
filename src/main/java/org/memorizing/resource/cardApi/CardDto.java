package org.memorizing.resource.cardApi;

import org.memorizing.service.IMappable;

public class CardDto implements IMappable {
    private int id;
    private String cardKey;
    private String cardValue;
    private int pointFromKey;
    private int pointToKey;
    private String statusFromKey;
    private String statusToKey;

    public CardDto() {
    }

    public CardDto(int id, String cardKey, String cardValue, int pointFromKey, int pointToKey, String statusFromKey, String statusToKey) {
        this.id = id;
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
        return "CardDto{" +
                "id=" + id +
                ", cardKey='" + cardKey + '\'' +
                ", cardValue='" + cardValue + '\'' +
                ", pointFromKey=" + pointFromKey +
                ", pointToKey=" + pointToKey +
                ", statusFromKey='" + statusFromKey + '\'' +
                ", statusToKey='" + statusToKey + '\'' +
                '}';
    }
}
