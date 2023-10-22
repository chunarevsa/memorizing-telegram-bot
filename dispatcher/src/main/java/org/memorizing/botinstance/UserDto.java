package org.memorizing.botinstance;

public class UserDto {
    private Integer id;
    private Long chatId;

    public UserDto(Integer id, Long chatId) {
        this.id = id;
        this.chatId = chatId;
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

    @Override
    public String toString() {
        return "UserDto{" +
                "id=" + id +
                ", chatId=" + chatId +
                '}';
    }
}
