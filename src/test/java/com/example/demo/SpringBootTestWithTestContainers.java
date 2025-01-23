package com.example.demo;

import com.example.demo.dao.CurrencyDao;
import com.example.demo.dao.RatesDao;
import com.example.demo.dto.CurrencyResponseDto;
import com.example.demo.dto.json.JsonCurrentInputDto;
import com.example.demo.dto.json.JsonHistoryInputDto;
import com.example.demo.dto.xml.XmlCommandDto;
import com.example.demo.exception.DuplicatedIdException;
import com.example.demo.exception.EmptyRecordException;
import com.example.demo.repository.ExchangeRatesRepository;
import com.example.demo.repository.StatisticRepository;
import com.example.demo.service.ExchangeService;
import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.demo.configuration.SpringCacheConfiguration.CURRENCIES_CACHE_NAME;
import static com.example.demo.configuration.SpringCacheConfiguration.REQUEST_IDS_CACHE_NAME;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SpringBootTestWithTestContainers {

    @LocalServerPort
    private Integer port;

    @Autowired
    ExchangeService exchangeService;

    @Autowired
    ExchangeRatesRepository exchangeRatesRepository;

    @Autowired
    CacheManager cacheManager;

    @Autowired
    StatisticRepository statisticRepository;

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:16-alpine");

    static RabbitMQContainer rabbitMQ = new RabbitMQContainer(
            DockerImageName.parse("rabbitmq:3.7.25-management-alpine"));

    static RedisContainer redis = new RedisContainer(
            DockerImageName.parse("redis:6.2.6")).withExposedPorts(6379);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getRedisPort);
        registry.add("spring.rabbitmq.host", rabbitMQ::getHost);
        registry.add("spring.rabbitmq.port", rabbitMQ::getAmqpPort);
    }

    @BeforeAll
    static void beforeAll() {
        postgres.start();
        rabbitMQ.start();
        redis.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
        rabbitMQ.stop();
        redis.stop();
    }

    @BeforeEach
    void beforeEach() {
        setupTestRecords();
    }

    @AfterEach
    void afterEach() throws InterruptedException {
        exchangeRatesRepository.deleteAll();
        cacheManager.getCache(CURRENCIES_CACHE_NAME).invalidate();
        cacheManager.getCache(REQUEST_IDS_CACHE_NAME).invalidate();
        statisticRepository.deleteAll();
    }

    @Test
    void testGetCurrentExchangeJson() {
        JsonCurrentInputDto inputDto = new JsonCurrentInputDto("mockRequest", 1111111L, "mockClient", "EUR");
        CurrencyResponseDto response = exchangeService.getCurrentExchangeJson(inputDto);
        assertNotNull(response);
    }

    @Test
    void testGetCurrentExchangeXml() {
        XmlCommandDto inputDto = new XmlCommandDto();
        inputDto.setId("mockId");
        XmlCommandDto.XmlGet getConsumer = new XmlCommandDto.XmlGet("consumer1", "EUR");
        inputDto.setGetConsumer(getConsumer);
        CurrencyResponseDto response = exchangeService.getCurrentExchangeXml(inputDto);
        assertNotNull(response);
    }

    @Test
    void testGetHistoryJson() {
        JsonHistoryInputDto inputDto = new JsonHistoryInputDto("mockId", 1111111L, "mockClient", "EUR", 24);
        List<CurrencyResponseDto> response = exchangeService.getHistoryJson(inputDto);
        assertNotNull(response);
        assertFalse(response.isEmpty());
    }

    @Test
    void testGetHistoryXml() {
        XmlCommandDto inputDto = new XmlCommandDto();
        inputDto.setId("mockId");
        XmlCommandDto.XmlHistory historyConsumer = new XmlCommandDto.XmlHistory("consumer2", "EUR", 30);
        inputDto.setHistoryConsumer(historyConsumer);
        inputDto.setGetConsumer(new XmlCommandDto.XmlGet("consumer1", "EUR"));
        List<CurrencyResponseDto> response = exchangeService.getHistoryXml(inputDto);
        assertNotNull(response);
        assertFalse(response.isEmpty());
    }

    @Test
    void testDuplicateIdCurrentExchangeXml() {
        XmlCommandDto inputDto = new XmlCommandDto();
        inputDto.setId("mockId");
        XmlCommandDto.XmlGet getConsumer = new XmlCommandDto.XmlGet("consumer1", "EUR");
        inputDto.setGetConsumer(getConsumer);
        exchangeService.getCurrentExchangeXml(inputDto);
        assertThrows(DuplicatedIdException.class, () -> exchangeService.getCurrentExchangeXml(inputDto));
    }

    @Test
    void testDuplicateIdCurrentExchangeJson() {
        JsonCurrentInputDto inputDto = new JsonCurrentInputDto("mockRequest", 1111111L, "mockClient", "EUR");
        exchangeService.getCurrentExchangeJson(inputDto);
        assertThrows(DuplicatedIdException.class, () -> exchangeService.getCurrentExchangeJson(inputDto));
    }

    @Test
    void testDuplicateIdGetHistoryJson() {
        JsonHistoryInputDto inputDto = new JsonHistoryInputDto("mockId", 1111111L, "mockClient", "EUR", 24);
        exchangeService.getHistoryJson(inputDto);
        assertThrows(DuplicatedIdException.class, () -> exchangeService.getHistoryJson(inputDto));

    }

    @Test
    void testDuplicateIdGetHistoryXml() {
        XmlCommandDto inputDto = new XmlCommandDto();
        inputDto.setId("mockId");
        XmlCommandDto.XmlHistory historyConsumer = new XmlCommandDto.XmlHistory("consumer2", "EUR", 30);
        inputDto.setHistoryConsumer(historyConsumer);
        inputDto.setGetConsumer(new XmlCommandDto.XmlGet("consumer1", "EUR"));
        exchangeService.getHistoryXml(inputDto);
        assertThrows(DuplicatedIdException.class, () -> exchangeService.getHistoryXml(inputDto));
    }

    @Test
    void testNoRecordExceptionGetCurrentExchangeJson() {
        JsonCurrentInputDto inputDto = new JsonCurrentInputDto("mockRequest", 1111111L, "mockClient", "GBP");
        assertThrows(EmptyRecordException.class, () -> exchangeService.getCurrentExchangeJson(inputDto));
    }

    @Test
    void testNoRecordExceptionGetCurrentExchangeXml() {
        exchangeRatesRepository.deleteAll();
        XmlCommandDto inputDto = new XmlCommandDto();
        inputDto.setId("mockId");
        XmlCommandDto.XmlGet getConsumer = new XmlCommandDto.XmlGet("consumer1", "EUR");
        inputDto.setGetConsumer(getConsumer);
        CurrencyResponseDto response = exchangeService.getCurrentExchangeXml(inputDto);
        assertNotNull(response);
    }

    private void setupTestRecords() {
        RatesDao inputRateDao = new RatesDao(null, "BGN", 1.95583);
        List<RatesDao> inputRatesDao = List.of(inputRateDao);
        CurrencyDao inputCurrencyDao = new CurrencyDao(null, "EUR", LocalDateTime.now(), inputRatesDao);
        exchangeRatesRepository.save(inputCurrencyDao);
    }
}
