package org.memorizing.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.memorizing.model.menu.EMenu;
import org.memorizing.model.menu.EMode;
import org.memorizing.utils.StudyingStateConverter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.memorizing.model.menu.EMode.*;

@Entity
public class UserState {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @JsonIgnore
    @OneToOne(optional = false, mappedBy = "userState")
    private User user;
    @Enumerated(EnumType.STRING)
    private EMenu currentMenu;
    private Integer cardStockId;
    private Integer cardId;

    @Convert(converter = StudyingStateConverter.class)
    private Map<String, List<Integer>> studyingState = new HashMap<>();

    public UserState() {
    }

    public UserState(User user) {
        this.user = user;
        this.currentMenu = EMenu.MAIN;
        this.cardStockId = null;
        this.cardId = null;

        studyingState.put(FORWARD_TESTING.name(), new ArrayList<>());
        studyingState.put(FORWARD_SELF_CHECK.name(), new ArrayList<>());
        studyingState.put(FORWARD_MEMORIZING.name(), new ArrayList<>());
        studyingState.put(BACKWARD_TESTING.name(), new ArrayList<>());
        studyingState.put(BACKWARD_SELF_CHECK.name(), new ArrayList<>());
        studyingState.put(BACKWARD_MEMORIZING.name(), new ArrayList<>());
    }

    public EMenu getLastMenu() {
        return this.currentMenu.getLastMenu();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public EMenu getCurrentMenu() {
        return currentMenu;
    }

    public void setCurrentMenu(EMenu currentMenu) {
        this.currentMenu = currentMenu;
    }

    public Integer getCardStockId() {
        return cardStockId;
    }

    public void setCardStockId(Integer cardStockId) {
        this.cardStockId = cardStockId;
    }

    public Integer getCardId() {
        return cardId;
    }

    public void setCardId(Integer cardId) {
        this.cardId = cardId;
    }

    public Map<String, List<Integer>> getStudyingState() {
        return studyingState;
    }

    public void setStudyingState(Map<String, List<Integer>> map) {
        this.studyingState = map;
    }

    public void updateStudyingStateIds(EMode mode, List<Integer> ids) {
        this.studyingState.put(mode.name(), ids);
    }

    @Override
    public String toString() {
        return "UserState{" +
                "id=" + id +
                ", currentMenu=" + currentMenu +
                ", cardStockId=" + cardStockId +
                ", cardId=" + cardId +
                '}';
    }
}
