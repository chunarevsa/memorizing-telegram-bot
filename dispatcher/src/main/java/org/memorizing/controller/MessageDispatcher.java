package org.memorizing.controller;

import org.apache.log4j.Logger;
import org.memorizing.botinstance.UserDto;
import org.memorizing.entity.EMenu;
import org.memorizing.entity.User;
import org.memorizing.repository.StorageResource;
import org.memorizing.repository.UserResource;
import org.memorizing.repository.UsersRepo;
import org.memorizing.utils.cardApi.CardStockDto;
import org.memorizing.utils.cardApi.StorageDto;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.*;

import static org.memorizing.entity.Constants.*;

@Component
public class MessageDispatcher {
    private static final Logger log = Logger.getLogger(MessageDispatcher.class);

    private final UsersRepo usersRepo;
    private final UserResource userResource;
    private final StorageResource storageResource;

    public MessageDispatcher(UsersRepo usersRepo, UserResource userResource, StorageResource storageResource) {
        this.usersRepo = usersRepo;
        this.userResource = userResource;
        this.storageResource = storageResource;
    }

    public SendMessage getResponseByRegularMessage(Long chatId, String messageText) {
        User user = usersRepo.findByChatId(chatId);
        EMenu menu = EMenu.valueOf(user.getCurrentMenuName());

        List<Integer> historyArray = new ArrayList<>(usersRepo.findByChatId(chatId).getHistoryArray());
        String text = null;
        ReplyKeyboard keyboard = null;

        switch (messageText) {
            case "Add card stock":
            case "/addCardStock":
//                if (currentQId == 0) {
//                    text = THATS_ALL.toString();
//                    keyboard = getKeyboardByMenu(menu);
//                } else {
//                    keyboard = getInlineKeyboard(new String[][]{{"Skip", "Expand description"}});
//                }
                keyboard = getKeyboardByMenu(menu);
                log.debug("Execute button NEXT");
                break;
            case "Info":
            case "/info":
                log.debug("Execute button INFO");
                text = INFO_MESSAGE.toString().replaceAll("\\{name}", user.getName());
                keyboard = getKeyboardByMenu(menu);
                break;
            default:
                log.debug("BAD BUTTON");
                text = BAD_REQUEST.toString();
                keyboard = getKeyboardByMenu(menu);
        }

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
        EMenu menu = EMenu.valueOf(user.getCurrentMenuName());
        EMenu nextMenu = menu.getNext();
        ReplyKeyboard keyboard = getInlineKeyboardByMenu(chatId, nextMenu, messageText);

        String text = null;

        // messageText = EnglishRussian
        // Может сначала получить все данные, потом их распихивать уже по методам

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

    private ReplyKeyboard getInlineKeyboardByMenu(Long chatId, EMenu menu, String messageText) {
        InlineKeyboardMarkup inlineKeyboard;
        switch (menu) {
            case MAIN:
                // TODO: temp
                inlineKeyboard = getInlineKeyboardForCardStocksMenu(chatId);
                break;
            case CARD_STOCKS:
                inlineKeyboard = getInlineKeyboardForCardStocksMenu(chatId);
                break;
            case CARDS:
                inlineKeyboard = getInlineKeyboardForCardsMenu(chatId, messageText);
                break;
            default:
                inlineKeyboard = null;
        }
        return inlineKeyboard;

    }

    private InlineKeyboardMarkup getInlineKeyboardForCardsMenu(Long chatId, String keyTypePlusKeyValue) {
        User user = usersRepo.findByChatId(chatId);
        Integer storageId = user.getStorageId();
        InlineKeyboardMarkup inlineKeyboard = null;
        List<CardStockDto> cardStocks = storageResource.getCardStocksByStorageId(storageId);
        String[] split = keyTypePlusKeyValue.split("/");
        String keyType = split[0];
        Optional<CardStockDto> cardStockDto = cardStocks.stream().filter(it -> Objects.equals(it.getKeyType(), keyType)).findAny();
        if (cardStockDto.isEmpty()) {
            return null;
        }

        List<CardDto> cards = storageResource.getCardsByCardStockId(cardStockDto.get().getId());
        if (!cards.isEmpty()) {
            String[][] cardValues = new String[1][cards.size()];
            for (int i = 0; i < cards.size(); i++) {
                cardValues[0][i] = cards.get(i).getCardKey();
            }
            inlineKeyboard = getInlineKeyboard(cardValues);
        }

        return inlineKeyboard;
    }

    public InlineKeyboardMarkup getInlineKeyboardForCardStocksMenu(Long chatId) {
        User user = usersRepo.findByChatId(chatId);
        List<CardStockDto> cardStocks = storageResource.getCardStocksByStorageId(user.getStorageId());
        InlineKeyboardMarkup inlineKeyboard = null;
        if (!cardStocks.isEmpty()) {
            String[][] cardStockTypes = new String[1][cardStocks.size()];
            for (int i = 0; i < cardStocks.size(); i++) {
                cardStockTypes[0][i] = cardStocks.get(i).getKeyType() + "/" + cardStocks.get(i).getValueType();
            }
            inlineKeyboard = getInlineKeyboard(cardStockTypes);
        }

        return inlineKeyboard;
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
     * Register in db through spring data
     *
     * @param chatId
     * @param userName
     */
    public void registerIfAbsent(Long chatId, String userName) {
        if (!usersRepo.existsByChatId(chatId)) {
            UserDto userDto = userResource.getUserByChatId(chatId);
            StorageDto storageDto = storageResource.getStorageByUserId(userDto.getId());
            usersRepo.save(new User(chatId, userName, 0, EMenu.MAIN.name(), new ArrayList<>(), storageDto.getId()));
        }
    }

    public SendMessage getMessageForMainMenu(Long chatId, String data) {
        User user = usersRepo.findByChatId(chatId);
        List<CardStockDto> cardStocks = storageResource.getCardStocksByStorageId(user.getStorageId());
        InlineKeyboardMarkup inlineKeyboard = getInlineKeyboardForCardStocksMenu(chatId);

        if (inlineKeyboard == null) {
            return SendMessage.builder()
                    .chatId(user.getChatId())
                    .text(WELCOME_MESSAGE.toString().replaceAll("\\{name}", user.getName()) + "Тут пустой список")
                    .build();
        } else {
            return SendMessage.builder()
                    .chatId(user.getChatId())
                    .replyMarkup(inlineKeyboard)
                    .text(WELCOME_MESSAGE.toString().replaceAll("\\{name}", user.getName()))
                    .build();
        }
    }


    /**
     * @param chatId
     * @param name
     * @return
     */
    public SendMessage getWelcomeMessage(Long chatId, String name) {
        return SendMessage.builder()
                .chatId(chatId)
                .replyMarkup(getKeyboardByMenu(EMenu.CARD_STOCKS))
                .text(WELCOME_MESSAGE.toString().replaceAll("\\{name}", name))
                .build();
    }

    public ReplyKeyboardMarkup getKeyboardByMenu(EMenu menu) {
        ReplyKeyboardMarkup keyboard = null;

        switch (menu) {
            case MAIN:
                keyboard = getKeyboard(new String[][]{
                        {"Add card stock"},
                        {"Info"}
                });
                break;
            case CARD_STOCKS:
                keyboard = getKeyboard(new String[][]{
                        {"Add card stock"},
                        {"Info"}
                });
                break;
            case CARD_STOCK:
                keyboard = getKeyboard(new String[][]{
                        {"Start studying"},
                        {"Show cards"},
                        {"Edit card stock"},
                        {"Delete card stock"},
                        {"Info"},
                        {"Go to back"},
                });
                break;
            case CARDS:
                keyboard = getKeyboard(new String[][]{
                        {"Add card"},
                        {"Info"},
                        {"Go to back"},
                });
                break;
            case CARD:
                keyboard = getKeyboard(new String[][]{
                        {"Edit card"},
                        {"Delete card"},
                        {"Info"},
                        {"Go to back"},
                });
                break;
            default:
                keyboard = getKeyboard(new String[][]{
                        {"Info"},
                        {"Go to back"},
                });
                break;
        }
        return keyboard;
    }

}
