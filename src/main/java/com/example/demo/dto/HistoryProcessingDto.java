package com.example.demo.dto;

import com.example.demo.dto.json.JsonHistoryInputDto;
import com.example.demo.dto.xml.XmlCommandDto;

public record HistoryProcessingDto(String requestId, long timestamp, String client, String currency, int period) {

    public static HistoryProcessingDto historyProcessingDtoFromJson(JsonHistoryInputDto jsonHistoryInputDto) {
        return new HistoryProcessingDto(jsonHistoryInputDto.requestId(), jsonHistoryInputDto.timestamp(), jsonHistoryInputDto.client(), jsonHistoryInputDto.currency(), jsonHistoryInputDto.period());
    }

    public static HistoryProcessingDto historyProcessingDtoFromXml(XmlCommandDto xmlHistoryInputDto) {
        return new HistoryProcessingDto(xmlHistoryInputDto.getId(), System.currentTimeMillis(), xmlHistoryInputDto.getHistoryConsumer().getConsumer(), xmlHistoryInputDto.getHistoryConsumer().getCurrency(), xmlHistoryInputDto.getHistoryConsumer().getPeriod());
    }
}
