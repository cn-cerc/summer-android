package com.yt.hz.financial.argame.util;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import static android.content.Context.SENSOR_SERVICE;

public class CustomSensorManager implements SensorEventListener {
    private  static  volatile  CustomSensorManager instance=null;
    private SensorManager sensorManager = null;
    private Context context;
    private Sensor sensor;
    private float toDegrees;

    public synchronized static CustomSensorManager getInstance(Context context) {
        synchronized(CustomSensorManager.class){
            if(instance == null){
                instance = new CustomSensorManager(context);
            }
            return instance;
        }
    }

    public CustomSensorManager(Context context){
        this.context = context.getApplicationContext();
        if(sensorManager==null){
            sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
            //通过 getDefaultSensor 获得指南针传感器
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
            //为传感器管理者注册监听器，第三个参数指获取速度正常
            boolean b = sensorManager.registerListener(CustomSensorManager.this, sensor, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        switch (sensorEvent.sensor.getType()) {
            case Sensor.TYPE_ORIENTATION:
                //顺时针转动为正，故手机顺时针转动时，图片得逆时针转动
                toDegrees = (float) -sensorEvent.values[0];
                if( this.onAnglaChanged !=null){
                    onAnglaChanged.onChanged(toDegrees);
                }
                break;
        }

    }

    public void cancel(){
        if(sensorManager!=null){
            sensorManager.unregisterListener(this);
        }
        if(onAnglaChanged!=null){
            onAnglaChanged = null;
        }
        if(instance!=null){
            instance = null;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
    public interface OnAnglaChanged{
        void onChanged(float angla);
    }
    private OnAnglaChanged onAnglaChanged;
    public void setOnAnglaChanged(OnAnglaChanged onAnglaChanged){
        this.onAnglaChanged=onAnglaChanged;
    }
}
