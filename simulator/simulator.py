import os
import random
import time
import requests
from datetime import datetime, timezone

API_BASE_URL = os.getenv("API_BASE_URL", "http://localhost:8080").rstrip("/")
INTERVAL_SECONDS = float(os.getenv("INTERVAL_SECONDS", "5"))

def wait_for_api():
    for attempt in range(30):
        try:
            requests.get(f"{API_BASE_URL}/actuator/health", timeout=3).raise_for_status()
            return
        except requests.RequestException:
            print(f"Waiting for API ({attempt + 1}/30)...", flush=True)
            time.sleep(2)
    raise RuntimeError("API did not become ready")

def create_demo_vehicle():
    vin = "1N4AL3AP0JC123456"
    response = requests.get(f"{API_BASE_URL}/api/v1/vehicles", timeout=5)
    response.raise_for_status()
    for vehicle in response.json():
        if vehicle["vin"] == vin:
            return vehicle["id"]

    response = requests.post(
        f"{API_BASE_URL}/api/v1/vehicles",
        json={"vin": vin, "make": "Nissan-inspired demo", "model": "Telemetry EV", "modelYear": 2025},
        timeout=5,
    )
    response.raise_for_status()
    return response.json()["id"]

def main():
    wait_for_api()
    vehicle_id = create_demo_vehicle()
    odometer = 12450.0
    print(f"Sending synthetic telemetry for vehicle id={vehicle_id}", flush=True)

    while True:
        # Occasionally inject abnormal values so the alert flow can be demonstrated.
        hot_event = random.random() < 0.08
        low_battery_event = random.random() < 0.08
        payload = {
            "vehicleId": vehicle_id,
            "recordedAt": datetime.now(timezone.utc).isoformat(timespec="seconds"),
            "speedKph": round(random.uniform(0, 110), 1),
            "engineTempC": round(random.uniform(106, 121) if hot_event else random.uniform(78, 98), 1),
            "batteryVoltage": round(random.uniform(10.8, 11.7) if low_battery_event else random.uniform(12.1, 14.4), 2),
            "odometerKm": round(odometer, 1),
        }
        try:
            response = requests.post(f"{API_BASE_URL}/api/v1/telemetry", json=payload, timeout=5)
            response.raise_for_status()
            print(f"Telemetry accepted: {response.json()}", flush=True)
            odometer += random.uniform(0.0, 0.3)
        except requests.RequestException as exc:
            print(f"Telemetry send failed: {exc}", flush=True)
        time.sleep(INTERVAL_SECONDS)

if __name__ == "__main__":
    main()
