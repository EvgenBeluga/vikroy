package com.vikroy.controller;

import com.vikroy.entity.MeasurementProfile;
import com.vikroy.entity.User;
import com.vikroy.service.PatternService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pattern")
public class PatternController {

    private final PatternService patternService;
    private final PdfExportService pdfExportService; // Обертка над оригинальным PdfExporter

    @PostMapping("/calculate")
    @PreAuthorize("hasAnyRole('USER', 'PREMIUM')")
    public ResponseEntity<PatternService.PatternData> calculate(
            @RequestBody MeasurementProfile measurements,
            @AuthenticationPrincipal User user) {

        measurements.setUser(user);
        PatternService.PatternData data = patternService.build(measurements);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/pdf")
    @PreAuthorize("hasRole('PREMIUM')") // Только для подписчиков!
    public ResponseEntity<byte[]> downloadPdf(@AuthenticationPrincipal User user) {
        // 1. Получаем последние сохраненные мерки пользователя из БД
        MeasurementProfile m = measurementRepository.findFirstByUserOrderByidDesc(user)
                .orElseThrow(() -> new RuntimeException("Мерки не найдены"));

        // 2. Генерируем PDF (адаптированная логика из оригинального PdfExporter)
        byte[] pdfBytes = pdfExportService.generatePdf(m);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=vikroy_pattern.pdf")
                .body(pdfBytes);
    }
}
