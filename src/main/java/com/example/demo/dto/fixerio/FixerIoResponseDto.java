package com.example.demo.dto.fixerio;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

public record FixerIoResponseDto(boolean success, Long timestamp, String base, LocalDate date,
                                 HashMap<String, Double> rates) {
}
