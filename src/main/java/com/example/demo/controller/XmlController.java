package com.example.demo.controller;

import com.example.demo.dto.xml.XmlCommandDto;
import com.example.demo.service.ExchangeService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/xml_api/command")
public class XmlController {

    private final ExchangeService exchangeService;

    public XmlController(ExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    //call it in git bash
    // curl -X POST -H 'Content-Type: application/xml' -d '<command id="1234"><get consumer="13617162" ><currency>EUR</currency></get></command>' http://localhost:8080/xml_api/command
    @PostMapping(consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<?> getCurrentData(@RequestBody XmlCommandDto request) {
        if (request.getGetConsumer() != null) {
            return ResponseEntity.ok(exchangeService.getCurrentExchangeXml(request));
        } else {
            return ResponseEntity.ok(exchangeService.getHistoryXml(request));
        }
    }

    // curl -X POST -H 'Content-Type: application/xml' -d '<command id="1234"><history consumer="13617162" currency="EUR" period="24"/></command>' http://localhost:8080/xml_api/command
//    @PostMapping(consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
//    public ResponseEntity<?> getHistory(@RequestBody XmlHistoryInputDto request) {
//        return ResponseEntity.ok(exchangeService.getHistoryXml(request));
//    }
}







