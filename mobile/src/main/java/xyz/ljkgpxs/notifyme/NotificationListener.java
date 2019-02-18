package xyz.ljkgpxs.notifyme;

import android.app.Service;
import android.os.PowerManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.concurrent.ExecutionException;

public class NotificationListener extends NotificationListenerService {
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        Log.d("NotificationListenerService", "Here coming a message");


        PowerManager powerManager = (PowerManager) getSystemService(Service.POWER_SERVICE);

        if (powerManager != null && powerManager.isInteractive())
            return;


        if ((sbn.getPackageName().equalsIgnoreCase("com.tencent.tim")
                || sbn.getPackageName().equalsIgnoreCase("com.tencent.mobileqq"))
                && sbn.isClearable()) {
            Log.d("NotificationListenerService", "Sync data");
            syncData();
        }
    }

    private void syncData() {
        PutDataMapRequest request = PutDataMapRequest.create("/QQ-incoming");
        request.getDataMap().putLong("update", System.currentTimeMillis());
        PutDataRequest dataRequest = request.asPutDataRequest();
        dataRequest.setUrgent();
        final Task<DataItem> task = Wearable.getDataClient(getApplicationContext()).putDataItem(dataRequest);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Tasks.await(task);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
