package com.manu.connectedvehicle.service;

import com.manu.connectedvehicle.dto.CreateVehicleRequest;
import com.manu.connectedvehicle.entity.Vehicle;
import com.manu.connectedvehicle.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.util.List;

@Service
public class VehicleService {
    private final VehicleRepository vehicles;

    public VehicleService(VehicleRepository vehicles) { this.vehicles = vehicles; }

    @Transactional
    public Vehicle create(CreateVehicleRequest request) {
        if (vehicles.existsByVin(request.vin())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A vehicle with this VIN already exists");
        }
        return vehicles.save(new Vehicle(request.vin(), request.make().trim(),
            request.model().trim(), request.modelYear()));
    }

    @Transactional(readOnly = true)
    public List<Vehicle> list() { return vehicles.findAll(); }

    @Transactional(readOnly = true)
    public Vehicle get(Long id) {
        return vehicles.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle not found: " + id));
    }
}
