package com.memorizing.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.memorizing.model.menu.EMenu;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Entity
@Table(name = "user_state")
public class UserState {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @JsonIgnore
    @OneToOne(optional = false, mappedBy = "userState")
    private User user;
    @Enumerated(EnumType.STRING)
    private EMenu currentMenu = EMenu.MAIN;
    private Integer cardStockId = null;
    private Integer cardId = null;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CardStockHistory> studyingHistory = new ArrayList<>();

    public UserState() {
    }

    public UserState(User user) {
        this.user = user;
    }

    public EMenu getLastMenu() {
        return this.currentMenu.getLastMenu();
    }

    public void addStudyingHistory(CardStockHistory history) {
        this.studyingHistory.add(history);
    }

    public void updateStudyingHistory(CardStockHistory history) {
        setStudyingHistory(this.studyingHistory.stream().map(it ->
            Objects.equals(it.getId(), history.getId()) ? history : it
        ).collect(Collectors.toList()));
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

    public List<CardStockHistory> getStudyingHistory() {
        return studyingHistory;
    }

    public void setStudyingHistory(List<CardStockHistory> cardStockIds) {
        this.studyingHistory = cardStockIds;
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
