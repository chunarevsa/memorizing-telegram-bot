package org.memorizing.model.storage;

import org.memorizing.service.IMappable;

import java.io.Serializable;

public class TestResult implements IMappable, Serializable {
    private Boolean rightAnswer;
    private Boolean answerToOtherCard;
    private Card card;

    public TestResult() {
    }

    public TestResult(Boolean rightAnswer, Boolean answerToOtherCard, Card card) {
        this.rightAnswer = rightAnswer;
        this.answerToOtherCard = answerToOtherCard;
        this.card = card;
    }

    public void setRightAnswer(Boolean rightAnswer) {
        this.rightAnswer = rightAnswer;
    }

    public void setAnswerToOtherCard(Boolean answerToOtherCard) {
        this.answerToOtherCard = answerToOtherCard;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
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
