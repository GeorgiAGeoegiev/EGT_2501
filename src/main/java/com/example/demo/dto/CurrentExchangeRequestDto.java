package com.example.demo.dto;

import com.example.demo.dto.json.JsonCurrentInputDto;
import com.example.demo.dto.xml.XmlCommandDto;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public record CurrentExchangeRequestDto(String requestId, long timestamp, String client, String currency) {

    public static CurrentExchangeRequestDto currentExchangeRequestFromJsonDto(JsonCurrentInputDto jsonCurrentInputDto) {
        return new CurrentExchangeRequestDto(jsonCurrentInputDto.requestId(), jsonCurrentInputDto.timestamp(), jsonCurrentInputDto.client(), jsonCurrentInputDto.currency());
    }

    public static CurrentExchangeRequestDto currentExchangeRequestFromXmlDto(XmlCommandDto xmlInputDto) {
        return new CurrentExchangeRequestDto(xmlInputDto.getId(), Timestamp.from(LocalDateTime.now().toInstant(ZoneOffset.UTC)).getTime(), xmlInputDto.getGetConsumer().getConsumer(), xmlInputDto.getGetConsumer().getCurrency());
    }
}
