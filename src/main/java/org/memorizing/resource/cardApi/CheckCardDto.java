package org.memorizing.resource.cardApi;

public class CheckCardDto {
    private final Integer cardStockId;
    private final String userValue;
    private Boolean fromKey;
    private final Integer maxPoint;

    public CheckCardDto(Integer cardStockId, String userValue, boolean isFromKey, Integer maxPoint) {
        this.cardStockId = cardStockId;
        this.userValue = userValue;
        this.fromKey = isFromKey;
        this.maxPoint = maxPoint;
    }

    public Boolean getFromKey() {
        return fromKey;
    }

    public void setFromKey(Boolean fromKey) {
        this.fromKey = fromKey;
    }

    public Integer getCardStockId() {
        return cardStockId;
    }

    public String getUserValue() {
        return userValue;
    }

    public Integer getMaxPoint() {
        return maxPoint;
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
