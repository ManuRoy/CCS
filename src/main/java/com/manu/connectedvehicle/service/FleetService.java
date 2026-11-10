package com.manu.connectedvehicle.service;

import com.manu.connectedvehicle.repository.DiagnosticAlertRepository;
import com.manu.connectedvehicle.repository.TelemetryRepository;
import com.manu.connectedvehicle.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class FleetService {
    private final VehicleRepository vehicles;
    private final TelemetryRepository telemetry;
    private final DiagnosticAlertRepository alerts;

    public FleetService(VehicleRepository vehicles, TelemetryRepository telemetry,
                        DiagnosticAlertRepository alerts) {
        this.vehicles = vehicles;
        this.telemetry = telemetry;
        this.alerts = alerts;
    }

    public Map<String, Object> summary() {
        return Map.of(
            "totalVehicles", vehicles.count(),
            "totalTelemetryRecords", telemetry.count(),
            "openAlerts", alerts.countByStatus("OPEN"),
            "resolvedAlerts", alerts.countByStatus("RESOLVED")
        );
    }
}
