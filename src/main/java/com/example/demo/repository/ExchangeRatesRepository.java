package com.example.demo.repository;

import com.example.demo.dao.CurrencyDao;
import com.example.demo.dto.CurrencyResponseDto;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.demo.configuration.SpringCacheConfiguration.CURRENCIES_CACHE_NAME;

@Repository
public interface ExchangeRatesRepository extends JpaRepository<CurrencyDao, Long> {

    @Cacheable(value = CURRENCIES_CACHE_NAME)
    CurrencyDao findFirstByBaseOrderByDateDesc(String base);

    @CachePut(value = CURRENCIES_CACHE_NAME, keyGenerator = "baseCurrenciesKeyGenerator")
    <S extends CurrencyDao> S save(S entity);

    List<CurrencyDao> findAllByBaseAndDateBetween(String base, LocalDateTime dateTimeStart, LocalDateTime dateTimeEnd);

}
