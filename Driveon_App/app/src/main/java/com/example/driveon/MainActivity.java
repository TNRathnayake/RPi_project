package com.example.driveon;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {

    private GearSliderView gearSlider;
    private SteeringSliderView steeringSlider;
    private Switch brakeSwitch;
    private WebView cameraWebView;

    private int currentSpeed = 0;
    private int currentAngle = 100;
    private boolean autoBrakeEnabled = false;

    // Replace with your Raspberry Pi's IP address
    private static final String PI_IP = "192.168.169.25"; // Change this to your Pi's IP

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find views
        gearSlider = findViewById(R.id.gearSlider);
        steeringSlider = findViewById(R.id.steeringSlider);
        brakeSwitch = findViewById(R.id.brakeSwitch);
        cameraWebView = findViewById(R.id.cameraWebView);

        // Setup WebView
        setupWebView();

        // Setup listeners
        setupListeners();
    }

    private void setupWebView() {
        cameraWebView.getSettings().setJavaScriptEnabled(true);
        cameraWebView.setWebViewClient(new WebViewClient());

        // Load camera feed
        String cameraUrl = "http://" + PI_IP + ":5000/video_feed";
        cameraWebView.loadUrl(cameraUrl);
    }

    private void setupListeners() {
        // Gear slider listener
        gearSlider.setOnValueChangeListener(new GearSliderView.OnValueChangeListener() {
            @Override
            public void onValueChanged(int value) {
                currentSpeed = value;
                sendDriveCommand(currentSpeed, currentAngle);
            }
        });

        // Steering slider listener
        steeringSlider.setOnAngleChangeListener(new SteeringSliderView.OnAngleChangeListener() {
            @Override
            public void onAngleChanged(int angle) {
                currentAngle = angle;
                sendDriveCommand(currentSpeed, currentAngle);
            }
        });

        // Brake switch listener
        brakeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            autoBrakeEnabled = isChecked;
            sendBrakeCommand(autoBrakeEnabled);
        });
    }

    private void sendDriveCommand(int speed, int angle) {
        new Thread(() -> {
            try {
                URL url = new URL("http://" + PI_IP + ":5000/drive");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                JSONObject jsonData = new JSONObject();
                jsonData.put("speed", speed);
                jsonData.put("angle", angle);

                String jsonString = jsonData.toString();
                byte[] input = jsonString.getBytes(StandardCharsets.UTF_8);

                try (OutputStream os = connection.getOutputStream()) {
                    os.write(input, 0, input.length);
                }

                int responseCode = connection.getResponseCode();
                connection.disconnect();

                runOnUiThread(() -> {
                    if (responseCode == 200) {
                        // Success - could add visual feedback here
                    } else {
                        Toast.makeText(MainActivity.this,
                                "Drive command failed: " + responseCode,
                                Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this,
                            "Network error: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void sendBrakeCommand(boolean enabled) {
        new Thread(() -> {
            try {
                URL url = new URL("http://" + PI_IP + ":5000/brake");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                JSONObject jsonData = new JSONObject();
                jsonData.put("enabled", enabled);

                String jsonString = jsonData.toString();
                byte[] input = jsonString.getBytes(StandardCharsets.UTF_8);

                try (OutputStream os = connection.getOutputStream()) {
                    os.write(input, 0, input.length);
                }

                int responseCode = connection.getResponseCode();
                connection.disconnect();

                runOnUiThread(() -> {
                    if (responseCode == 200) {
                        // Success - could add visual feedback here
                    } else {
                        Toast.makeText(MainActivity.this,
                                "Brake command failed: " + responseCode,
                                Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this,
                            "Network error: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }
}