package com.example.stepscounter;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager = null;
    private Sensor stepsSensor;
    private int totalSteps = 0;
    private int previewsTotalSteps = 0;
    private ProgressBar progressBar;
    private TextView steps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        progressBar = findViewById(R.id.progressBar);
        steps = findViewById(R.id.steps);

        resetSteps(); // Set up click and long click listeners
        loadData();   // Load previously saved steps data

        // Initialize SensorManager and get the step sensor
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        stepsSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        if (stepsSensor == null) {
            Toast.makeText(this, "This device does not have a step counter sensor", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Register sensor listener if the step sensor is available
        if (stepsSensor != null) {
            mSensorManager.registerListener(this, stepsSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister sensor listener to save battery
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Handle step counter updates
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            totalSteps = (int) event.values[0];
            int currentSteps = totalSteps - previewsTotalSteps;
            steps.setText(String.valueOf(currentSteps));
            progressBar.setProgress(currentSteps);
        }
    }

    private void resetSteps() {
        // Set onClick and onLongClick listeners for reset functionality
        steps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Long Press to reset steps", Toast.LENGTH_SHORT).show();
            }
        });

        steps.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                previewsTotalSteps = totalSteps; // Reset the steps count
                steps.setText("0");
                progressBar.setProgress(0);
                saveData();  // Save the reset step count
                return true;
            }
        });
    }

    private void saveData() {
        // Save the total steps data to shared preferences
        SharedPreferences sharedPref = getSharedPreferences("myPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putFloat("key1", previewsTotalSteps); // Store as float
        editor.apply();
    }

    private void loadData() {
        // Load saved step count data from shared preferences
        SharedPreferences sharedPref = getSharedPreferences("myPref", Context.MODE_PRIVATE);
        previewsTotalSteps = (int) sharedPref.getFloat("key1", 0f); // Cast back to int
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // Not used in this case
    }
}
