# 🤖 Smart Wi-Fi Controlled Robot Car with Auto-Brake and Live Camera

This project implements a Wi-Fi-controlled robot car powered by a **Raspberry Pi**. It features real-time control via an Android app, **servo steering**, **motor drive**, **obstacle auto-braking**, and a **live camera feed**. Designed with a responsive UI, robust embedded logic, and safety features like ultrasonic-based collision prevention, the system is modular, expandable, and IoT-ready.

---

## 🚀 Features

- 📱 **Android App Control**  
  Smooth control of direction and speed via intuitive sliders.

- 🎮 **Dual Motor Drive**  
  Controlled via ENA/ENB PWM pins and H-bridge logic for forward/backward motion.

- 🌀 **Steering Servo**  
  Servo motor smoothly steers based on user input angle (45° to 135°).

- 🧠 **Auto-Brake System**  
  HC-SR04 distance sensor constantly monitors for nearby obstacles. If within threshold, the system:
  - Stops motors
  - Turns on Brake LED

- 🎥 **Live Camera Feed**  
  WebView embedded video stream via `cv2.VideoCapture` and Flask endpoint.

- 🌐 **Flask Web Server**  
  - `/drive`: Receives speed and steering angle from app
  - `/autobrake`: Toggles auto-brake logic
  - `/distance`: Returns real-time ultrasonic distance
  - `/video_feed`: Streams camera frames

---

## 📡 Hardware Overview

| Component        | Description                              |
|------------------|------------------------------------------|
| Raspberry Pi     | Central controller                       |
| L298N Module     | Dual motor driver                        |
| SG90 Servo       | Steering control                         |
| HC-SR04          | Distance sensor for auto-brake           |
| LEDs             | Visual indicators                        |
| Webcam/CSI cam   | Live feed                                |
| Android Phone    | Control interface via custom app         |

---

## 🔌 GPIO Pin Assignments (BCM Mode)

| Signal         | GPIO Pin | Usage                    |
|----------------|-----------|--------------------------|
| ENA            | 12        | Right Motor PWM          |
| ENB            | 18        | Left Motor PWM           |
| IN1, IN2       | 27, 17    | Right Motor Direction     |
| IN3, IN4       | 4, 3      | Left Motor Direction      |
| SERVO          | 13        | PWM for Steering Servo    |
| TRIG, ECHO     | 10, 21    | HC-SR04 Ultrasonic Sensor |
| BRAKE_LED      | 26        | Obstacle Detected LED     |

---

## 📱 App UI and Design

- Built with Android Studio using **ConstraintLayout**
- Background UI features futuristic circuit board aesthetics
- Gear and steering sliders visually enhanced with gradients and rounded shapes
- Brake switch toggle for enabling/disabling auto-brake

---

## 🛠️ Running the Flask Server Automatically on Boot

To ensure the robot is always responsive without needing to manually launch the script:

### 🔄 Method: Using `crontab`

1. Edit crontab:
   ```bash
   crontab -e
