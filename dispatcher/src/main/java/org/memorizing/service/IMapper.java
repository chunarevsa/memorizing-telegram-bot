package org.memorizing.service;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.net.ProtocolException;

public interface IMapper {
    /**
     * Сереализация обьекта.
     *
     * @param value Сериализуемый объект
     * @return Строковое представление объекта
     */
    String writeValueAsString(IMappable value) throws ProtocolException;

    /**
     * Десерелизация
     *
     * @param content   Строковое представление объекта
     * @param valueType Класс объекта
     * @return Объект заданного класса
     */
    <T extends IMappable> T readValue(String content, Class<T> valueType) throws ProtocolException, JsonProcessingException;


}
