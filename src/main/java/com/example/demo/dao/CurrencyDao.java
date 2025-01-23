package com.example.demo.dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;


import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table( name = "currencies")
public class CurrencyDao implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "currencies_id")
    @JsonIgnore
    private Long id;

    private String base;

    private LocalDateTime date;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "currencies_id")
    private List<RatesDao> rates;

    public CurrencyDao() {}

    public CurrencyDao(Long id, String base, LocalDateTime date, List<RatesDao> rates) {
        this.id = id;
        this.base = base;
        this.date = date;
        this.rates = rates;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public List<RatesDao> getRates() {
        return rates;
    }

    public void setRates(List<RatesDao> rates) {
        this.rates = rates;
    }
}

