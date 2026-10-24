package com.manu.connectedvehicle.dto;

import com.manu.connectedvehicle.entity.Telemetry;
import java.time.Instant;

public record TelemetryResponse(
    Long id, Long vehicleId, String vin, Instant recordedAt,
    Double speedKph, Double engineTempC, Double batteryVoltage,
    Double odometerKm, Integer healthScore
) {
    public static TelemetryResponse from(Telemetry t) {
        return new TelemetryResponse(t.getId(), t.getVehicle().getId(), t.getVehicle().getVin(),
            t.getRecordedAt(), t.getSpeedKph(), t.getEngineTempC(), t.getBatteryVoltage(),
            t.getOdometerKm(), t.getHealthScore());
    }
}
