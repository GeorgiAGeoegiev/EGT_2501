package com.example.demo.service;

import com.example.demo.dto.StatisticDataDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQService {

    private final RabbitTemplate rabbitTemplate;

    public RabbitMQService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendRabbitMqMessage(StatisticDataDto statisticDataDto) {
        rabbitTemplate.convertAndSend(statisticDataDto);
    }
}
