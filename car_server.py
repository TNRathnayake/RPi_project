# Merged car_server.py with robust HC-SR04 continuous monitoring
from flask import Flask, request, jsonify, Response
import RPi.GPIO as GPIO
import pigpio
import time
import cv2
import threading

app = Flask(__name__)

# Configuration
AUTO_BRAKE_DISTANCE = 20.0  # cm threshold
POLL_INTERVAL = 0.1         # seconds between distance checks

# GPIO pins (BCM)
IN1, IN2, IN3, IN4 = 27, 17, 3, 2
ENA, ENB = 12, 18
SERVO_PIN = 13
TRIG, ECHO = 10, 21
BRAKE_LED = 26

# State
auto_brake_enabled = False
obstacle_detected = False
current_direction = 'stop'

# Setup
GPIO.setmode(GPIO.BCM)
GPIO.setwarnings(False)
GPIO.setup([IN1, IN2, IN3, IN4, TRIG, BRAKE_LED], GPIO.OUT)
GPIO.setup(ECHO, GPIO.IN)
GPIO.output(BRAKE_LED, False)

pi = pigpio.pi()
pi.set_mode(ENA, pigpio.ALT0)
pi.set_mode(ENB, pigpio.ALT0)
pi.set_PWM_frequency(ENA, 1000)
pi.set_PWM_frequency(ENB, 1000)
pi.set_servo_pulsewidth(SERVO_PIN, 1600)

camera = cv2.VideoCapture(0)

# Helper functions
def set_motors(direction, speed):
    global current_direction
    current_direction = direction
    duty = max(0, min(abs(speed), 100))
    pwm_val = int(duty * 10000)
    if direction == 'stop':
        pwm_val = 0
    pi.hardware_PWM(ENA, 1000, pwm_val)
    pi.hardware_PWM(ENB, 1000, pwm_val)
    if direction == 'forward':
        GPIO.output(IN1, True); GPIO.output(IN2, False)
        GPIO.output(IN3, True); GPIO.output(IN4, False)
    elif direction == 'backward':
        GPIO.output(IN1, False); GPIO.output(IN2, True)
        GPIO.output(IN3, False); GPIO.output(IN4, True)
    else:
        GPIO.output(IN1, False); GPIO.output(IN2, False)
        GPIO.output(IN3, False); GPIO.output(IN4, False)

def set_steering(angle):
    angle = max(45, min(angle, 135))
    pulse = 1000 + ((angle - 45) / (135-45)) * 1200
    pi.set_servo_pulsewidth(SERVO_PIN, int(pulse))

def get_distance():
    GPIO.output(TRIG, False)
    time.sleep(0.01)
    GPIO.output(TRIG, True)
    time.sleep(0.00001)
    GPIO.output(TRIG, False)
    start = time.time()
    timeout = start + 0.02
    while GPIO.input(ECHO) == 0 and time.time() < timeout:
        start = time.time()
    end = start
    timeout = end + 0.02
    while GPIO.input(ECHO) == 1 and time.time() < timeout:
        end = time.time()
    duration = end - start
    return round(duration * 17150, 2)

# Auto-brake monitor thread
def auto_brake_monitor():
    global obstacle_detected
    while True:
        if auto_brake_enabled:
            dist = get_distance()
            if 0 < dist < AUTO_BRAKE_DISTANCE:
                obstacle_detected = True
                if current_direction == 'forward':
                    set_motors('stop', 0)
                GPIO.output(BRAKE_LED, True)
            else:
                obstacle_detected = False
                GPIO.output(BRAKE_LED, False)
        time.sleep(POLL_INTERVAL)
threading.Thread(target=auto_brake_monitor, daemon=True).start()

# Routes
@app.route('/autobrake', methods=['POST'])
def autobrake():
    global auto_brake_enabled, obstacle_detected
    enabled = bool(request.get_json().get('enabled', False))
    auto_brake_enabled = enabled
    if not enabled:
        obstacle_detected = False
        GPIO.output(BRAKE_LED, False)
    return jsonify(auto_brake='on' if enabled else 'off')

@app.route('/drive', methods=['POST'])
def drive():
    data = request.get_json()
    speed = int(data.get('speed', 0))
    angle = int(data.get('angle', 90))
    direction = 'forward' if speed >= 0 else 'backward'
    if auto_brake_enabled and obstacle_detected and direction == 'forward':
        return jsonify(status='AUTO-BRAKED', distance='obstacle')
    set_steering(angle)
    set_motors(direction, speed)
    return jsonify(status='OK', speed=speed, angle=angle)

@app.route('/brake', methods=['POST'])
def brake():
    enabled = bool(request.get_json().get('enabled', False))
    if enabled:
        set_motors('stop', 0)
        GPIO.output(BRAKE_LED, True)
    else:
        GPIO.output(BRAKE_LED, False)
    return jsonify(brake='on' if enabled else 'off')

@app.route('/video_feed')
def video_feed():
    def gen():
        while True:
            ret, frame = camera.read()
            if not ret: break
            _, buf = cv2.imencode('.jpg', frame)
            yield (b'--frame\r\n'
                   b'Content-Type: image/jpeg\r\n\r\n' + buf.tobytes() + b'\r\n')
    return Response(gen(), mimetype='multipart/x-mixed-replace; boundary=frame')

@app.route('/distance')
def distance():
    return jsonify(distance=get_distance())

@app.route('/')
def index():
    return "🛠️ RPi Robot Car Server ONLINE"

if __name__ == '__main__':
    try:
        app.run(host='0.0.0.0', port=5000)
    finally:
        pi.set_PWM_dutycycle(ENA, 0)
        pi.set_PWM_dutycycle(ENB, 0)
        pi.set_servo_pulsewidth(SERVO_PIN, 0)
        pi.stop()
        GPIO.cleanup()

