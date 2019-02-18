package xyz.ljkgpxs.notifyme;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;

import java.util.Set;

import xyz.ljkgpxs.notifyme.dialog.DeniedPreminnisonDialog;
import xyz.ljkgpxs.notifyme.dialog.RequestPermissionDialog;

public class MainActivity extends AppCompatActivity {
    private int i = 0;
    private boolean mIsAsked = false;
    private boolean mIsShowed = false;
    private boolean mIsWearConnected = false;
    private Handler mHandler;
    private View mRootView;

    private final int TIME_PRE_RUNNABLE = 2000;

    private Runnable mDetectWear = new Runnable() {
        @Override
        public void run() {
            BluetoothAdapter mBtAdapter = BluetoothAdapter.getDefaultAdapter();

            Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
            for (BluetoothDevice device : pairedDevices) {
                if (device.getBluetoothClass().getDeviceClass()
                        == BluetoothClass.Device.WEARABLE_WRIST_WATCH) {
                    if (!mIsWearConnected) {
                        mIsShowed = true;
                        postSnack("手表已连接");
                    }
                    mIsWearConnected = true;
                    mHandler.postDelayed(this, TIME_PRE_RUNNABLE);
                    return;
                }
            }
            if (mIsWearConnected) {
                postSnack("手表已断开");
            }

            if (!mIsWearConnected && !mIsShowed) {
                mIsShowed = true;
                postSnack("手表已断开");
            }
            mIsWearConnected = false;

            mHandler.postDelayed(this, TIME_PRE_RUNNABLE);
        }

        private void postSnack(String text) {
            Snackbar.make(mRootView, text, Snackbar.LENGTH_INDEFINITE)
                    .setAction("知道了", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    }).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRootView = findViewById(R.id.rootView);

        mHandler = new Handler();
        mHandler.postDelayed(mDetectWear, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isEnabled()) {
            if (mIsAsked) {
                new DeniedPreminnisonDialog().show(getSupportFragmentManager(), "AskDialog");
            } else {
                new RequestPermissionDialog().show(getSupportFragmentManager(), "RequestDialog");
                mIsAsked = true;
            }
        }
    }

    private boolean isEnabled() {
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(), "enabled_notification_listeners");
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
