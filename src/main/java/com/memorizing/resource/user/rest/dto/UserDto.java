package com.memorizing.resource.user.rest.dto;

public class UserDto {
    private Integer id;
    private Long chatId;

    private String telegramUserName;
    private Integer storageId;

    public UserDto(Integer id, Long chatId, String telegramUserName, Integer storageId) {
        this.id = id;
        this.chatId = chatId;
        this.telegramUserName = telegramUserName;
        this.storageId = storageId;
    }

    public UserDto() {
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

    public String getTelegramUserName() {
        return telegramUserName;
    }

    public void setTelegramUserName(String telegramUserName) {
        this.telegramUserName = telegramUserName;
    }

    public Integer getStorageId() {
        return storageId;
    }

    public void setStorageId(Integer storageId) {
        this.storageId = storageId;
    }

    @Override
    public String toString() {
        return "UserDto{" +
                "id=" + id +
                ", chatId=" + chatId +
                ", telegramUserName='" + telegramUserName + '\'' +
                ", storageId=" + storageId +
                '}';
    }
}
