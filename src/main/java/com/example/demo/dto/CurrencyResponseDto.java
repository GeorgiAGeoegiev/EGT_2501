package com.example.demo.dto;

import com.example.demo.dao.CurrencyDao;
import com.example.demo.dao.RatesDao;

import java.time.LocalDateTime;
import java.util.List;

public record CurrencyResponseDto(String currencyBase, List<RatesDao> rates, LocalDateTime timestamp){
    public static CurrencyResponseDto currencyResponseDtoFromCurrencyDao(CurrencyDao currencyDao) {
        return new CurrencyResponseDto(currencyDao.getBase(), currencyDao.getRates(), currencyDao.getDate());
    }
}
