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
    @ElementCollection(fetch = FetchType.EAGER)
    List<Integer> historyArray;

    protected User() {
    }

    public User(Long chatId, String name, Integer currentQId, List<Integer> historyArray) {
        this.chatId = chatId;
        this.name = name;
        this.currentQId = currentQId;
        this.historyArray = historyArray;
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

    public List<Integer> getHistoryArray() {
        return historyArray;
    }

    public void setHistoryArray(List<Integer> historyArray) {
        this.historyArray = historyArray;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", chatId=" + chatId +
                ", name='" + name + '\'' +
                ", currentQId=" + currentQId +
                ", historyArray=" + historyArray +
                '}';
    }
}
