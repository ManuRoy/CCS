package com.manu.connectedvehicle.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateVehicleRequest(
    @NotBlank @Pattern(regexp = "^[A-HJ-NPR-Z0-9]{17}$", message = "VIN must be 17 valid letters/numbers") String vin,
    @NotBlank @Size(max = 80) String make,
    @NotBlank @Size(max = 80) String model,
    @Min(1980) @Max(2100) Integer modelYear
) {}
