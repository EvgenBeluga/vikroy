package com.vikroy.controller;

import com.vikroy.dto.MeasurementRequest;
import com.vikroy.dto.PatternResponse;
import com.vikroy.entity.MeasurementProfile;
import com.vikroy.entity.User;
import com.vikroy.repository.MeasurementProfileRepository;
import com.vikroy.service.PatternService;
import com.vikroy.service.PdfExportService;
import jakarta.validation.Valid;
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
    private final PdfExportService pdfExportService;
    private final MeasurementProfileRepository measurementRepository;

    public PatternController(PatternService patternService,
                             PdfExportService pdfExportService,
                             MeasurementProfileRepository measurementRepository) {
        this.patternService = patternService;
        this.pdfExportService = pdfExportService;
        this.measurementRepository = measurementRepository;
    }

    @PostMapping("/calculate")
    @PreAuthorize("hasAnyRole('USER', 'PREMIUM')")
    public ResponseEntity<PatternResponse> calculate(@Valid @RequestBody MeasurementRequest request,
                                                     @AuthenticationPrincipal User user) {
        MeasurementProfile profile = patternService.saveMeasurements(user, request);
        PatternResponse response = patternService.build(profile);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pdf")
    @PreAuthorize("hasRole('PREMIUM')")
    public ResponseEntity<byte[]> downloadPdf(@AuthenticationPrincipal User user) {
        MeasurementProfile profile = measurementRepository
                .findFirstByUserOrderByIdDesc(user)
                .orElseThrow(() -> new IllegalStateException("Мерки не найдены"));

        byte[] pdfBytes = pdfExportService.generatePdf(profile);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=vikroy_pattern.pdf")
                .body(pdfBytes);
    }
}