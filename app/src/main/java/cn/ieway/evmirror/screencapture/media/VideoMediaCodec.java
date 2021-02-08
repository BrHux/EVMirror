package cn.ieway.evmirror.screencapture.media;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import cn.ieway.evmirror.application.Const;

import static cn.ieway.evmirror.application.MirrorApplication.sMe;

/**
 * Created by zpf on 2018/3/7.
 */

public class VideoMediaCodec extends MediaCodecBase {

    private final static String TAG = "VideoMediaCodec";


    private Surface mSurface;
    private long startTime = 0;
    private int TIMEOUT_USEC = 12000;
    public byte[] configbyte;

    private static String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/test1.h264";
    private BufferedOutputStream outputStream;
    FileOutputStream outStream;

    private MediaCodecCallBack mediaCodecCallBack;

    private void createfile() {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     **/
    public VideoMediaCodec() {
        //createfile();
        prepare();
    }

    public Surface getSurface() {
        return mSurface;
    }

    public void isRun(boolean isR) {
        if (isR == isRun) {
            return;
        }
        this.isRun = isR;
    }

    public boolean isRun(){
        return  this.isRun;
    }


    public void setMediaCodecCallBack(MediaCodecCallBack codecCallBack) {
        this.mediaCodecCallBack = codecCallBack;
    }

    @Override
    public void prepare() {
        try {
            if (mEncoder != null) {
                isRun = false;
                mEncoder.stop();
            }
            Log.d(TAG, "prepare: ============ " + sMe.screenWidth + "  " + sMe.screenHeight);

            if ((sMe.screenWidth & 1) == 1) {
                sMe.screenWidth--;
            }
            if ((sMe.screenHeight & 1) == 1) {
                sMe.screenHeight--;
            }

            MediaFormat format = MediaFormat.createVideoFormat(Const.MIME_TYPE, sMe.screenWidth, sMe.screenHeight);
            format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
            format.setInteger(MediaFormat.KEY_BITRATE_MODE, MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_CBR);
            format.setInteger(MediaFormat.KEY_BIT_RATE, sMe.biteRate);
            format.setInteger(MediaFormat.KEY_FRAME_RATE, Const.VIDEO_FRAMERATE);
            format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, Const.VIDEO_IFRAME_INTER);
            mEncoder = MediaCodec.createEncoderByType(Const.MIME_TYPE);
            mEncoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);

            mSurface = mEncoder.createInputSurface();
            isRun = true;
            mEncoder.start();
        } catch (IOException e) {

        }
    }

    @Override
    public void release() {
        this.isRun = false;
        try {
            mEncoder.stop();
            mEncoder.release();
            mEncoder = null;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取h264数据
     **/
    public void getBuffer() {
        try {
            Log.d(TAG, "getBuffer  isRun: " + isRun);
            while (isRun) {
                if (mEncoder == null) break;
                MediaCodec.BufferInfo mBufferInfo = new MediaCodec.BufferInfo();
                int outputBufferIndex = mEncoder.dequeueOutputBuffer(mBufferInfo, TIMEOUT_USEC);
                while (outputBufferIndex >= 0) {
                    ByteBuffer outputBuffer = mEncoder.getOutputBuffers()[outputBufferIndex];
                    byte[] outData = new byte[mBufferInfo.size];
                    outputBuffer.get(outData);

                    if (mBufferInfo.flags == 2) {
                        if (mediaCodecCallBack != null) {
                            mediaCodecCallBack.onVideoFrameMessage(outData, 1, 2);
                        }

                    } else if (mBufferInfo.flags == 1) {
                        if (mediaCodecCallBack != null) {
                            mediaCodecCallBack.onVideoFrameMessage(outData, 1, 1);
                        }
                    } else {

                        if (mediaCodecCallBack != null) {
                            mediaCodecCallBack.onVideoFrameMessage(outData, 1, 0);
                        }
                    }

                    mEncoder.releaseOutputBuffer(outputBufferIndex, false);
                    outputBufferIndex = mEncoder.dequeueOutputBuffer(mBufferInfo, TIMEOUT_USEC);
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "getBuffer: " + e.getMessage());
        }
    }


    public String writeContent(byte[] array) {
        char[] HEX_CHAR_TABLE = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
        };
        StringBuilder sb = new StringBuilder();
        for (byte b : array) {
            sb.append(HEX_CHAR_TABLE[(b & 0xf0) >> 4]);
            sb.append(HEX_CHAR_TABLE[b & 0x0f]);
        }
        Log.i(TAG, "writeContent: " + sb.toString());
        FileWriter writer = null;
        try {
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            writer = new FileWriter(Environment.getExternalStorageDirectory() + "/codec.txt", true);
            writer.write(sb.toString());
            writer.write("\n");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public void writeBytes(byte[] array) {

        Log.d(TAG, "writeBytes:  -----------------------------   " + array.length);
        Log.d(TAG, "writeBytes:  " + Arrays.toString(array));


        FileOutputStream writer = null;
        try {
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            writer = new FileOutputStream(Environment.getExternalStorageDirectory() + "/codec.h264", true);
//            writer = new FileOutputStream(Environment.getExternalStorageDirectory() + "/codec_265.h265", true);
            writer.write(array);
            writer.write('\n');

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 编码后视频帧数据回调
     */
    public interface MediaCodecCallBack {
        void onVideoFrameMessage(byte[] bytes, int type, int frame);
    }

}
