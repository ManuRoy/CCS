#!/usr/bin/env bash

BASE_URL="http://localhost:8070/api/v1/vehicles"

register_vehicle() {
  local vin="$1"
  local make="$2"
  local model="$3"
  local year="$4"

  response=$(curl -s -w '\n%{http_code}' \
    -X POST "$BASE_URL" \
    -H "Content-Type: application/json" \
    -d "{\"vin\":\"$vin\",\"make\":\"$make\",\"model\":\"$model\",\"modelYear\":$year}")

  status=$(printf '%s\n' "$response" | tail -n 1)
  body=$(printf '%s\n' "$response" | sed '$d')

  printf '%s | %-15s | %-20s | HTTP %s\n' \
    "$vin" "$make" "$model" "$status"

  if [[ "$status" != "201" ]]; then
    printf '  Response: %s\n' "$body"
  fi
}

register_vehicle DEMN0000000000001 Toyota Corolla 2023
register_vehicle DEMN0000000000002 Toyota Camry 2023
register_vehicle DEMN0000000000003 Toyota RAV4 2025
register_vehicle DEMN0000000000004 Toyota Hilux 2024
register_vehicle DEMN0000000000005 Honda Civic 2024
register_vehicle DEMN0000000000006 Honda City 2024
register_vehicle DEMN0000000000007 Honda CR-V 2023
register_vehicle DEMN0000000000008 Honda Accord 2023
register_vehicle DEMN0000000000009 Hyundai i20 2026
register_vehicle DEMN0000000000010 Hyundai Creta 2023
register_vehicle DEMN0000000000011 Hyundai Venue 2023
register_vehicle DEMN0000000000012 Hyundai Verna 2023
register_vehicle DEMN0000000000013 Kia Seltos 2024
register_vehicle DEMN0000000000014 Kia Sonet 2024
register_vehicle DEMN0000000000015 Kia Carens 2023
register_vehicle DEMN0000000000016 Kia EV6 2024
register_vehicle DEMN0000000000017 "Maruti Suzuki" Swift 2026
register_vehicle DEMN0000000000018 "Maruti Suzuki" Baleno 2024
register_vehicle DEMN0000000000019 "Maruti Suzuki" Brezza 2026
register_vehicle DEMN0000000000020 "Maruti Suzuki" Ertiga 2025
register_vehicle DEMN0000000000021 Tata Nexon 2023
register_vehicle DEMN0000000000022 Tata Punch 2024
register_vehicle DEMN0000000000023 Tata Harrier 2026
register_vehicle DEMN0000000000024 Tata Safari 2025
register_vehicle DEMN0000000000025 Mahindra Thar 2025
register_vehicle DEMN0000000000026 Mahindra XUV700 2024
register_vehicle DEMN0000000000027 Mahindra Scorpio-N 2024
register_vehicle DEMN0000000000028 Mahindra Bolero 2025
register_vehicle DEMN0000000000029 Nissan Magnite 2023
register_vehicle DEMN0000000000030 Nissan Ariya 2023
register_vehicle DEMN0000000000031 Nissan X-Trail 2026
register_vehicle DEMN0000000000032 Ford "Mustang Mach-E" 2023
register_vehicle DEMN0000000000033 Ford F-150 2025
register_vehicle DEMN0000000000034 Ford Explorer 2025
register_vehicle DEMN0000000000035 Volkswagen Polo 2025
register_vehicle DEMN0000000000036 Volkswagen Taigun 2023
register_vehicle DEMN0000000000037 Volkswagen ID.4 2026
register_vehicle DEMN0000000000038 Skoda Kushaq 2023
register_vehicle DEMN0000000000039 Skoda Slavia 2026
register_vehicle DEMN0000000000040 Skoda Octavia 2023
register_vehicle DEMN0000000000041 MG "ZS EV" 2025
register_vehicle DEMN0000000000042 MG Hector 2025
register_vehicle DEMN0000000000043 MG "Comet EV" 2024
register_vehicle DEMN0000000000044 Tesla "Model 3" 2023
register_vehicle DEMN0000000000045 Tesla "Model Y" 2023
register_vehicle DEMN0000000000046 Tesla "Model S" 2024
register_vehicle DEMN0000000000047 BMW i4 2025
register_vehicle DEMN0000000000048 "Mercedes-Benz" EQB 2023
register_vehicle DEMN0000000000049 Audi "Q4 e-tron" 2024
register_vehicle DEMN0000000000050 Volvo "XC40 Recharge" 2023
