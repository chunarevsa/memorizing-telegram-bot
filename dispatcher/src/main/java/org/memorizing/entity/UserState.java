package org.memorizing.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
public class UserState {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @JsonIgnore
    @OneToOne(optional = false, mappedBy = "userState")
    private User user;
    private EMenu currentMenu;
    private EMenu lastMenu;
    private Integer cardStockId;
    private Integer cardId;

    public UserState() {
    }

    public UserState(User user) {
        this.user = user;
        this.currentMenu = EMenu.MAIN;
        this.lastMenu = EMenu.MAIN;
        this.cardStockId = null;
        this.cardId = null;
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

    public EMenu getLastMenu() {
        return lastMenu;
    }

    public void setLastMenu(EMenu lastMenu) {
        this.lastMenu = lastMenu;
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

    @Override
    public String toString() {
        return "UserState{" +
                "id=" + id +
                ", user=" + user +
                ", currentMenu=" + currentMenu +
                ", lastMenu=" + lastMenu +
                ", cardStockId=" + cardStockId +
                ", cardId=" + cardId +
                '}';
    }
}
