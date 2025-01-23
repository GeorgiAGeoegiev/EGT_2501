package com.example.demo.dto.json;


public record JsonHistoryInputDto(String requestId, long timestamp, String client, String currency, int period) {
}
