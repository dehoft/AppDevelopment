package edu.ktu.lab3;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Compass extends AppCompatActivity implements SensorEventListener {


    private ImageView imageView;
    private float[] mGravity = new float[3];
    private float[] mGeometric = new  float[3];
    private float azimuth = 0f;
    private float currentAzimuth= 0f;
    private SensorManager mSensorManager;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.compassdesign);

        imageView=(ImageView)findViewById(R.id.compass);
        mSensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);

    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        final float alpha = 0.97f;
        synchronized (this){
            if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            {
                mGravity[0]=alpha*mGravity[0]+(1-alpha)*event.values[0];
                mGravity[1]=alpha*mGravity[1]+(1-alpha)*event.values[1];
                mGravity[2]=alpha*mGravity[2]+(1-alpha)*event.values[2];

            }

            if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            {
                mGeometric[0]=alpha*mGeometric[0]+(1-alpha)*event.values[0];
                mGeometric[1]=alpha*mGeometric[1]+(1-alpha)*event.values[1];
                mGeometric[2]=alpha*mGeometric[2]+(1-alpha)*event.values[2];

            }

            float R[] = new float[9];
            float I[] = new float[9];
            boolean success=SensorManager.getRotationMatrix(R,I,mGravity,mGeometric);
            if(success)
            {
                float orientation[] =new float[3];
                SensorManager.getOrientation(R,orientation);
                azimuth=(float) Math.toDegrees(orientation[0]);
                azimuth=(azimuth+360)%360;

                Animation animation = new RotateAnimation(-currentAzimuth,-azimuth, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
                currentAzimuth=azimuth;
                animation.setDuration(500);
                animation.setRepeatCount(0);
                animation.setFillAfter(true);

                imageView.startAnimation(animation);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
