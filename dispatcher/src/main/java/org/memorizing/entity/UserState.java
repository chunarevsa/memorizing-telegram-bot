package org.memorizing.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.memorizing.model.menu.EMenu;

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
    private Integer cardStockId;
    private Integer cardId;

    public UserState() {
    }

    public UserState(User user) {
        this.user = user;
        this.currentMenu = EMenu.MAIN;
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
                ", cardStockId=" + cardStockId +
                ", cardId=" + cardId +
                '}';
    }

    public EMenu getLastMenu() {
        switch (this.currentMenu) {
            case MAIN:
            case CARD_STOCKS: return EMenu.MAIN;
            case CARD_STOCK: return EMenu.CARD_STOCKS;
            case CARDS: return EMenu.CARD_STOCK;
            case CARD: return EMenu.CARDS;
            default: return EMenu.MAIN;
        }
    }
}
