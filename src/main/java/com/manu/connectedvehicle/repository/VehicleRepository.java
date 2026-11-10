package com.manu.connectedvehicle.repository;

import com.manu.connectedvehicle.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Optional<Vehicle> findByVin(String vin);
    boolean existsByVin(String vin);
}
