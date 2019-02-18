package xyz.ljkgpxs.notifyme;

import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.wearable.activity.WearableActivity;
import android.widget.RadioGroup;
import android.widget.TextView;

public class MainActivity extends WearableActivity {
    public final static String SETTINGS_NAME = "settings";
    public final static String MODE = "mode";
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SharedPreferences preferences = getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
        final Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
        int mode = preferences.getInt(MODE, 1);
        if (mode < 0 || mode > 4)
            mode = 1;
        RadioGroup group = findViewById(R.id.group);
        group.check(R.id.mode0 + mode);
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.mode0:
                        preferences.edit().putInt(MODE, 0).apply();
                        break;
                    case R.id.mode1:
                        preferences.edit().putInt(MODE, 1).apply();
                        vibrator.vibrate(
                                VibrationEffect.createWaveform(DataListener.sVibrateMode[0], -1)
                        );
                        break;
                    case R.id.mode2:
                        preferences.edit().putInt(MODE, 2).apply();
                        vibrator.vibrate(
                                VibrationEffect.createWaveform(DataListener.sVibrateMode[1], -1)
                        );
                        break;
                    case R.id.mode3:
                        preferences.edit().putInt(MODE, 3).apply();
                        vibrator.vibrate(
                                VibrationEffect.createWaveform(DataListener.sVibrateMode[2], -1)
                        );
                        break;
                    case R.id.mode4:
                        preferences.edit().putInt(MODE, 4).apply();
                        vibrator.vibrate(
                                VibrationEffect.createWaveform(DataListener.sVibrateMode[3], -1)
                        );
                        break;
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
