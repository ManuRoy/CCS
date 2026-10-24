package com.manu.connectedvehicle.repository;

import com.manu.connectedvehicle.entity.DiagnosticAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DiagnosticAlertRepository extends JpaRepository<DiagnosticAlert, Long> {
    List<DiagnosticAlert> findByStatusOrderByCreatedAtDesc(String status);
    List<DiagnosticAlert> findByVehicle_IdOrderByCreatedAtDesc(Long vehicleId);
    long countByStatus(String status);
}
