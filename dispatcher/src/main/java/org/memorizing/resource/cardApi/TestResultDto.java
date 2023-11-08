package org.memorizing.resource.cardApi;

public class TestResultDto {
    private Boolean rightAnswer;
    private Boolean answerToOtherCard;
    private CardDto card;

    public TestResultDto(Boolean rightAnswer, Boolean answerToOtherCard, CardDto card) {
        this.rightAnswer = rightAnswer;
        this.answerToOtherCard = answerToOtherCard;
        this.card = card;
    }

    public TestResultDto() {
    }

    public void setRightAnswer(Boolean rightAnswer) {
        this.rightAnswer = rightAnswer;
    }

    public void setAnswerToOtherCard(Boolean answerToOtherCard) {
        this.answerToOtherCard = answerToOtherCard;
    }

    public CardDto getCard() {
        return card;
    }

    public void setCard(CardDto card) {
        this.card = card;
    }

    public Boolean getRightAnswer() {
        return rightAnswer;
    }

    public Boolean getAnswerToOtherCard() {
        return answerToOtherCard;
    }

    @Override
    public String toString() {
        return "TestResultDto{" +
                "rightAnswer=" + rightAnswer +
                ", answerToOtherCard=" + answerToOtherCard +
                ", card=" + card +
                '}';
    }
}
