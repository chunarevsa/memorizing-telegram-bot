package org.memorizing.model.menu;

import org.memorizing.resource.cardApi.CardDto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import static org.memorizing.model.command.EKeyboardCommand.GET_INFO;
import static org.memorizing.model.command.EKeyboardCommand.GO_BACK;

public class CardUpdateMenu extends AMenu {

    private final CardDto card;

    public CardUpdateMenu(CardDto card) {
        this.card = card;
    }

    public CardDto getCard() {
        return card;
    }

    @Override
    public EMenu getCurrentMenu() {
        return EMenu.CARD_UPDATE;
    }

    @Override
    public ReplyKeyboardMarkup getKeyboard() {
        return getKeyboardByButtons(new String[][]{
                {GET_INFO.getButtonText(), GO_BACK.getButtonText()}
        });
    }

    @Override
    public String getInfoText() {
        return "Copy text start with `#` and edit fields that you want:\n" +
                "Send all fields, even if they haven't changed:\n" +
                "Please use this format:\n" +
                "\n" +
                "#update-Card\n" +
                "`cardValue`: "+card.getCardValue()+"\n" +
                "\n";
    }

    @Override
    public String getText() {
        return "#update-Card\n" +
                "`cardValue`: "+card.getCardValue()+"\n" +
                "\n";
    }

    @Override
    public String getTitle() {
        return "*Card add menu*\n"+
                "Send me information about your new card stock.\n" +
                "if you need descriptions these, push the button info\n" +
                "Please use this format:\n" +
                "\n";
    }
}
