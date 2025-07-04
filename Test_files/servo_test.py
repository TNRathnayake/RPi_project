#!/usr/bin/env python3
import pigpio
import time

SERVO_PIN = 13
FREQ_HZ = 50.0
PERIOD_US = 1_000_000.0 / FREQ_HZ

MIN_PW = 1000
MAX_PW = 2200

pi = pigpio.pi()
if not pi.connected:
    exit(1)

pi.set_mode(SERVO_PIN, pigpio.OUTPUT)

def duty_pct(pw):
    return (pw / PERIOD_US) * 100

try:
    print(f"Moving to MIN (pulse={MIN_PW} us, duty={duty_pct(MIN_PW):.2f}%)")
    pi.set_servo_pulsewidth(SERVO_PIN, MIN_PW)
    time.sleep(2.0)

    print(f"Moving to MAX (pulse={MAX_PW} us, duty={duty_pct(MAX_PW):.2f}%)")
    pi.set_servo_pulsewidth(SERVO_PIN, MAX_PW)
    time.sleep(2.0)

finally:
    CENTER_PW = (MIN_PW + MAX_PW) // 2
    print(f"Centering (pulse={CENTER_PW} us, duty={duty_pct(CENTER_PW):.2f}%)")
    pi.set_servo_pulsewidth(SERVO_PIN, CENTER_PW)
    time.sleep(1.0)
    pi.set_servo_pulsewidth(SERVO_PIN, 0)
    pi.stop()
