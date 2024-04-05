package com.memorizing.model.command;

import com.memorizing.model.storage.Card;
import com.memorizing.model.storage.CardStock;
import com.memorizing.service.IMappable;

import java.util.Arrays;

public enum EPlaceholderCommand {
    ADD_CARD_STOCK("#add-CardStock", "add"),
    UPDATE_CARD_STOCK("#update-CardStock", "update"),
    DELETE_CARD_STOCK("#delete-CardStock", "delete"),

    ADD_CARD("#add-Card", "add"),
    UPDATE_CARD("#update-Card", "update"),
    DELETE_CARD("#delete-Card", "delete"),
    ;

//    SEND_REPORT("#report", CardFieldsDto.class); // TODO :create Entity

    private final String pref;
    private final String method;

    EPlaceholderCommand(String pref, String method) {
        this.pref = pref;
        this.method = method;
    }

    public String getPref() {
        return pref;
    }

    public String getMethod() {
        return method;
    }

    public IMappable getNewEntity() {
        switch (this) {
            case ADD_CARD_STOCK:
            case UPDATE_CARD_STOCK:
            case DELETE_CARD_STOCK:
                return new CardStock();
            case ADD_CARD:
            case UPDATE_CARD:
            case DELETE_CARD:
                return new Card();
        }
        return null;
    }

    public static EPlaceholderCommand getPlaceholderCommandByPref(String command) {
        return Arrays.stream(EPlaceholderCommand.values())
                .filter(it -> command != null && command.startsWith(it.pref))
                .findFirst().orElse(null);

    }

}
