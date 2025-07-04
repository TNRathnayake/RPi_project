# 🚗 Wi-Fi Controlled Smart Robot Car

This is a Raspberry Pi–based intelligent robot car controlled via a custom Android app. It integrates Wi-Fi-based directional control, real-time camera streaming, an auto-brake system using the HC-SR04 ultrasonic sensor, and servo steering—all powered by a Python Flask server.

---

## 🔧 Features

- 🎮 **App-Controlled**: Drive forward/backward and steer left/right via intuitive Android app sliders.
- 🧠 **Auto-Brake System**: Continuously monitors distance using an HC-SR04 ultrasonic sensor. If an obstacle is detected within a threshold, the car stops automatically and turns on a brake LED.
- 📸 **Live Camera Feed**: Real-time video streaming to the app using a USB camera connected to the Raspberry Pi.
- 🔄 **Servo Steering**: Smooth control over direction using a 0°–180° range servo motor.
- 💡 **Expandable**: Designed to include future features like headlight control, obstacle avoidance, and object detection.

---

## 🧱 System Architecture

### Raspberry Pi (Hardware)

| Component        | Function                             |
|------------------|--------------------------------------|
| L298N H-Bridge   | Motor driver for left & right motors |
| DC Motors        | For movement                         |
| pigpio (GPIO 12,18) | Hardware PWM control              |
| HC-SR04 Sensor   | Distance detection for auto-brake    |
| SG90 Servo       | Steering control (via GPIO 13)       |
| LED              | Brake indication (GPIO 26)           |
| v1.3 5MP Camera       | Real-time video feed to app          |

### Android App (Client)

- Built using **Android Studio**
- Controls:
  - Gear (forward/backward)
  - Steering angle
  - Auto-brake toggle
- Displays:
  - Live video feed from Pi
- UI Elements:
  - Custom sliders with gradients
  - Circuit-themed background

---

## 🚀 How It Works

- App sends speed and angle via HTTP POST to Flask server
- `car_server.py` handles:
  - Driving logic with PWM
  - Servo angle mapping
  - HC-SR04 distance monitoring in a background thread
- If an obstacle is too close:
  - Motors stop (Doesn't move forward)
  - Brake LED turns on
- Once clear or auto-brake is disabled:
  - Resume motion
  - LED off

---

## 🗂 Project Structure

### Raspberry Pi Code

- `car_server.py` – Main Flask server code for handling control, auto-brake, camera feed

### Auto-Start on Boot (systemd)

To have `car_server.py` launch automatically without manual intervention, we created a systemd service.

### Android Studio App

- `MainActivity.java` – UI and networking logic
- `GearSliderView.java`, `SteeringSliderView.java` – Custom sliders
- `res/drawable/` – Backgrounds and gradients
- `AndroidManifest.xml` and layout XMLs

---

## 📸 Demo

![App Demo](screenshots/demo.gif)


---

## 📦 Getting Started

### Raspberry Pi Setup

1. Enable camera and install required packages:

    ```bash
    sudo apt update
    sudo apt install pigpio python3-flask python3-opencv
    sudo systemctl enable pigpiod
    sudo systemctl start pigpiod
    ```

2. Clone the repo and run:

    ```bash
    python3 car_server.py
    ```

### Android App

1. Open project in Android Studio.
2. Update server IP in the app code.
3. Build and install APK on your phone.

---

## 📁 Addtional Resources

- 📂 **[Google Drive Folder](https://drive.google.com/drive/folders/1aB…XYZ)** – Contains diagrams, videos, test results, and APK  
- 🧠 **[GitHub Repository](https://github.com/TNRathnayake/RPi_project/)** – Full source code for both Android and Raspberry Pi sides


---

## 📌 Project Scope

- **Embedded Systems**
- **Computer Vision**
- **Robotics**
- **Mobile App Development**
- **IoT & Networking**

---

## 🔮 Upcoming Features

- 💡 **Headlight Control**  
  Remotely toggle front and rear LEDs via the app for night driving.

- 📷 **Object Detection**  
  Integrate a lightweight ML model (e.g. YOLO) on the Pi to identify obstacles and adjust behavior.
  
- 📲 **Device Discovery & Selection**  
  Build an app interface that scans the local network for available robot devices and lets the user choose which Raspberry Pi to connect to before driving.

- 🗣️ **Voice Commands**  
  Add speech recognition to control movement, lights, and capture images hands-free.

- 🌐 **Remote Access**  
  Enable secure tunnel (e.g. ngrok or VPN) to control the robot over the internet, not just local Wi-Fi.

---
## ✍️ Author

> **Tharindu Nimesh**, *University of Moratuwa*  
> Index No: *220528X*  
> Email: [rathnayakermtnb.22@uom.lk](mailto:nimeshrathnayake1000@gmail.com)

---


