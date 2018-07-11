package com.example.takethraithip.myproject;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class RotationActivity extends AppCompatActivity implements SensorEventListener{

    private SensorManager mSensorManager;
    private Sensor mRotationSensor;
    private  Sensor mPressure;
    TextView xValue;
    TextView yValue;
    TextView zValue;
    TextView airTemp;
    private ScreenStateReciever mReceiver;
    private static final int SENSOR_DELAY = 500 * 1000; // 500ms
    private static final int FROM_RADS_TO_DEGS = 57;

    Button rotationBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rotation);

        xValue = (TextView) findViewById(R.id.pitch);
        yValue = (TextView) findViewById(R.id.roll);
        zValue = (TextView) findViewById(R.id.zAxis);
        rotationBack = (Button) findViewById(R.id.backRotation) ;



        mSensorManager = (SensorManager) getSystemService(Activity.SENSOR_SERVICE);
        mRotationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mRotationSensor, SENSOR_DELAY);

        rotationBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSensorManager.unregisterListener(RotationActivity.this,mRotationSensor);
                Intent intent = new Intent(RotationActivity.this,MainActivity.class);
                startActivity(intent);
                finish();

            }
        });

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        mReceiver = new ScreenStateReciever();
        registerReceiver(mReceiver, intentFilter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == mRotationSensor) {
                xValue.setText("xValue" + event.values[0]);
                yValue.setText("yValue" + event.values[1]);
                zValue.setText("zValue" + event.values[2]);

                if (event.values[0] > 0.5 || event.values[0] < 0.5
                        && event.values[1] > 0.5 || event.values[1] < 0.5
                        && event.values[2] > 10.0 || event.values[2] < 9.5){
                    //Toast.makeText(this,"Rotation Change",Toast.LENGTH_SHORT).show();
                }

        }

    }

}
