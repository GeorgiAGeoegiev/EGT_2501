package com.example.demo.configuration;

import com.example.demo.dao.CurrencyDao;
import com.example.demo.dao.StatisticDataEntryDao;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;

import java.lang.reflect.Method;
import java.time.Duration;

@EnableCaching
@Configuration
public class SpringCacheConfiguration {

    public static final String CURRENCIES_CACHE_NAME = "currencies";
    public static final String REQUEST_IDS_CACHE_NAME = "requestIds";

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return (builder) -> builder
                .withCacheConfiguration(CURRENCIES_CACHE_NAME,
                        RedisCacheConfiguration
                                .defaultCacheConfig()
                                .entryTtl(Duration.ofMinutes(60)))

                .withCacheConfiguration(REQUEST_IDS_CACHE_NAME,
                        RedisCacheConfiguration
                                .defaultCacheConfig()
                                //keep the cache small as thousands of requests could occur
                                .entryTtl(Duration.ofMinutes(1))
                );
    }

    @Bean
    public KeyGenerator baseCurrenciesKeyGenerator() {
        return new BaseCurrenciesKeyGenerator();
    }

    static class BaseCurrenciesKeyGenerator implements KeyGenerator {
        @Override
        public Object generate(Object target, Method method, Object... params) {
            CurrencyDao currencyDao = (CurrencyDao) params[0];
            return currencyDao.getBase();
        }
    }
}
