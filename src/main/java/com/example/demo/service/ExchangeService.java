package com.example.demo.service;

import com.example.demo.dao.CurrencyDao;
import com.example.demo.dto.CurrencyResponseDto;
import com.example.demo.dto.CurrentExchangeRequestDto;
import com.example.demo.dto.HistoryProcessingDto;
import com.example.demo.dto.StatisticDataDto;
import com.example.demo.dto.json.JsonCurrentInputDto;
import com.example.demo.dto.json.JsonHistoryInputDto;
import com.example.demo.dto.xml.XmlCommandDto;
import com.example.demo.exception.DuplicatedIdException;
import com.example.demo.exception.EmptyRecordException;
import com.example.demo.repository.ExchangeRatesRepository;
import com.example.demo.repository.StatisticRepository;
import com.example.demo.tasks.GatherStatisticsInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.example.demo.configuration.SpringCacheConfiguration.REQUEST_IDS_CACHE_NAME;
import static com.example.demo.dto.CurrencyResponseDto.currencyResponseDtoFromCurrencyDao;

@Service
public class ExchangeService implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(ExchangeService.class);

    public static final String JSON_SERVICE = "EXT_SERVICE_1";
    public static final String XML_SERVICE = "EXT_SERVICE_2";

    //As it is I/O bound executorService it does little computation so large number of threads
    //does not hurt the performance as it is not like cpu bound tasks
    private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

    private final ExchangeRatesRepository exchangeRatesRepository;
    private final StatisticRepository statisticRepository;
    private final RabbitMQService rabbitMQService;
    private final CacheManager cacheManager;

    private Cache redisRequestIdsCache;

    public ExchangeService(ExchangeRatesRepository exchangeRatesRepository, StatisticRepository statisticRepository, RabbitMQService rabbitMQService, CacheManager cacheManager) {
        this.exchangeRatesRepository = exchangeRatesRepository;
        this.statisticRepository = statisticRepository;
        this.rabbitMQService = rabbitMQService;
        this.cacheManager = cacheManager;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        redisRequestIdsCache = cacheManager.getCache(REQUEST_IDS_CACHE_NAME);
    }

    public CurrencyResponseDto getCurrentExchangeJson(JsonCurrentInputDto currentInputDto) {

        return getCurrentExchange(
                CurrentExchangeRequestDto.currentExchangeRequestFromJsonDto(currentInputDto), JSON_SERVICE);
    }

    public CurrencyResponseDto getCurrentExchangeXml(XmlCommandDto xmlCommandDto) {

        return getCurrentExchange(
                CurrentExchangeRequestDto.currentExchangeRequestFromXmlDto(xmlCommandDto), XML_SERVICE);
    }

    public List<CurrencyResponseDto> getHistoryXml(XmlCommandDto xmlCommandDto) {
        return getHistory(HistoryProcessingDto.historyProcessingDtoFromXml(xmlCommandDto), XML_SERVICE);
    }

    public List<CurrencyResponseDto> getHistoryJson(JsonHistoryInputDto historyInputDto) {
        return getHistory(HistoryProcessingDto.historyProcessingDtoFromJson(historyInputDto), JSON_SERVICE);
    }

    private CurrencyResponseDto getCurrentExchange(CurrentExchangeRequestDto currentInputDto, String service) {

        validateRequestId(currentInputDto.requestId());

        //db call to latest exchange if exceptions occur here do not schedule a statistics job
        CurrencyResponseDto existingEntry = getLatestExchange(currentInputDto.currency());

        //schedule a job for writing the statistics
        //non-blocking
        submitStatisticData(new StatisticDataDto(currentInputDto.requestId(), currentInputDto.timestamp(), currentInputDto.client(), service));

        logger.info("Latest exchange rates fetched successfully");
        return existingEntry;
    }

    private List<CurrencyResponseDto> getHistory(HistoryProcessingDto historyProcessingDto, String service) {

        validateRequestId(historyProcessingDto.requestId());

        //db call to get history if exceptions occur her do not schedule a statistic job
        List<CurrencyResponseDto> history = getHistoryForPeriod(historyProcessingDto.currency(), historyProcessingDto.period());

        submitStatisticData(new StatisticDataDto(historyProcessingDto.requestId(), historyProcessingDto.timestamp(), historyProcessingDto.client(), service));

        logger.info("History records fetched successfully");
        return history;
    }

    private void validateRequestId(String requestId) {
        // Check the redis cache first that is kept small
        // but there could be millions of requests so call and check in the DB index
        Cache.ValueWrapper valueWrapper = redisRequestIdsCache.get(requestId);
        if ((valueWrapper != null && ((boolean) valueWrapper.get()))
                || statisticRepository.existsByRequestId(requestId)) {
            logger.error("Duplicated id detected");
            throw new DuplicatedIdException("Request with ID: '%s' is already processing".formatted(requestId));
        }
        //cache it right away and by design only the TTL will evict those entries
        redisRequestIdsCache.put(requestId, true);
    }

    private void submitStatisticData(StatisticDataDto statisticDataDto) {
        executorService.submit(new GatherStatisticsInfo(statisticRepository, rabbitMQService, statisticDataDto));
    }

    private List<CurrencyResponseDto> getHistoryForPeriod(String baseCurrency, int period) {
        LocalDateTime endDateTime = LocalDateTime.now();
        LocalDateTime startDateTime = endDateTime.minusHours(period);
        List<CurrencyDao> allByBaseAndDateBetween = exchangeRatesRepository.findAllByBaseAndDateBetween(baseCurrency, startDateTime, endDateTime);

        List<CurrencyResponseDto> historyResult = new ArrayList<>();
        for (CurrencyDao currencyDao : allByBaseAndDateBetween) {
            historyResult.add(currencyResponseDtoFromCurrencyDao(currencyDao));
        }

        return historyResult;
    }

    private CurrencyResponseDto getLatestExchange(String currency) {
        CurrencyDao currencyDaoResponse = exchangeRatesRepository.findFirstByBaseOrderByDateDesc(currency);
        if (currencyDaoResponse == null) {
            logger.info("No records in DB");
            throw new EmptyRecordException("No records for the requested parameters");
        }
        return new CurrencyResponseDto(
                currencyDaoResponse.getBase(),
                currencyDaoResponse.getRates(),
                LocalDateTime.now()
        );
    }
}
