package com.alikocak.compassapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.Image;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    public ImageView compass;
    private float[] mGravity = new float[3];
    private float[] mGeomagnetic = new float[3];
    private float azimuth = 0f;
    private float currectAzimuth = 0f;
    private SensorManager mSensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        compass = (ImageView) findViewById(R.id.imageView3);
        compass.setImageBitmap(drawShapes());

        mSensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
    }



    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this,mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this,mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_GAME);
    }


    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        final float alpha = 0.97f;
        synchronized (this){
            if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
                mGravity[0] = alpha*mGravity[0]+(1-alpha)*sensorEvent.values[0];
                mGravity[1] = alpha*mGravity[1]+(1-alpha)*sensorEvent.values[1];
                mGravity[2] = alpha*mGravity[2]+(1-alpha)*sensorEvent.values[2];
            }

            if(sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
                mGeomagnetic[0] = alpha*mGeomagnetic[0]+(1-alpha)*sensorEvent.values[0];
                mGeomagnetic[1] = alpha*mGeomagnetic[1]+(1-alpha)*sensorEvent.values[1];
                mGeomagnetic[2] = alpha*mGeomagnetic[2]+(1-alpha)*sensorEvent.values[2];
            }

            float R[] = new float[9];
            float I[] = new float[9];

            boolean success = SensorManager.getRotationMatrix(R,I,mGravity,mGeomagnetic);
            if(success){
                float orientation[] = new float[3];
                SensorManager.getOrientation(R,orientation);
                azimuth = (float)Math.toDegrees(orientation[0]);
                azimuth = (azimuth+360)%360;




                Animation anim = new RotateAnimation(-currectAzimuth,-azimuth,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
                currectAzimuth = azimuth;
                anim.setDuration(500);
                anim.setRepeatCount(0);
                anim.setFillAfter(true);

                compass.startAnimation(anim);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    public Bitmap drawShapes() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        Bitmap bitmap = Bitmap.createBitmap(width+130, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        int x = canvas.getWidth();
        int y = canvas.getHeight();
        int radius;
        radius = 350;

        //Paint white = new Paint();
        //white.setARGB(255, 111, 111, 111);
        //canvas.drawPaint(white);


        Paint black = new Paint();
        black.setARGB(255, 0, 0, 0);
        black.setStrokeWidth(25);


        black.setStyle(Paint.Style.STROKE);


        canvas.drawCircle(x / 2, y / 2, radius, black);

        Paint pen = new Paint();
        pen.setStyle(Paint.Style.FILL_AND_STROKE);
        pen.setStrokeWidth(5);
        pen.setARGB(255, 255, 0, 0);
        pen.setTextSize(100);
        canvas.drawText("W", x / 15, y / 2 + 50, pen);
        canvas.drawText("E", x - x / 7, y / 2 + 50, pen);
        canvas.drawText("N", x / 2 - 40, y / 2 - 400, pen);
        canvas.drawText("S", x / 2 - 30, y / 2 + 470, pen);

        Paint pen2 = new Paint();
        pen2.setStyle(Paint.Style.FILL_AND_STROKE);
        pen2.setStrokeWidth(5);
        pen2.setARGB(255, 0, 0, 0);
        pen2.setTextSize(55);

        canvas.drawText("NW", x / 6, y / 2 - 250, pen2);
        canvas.drawText("NE", x - x / 4, y / 2 - 260, pen2);
        canvas.drawText("SW", x / 6, y / 2 + 320, pen2);
        canvas.drawText("SE", x - x / 4, y / 2 + 320, pen2);


        Paint pen4 = new Paint();
        pen4.setARGB(255, 0, 153, 0);
        pen4.setStyle(Paint.Style.FILL_AND_STROKE);
        pen4.setStrokeWidth(2);


        Point p1 = new Point(x / 2 - 30, y / 2);
        Point p2 = new Point(x / 2, y / 3 + 20);
        Point p3 = new Point(x / 2 + 30, y / 2);


        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(p1.x, p1.y);
        path.lineTo(p2.x, p2.y);
        path.lineTo(p3.x, p3.y);
        path.lineTo(p1.x, p1.y);


        canvas.drawPath(path, pen4);


        Paint pen3 = new Paint();

        canvas.drawCircle(x / 2, y / 2, 40, pen3);
        return bitmap;
    }
}