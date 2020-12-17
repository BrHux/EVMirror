package cn.ieway.evmirror.modules.screenshare;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.IBinder;

import java.util.Objects;

import cn.ieway.evmirror.R;
import cn.ieway.evmirror.util.LogUtil;

import static cn.ieway.evmirror.application.MirrorApplication.webRtcClient;

public class ScreenShareService extends Service {

    int mResultCode;
    Intent mResultData;
    MediaProjection mMediaProjection;
    MediaProjectionManager mMediaProjectionManager;

    public ScreenShareService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel("");
        if (intent != null) {
            mResultCode = intent.getIntExtra("code", -1);
            mResultData = intent.getParcelableExtra("data");
            if (mResultData == null) {
                return START_STICKY;
            }

            try {
                mMediaProjectionManager =
                        (MediaProjectionManager) getApplication().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
                mMediaProjection = mMediaProjectionManager.getMediaProjection(mResultCode, Objects.requireNonNull(mResultData));
                if (webRtcClient != null && mMediaProjection != null) {
                    webRtcClient.screenCapturer(mResultData, mMediaProjection);
                }
            } catch (Exception e) {
                LogUtil.i("MediaProjection Exception : " + e.toString());
                return START_STICKY;
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void createNotificationChannel(String msg) {
        Notification.Builder builder = new Notification.Builder(this.getApplicationContext()); //获取一个Notification构造器
        Intent nfIntent = new Intent(this, ScreenShareActivity.class); //点击后跳转的界面，可以设置跳转数据
        if (msg == null || msg.isEmpty()) {
            msg = "EV投屏正在运行...";
        }
        builder/*.setContentIntent(PendingIntent.getActivity(this, 0, nfIntent, 0))*/ // 设置PendingIntent
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_logo)) // 设置下拉列表中的图标(大图标)
                //.setContentTitle("SMI InstantView") // 设置下拉列表里的标题
                .setSmallIcon(R.mipmap.ic_logo) // 设置状态栏内的小图标
                .setContentText(msg) // 设置上下文内容
                .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间

        /*以下是对Android 8.0的适配*/
        //普通notification适配
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId("notification_id");
        }
        //前台服务notification适配
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel("notification_id", "notification_name", NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(channel);
        }

        Notification notification = builder.build(); // 获取构建好的Notification
        notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音
        startForeground(110, notification);

    }
}