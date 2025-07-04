import RPi.GPIO as GPIO, time

ENA, ENB = 18, 12         # PWM speed pins
IN1, IN2 = 27, 17           # RIGHT motor direction
IN3, IN4 = 3, 2         # LEFT  motor direction
PWM_FREQ = 1000

GPIO.setmode(GPIO.BCM)
GPIO.setwarnings(False)

# 1) configure pins as outputs
pins = [ENA, ENB, IN1, IN2, IN3, IN4]
GPIO.setup(pins, GPIO.OUT)

# 2) drive them low initially
GPIO.output(pins, GPIO.LOW)

# PWM setup
pwm_r = GPIO.PWM(ENA, PWM_FREQ)
pwm_l = GPIO.PWM(ENB, PWM_FREQ)
pwm_r.start(0)
pwm_l.start(0)

def set_speed(left_pct, right_pct):
    pwm_l.ChangeDutyCycle(max(0, min(100, left_pct)))
    pwm_r.ChangeDutyCycle(max(0, min(100, right_pct)))

def forward():
    # IN1/IN3 HIGH = forward, IN2/IN4 LOW
    GPIO.output((IN1, IN2, IN3, IN4), (0, 1, 0, 1))

def back():
    GPIO.output((IN1, IN2, IN3, IN4), (1, 0, 1, 0))

def stop():
    GPIO.output((IN1, IN2, IN3, IN4), GPIO.LOW)

try:
    set_speed(70, 70)
    forward(); time.sleep(2)

    stop();      time.sleep(1)

    set_speed(50, 50)
    back();    time.sleep(2)

    stop()
    set_speed(0, 0)

finally:
    pwm_r.stop()
    pwm_l.stop()
    GPIO.cleanup()

