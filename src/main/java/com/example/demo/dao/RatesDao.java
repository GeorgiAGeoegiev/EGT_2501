package com.example.demo.dao;

//import com.example.demo.dto.fixerio.FixerIoRateDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "rates")
public class RatesDao implements Serializable {

    public RatesDao() {}

    public RatesDao(Long id, String targetCurrency, Double exchangeRate) {
        this.id = id;
        this.targetCurrency = targetCurrency;
        this.exchangeRate = exchangeRate;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @JsonIgnore
    private Long id;

    @Column(name = "target_currency")
    private String targetCurrency;

    @Column(name = "exchange_rate")
    private Double exchangeRate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTargetCurrency() {
        return targetCurrency;
    }

    public void setTargetCurrency(String targetCurrency) {
        this.targetCurrency = targetCurrency;
    }

    public Double getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(Double exchangeRate) {
        this.exchangeRate = exchangeRate;
    }
}
