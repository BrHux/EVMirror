package cn.ieway.evmirror.modules.screenshare;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
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
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import java.util.Objects;

import cn.ieway.evmirror.R;
import cn.ieway.evmirror.screencapture.ScreenRecord;

import static cn.ieway.evmirror.application.MirrorApplication.sMe;

public class ScreenShareService extends Service {

    public static final String EXTRA_CDM = "extra_cmd";
    public static final int POP_START = 100;
    public static final int POP_QUIT = 102;

    int mResultCode;
    Intent mResultData;
    MediaProjection mMediaProjection;
    MediaProjectionManager mMediaProjectionManager;
    private NotificationManager mNotificationManager;
    private NotificationChannel notificationChannel;
    private static final int CODE_NOTIFICATION_1 = android.os.Process.myPid();
    private WindowManager windowManager;
    private Point mPoint = new Point();
    private ScreenRecord screenRecord;
    private int socket_point;
    private String socket_url;
    private String socket_key;

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

        if (intent == null) return START_STICKY;
        int order = intent.getIntExtra(EXTRA_CDM, -1);
        switch (order) {
            case POP_START: {
                try {
                    startRecord(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        return START_STICKY;
    }

    private void startRecord(Intent intent) throws Exception {
        if (intent != null) {
            mResultCode = intent.getIntExtra("md_code", -1);
            mResultData = intent.getParcelableExtra("md_data");

            socket_point = intent.getIntExtra("socket_point", 0);
            socket_url = intent.getStringExtra("socket_url");
            socket_key = intent.getStringExtra("socket_key");

            if (mResultData == null || socket_point == 0 || socket_url.isEmpty() || socket_key.isEmpty()) {
                //参数异常
                return;
            }

            mMediaProjectionManager = (MediaProjectionManager) getApplication().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            mMediaProjection = mMediaProjectionManager.getMediaProjection(mResultCode, Objects.requireNonNull(mResultData));
            if (mMediaProjection == null) return;
            if (screenRecord != null) {
                screenRecord.release();
            }

            screenRecord = new ScreenRecord(mMediaProjection, socket_key);
            screenRecord.setSocket(socket_url, socket_point);
            screenRecord.start();
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (windowManager == null) return;
        if (mMediaProjection == null) return;
//        Point outSize = new Point();
//        windowManager.getDefaultDisplay().getRealSize(outSize);
//        sMe.setScreenSize(outSize);
        screenRecord.surfaceChange();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        destoryCustomNotification();
        if (screenRecord != null) {
            screenRecord.release();
            screenRecord = null;
        }
    }


    //=====================================  Notification  =========================================================

    private void createNotificationChannel(String msg) {
        Notification.Builder builder = new Notification.Builder(this.getApplicationContext()); //获取一个Notification构造器
        Intent nfIntent = new Intent(this, ScreenShareActivityNew.class); //点击后跳转的界面，可以设置跳转数据
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