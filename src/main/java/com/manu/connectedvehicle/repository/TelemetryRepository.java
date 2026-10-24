package com.manu.connectedvehicle.repository;

import com.manu.connectedvehicle.entity.Telemetry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TelemetryRepository extends JpaRepository<Telemetry, Long> {
    List<Telemetry> findTop100ByVehicle_IdOrderByRecordedAtDesc(Long vehicleId);
    long countByVehicle_Id(Long vehicleId);
}
