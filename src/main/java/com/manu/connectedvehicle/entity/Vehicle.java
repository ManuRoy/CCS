package com.manu.connectedvehicle.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "vehicles", uniqueConstraints = @UniqueConstraint(name = "uk_vehicle_vin", columnNames = "vin"))
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 17, unique = true)
    private String vin;

    @Column(nullable = false, length = 80)
    private String make;

    @Column(nullable = false, length = 80)
    private String model;

    @Column(nullable = false)
    private Integer modelYear;

    @Column(nullable = false, length = 40)
    private String status = "ACTIVE";

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    protected Vehicle() {}

    public Vehicle(String vin, String make, String model, Integer modelYear) {
        this.vin = vin;
        this.make = make;
        this.model = model;
        this.modelYear = modelYear;
        this.status = "ACTIVE";
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getVin() { return vin; }
    public String getMake() { return make; }
    public String getModel() { return model; }
    public Integer getModelYear() { return modelYear; }
    public String getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public void setStatus(String status) { this.status = status; }
}
