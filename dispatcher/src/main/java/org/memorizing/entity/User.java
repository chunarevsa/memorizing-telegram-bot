package org.memorizing.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    Long chatId;
    String name;
    @Column(name = "current_q_id")
    Integer currentQId;

    @Column(name = "current_menu_name")
    String currentMenuName;
    @ElementCollection(fetch = FetchType.EAGER)
    List<Integer> historyArray;

    @Column(name = "storage_id")
    Integer storageId;

    protected User() {
    }

    public User(Long chatId, String name, Integer currentQId, String currentMenuName, List<Integer> historyArray, Integer storageId) {
        this.chatId = chatId;
        this.name = name;
        this.currentQId = currentQId;
        this.currentMenuName = currentMenuName;
        this.historyArray = historyArray;
        this.storageId = storageId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCurrentQId() {
        return currentQId;
    }

    public void setCurrentQId(Integer currentQId) {
        this.currentQId = currentQId;
    }

    public String getCurrentMenuName() {
        return currentMenuName;
    }

    public void setCurrentMenuName(String currentMenuName) {
        this.currentMenuName = currentMenuName;
    }

    public List<Integer> getHistoryArray() {
        return historyArray;
    }

    public void setHistoryArray(List<Integer> historyArray) {
        this.historyArray = historyArray;
    }

    public Integer getStorageId() {
        return storageId;
    }

    public void setStorageId(Integer storageId) {
        this.storageId = storageId;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", chatId=" + chatId +
                ", name='" + name + '\'' +
                ", currentQId=" + currentQId +
                ", currentMenuName='" + currentMenuName + '\'' +
                ", historyArray=" + historyArray +
                ", storageId=" + storageId +
                '}';
    }
}
