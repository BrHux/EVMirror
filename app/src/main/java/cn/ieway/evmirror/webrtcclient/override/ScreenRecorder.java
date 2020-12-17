package cn.ieway.evmirror.webrtcclient.override;


import android.content.Context;
import android.content.Intent;
import android.hardware.display.VirtualDisplay;
import android.media.projection.MediaProjection;
import android.os.Handler;
import android.view.Surface;

import androidx.annotation.Nullable;

import org.webrtc.CapturerObserver;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.ThreadUtils;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoFrame;
import org.webrtc.VideoSink;

public class ScreenRecorder  implements VideoCapturer, VideoSink {

    private static final int DISPLAY_FLAGS = 3;
    private static final int VIRTUAL_DISPLAY_DPI = 400;
    private final MediaProjection.Callback mediaProjectionCallback;
    private int width;
    private int height;
    @Nullable
    private VirtualDisplay virtualDisplay;
    @Nullable
    private SurfaceTextureHelper surfaceTextureHelper;
    @Nullable
    private CapturerObserver capturerObserver;
    private long numCapturedFrames;
    @Nullable
    private MediaProjection mediaProjection;
    private boolean isDisposed;

    public ScreenRecorder(Intent mediaProjectionPermissionResultData, MediaProjection mediaProjection, MediaProjection.Callback mediaProjectionCallback) {
//        super(mediaProjectionPermissionResultData,mediaProjectionCallback);
        this.mediaProjectionCallback = mediaProjectionCallback;
        this.mediaProjection = mediaProjection;
    }

    private void checkNotDisposed() {
        if (this.isDisposed) {
            throw new RuntimeException("capturer is disposed.");
        }
    }

    public synchronized void initialize(SurfaceTextureHelper surfaceTextureHelper, Context applicationContext, CapturerObserver capturerObserver) {
        this.checkNotDisposed();
        if (capturerObserver == null) {
            throw new RuntimeException("capturerObserver not set.");
        } else {
            this.capturerObserver = capturerObserver;
            if (surfaceTextureHelper == null) {
                throw new RuntimeException("surfaceTextureHelper not set.");
            } else {
                this.surfaceTextureHelper = surfaceTextureHelper;
            }
        }
    }

    @Override
    public synchronized void startCapture(int width, int height, int ignoredFramerate) {
        checkNotDisposed();
        this.width = width;
        this.height = height;
        this.mediaProjection.registerCallback(this.mediaProjectionCallback, this.surfaceTextureHelper.getHandler());
        this.createVirtualDisplay();
        this.capturerObserver.onCapturerStarted(true);
        this.surfaceTextureHelper.startListening(this);
    }
    public synchronized void stopCaptureNew() {
        this.checkNotDisposed();
        ThreadUtils.invokeAtFrontUninterruptibly(this.surfaceTextureHelper.getHandler(), new Runnable() {
            public void run() {
                ScreenRecorder.this.surfaceTextureHelper.stopListening();
                ScreenRecorder.this.capturerObserver.onCapturerStopped();
                if (ScreenRecorder.this.virtualDisplay != null) {
                    ScreenRecorder.this.virtualDisplay.release();
                    ScreenRecorder.this.virtualDisplay = null;
                }
            }
        });
    }

    public synchronized void stopCapture() {
        this.checkNotDisposed();
        ThreadUtils.invokeAtFrontUninterruptibly(this.surfaceTextureHelper.getHandler(), new Runnable() {
            public void run() {
                ScreenRecorder.this.surfaceTextureHelper.stopListening();
                ScreenRecorder.this.capturerObserver.onCapturerStopped();
                if (ScreenRecorder.this.virtualDisplay != null) {
                    ScreenRecorder.this.virtualDisplay.release();
                    ScreenRecorder.this.virtualDisplay = null;
                }

                if (ScreenRecorder.this.mediaProjection != null) {
                    ScreenRecorder.this.mediaProjection.unregisterCallback(ScreenRecorder.this.mediaProjectionCallback);
                    ScreenRecorder.this.mediaProjection.stop();
                    ScreenRecorder.this.mediaProjection = null;
                }
            }
        });
    }

    public synchronized void dispose() {
        this.isDisposed = true;
    }

    public synchronized void changeCaptureFormat(int width, int height, int ignoredFramerate) {
        this.checkNotDisposed();
        this.width = width;
        this.height = height;
        if (this.virtualDisplay != null) {
            ThreadUtils.invokeAtFrontUninterruptibly(this.surfaceTextureHelper.getHandler(), new Runnable() {
                public void run() {
                    ScreenRecorder.this.virtualDisplay.release();
                    ScreenRecorder.this.createVirtualDisplay();
                }
            });
        }
    }

    private void createVirtualDisplay() {
        this.surfaceTextureHelper.setTextureSize(this.width, this.height);
        this.virtualDisplay = this.mediaProjection.createVirtualDisplay("WebRTC_ScreenCapture", this.width, this.height, VIRTUAL_DISPLAY_DPI, 3, new Surface(this.surfaceTextureHelper.getSurfaceTexture()), (VirtualDisplay.Callback)null, (Handler)null);
    }

    public void onFrame(VideoFrame frame) {
        ++this.numCapturedFrames;
        this.capturerObserver.onFrameCaptured(frame);
    }

    public boolean isScreencast() {
        return true;
    }

    public long getNumCapturedFrames() {
        return this.numCapturedFrames;
    }
}
