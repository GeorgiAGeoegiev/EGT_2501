package com.example.demo.controller;

import com.example.demo.dao.CurrencyDao;
import com.example.demo.dto.CurrencyResponseDto;
import com.example.demo.dto.json.JsonCurrentInputDto;
import com.example.demo.dto.json.JsonHistoryInputDto;
import com.example.demo.exception.DuplicatedIdException;
import com.example.demo.exception.EmptyRecordException;
import com.example.demo.exception.FailedFixerIoDataFetchException;
import com.example.demo.service.ExchangeService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/json_api")
public class JsonController {


    private final ExchangeService exchangeService;

    public JsonController(ExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    //call it in git bash
    // curl -X POST -H 'Content-Type: application/json' -d '{"requestId": "123123123-1231", "timestamp": 1737414058, "client": "joedoe", "currency": "eur"}' http://localhost:8080/json_api/current
    @PostMapping(value = "/current", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getCurrentData(@RequestBody JsonCurrentInputDto request) {
        try {
            CurrencyResponseDto currentResponse = exchangeService.getCurrentExchangeJson(request);
            return ResponseEntity.ok(currentResponse);
        } catch (DuplicatedIdException die) {
            return ResponseEntity.status(500).body(die.getMessage());
        } catch (EmptyRecordException ere) {
            return ResponseEntity.status(200).body(ere.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(418).build();
        }
    }

    @PostMapping("/history")
    public ResponseEntity<?> getHistory(@RequestBody JsonHistoryInputDto request) {
        return ResponseEntity.ok(exchangeService.getHistoryJson(request));
    }
}
