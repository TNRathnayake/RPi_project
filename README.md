🚗 Wi-Fi Controlled Smart Robot Car with Auto-Brake and Camera Feed
This is a Raspberry Pi–based intelligent robot car controlled via a custom Android app. It integrates Wi-Fi-based directional control, real-time camera streaming, an auto-brake system using the HC-SR04 ultrasonic sensor, and servo steering—all powered by a Python Flask server.

🔧 Features
🎮 App-Controlled: Drive forward/backward and steer left/right via intuitive Android app sliders.

🧠 Auto-Brake System: Continuously monitors distance using an HC-SR04 ultrasonic sensor. If an obstacle is detected within a threshold, the car stops automatically and turns on a brake LED.

📸 Live Camera Feed: Real-time video streaming to the app using a USB camera connected to the Raspberry Pi.

🔄 Servo Steering: Smooth control over direction using a 0°–180° range servo motor.

💡 Expandable: Designed to include future features like headlight control, obstacle avoidance, and object detection.

🧱 System Architecture
Raspberry Pi (Hardware)
Motor Driver: L298N H-Bridge module

Motors: 2 DC motors (left/right)

PWM Pins: Hardware PWM via pigpio on GPIO 12 & 18

Ultrasonic Sensor: HC-SR04 for distance sensing

Servo Motor: For steering (controlled via pigpio)

LED: Indicates auto-brake activation

USB Camera: For live streaming

Android App (Client)
Built using Android Studio

Controls gear (speed), steering angle, and toggles auto-brake

Displays live camera feed from the Raspberry Pi

Simple, clean UI with gradient backgrounds and custom slider views

🚀 How It Works
The app sends drive and angle commands via HTTP POST requests.

The Flask server on the Pi processes these commands, sets motor directions and speeds, and adjusts the servo angle.

A background thread continuously monitors the HC-SR04 sensor.

If an obstacle is too close and auto-brake is enabled, the car stops and LED turns on.

If obstacle is cleared or auto-brake is turned off, driving resumes.

🗂 Project Structure
Raspberry Pi Code
car_server.py – Main Flask server code for handling control, auto-brake, camera feed

autobrake_test.py – Standalone script for testing the ultrasonic-based auto-brake system

Android Studio App
MainActivity.java – UI and networking logic

GearSliderView.java, SteeringSliderView.java – Custom sliders

res/drawable – Backgrounds and gradients

AndroidManifest.xml and layout XMLs

📸 Demo
Add a short screen recording or GIF of the app controlling the robot, or a YouTube link if available.

📦 Getting Started
Raspberry Pi Setup
Enable camera and install required packages:

bash
Copy
Edit
sudo apt update
sudo apt install pigpio python3-flask python3-opencv
sudo systemctl enable pigpiod
sudo systemctl start pigpiod
Clone the repo and run:

bash
Copy
Edit
python3 car_server.py
Android App
Open project in Android Studio.

Update server IP in the app code.

Build and install APK on your phone.

📁 Resources
📂 [Google Drive Link] – Contains diagrams, videos, test results, and APK

🧠 [GitHub Repo] – Full source code for both Android and Raspberry Pi sides

📌 Project Scope
Embedded Systems

Computer Vision

Robotics

Mobile App Development

IoT & Networking

✍️ Author
Name, University of Moratuwa
Index No: Your Index
Email: youruni@uom.lk

