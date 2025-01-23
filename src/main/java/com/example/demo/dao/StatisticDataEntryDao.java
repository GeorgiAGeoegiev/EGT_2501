package com.example.demo.dao;

import com.example.demo.dto.StatisticDataDto;
import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "requestsStatistics")
public class StatisticDataEntryDao implements Serializable {

    public StatisticDataEntryDao() {}

    public StatisticDataEntryDao(String requestId, long time, String clientId, String serviceName) {
        this.requestId = requestId;
        this.time = time;
        this.clientId = clientId;
        this.serviceName = serviceName;
    }

    @Id
    @Column(name = "request_id")
    private String requestId;
    @Temporal(TemporalType.TIMESTAMP)
    private long time;
    @Column(name = "client_id")
    private String clientId;
    @Column(name = "service_name")
    private String serviceName;

    public static StatisticDataEntryDao fromStatisticsDataDto(StatisticDataDto statisticDataDto) {
        return new StatisticDataEntryDao(statisticDataDto.requestId(), statisticDataDto.time(), statisticDataDto.clientId(), statisticDataDto.serviceName());
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

}
