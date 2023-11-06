package org.memorizing.resource;

public class TestResultDto {
    private Boolean isRightAnswer;
    private Boolean isAnswerToOtherCard;
    private String cardKey;

    public TestResultDto(Boolean isRightAnswer, Boolean isAnswerToOtherCard, String cardKey) {
        this.isRightAnswer = isRightAnswer;
        this.isAnswerToOtherCard = isAnswerToOtherCard;
        this.cardKey = cardKey;
    }

    public TestResultDto() {
    }

    public Boolean getRightAnswer() {
        return isRightAnswer;
    }

    public void setRightAnswer(Boolean rightAnswer) {
        isRightAnswer = rightAnswer;
    }

    public Boolean getAnswerToOtherCard() {
        return isAnswerToOtherCard;
    }

    public void setAnswerToOtherCard(Boolean answerToOtherCard) {
        isAnswerToOtherCard = answerToOtherCard;
    }

    public String getCardKey() {
        return cardKey;
    }

    public void setCardKey(String cardKey) {
        this.cardKey = cardKey;
    }

    @Override
    public String toString() {
        return "TestResultDto{" +
                "isRightAnswer=" + isRightAnswer +
                ", isAnswerToOtherCard=" + isAnswerToOtherCard +
                ", cardKey='" + cardKey + '\'' +
                '}';
    }
}
