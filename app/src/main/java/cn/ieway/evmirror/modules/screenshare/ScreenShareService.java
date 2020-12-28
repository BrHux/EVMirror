package cn.ieway.evmirror.modules.screenshare;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import java.util.Objects;

import cn.ieway.evmirror.R;
import cn.ieway.evmirror.application.MirrorApplication;
import cn.ieway.evmirror.util.LogUtil;

import static cn.ieway.evmirror.application.MirrorApplication.sMe;
import static cn.ieway.evmirror.application.MirrorApplication.webRtcClient;

public class ScreenShareService extends Service {

    int mResultCode;
    Intent mResultData;
    MediaProjection mMediaProjection;
    MediaProjectionManager mMediaProjectionManager;
    private NotificationManager mNotificationManager;
    private NotificationChannel notificationChannel;
    private static final int CODE_NOTIFICATION_1 = android.os.Process.myPid();
    private WindowManager windowManager;
    private Point mPoint = new Point();

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

        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        setForeground();
        createCustomNotification();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        createNotificationChannel("");
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
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (windowManager == null) return;
        Point outSize = new Point();
        windowManager.getDefaultDisplay().getRealSize(outSize);
        if (outSize.equals(mPoint)) return;
        if (webRtcClient == null) return;

        int width = outSize.x > outSize.y ? sMe.screenHeight: sMe.screenWidth;
        int height = outSize.x > outSize.y ? sMe.screenWidth : sMe.screenHeight;

        webRtcClient.changeCaptureFormat(width, height, sMe.getVideo_fps());
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