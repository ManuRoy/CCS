package com.manu.connectedvehicle.service;

import com.manu.connectedvehicle.dto.TelemetryRequest;
import com.manu.connectedvehicle.entity.DiagnosticAlert;
import com.manu.connectedvehicle.entity.Telemetry;
import com.manu.connectedvehicle.entity.Vehicle;
import com.manu.connectedvehicle.repository.DiagnosticAlertRepository;
import com.manu.connectedvehicle.repository.TelemetryRepository;
import com.manu.connectedvehicle.repository.VehicleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class TelemetryService {
    private final VehicleRepository vehicles;
    private final TelemetryRepository telemetry;
    private final DiagnosticAlertRepository alerts;

    public TelemetryService(VehicleRepository vehicles, TelemetryRepository telemetry,
                            DiagnosticAlertRepository alerts) {
        this.vehicles = vehicles;
        this.telemetry = telemetry;
        this.alerts = alerts;
    }

    @Transactional
    public Telemetry ingest(TelemetryRequest request) {
        Vehicle vehicle = vehicles.findById(request.vehicleId()).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle not found: " + request.vehicleId()));

        int health = calculateHealth(request.engineTempC(), request.batteryVoltage());
        Telemetry saved = telemetry.save(new Telemetry(vehicle,
            request.recordedAt() == null ? Instant.now() : request.recordedAt(),
            request.speedKph(), request.engineTempC(), request.batteryVoltage(),
            request.odometerKm(), health));

        List<DiagnosticAlert> generated = new ArrayList<>();
        if (request.engineTempC() >= 115.0) {
            generated.add(new DiagnosticAlert(vehicle, "CRITICAL", "ENGINE_OVERHEAT",
                "Engine temperature is " + request.engineTempC() + " C"));
        } else if (request.engineTempC() >= 105.0) {
            generated.add(new DiagnosticAlert(vehicle, "WARNING", "HIGH_ENGINE_TEMP",
                "Engine temperature is " + request.engineTempC() + " C"));
        }
        if (request.batteryVoltage() < 11.8) {
            generated.add(new DiagnosticAlert(vehicle, "WARNING", "LOW_BATTERY_VOLTAGE",
                "Battery voltage is " + request.batteryVoltage() + " V"));
        }
        if (!generated.isEmpty()) alerts.saveAll(generated);
        return saved;
    }

    private int calculateHealth(double temp, double voltage) {
        int score = 100;
        if (temp > 95) score -= Math.min(45, (int) ((temp - 95) * 2));
        if (voltage < 12.2) score -= Math.min(35, (int) ((12.2 - voltage) * 15));
        return Math.max(0, Math.min(100, score));
    }

    @Transactional(readOnly = true)
    public List<Telemetry> history(Long vehicleId) {
        if (!vehicles.existsById(vehicleId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle not found: " + vehicleId);
        }
        return telemetry.findTop100ByVehicle_IdOrderByRecordedAtDesc(vehicleId);
    }

    @Transactional(readOnly = true)
    public List<DiagnosticAlert> alertsForVehicle(Long vehicleId) {
        if (!vehicles.existsById(vehicleId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle not found: " + vehicleId);
        }
        return alerts.findByVehicle_IdOrderByCreatedAtDesc(vehicleId);
    }
}
