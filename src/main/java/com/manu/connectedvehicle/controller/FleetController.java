package com.manu.connectedvehicle.controller;

import com.manu.connectedvehicle.service.FleetService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/fleet")
public class FleetController {
    private final FleetService service;
    public FleetController(FleetService service) { this.service = service; }

    @GetMapping("/summary")
    public Map<String, Object> summary() { return service.summary(); }
}
