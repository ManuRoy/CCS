package com.manu.connectedvehicle.controller;

import com.manu.connectedvehicle.dto.TelemetryRequest;
import com.manu.connectedvehicle.dto.TelemetryResponse;
import com.manu.connectedvehicle.entity.DiagnosticAlert;
import com.manu.connectedvehicle.service.TelemetryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class TelemetryController {
    private final TelemetryService service;
    public TelemetryController(TelemetryService service) { this.service = service; }

    @PostMapping("/telemetry")
    @ResponseStatus(HttpStatus.CREATED)
    public TelemetryResponse ingest(@Valid @RequestBody TelemetryRequest request) {
        return TelemetryResponse.from(service.ingest(request));
    }

    @GetMapping("/vehicles/{vehicleId}/telemetry")
    public List<TelemetryResponse> history(@PathVariable Long vehicleId) {
        return service.history(vehicleId).stream().map(TelemetryResponse::from).toList();
    }

    @GetMapping("/vehicles/{vehicleId}/alerts")
    public List<DiagnosticAlert> alerts(@PathVariable Long vehicleId) {
        return service.alertsForVehicle(vehicleId);
    }
}
