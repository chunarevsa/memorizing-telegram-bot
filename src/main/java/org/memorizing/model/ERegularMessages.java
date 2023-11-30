package org.memorizing.model;

public enum ERegularMessages {

    WELCOME("Welcome, {name}!\n" +
            "Let's start memorizing\n" +
            "\n"),

    HOW_IT_WORKS("How it works?\n" +
            "This service has two basic entities: Card stock and Card\n" +
            "\n" +
            "Card stock is a card storage with a one topic.\n" +
            "\n" +
            "For example:\n" +
            "The card stock \"English words\" will have\n" +
            "`keyType` = \"English word\"\n" +
            "`valueType` = \"Russian translations\"\n" +
            "\n" +
            "We can add a Card to this Card stock\n" +
            "`key` = \"memory\"\n" +
            "`value` = \"память\"\n" +
            "\n" +
            "After that, you can start to learn your card stock\n" +
            "\n" +
            "You can use 3 modes:\n" +
            "▪ *Memorizing*\n" +
            "▪ *Self-check*\n" +
            "▪ *Testing*\n" +
            "\n" +
            "Lastly has 2 submodes:\n" +
            "▪ *Forward* or *FromKey* (key -> value)\n" +
            "Bot shows you key (”memory”). You should send a value (”память”)\n" +
            "▪ *Backward* or *ToKey* (value -> key)\n" +
            "Bot shows you a value (”память”). You should send a key (”memory”)\n" +
            "\n" +
            "If you need the *Backward* mode, specify it when you create a Card stock.\n" +
            "You can read more information about it in the Mode menu by pressing the `info` button\n" +
            "\n" +
            "You always can get more information about current menu by pressing the `info` button\n" +
            "If it isn't clear, please, use the command /help\n" +
            "*Good luck!*\n" +
            "\n"),

    HELP("Bot commands:\n" +
            "/start\n" +
            "/help\n" +
            "/howitworks\n"+
            "#report {your problem}\n" +
            "\n" +
            "Main menus:\n" +
            "▪️*Card stocks* - your list of card stocks\n" +
            "▪️*Card stock* - information about selected card stock\n" +
            "▪️*Cards* - your list of cards\n" +
            "▪️*Card* - information about selected card stock\n" +
            "▪️*Mode menu* - You can choose necessary a mode for studying" +
            "\n" +
            "Additional menus:\n" +
            "▪️*Adding card stock* - creating a new card stock\n" +
            "▪️*Updating card stock* - updating your existing card stock\n" +
            "▪️*Adding card* - creating a new card\n" +
            "▪️*Updating card* - updating your existing card\n" +
            "\n" +
            "If you found a bug, please, send the message to @chunarevsea in telegram or use the command:\n" +
            "`#report  <your problem>`");

    private final String text;

    ERegularMessages(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
