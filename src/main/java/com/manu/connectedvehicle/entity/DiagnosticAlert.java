package com.manu.connectedvehicle.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "diagnostic_alerts", indexes = @Index(name = "idx_alert_vehicle_time", columnList = "vehicle_id, created_at"))
public class DiagnosticAlert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Column(nullable = false, length = 30)
    private String severity;

    @Column(nullable = false, length = 80)
    private String code;

    @Column(nullable = false, length = 500)
    private String message;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false, length = 20)
    private String status = "OPEN";

    protected DiagnosticAlert() {}

    public DiagnosticAlert(Vehicle vehicle, String severity, String code, String message) {
        this.vehicle = vehicle;
        this.severity = severity;
        this.code = code;
        this.message = message;
        this.createdAt = Instant.now();
        this.status = "OPEN";
    }

    public Long getId() { return id; }
    public Vehicle getVehicle() { return vehicle; }
    public String getSeverity() { return severity; }
    public String getCode() { return code; }
    public String getMessage() { return message; }
    public Instant getCreatedAt() { return createdAt; }
    public String getStatus() { return status; }
    public void resolve() { this.status = "RESOLVED"; }
}
