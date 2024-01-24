package org.memorizing.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    Long chatId;
    String name;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_state_id", unique = true)
    UserState userState;

    @Column(name = "storage_id")
    Integer storageId;

    protected User() {
    }

    public User(Long chatId, String name, Integer storageId) {
        this.chatId = chatId;
        this.name = name;
        this.storageId = storageId;
        this.userState = new UserState(this);
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

    public UserState getUserState() {
        return userState;
    }

    public void setUserState(UserState userState) {
        this.userState = userState;
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
                ", userState=" + userState +
                ", storageId=" + storageId +
                '}';
    }
}
