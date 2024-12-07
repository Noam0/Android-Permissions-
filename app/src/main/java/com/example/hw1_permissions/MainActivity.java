package com.example.hw1_permissions;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private AppCompatButton loginButton;

    // Variable to track flashlight state
    private boolean isFlashlightOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginButton = findViewById(R.id.login_button);

        // Register TorchCallback to track flashlight state
        registerTorchCallback();

        // Set button click listener
        loginButton.setOnClickListener(v -> {
            if (validateAllConditions()) {
                // Proceed to the next activity
                startActivity(new Intent(MainActivity.this, NextActivity.class));
            } else {
                Toast.makeText(this, "All conditions and permissions must be met to proceed!", Toast.LENGTH_SHORT).show();
            }
        });

        // Request necessary permissions
        requestPermissions();
    }

    // Request required permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, PERMISSION_REQUEST_CODE);
    }

    // Validate all conditions and permissions
    private boolean validateAllConditions() {
        // Constraint 1: Battery level must be odd
        if (!isBatteryLevelOdd()) {
            Toast.makeText(this, "Constraint not met: Battery level must be odd.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Constraint 2: Location permission must be granted
        if (!hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(this, "Constraint not met: Location permission not granted.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Constraint 3: Storage permission must be granted
        if (!hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(this, "Constraint not met: Storage permission not granted.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Constraint 4: Flashlight must be on
        if (!isFlashlightOn()) {
            Toast.makeText(this, "Constraint not met: Flashlight must be on.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // All constraints are met
        return true;
    }

    // Check if battery level is odd
    private boolean isBatteryLevelOdd() {
        BatteryManager batteryManager = (BatteryManager) getSystemService(BATTERY_SERVICE);
        int batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        return batteryLevel % 2 != 0;
    }

    // Check if the specified permission is granted
    private boolean hasPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    // Register TorchCallback to track flashlight state
    private void registerTorchCallback() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        CameraManager.TorchCallback torchCallback = new CameraManager.TorchCallback() {
            @Override
            public void onTorchModeChanged(@NonNull String cameraId, boolean enabled) {
                isFlashlightOn = enabled; // Update flashlight state
            }

            @Override
            public void onTorchModeUnavailable(@NonNull String cameraId) {
                isFlashlightOn = false; // Reset flashlight state if unavailable
            }
        };
        cameraManager.registerTorchCallback(torchCallback, null);
    }

    // Return flashlight state
    private boolean isFlashlightOn() {
        return isFlashlightOn;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "All permissions are required for the app to function!", Toast.LENGTH_LONG).show();
                    return;
                }
            }
            Toast.makeText(this, "Permissions granted! You may proceed.", Toast.LENGTH_SHORT).show();
        }
    }
}
