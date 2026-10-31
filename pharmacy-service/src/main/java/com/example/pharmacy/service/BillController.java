package com.example.pharmacy.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/bill")
@RefreshScope
public class BillController {
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");
    private final BigDecimal vatRate;

    public BillController(@Value("${pharmacy.vat-rate}") BigDecimal vatRate) {
        this.vatRate = vatRate;
    }

    @PostMapping
    public ResponseEntity<BillResponse> calculate(@Valid @RequestBody BillRequest request) {
        BigDecimal subtotal = request.medicineTotal().setScale(2, RoundingMode.HALF_UP);
        BigDecimal vatAmount = subtotal.multiply(vatRate)
                .divide(ONE_HUNDRED, 2, RoundingMode.HALF_UP);
        return ResponseEntity.ok(new BillResponse(subtotal, vatRate, vatAmount, subtotal.add(vatAmount)));
    }

    public record BillRequest(@NotNull @DecimalMin("0.00") BigDecimal medicineTotal) {}
    public record BillResponse(BigDecimal medicineTotal, BigDecimal vatRate,
                               BigDecimal vatAmount, BigDecimal total) {}
}
