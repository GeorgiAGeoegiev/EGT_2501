package com.example.demo.service;


import com.example.demo.dao.CurrencyDao;
import com.example.demo.dao.RatesDao;
import com.example.demo.dto.fixerio.FixerIoResponseDto;
import com.example.demo.exception.FailedFixerIoDataFetchException;
import com.example.demo.repository.ExchangeRatesRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


//import static com.example.demo.dto.fixerio.FixerIoRateDto.fixerIoRateDtoToRatesDao;

@Component
public class ScheduledRead {
    public static final Logger logger = Logger.getLogger(ScheduledRead.class.getName());

    private static final String FIXER_API_URL = "https://data.fixer.io/api/latest";
    //fixer.io base does not seem to be returning a valid response (some kind of error on their API) so I'm running just the default one without base and target
    //    public static final String FIXER_URL_FORMAT = "%s?access_key=%s&base=%s&symbols=%s";
    public static final String FIXER_URL_FORMAT = "%s?access_key=%s";

    @Value("${fixer.api.key}")
    private String apiKey;

    //fixer.io base does not seem to be returning a valid response (some kind of error on their API) so I'm running just the default one without base and target
//    @Value("${fixer.api.base}")
//    private String apiBase;
//
//    @Value("${fixer.api.target}")
//    private String apiTarget;

    private RestTemplate restTemplate = new RestTemplate();

    public ScheduledRead(ExchangeRatesRepository exchangeRatesRepository) {
        this.exchangeRatesRepository = exchangeRatesRepository;
    }

    private final ExchangeRatesRepository exchangeRatesRepository;

    //For local testing as to not expire all the free requests from fixer.io
    //    @Scheduled(fixedDelayString = "${fixer.rate-in-millis:50000}")
    @Scheduled(fixedDelayString = "${fixer.rate-in-millis:3600000}")
    /*
        waits for the call to fixer to finish
     */
    public void readFromFixer() {
        //fixer.io base does not seem to be returning a valid response (some kind of error on their API) so I'm running just the default one without base and target
//        String url = String.format(FIXER_URL_FORMAT, FIXER_API_URL, apiKey, apiBase, apiTarget);
        String url = String.format(FIXER_URL_FORMAT, FIXER_API_URL, apiKey);
        ResponseEntity<FixerIoResponseDto> fixerIoResponse = restTemplate.getForEntity(url, FixerIoResponseDto.class);
        FixerIoResponseDto fixerIoResponseDto = fixerIoResponse.getBody();
        if (fixerIoResponseDto == null) {
            logger.info("Failed to get response from fixer.io");
            throw new FailedFixerIoDataFetchException("Failed to get response from fixer.io");
        }
        List<RatesDao> rates = new ArrayList<>();
        for (Map.Entry<String, Double> fixerIoRate : fixerIoResponseDto.rates().entrySet()) {
            rates.add(new RatesDao(null, fixerIoRate.getKey(), fixerIoRate.getValue()));
        }

        CurrencyDao response = new CurrencyDao(null, fixerIoResponseDto.base(), LocalDateTime.ofInstant(Instant.ofEpochSecond(fixerIoResponseDto.timestamp()), ZoneId.systemDefault()), rates);
        exchangeRatesRepository.save(response);
        logger.info("Successful saved currency exchange rates");
        //For local testing as to not expire all the free requests from fixer.io

//        Random random = new Random();
//
//        CurrencyDao currencyDao = new CurrencyDao(null, "usd", LocalDateTime.now(), List.of(new RatesDao(null, "eur", random.nextDouble()), new RatesDao(null, "chd", random.nextDouble())));
//        exchangeRatesRepository.save(currencyDao);
    }
}

