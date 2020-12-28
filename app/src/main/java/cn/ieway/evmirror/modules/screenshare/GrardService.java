package cn.ieway.evmirror.modules.screenshare;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import cn.ieway.evmirror.R;

public class GrardService extends Service {
    private NotificationManager mNotificationManager;
    private NotificationChannel notificationChannel;
    private static final int CODE_NOTIFICATION_1 = android.os.Process.myPid();

    public GrardService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setForeground();
        createCustomNotification();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private void setForeground() {
        initNotificationManager();
        //createCustomNotification(BaseApplication.isCapturing());
    }

    private void initNotificationManager() {

        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
    }


    private void destoryCustomNotification() {
        mNotificationManager.cancel(CODE_NOTIFICATION_1);
    }

    public void createCustomNotification() {
        //先刪除之前的通知
        mNotificationManager.cancel(CODE_NOTIFICATION_1);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, getChannelId())
                .setSmallIcon(R.mipmap.ic_logo)
                .setContentTitle("EV投屏")
                .setContentIntent(null)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentText("投屏正在投屏中...");

        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_ONGOING_EVENT | Notification.FLAG_AUTO_CANCEL;
        //打开/更新通知
        mNotificationManager.notify(CODE_NOTIFICATION_1, notification);
        startForeground(CODE_NOTIFICATION_1, notification);
    }

    public String getChannelId() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "channel01";
            CharSequence channelName = "服务";
            String description = "维护投屏功能的稳定性";
            int channelImportance = NotificationManager.IMPORTANCE_LOW;

            notificationChannel = new NotificationChannel(channelId, channelName, channelImportance);
            // 设置描述 最长30字符
            notificationChannel.setDescription(description);
            // 设置显示模式
            notificationChannel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            mNotificationManager.createNotificationChannel(notificationChannel);

            notificationChannel = mNotificationManager.getNotificationChannel(channelId);

            //  Log.d(TAG, "[GuarderService] createNotificationChannel: Importance = " + notificationChannel.getImportance());
            return channelId;
        } else {
            return null;
        }
    }

}