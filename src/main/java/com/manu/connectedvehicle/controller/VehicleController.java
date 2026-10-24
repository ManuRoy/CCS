package com.manu.connectedvehicle.controller;

import com.manu.connectedvehicle.dto.CreateVehicleRequest;
import com.manu.connectedvehicle.entity.Vehicle;
import com.manu.connectedvehicle.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/vehicles")
public class VehicleController {
    private final VehicleService service;
    public VehicleController(VehicleService service) { this.service = service; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Vehicle create(@Valid @RequestBody CreateVehicleRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<Vehicle> list() { return service.list(); }

    @GetMapping("/{id}")
    public Vehicle get(@PathVariable Long id) { return service.get(id); }
}
