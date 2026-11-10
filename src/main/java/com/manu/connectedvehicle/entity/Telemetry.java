package com.manu.connectedvehicle.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "telemetry", indexes = {
    @Index(name = "idx_telemetry_vehicle_time", columnList = "vehicle_id, recorded_at")
})
public class Telemetry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Column(nullable = false)
    private Instant recordedAt;

    @Column(nullable = false)
    private Double speedKph;

    @Column(name = "engine_temp_c", nullable = false)
    private Double engineTempC;

    @Column(nullable = false)
    private Double batteryVoltage;

    @Column(nullable = false)
    private Double odometerKm;

    @Column(nullable = false)
    private Integer healthScore;

    protected Telemetry() {}

    public Telemetry(Vehicle vehicle, Instant recordedAt, Double speedKph,
                     Double engineTempC, Double batteryVoltage, Double odometerKm, Integer healthScore) {
        this.vehicle = vehicle;
        this.recordedAt = recordedAt;
        this.speedKph = speedKph;
        this.engineTempC = engineTempC;
        this.batteryVoltage = batteryVoltage;
        this.odometerKm = odometerKm;
        this.healthScore = healthScore;
    }

    public Long getId() { return id; }
    public Vehicle getVehicle() { return vehicle; }
    public Instant getRecordedAt() { return recordedAt; }
    public Double getSpeedKph() { return speedKph; }
    public Double getEngineTempC() { return engineTempC; }
    public Double getBatteryVoltage() { return batteryVoltage; }
    public Double getOdometerKm() { return odometerKm; }
    public Integer getHealthScore() { return healthScore; }
}
