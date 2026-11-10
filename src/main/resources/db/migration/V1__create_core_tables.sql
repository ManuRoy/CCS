CREATE TABLE vehicles (
  id BIGSERIAL PRIMARY KEY,
  vin VARCHAR(17) NOT NULL UNIQUE,
  make VARCHAR(80) NOT NULL,
  model VARCHAR(80) NOT NULL,
  model_year INTEGER NOT NULL,
  status VARCHAR(40) NOT NULL DEFAULT 'ACTIVE',
  created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE telemetry (
  id BIGSERIAL PRIMARY KEY,
  vehicle_id BIGINT NOT NULL REFERENCES vehicles(id),
  recorded_at TIMESTAMPTZ NOT NULL,
  speed_kph DOUBLE PRECISION NOT NULL CHECK (speed_kph >= 0),
  engine_temp_c DOUBLE PRECISION NOT NULL,
  battery_voltage DOUBLE PRECISION NOT NULL,
  odometer_km DOUBLE PRECISION NOT NULL CHECK (odometer_km >= 0),
  health_score INTEGER NOT NULL CHECK (health_score BETWEEN 0 AND 100)
);

CREATE INDEX idx_telemetry_vehicle_time ON telemetry(vehicle_id, recorded_at DESC);

CREATE TABLE diagnostic_alerts (
  id BIGSERIAL PRIMARY KEY,
  vehicle_id BIGINT NOT NULL REFERENCES vehicles(id),
  severity VARCHAR(30) NOT NULL,
  code VARCHAR(80) NOT NULL,
  message VARCHAR(500) NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  status VARCHAR(20) NOT NULL DEFAULT 'OPEN'
);

CREATE INDEX idx_alert_vehicle_time ON diagnostic_alerts(vehicle_id, created_at DESC);
