package xyz.ljkgpxs.notifyme;

import android.app.Service;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;

import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.WearableListenerService;

public class DataListener extends WearableListenerService implements SensorEventListener {
    private Vibrator mVibrator;
    private boolean mIsOnBody = true;
    private SensorManager mSensorManager;
    public final static long[][] sVibrateMode = new long[][]{
            {0, 100, 150, 200, 150, 400},
            {0, 500},
            {0, 100, 200, 400},
            {0, 100, 100, 100, 100, 300}
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("DataListener", "OnCreate");
        mVibrator = (Vibrator) getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
        mSensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager != null) {
            boolean hasSensor = mSensorManager.getDefaultSensor(34, true) != null;
            Sensor sensor = mSensorManager.getDefaultSensor(34, true);
            mSensorManager.registerListener(this, sensor, 0);
        }
    }

    @Override
    public void onDestroy() {
        mSensorManager.unregisterListener(this);
        super.onDestroy();
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        super.onDataChanged(dataEventBuffer);
        Log.d("DataListener", "OnDataChanged");

        try {
            // 免打扰模式， 开为1， 关为0
            int zen = Settings.Global.getInt(getContentResolver(), "zen_mode");
            Log.d("State", "" + zen + " " + mIsOnBody);

            if (zen != 0 || !mIsOnBody) {
                return;
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        final int mode = getSharedPreferences(MainActivity.SETTINGS_NAME, Context.MODE_PRIVATE)
                .getInt(MainActivity.MODE, 1);
        if (mode <= 0 || mode > 4)
            return;

        new Thread(new Runnable() {
            @Override
            public void run() {
                // 防止与通知振动冲突
                SystemClock.sleep(500);
                mVibrator.vibrate(
                        VibrationEffect.createWaveform(sVibrateMode[mode - 1], -1)
                );
            }
        }).start();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.values[0] == 1.0) {
            mIsOnBody = true;
        } else {
            mIsOnBody = false;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
