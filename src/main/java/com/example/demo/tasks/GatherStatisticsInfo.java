package com.example.demo.tasks;

import com.example.demo.dao.StatisticDataEntryDao;
import com.example.demo.dto.StatisticDataDto;
import com.example.demo.repository.StatisticRepository;
import com.example.demo.service.RabbitMQService;

public class GatherStatisticsInfo implements Runnable {

    private static final int MAX_TRIES = 5;

    private final StatisticRepository statisticRepository;
    private final StatisticDataDto entry;
    private final RabbitMQService rabbitMQService;

    public GatherStatisticsInfo(StatisticRepository statisticRepository, RabbitMQService rabbitMQService, StatisticDataDto entry) {
        this.statisticRepository = statisticRepository;
        this.rabbitMQService = rabbitMQService;
        this.entry = entry;
    }

    @Override
    public void run() {
        int triedTimes = 0;
        boolean isSuccessfulSave = false;
        do {
            try {
                statisticRepository.save(StatisticDataEntryDao.fromStatisticsDataDto(entry));
                isSuccessfulSave = true;
                break;
            } catch (Exception ex) {

                //if we want to give the db time to recover
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

        } while (triedTimes++ <= MAX_TRIES);

        if (isSuccessfulSave) {
            rabbitMQService.sendRabbitMqMessage(entry);
        }
    }
}
