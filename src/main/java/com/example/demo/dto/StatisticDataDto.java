package com.example.demo.dto;

import java.io.Serializable;

public record StatisticDataDto(String requestId, long time, String clientId, String serviceName) implements Serializable {
}
