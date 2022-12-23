package ru.bykov.service;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.bykov.entity.AppDocument;

public interface FileService {
    AppDocument processDoc(Message externalMessage);
}
