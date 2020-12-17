package cn.ieway.evmirror.webrtcclient.override;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.graphics.Point;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;

import org.webrtc.EglBase;
import org.webrtc.EglRenderer.FrameListener;
import org.webrtc.GlRectDrawer;
import org.webrtc.Logging;
import org.webrtc.RendererCommon.GlDrawer;
import org.webrtc.RendererCommon.RendererEvents;
import org.webrtc.RendererCommon.ScalingType;
import org.webrtc.RendererCommon.VideoLayoutMeasure;
import org.webrtc.SurfaceEglRenderer;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.ThreadUtils;
import org.webrtc.VideoFrame;

public class MSurfaceViewRenderer extends SurfaceViewRenderer {
    private static final String TAG = "rtc-2";
    private final String resourceName = this.getResourceName();
    private final VideoLayoutMeasure videoLayoutMeasure = new VideoLayoutMeasure();
    private final SurfaceEglRenderer eglRenderer;
    private RendererEvents rendererEvents;
    private int rotatedFrameWidth;
    private int rotatedFrameHeight;
    private boolean enableFixedSize;
    private int surfaceWidth;
    private int surfaceHeight;
   private MSurfaceViewListener mSurfaceViewListener;
    Point displaySize = new Point();

    public interface MSurfaceViewListener {
        void surfaceSizeChange(int width, int height);
    }


    public void onSurfaceSizeChangeListener(MSurfaceViewListener listener){
        mSurfaceViewListener = listener;
    }

    public MSurfaceViewRenderer(Context context) {
        super(context);
        this.eglRenderer = new SurfaceEglRenderer(this.resourceName);
        this.getHolder().addCallback(this);
        this.getHolder().addCallback(this.eglRenderer);

    }

    public MSurfaceViewRenderer(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.eglRenderer = new SurfaceEglRenderer(this.resourceName);
        this.getHolder().addCallback(this);
        this.getHolder().addCallback(this.eglRenderer);
    }

    public void init(EglBase.Context sharedContext, RendererEvents rendererEvents) {
        this.init(sharedContext, rendererEvents, EglBase.CONFIG_PLAIN, new GlRectDrawer());
    }
    public void init(MSurfaceViewListener viewInterface , EglBase.Context sharedContext, RendererEvents rendererEvents) {
        mSurfaceViewListener = viewInterface;
        this.init(sharedContext, rendererEvents, EglBase.CONFIG_PLAIN, new GlRectDrawer());
    }

    public void init(EglBase.Context sharedContext, RendererEvents rendererEvents, int[] configAttributes, GlDrawer drawer) {
        ThreadUtils.checkIsOnMainThread();
        this.rendererEvents = rendererEvents;
        this.rotatedFrameWidth = 0;
        this.rotatedFrameHeight = 0;
        this.eglRenderer.init(sharedContext, this, configAttributes, drawer);
    }

    public void release() {
        this.eglRenderer.release();
    }

    public void addFrameListener(FrameListener listener, float scale, GlDrawer drawerParam) {
        this.eglRenderer.addFrameListener(listener, scale, drawerParam);
    }

    public void addFrameListener(FrameListener listener, float scale) {
        this.eglRenderer.addFrameListener(listener, scale);
    }

    public void removeFrameListener(FrameListener listener) {
        this.eglRenderer.removeFrameListener(listener);
    }

    public void setEnableHardwareScaler(boolean enabled) {
        ThreadUtils.checkIsOnMainThread();
        this.enableFixedSize = enabled;
        this.updateSurfaceSize();
    }

    public void setMirror(boolean mirror) {
        this.eglRenderer.setMirror(mirror);
    }

    public void setScalingType(ScalingType scalingType) {
        ThreadUtils.checkIsOnMainThread();
        this.videoLayoutMeasure.setScalingType(scalingType);
        this.requestLayout();
    }

    public void setScalingType(ScalingType scalingTypeMatchOrientation, ScalingType scalingTypeMismatchOrientation) {
        ThreadUtils.checkIsOnMainThread();
        this.videoLayoutMeasure.setScalingType(scalingTypeMatchOrientation, scalingTypeMismatchOrientation);
        this.requestLayout();
    }

    public void setFpsReduction(float fps) {
        this.eglRenderer.setFpsReduction(fps);
    }

    public void disableFpsReduction() {
        this.eglRenderer.disableFpsReduction();
    }

    public void pauseVideo() {
        this.eglRenderer.pauseVideo();
    }

    public void onFrame(VideoFrame frame) {
        this.eglRenderer.onFrame(frame);
    }

    protected void onMeasure(int widthSpec, int heightSpec) {
        ThreadUtils.checkIsOnMainThread();
        Point size = this.videoLayoutMeasure.measure(widthSpec, heightSpec, this.rotatedFrameWidth, this.rotatedFrameHeight);
        this.setMeasuredDimension(size.x, size.y);
        Log.d(TAG, "onMeasure: . New size: " + size.x + "x" + size.y);
        this.logD("onMeasure(). New size: " + size.x + "x" + size.y);
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        ThreadUtils.checkIsOnMainThread();
        this.eglRenderer.setLayoutAspectRatio((float) (right - left) / (float) (bottom - top));
        this.updateSurfaceSize();
    }

    public void setDisplaySize(Point size){
        displaySize = size;
        this.updateSurfaceSize();
    }
    private void updateSurfaceSize() {
        ThreadUtils.checkIsOnMainThread();
        if (displaySize.x ==0 || displaySize.y == 0){
            displaySize.set(this.getWidth(),this.getHeight());
        }

        if (this.enableFixedSize && this.rotatedFrameWidth != 0 && this.rotatedFrameHeight != 0 && displaySize.x != 0 && displaySize.y != 0) {
            float layoutAspectRatio = (float) displaySize.x / (float) displaySize.y;
            float frameAspectRatio = (float) this.rotatedFrameWidth / (float) this.rotatedFrameHeight;
            int drawnFrameWidth;
            int drawnFrameHeight;
            if (frameAspectRatio > layoutAspectRatio) {
                drawnFrameWidth = displaySize.x;
                drawnFrameHeight = (int) ((float) this.rotatedFrameHeight * ((float) displaySize.x / (float) this.rotatedFrameWidth));
            } else {
                drawnFrameWidth = (int) (((float) displaySize.y / (float) this.rotatedFrameHeight) * (float) this.rotatedFrameWidth);
                drawnFrameHeight = displaySize.y;
            }

            Log.d(TAG, "updateSurfaceSize: layoutAspectRatio=" + layoutAspectRatio + "    frameAspectRatio=" + frameAspectRatio);
            Log.d(TAG, "updateSurfaceSize: drawnFrameWidth=" + drawnFrameWidth + "    drawnFrameHeight=" + drawnFrameHeight);

            int width = Math.min(displaySize.x, drawnFrameWidth);
            int height = Math.min(displaySize.y, drawnFrameHeight);
            this.logD("updateSurfaceSize. Layout size: " + displaySize.x + "x" + displaySize.y + ", frame size: " + this.rotatedFrameWidth + "x" + this.rotatedFrameHeight + ", requested surface size: " + width + "x" + height + ", old surface size: " + this.surfaceWidth + "x" + this.surfaceHeight);
            if (width != this.surfaceWidth || height != this.surfaceHeight) {
                this.surfaceWidth = width;
                this.surfaceHeight = height;
                this.getHolder().setFixedSize(width, height);
                if(mSurfaceViewListener != null){
                    Log.d(TAG, "updateSurfaceSize: mSurfaceViewListener != null +++++++ " );
                    mSurfaceViewListener.surfaceSizeChange(width,height);
                }
            }
            Log.d(TAG, "updateSurfaceSize. Layout size: " + displaySize.x + "x" + displaySize.y + ", frame size: " + this.rotatedFrameWidth + "x" + this.rotatedFrameHeight +
                    ", requested surface size: " + width + "x" + height + ", old surface size: " + this.surfaceWidth + "x" + this.surfaceHeight);
        } else {
            this.surfaceWidth = this.surfaceHeight = 0;
            this.getHolder().setSizeFromLayout();
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        ThreadUtils.checkIsOnMainThread();
        this.surfaceWidth = this.surfaceHeight = 0;
        this.updateSurfaceSize();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "surfaceChanged: format=" + format + "   width=" + width + "  height=" + height);
        if(mSurfaceViewListener != null){
            Log.d(TAG, "surfaceChanged: surfaceChanged +++++++ " );
            mSurfaceViewListener.surfaceSizeChange(width,height);
        }
    }

    private String getResourceName() {
        try {
            return this.getResources().getResourceEntryName(this.getId());
        } catch (NotFoundException var2) {
            return "";
        }
    }

    public void clearImage() {
        this.eglRenderer.clearImage();
    }

    public void onFirstFrameRendered() {
        if (this.rendererEvents != null) {
            this.rendererEvents.onFirstFrameRendered();
        }

    }

    public void onFrameResolutionChanged(int videoWidth, int videoHeight, int rotation) {
        Log.d(TAG, "onFrameResolutionChanged: videoWidth:" + videoWidth + "  videoHeight:");
        if (this.rendererEvents != null) {
            this.rendererEvents.onFrameResolutionChanged(videoWidth, videoHeight, rotation);
        }

        int rotatedWidth = rotation != 0 && rotation != 180 ? videoHeight : videoWidth;
        int rotatedHeight = rotation != 0 && rotation != 180 ? videoWidth : videoHeight;
        this.postOrRun(() -> {
            this.rotatedFrameWidth = rotatedWidth;
            this.rotatedFrameHeight = rotatedHeight;
            this.updateSurfaceSize();
            this.requestLayout();
        });
    }

    private void postOrRun(Runnable r) {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            r.run();
        } else {
            this.post(r);
        }

    }

    private void logD(String string) {
        Logging.d("SurfaceViewRenderer", this.resourceName + ": " + string);
    }
}