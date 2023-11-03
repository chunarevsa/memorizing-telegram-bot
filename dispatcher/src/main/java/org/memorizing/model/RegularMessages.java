package org.memorizing.model;

public enum RegularMessages {

    WELCOME("Welcome, {name}!\n" +
            "Let's start memorizing\n" +
            "\n"),

    HOW_IT_WORKS("How it works?\n" +
            "This service has two basic entities: Card stock and card\n" +
            "\n" +
            "Card stock is a card storage with one topic.\n" +
            "For example:\n" +
            "The card stock \"English words\" will have\n" +
            "`keyType` = \"English word\"\n" +
            "`valueType` = \"Russian translations\"\n" +
            "\n" +
            "We can add card\n" +
            "`key` = \"memory\"\n" +
            "`value` = \"память\"\n" +
            "\n" +
            "After that, you can start to learn your card stok\n" +
            "\n" +
            "You can use 3 mods:\n" +
            "▪ *Memorizing*\n" +
            "▪ *Self-check*\n" +
            "▪ *Testing*\n" +
            "Lastly has 2 submods:\n" +
            "▪ *Forward* or *FromKey* (key -> value)\n" +
            "Bot show you key. You should send value\n" +
            "▪ *Backward* or *ToKey* (value -> key)\n" +
            "Bot show you value. You should send key\n" +
            "If you need *Backward* mode, specify it when you create card stock\n" +
            "You can read more information about it in the testing menu by pressing the /info button\n" +
            "\n" +
            "You always can get more information about current menu by pressing the /info button\n" +
            "If it isn't clear, please, use command /help\n" +
            "*Good luck!*\n" +
            "\n"),

    HELP("Bot commands:\n" +
            "/start\n" +
            "/back\n" +
            "/info\n" +
            "/report\n" +
            "\n" +
            "Main menus:\n" +
            "▪️*Card stocks* - your list of card stocks\n" +
            "▪️*Card stock* - information about selected card stock\n" +
            "▪️*Cards* - your list of cards\n" +
            "▪️*Card* - information about selected card stock\n" +
            "\n" +
            "Additional menus:\n" +
            "▪️*Adding card stock* - creating new card stock\n" +
            "▪️*Updating card stock* - updating your existing card stock\n" +
            "▪️*Adding card* - creating new card\n" +
            "▪️*Updating card* - updating your existing card\n" +
            "\n" +
            "If you found bug, please, send message to @chunarevsea in telegram or use command:\n" +
            "`/report  <your problem>`"),

    BAD_REQUEST("❌ Bad request."),
    SOMETHING_WENT_WRONG("❌ Sorry, something went wrong. We try to manage it rapidly"),
    SUCCESSFULLY("✅ Success ");

    private final String value;

    RegularMessages(String s) {
        this.value = s;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
