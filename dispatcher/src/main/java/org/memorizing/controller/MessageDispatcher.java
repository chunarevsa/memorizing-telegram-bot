package org.memorizing.controller;

import org.apache.log4j.Logger;
import org.memorizing.botinstance.UserDto;
import org.memorizing.entity.User;
import org.memorizing.repository.UsersRepo;
import org.memorizing.utils.cardApi.StorageDto;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.memorizing.entity.Constants.*;

@Component
public class MessageDispatcher {
    private static final Logger log = Logger.getLogger(MessageDispatcher.class);

    private final UsersRepo usersRepo;

    public MessageDispatcher(UsersRepo usersRepo) {
        this.usersRepo = usersRepo;
    }

    public SendMessage getResponseByRegularMessage(Long chatId, String messageText) {
        Integer userId = null;

//        if (usersWithChatIds.containsKey(chatId)) {
//            userId = usersWithChatIds.get(chatId);
//        } else {
//            UserDto userDto = userResource.getUserByChatId(update.getMessage().getChatId());
//            if (userDto != null && userDto.getId() != null) {
//                userId = userDto.getId();
//            } else return; // TODO: добавить обработку ошибки
//        }
//
//        StorageDto storage = storageResource.getStorageByUserId(userId);


        User user = usersRepo.findByChatId(chatId);
        Integer currentQId = user.getCurrentQId();
        List<Integer> historyArray = new ArrayList<>(usersRepo.findByChatId(chatId).getHistoryArray());
        String text = null;
        ReplyKeyboard keyboard = null;

        switch (messageText) {
            case "Next":
            case "/next":
//                currentQId = RandomUtil.inRangeExcludeList(1, maxQNumber, historyArray);
                if (currentQId == 0) {
                    text = THATS_ALL.toString();
                    keyboard = getDefaultKeyboard();
                } else {
//                    Question question = questionsRepo.findById(currentQId).get();
//                    historyArray.add(currentQId);
//                    text = question.getQuestion();
                    keyboard = getInlineKeyboard(new String[][]{{"Skip", "Expand description"}});
                }
                log.debug("Execute button NEXT");
                break;
            case "Statistics":
            case "/statistics":
                keyboard = getDefaultKeyboard();
                break;
            case "Reset":
            case "/reset":
                historyArray = Collections.emptyList();
                text = RESET_SUCCESSFUL.toString();
                keyboard = getDefaultKeyboard();
                log.debug("Execute button RESET");
                break;
            case "Info":
            case "/info":
                log.debug("Execute button INFO");
                text = INFO_MESSAGE.toString().replaceAll("\\{name}", user.getName());
                keyboard = getDefaultKeyboard();
                break;
            default:
                log.debug("BAD BUTTON");
                text = BAD_REQUEST.toString();
                keyboard = getDefaultKeyboard();
        }

        user.setCurrentQId(currentQId);
        user.setHistoryArray(historyArray);
        usersRepo.save(user);
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .replyMarkup(keyboard)
                .text(text)
                .build();
        sendMessage.enableMarkdown(true);
        sendMessage.disableWebPagePreview();
        return sendMessage;
    }

    /**
     * Callback always work after showing the question
     *
     * @param chatId
     * @param messageText
     * @return
     */
    
    public SendMessage getResponseByCallback(Long chatId, String messageText) {
        User user = usersRepo.findByChatId(chatId);
//        Question question = questionsRepo.findById(user.getCurrentQId()).get();
        String text = null;
        ReplyKeyboard keyboard = getDefaultKeyboard();

        switch (messageText) {
            case "Expand description":
//                text = question.getDescription(); // TODO: показать ответ
                log.debug("Execute button Expand description");
                break;
            case "Skip":
                SendMessage response = getResponseByRegularMessage(chatId, "/next");
                text = response.getText();
                keyboard = response.getReplyMarkup();
                break;
            default:
                text = BAD_REQUEST.toString();
        }
        if (text == null) text = "!";
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .replyMarkup(keyboard)
                .text(text)
                .build();
        sendMessage.enableMarkdown(true);
        return sendMessage;
    }

    /**
     * Inline keyboard - buttons below questions
     *
     * @param strings
     * @return
     */
    private InlineKeyboardMarkup getInlineKeyboard(String[][] strings) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        for (String[] ss : strings) {
            List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
            for (String s : ss) {
                InlineKeyboardButton inlineKeyboardButton = InlineKeyboardButton.builder()
                        .text(s)
                        .callbackData(s)
                        .build();
                keyboardButtonsRow.add(inlineKeyboardButton);
            }
            rowList.add(keyboardButtonsRow);
        }

        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    /**
     * Standart keyboard, instead regular qwerty
     *
     * @param strings
     * @return
     */
    private ReplyKeyboardMarkup getKeyboard(String[][] strings) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> rowList = new ArrayList<>();

        for (String[] ss : strings) {
            KeyboardRow row = new KeyboardRow();
            row.addAll(Arrays.asList(ss));
            rowList.add(row);
        }

        keyboardMarkup.setKeyboard(rowList);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);
        return keyboardMarkup;
    }

    /**
     * Default regular keyboard
     *
     * @return
     */
    private ReplyKeyboardMarkup getDefaultKeyboard() {
        return getKeyboard(new String[][]{
                {"Next"},
                {"Statistics"},
                {"Reset"},
                {"Info"}
        });
    }

    /**
     * Register in db through spring data
     *
     * @param chatId
     * @param userName
     */
    public void registerIfAbsent(Long chatId, String userName) {
        if (usersRepo.existsByChatId(chatId)) return;
        else usersRepo.save(new User(chatId, userName, 0, new ArrayList<>()));
    }

    /**
     * @param chatId
     * @param name
     * @return
     */
    public SendMessage getWelcomeMessage(Long chatId, String name) {
        return SendMessage.builder()
                .chatId(chatId)
                .replyMarkup(getDefaultKeyboard())
                .text(WELCOME_MESSAGE.toString().replaceAll("\\{name}", name))
                .build();
    }

}
