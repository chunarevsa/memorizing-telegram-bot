package org.memorizing.resource.core.rest.dto.card;

public class CheckCardDto {
    private Integer cardStockId;
    private String userValue;
    private Boolean fromKey;
    private Integer maxPoint;

    public CheckCardDto() {}

    public CheckCardDto(Integer cardStockId, String userValue, Boolean isFromKey, Integer maxPoint) {
        this.cardStockId = cardStockId;
        this.userValue = userValue;
        this.fromKey = isFromKey;
        this.maxPoint = maxPoint;
    }

    public Integer getCardStockId() {
        return cardStockId;
    }

    public void setCardStockId(Integer cardStockId) {
        this.cardStockId = cardStockId;
    }

    public String getUserValue() {
        return userValue;
    }

    public void setUserValue(String userValue) {
        this.userValue = userValue;
    }

    public Boolean getFromKey() {
        return fromKey;
    }

    public void setFromKey(Boolean fromKey) {
        this.fromKey = fromKey;
    }

    public Integer getMaxPoint() {
        return maxPoint;
    }

    public void setMaxPoint(Integer maxPoint) {
        this.maxPoint = maxPoint;
    }

    @Override
    public String toString() {
        return "CheckCardDto{" +
                "cardStockId=" + cardStockId +
                ", userValue='" + userValue + '\'' +
                ", fromKey=" + fromKey +
                ", maxPoint=" + maxPoint +
                '}';
    }
}
