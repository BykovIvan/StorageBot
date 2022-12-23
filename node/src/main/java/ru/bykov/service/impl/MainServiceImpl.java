package ru.bykov.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.bykov.dao.AppUserDAO;
import ru.bykov.dao.RawDataDAO;
import ru.bykov.entity.AppDocument;
import ru.bykov.entity.AppUser;
import ru.bykov.entity.RawData;
import ru.bykov.exceptions.UploadFileException;
import ru.bykov.service.FileService;
import ru.bykov.service.MainService;
import ru.bykov.service.ProducerService;
import ru.bykov.service.enums.ServiceCommand;

import static ru.bykov.entity.enums.UserState.BASIC_STATE;
import static ru.bykov.entity.enums.UserState.WAIT_FOR_EMAIL_STATE;
import static ru.bykov.service.enums.ServiceCommand.*;

@Service
@Log4j
@RequiredArgsConstructor
public class MainServiceImpl implements MainService {
    private final RawDataDAO rawDataDAO;
    private final ProducerService producerService;
    private final AppUserDAO appUserDAO;
    private final FileService fileService;

    @Override
    public void processTextMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var userState = appUser.getState();
        var text = update.getMessage().getText();
        var output = "";
        var serviceCommand = ServiceCommand.fromValue(text);
        if (CANSEL.equals(serviceCommand)) {
            output = canselProcess(appUser);
        } else if (BASIC_STATE.equals(userState)) {
            output = processServiceCommand(appUser, text);
        } else if (WAIT_FOR_EMAIL_STATE.equals(userState)) {
            //TODO Добавить обработку емайла
        } else {
            log.error("Unknown user state : " + userState);
            output = "Неизвестная ошибка! Введите /cansel и попробуйте снова!";
        }

        var chatId = update.getMessage().getChatId();
        sendAnswer(output, chatId);


    }

    @Override
    public void processDocMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();

        if (isNotAllowToSendContent(chatId, appUser)){
            return;
        }
        try {
            AppDocument doc = fileService.processDoc(update.getMessage());
            //TODO добавить сохранение документа :)
            var answer = "Документ успешно загружен! " +
                    "Ссылка для фото: http://test.ru/get-doc/777";
            sendAnswer(answer, chatId);
        } catch (UploadFileException e) {
            log.error(e);
            String error = "К сожалению, загрузка файла не удалась. Повторите попытку позже!";
            sendAnswer(error, chatId);
        }


    }

    private boolean isNotAllowToSendContent(Long chatId, AppUser appUser) {
        var userState = appUser.getState();
        if (!appUser.getIsActive()){
            var error = "Зарегистрируйтесь или активируйте свою учетную запись для загрузки контента";
            sendAnswer(error, chatId);
            return true;
        } else if (!BASIC_STATE.equals(userState)){
            var error = "Зарегистрируйтесь или активируйте свою учетную запись для загрузки контента";
            sendAnswer(error, chatId);
            return true;
        }
        return false;
    }

    @Override
    public void processPhotoMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();

        if (isNotAllowToSendContent(chatId, appUser)){
            return;
        }
        //TODO добавить сохранение фото :)
        var answer = "Фото успешно загружен! Ссылка для фото: http://test.ru/get-photo/777";
        sendAnswer(answer, chatId);
    }

    private void sendAnswer(String output, Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);
        producerService.producerAnswer(sendMessage);
    }

    private String processServiceCommand(AppUser appUser, String cmd) {
        if (REGISTRATION.equals(cmd)){
            //TODO добавить регистрацию
            return "Временно не доступно!";
        } else if (HELP.equals(cmd)){
            return help();
        } else if (START.equals(cmd)){
            return "Приветствую! Чтобы посмотреть список доступных команд введите /help";
        } else {
            return "Неизвестная команда! Чтобы посмотреть список доступных команд введите /help";
        }
    }

    private String help() {
        return "Список доступных команд:\n" +
                "/cansel - отмена выполнения текущий команды;\n" +
                "/registration - регистрация пользователя";
    }

    private String canselProcess(AppUser appUser) {
        appUser.setState(BASIC_STATE);
        appUserDAO.save(appUser);
        return "Команда отменена!";
    }


    private AppUser findOrSaveAppUser(Update update) {
        User telegramUser = update.getMessage().getFrom();
        AppUser persistentAppUser = appUserDAO.findAppUserByTelegramUserId(telegramUser.getId());
        if (persistentAppUser == null) {
            AppUser transientAppUser = AppUser.builder()
                    .telegramUserId(telegramUser.getId())
                    .username(telegramUser.getUserName())
                    .firstName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    //TODO изменить значения по умолчанию после регистрации
                    .isActive(true)
                    .state(BASIC_STATE)
                    .build();
            return appUserDAO.save(transientAppUser);
        }
        return persistentAppUser;
    }

    private void saveRawData(Update update) {
        RawData rawData = RawData.builder()
                .event(update)
                .build();
        rawDataDAO.save(rawData);
    }
}
