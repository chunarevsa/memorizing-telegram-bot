package org.memorizing.model.menu;

import org.memorizing.model.EMode;
import org.memorizing.resource.cardApi.CardDto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.List;

import static org.memorizing.model.command.EKeyboardCommand.*;

public class SelfCheckMenu extends AStudyingMenu {

    public SelfCheckMenu(CardDto card, EMode mode, List<Integer> ids) {
        super(card, mode, ids);
    }

    @Override
    public EMenu getCurrentMenu() {
        return getMode().isFromKeyMode() ? EMenu.FORWARD_SELF_CHECK : EMenu.BACKWARD_SELF_CHECK;
    }

    @Override
    public ReplyKeyboardMarkup getKeyboard() {
        return getKeyboardByButtons(new String[][]{
                {NEXT.getButtonText()},
                {GET_INFO.getButtonText(), GO_BACK.getButtonText()}
        });
    }

    @Override
    public String getInfoText() {
        return "Some info text from Self-check menu";
    }

    @Override
    public String getText() {
        String key = getTextForMDV2(getCard().getCardKey());
        String value = getTextForMDV2(getCard().getCardValue());

        return getMode().isFromKeyMode()
                ? (key + "\n" +
                "||" + value) + "||"
                : (value + "\n" +
                "||" + key)   + "||";
    }

    @Override
    public String getTitle() {
        return "*Self-check menu*\n";
    }

    public String getTextForMDV2(String str) {
        return str
                .replaceAll("-", " ")
                .replaceAll("\\.", " ");
    }
}
