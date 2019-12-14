package com.vivekmohanan.finalassignment.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vivekmohanan.finalassignment.R;
import com.vivekmohanan.finalassignment.models.SensorValues;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final String PRIMARY_CHANNEL_ID = "primary_notifaction_channel";
    private NotificationManager mNotificationManager;
    private static final int NOTIFICATION_ID=0;

    // System sensor manager instance.
    private SensorManager mSensorManager;

    // Accelerometer and magnetometer sensors, as retrieved from the
    // sensor manager.
    private Sensor mSensorAccelerometer;
    private Sensor mSensorMagnetometer;

    // TextViews to display current sensor values.
    private TextView mTextSensorAzimuth;
    private TextView mTextSensorPitch;
    private TextView mTextSensorRoll;

    private ImageView mSpotTop;
    private ImageView mSpotBottom;
    private ImageView mSpotLeft;
    private ImageView mSpotRight;
    private Button save;

    private float[] mAccelerometerData = new float[3];
    private float[] mMagnetometerData = new float[3];

    // Very small values for the accelerometer (on all three axes) should
    // be interpreted as 0. This value is the amount of acceptable
    // non-zero drift.
    private static final float VALUE_DRIFT = 0.05f;

    int Id = 0;

    //Top to bottom tilt. 0 is flat on a surface
    public float pitchPrint;
    //Left to right tilt
    public float rollPrint;

    SensorValues value;


    //private DatabaseReference mDatabase;

    ArrayList<SensorValues> SensorValuesArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

// ...
        //mDatabase = FirebaseDatabase.getInstance().getReference();

        // Lock the orientation to portrait (for now)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);



        mSpotTop = findViewById(R.id.spot_top);
        mSpotBottom = findViewById(R.id.spot_bottom);
        mSpotLeft = findViewById(R.id.spot_left);
        mSpotRight = findViewById(R.id.spot_right);

        mTextSensorAzimuth = findViewById(R.id.value_azimuth);
        mTextSensorPitch = findViewById(R.id.value_pitch);
        mTextSensorRoll = findViewById(R.id.value_roll);

        // Get accelerometer and magnetometer sensors from the sensor manager.
        // The getDefaultSensor() method returns null if the sensor
        // is not available on the device.
        mSensorManager = (SensorManager) getSystemService(
                Context.SENSOR_SERVICE);
        mSensorAccelerometer = mSensorManager.getDefaultSensor(
                Sensor.TYPE_ACCELEROMETER);
        mSensorMagnetometer = mSensorManager.getDefaultSensor(
                Sensor.TYPE_MAGNETIC_FIELD);


    }

    @Override
    protected void onStart() {
        super.onStart();

        // Listeners for the sensors are registered in this callback and
        // can be unregistered in onStop().
        //
        // Check to ensure sensors are available before registering listeners.
        // Both listeners are registered with a "normal" amount of delay
        // (SENSOR_DELAY_NORMAL).
        createNotificationChannel();
        if (mSensorAccelerometer != null) {
            mSensorManager.registerListener(this, mSensorAccelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (mSensorMagnetometer != null) {
            mSensorManager.registerListener(this, mSensorMagnetometer,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }


    public void createNotificationChannel(){
        mNotificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID,"Test Notification Channel",NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notification from Test Notiication Channel");
            mNotificationManager.createNotificationChannel(notificationChannel);

        }
    }

    public void send_notification(){


        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();
        mNotificationManager.notify(NOTIFICATION_ID,notifyBuilder.build());


    }

    private NotificationCompat.Builder getNotificationBuilder(){

        Intent notificationIntent = new Intent(this,MainActivity.class);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(this,NOTIFICATION_ID,notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this,PRIMARY_CHANNEL_ID).setContentTitle("Centered").setContentTitle("Centered: " + getTimestamp()).setSmallIcon(R.drawable.uparrow).setContentIntent(notificationPendingIntent).setAutoCancel(true);
        return notifyBuilder;

    }

    public void pointerMove(View view,float pitchaxis,float rollaxis){
        ObjectAnimator X;
        ObjectAnimator Y;
        AnimatorSet animatorXY = new AnimatorSet();

        X = ObjectAnimator.ofFloat(view,"X",4000 * rollaxis + 450);
        Y = ObjectAnimator.ofFloat(view,"Y",4000 * pitchaxis + 700);
        animatorXY.playTogether(X,Y);
        animatorXY.setDuration(150);
        animatorXY.start();
    }



    @Override
    protected void onStop() {
        super.onStop();

        // Unregister all sensor listeners in this callback so they don't
        // continue to use resources when the app is stopped.
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int sensorType = event.sensor.getType();

        //Check what sensor was triggered
        switch (sensorType){
            case Sensor.TYPE_ACCELEROMETER:
                mAccelerometerData = event.values.clone();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                mMagnetometerData = event.values.clone();
                break;
            default:
                return;
        }

        float[] rotationMatrix = new float[9];
        boolean rotationOK = SensorManager.getRotationMatrix(rotationMatrix, null, mAccelerometerData, mMagnetometerData);

        //Create array of three float numbers that will hold three components of orientation
        float orientationValues[] = new float[3];
        if (rotationOK){
            SensorManager.getOrientation(rotationMatrix, orientationValues);
        }

        //Direction the device is pointing. Zero is magnetic north
        float azimuth = orientationValues[0];
        //Top to bottom tilt. 0 is flat on a surface
        float pitch = orientationValues[1];

        //Left to right tilt
        float roll = orientationValues[2];

        if (Math.abs(pitch) < VALUE_DRIFT){
            pitch = 0;
        }
        if (Math.abs(roll) < VALUE_DRIFT){
            roll = 0;
        }

        mSpotTop.setAlpha(1f);
        mSpotBottom.setAlpha(0f);
        mSpotLeft.setAlpha(0f);
        mSpotRight.setAlpha(0f);

        pointerMove(mSpotTop,pitch,roll);


        if(pitch == 0f && roll == 0f){

            addSensorValues(pitch,roll);
            send_notification();

        }



        mTextSensorAzimuth.setText(getResources().getString(R.string.value_format, azimuth));
        mTextSensorPitch.setText(getResources().getString(R.string.value_format, pitch));
        mTextSensorRoll.setText(getResources().getString(R.string.value_format, roll));

        pitchPrint = pitch;
        BigDecimal pitchtemp = new BigDecimal(Float.toString(pitchPrint));
        pitchtemp = pitchtemp.setScale(2, BigDecimal.ROUND_HALF_UP);
        pitchPrint =  pitchtemp.floatValue();

        rollPrint = roll;
        BigDecimal rolltemp = new BigDecimal(Float.toString(rollPrint));
        rolltemp = rolltemp.setScale(2, BigDecimal.ROUND_HALF_UP);
        rollPrint =  rolltemp.floatValue();

    }

    private void addSensorValues(float pitch, float roll) {
        value = new SensorValues(pitch, roll, getTimestamp());
        SensorValuesArrayList.add(value);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /*
    private void writeToDb(int userId, String pitch, String roll, String time) {
        //SensorValues value = new SensorValues("X: " + pitch,"Y: " + roll,"Time: " + time);

        mDatabase.child("XY History").child(String.valueOf(userId)).setValue(value);
        Id ++;
    }
*/
    private String getTimestamp(){
        Date currentTime = Calendar.getInstance().getTime();
        String timestamp = String.valueOf(currentTime);
        return timestamp;
    }

    public void save_clicked(View view) {
        addSensorValues(pitchPrint,rollPrint);

        send_notification();
        Toast toast = Toast.makeText(getApplicationContext(), "Saved X/Y Values to history", Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_history){
            if (!SensorValuesArrayList.isEmpty()){
                Bundle b = new Bundle();
                b.putParcelableArrayList("Sensor Values", SensorValuesArrayList);
                Intent i = new Intent(this, ViewHistory.class);

                i.putExtras(b);

                startActivity(i);
            } else {

                Toast toast = Toast.makeText(getApplicationContext(), "No History Found", Toast.LENGTH_LONG);
                toast.show();
            }

        }
        return super.onOptionsItemSelected(item);
    }
}
