package com.memorizing.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.NaturalId;
import com.memorizing.model.EMode;
import com.memorizing.utils.StudyingStateConverter;

import javax.persistence.*;
import java.util.*;

import static com.memorizing.model.EMode.values;

@Entity
public class  CardStockHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NaturalId
    private Integer cardStockId;

    @JsonIgnore
    @ManyToOne
    private UserState userState;

    @Convert(converter = StudyingStateConverter.class)
    private Map<String, List<Integer>> studyingState = new HashMap<>();

    public CardStockHistory() {
    }

    public CardStockHistory(UserState userState) {
        this.cardStockId = userState.getCardStockId();
        this.userState = userState;
        Arrays.stream(values()).forEach(it -> studyingState.put(it.name(), new ArrayList<>()));
    }

    public void updateStudyingStateIds(EMode mode, List<Integer> ids) {
        this.studyingState.put(mode.name(), ids);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Integer getCardStockId() {
        return cardStockId;
    }

    public void setCardStockId(Integer cardStockId) {
        this.cardStockId = cardStockId;
    }

    public Map<String, List<Integer>> getStudyingState() {
        return studyingState;
    }

    public void setStudyingState(Map<String, List<Integer>> studyingState) {
        this.studyingState = studyingState;
    }

    @Override
    public String toString() {
        return "CardStockHistory{" +
                "id=" + id +
                ", cardStockId=" + cardStockId +
                ", studyingState=" + studyingState +
                '}';
    }
}
