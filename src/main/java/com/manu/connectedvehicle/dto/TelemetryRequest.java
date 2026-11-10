package com.manu.connectedvehicle.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.Instant;

public record TelemetryRequest(
    @NotNull Long vehicleId,
    @PastOrPresent Instant recordedAt,
    @NotNull @DecimalMin("0.0") Double speedKph,
    @NotNull Double engineTempC,
    @NotNull Double batteryVoltage,
    @NotNull @DecimalMin("0.0") Double odometerKm
) {}
