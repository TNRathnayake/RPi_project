#!/usr/bin/env python3
import time
import pigpio
import RPi.GPIO as GPIO

# CONFIG
# BCM pin numbers:
IN1, IN2, IN3, IN4 = 27, 17, 2, 3 
ENA, ENB           = 12, 18    # hardware PWM pins
TRIG, ECHO         = 10, 21
BRAKE_LED          = 26

PWM_FREQ_HZ        = 1000      # motor PWM frequency
FORWARD_SPEED_PC   = 50        # duty cycle when moving forward
THRESHOLD_CM       = 20.0      # stop if closer than this

# SETUP
GPIO.setmode(GPIO.BCM)
GPIO.setwarnings(False)

# direction pins + trigger + LED as outputs
GPIO.setup([IN1, IN2, IN3, IN4, TRIG, BRAKE_LED], GPIO.OUT, initial=GPIO.LOW)
# echo pin as input
GPIO.setup(ECHO, GPIO.IN)

pi = pigpio.pi()
if not pi.connected:
    raise SystemExit("Cannot connect to pigpio daemon")

# configure hardware PWM channels
pi.set_mode(ENA, pigpio.ALT0)
pi.set_mode(ENB, pigpio.ALT0)
pi.set_PWM_frequency(ENA, PWM_FREQ_HZ)
pi.set_PWM_frequency(ENB, PWM_FREQ_HZ)

def set_forward(speed_pc):
    """Drive both motors forward at speed_pc percent."""
    duty = int(max(0, min(100, speed_pc)) * 10000)  # pigpio duty out of 1e6
    pi.hardware_PWM(ENA, PWM_FREQ_HZ, duty)
    pi.hardware_PWM(ENB, PWM_FREQ_HZ, duty)
    GPIO.output(IN1, GPIO.HIGH)
    GPIO.output(IN2, GPIO.LOW)
    GPIO.output(IN3, GPIO.HIGH)
    GPIO.output(IN4, GPIO.LOW)

def stop_motors():
    """Immediately stop both motors."""
    pi.hardware_PWM(ENA, PWM_FREQ_HZ, 0)
    pi.hardware_PWM(ENB, PWM_FREQ_HZ, 0)
    GPIO.output([IN1, IN2, IN3, IN4], GPIO.LOW)

def get_distance_cm():
    """Trigger the HC-SR04 and return distance in cm (or None on timeout)."""
    # send a 10 microsecond pulse
    GPIO.output(TRIG, False)
    time.sleep(0.01)
    GPIO.output(TRIG, True)
    time.sleep(0.00001)
    GPIO.output(TRIG, False)

    start = time.time()
    # wait for echo high
    while GPIO.input(ECHO) == 0 and (time.time() - start) < 0.02:
        pass
    t0 = time.time()
    # wait for echo low
    while GPIO.input(ECHO) == 1 and (time.time() - t0) < 0.02:
        pass
    t1 = time.time()
    dt = t1 - t0
    # only return if we got a valid reading
    return round(dt * 17150, 2) if dt > 0.0001 else None

# MAIN LOOP
try:
    print("Starting auto-brake test. Threshold:", THRESHOLD_CM, "cm")
    while True:
        # persistently move forward (PWM runs on hardware)
        set_forward(FORWARD_SPEED_PC)

        dist = get_distance_cm()
        if dist is not None and dist < THRESHOLD_CM:
            # obstacle too close -> stop + LED on
            stop_motors()
            GPIO.output(BRAKE_LED, GPIO.HIGH)
            print(f"[{dist} cm] BRAKE!")
            # wait until clear
            while True:
                d2 = get_distance_cm()
                if d2 is None or d2 >= THRESHOLD_CM:
                    break
                time.sleep(0.05)
            print(f"[{d2} cm] CLEAR, RESUME")
            GPIO.output(BRAKE_LED, GPIO.LOW)

        time.sleep(0.05)

finally:
    stop_motors()
    GPIO.output(BRAKE_LED, GPIO.LOW)
    pi.stop()
    GPIO.cleanup()
