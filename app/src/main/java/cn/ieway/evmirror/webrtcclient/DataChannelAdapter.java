package cn.ieway.evmirror.webrtcclient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.webrtc.DataChannel;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Arrays;

import cn.ieway.evmirror.webrtcclient.entity.DataChannelBean;

public class DataChannelAdapter implements DataChannel.Observer {
    public static final String SEND_REQ = "send_req";
    public static final String RECEIVE_RESP = "receive_resp";
    public static final String BEGIN = "begin";
    public static final String COMPLETE = "complete";
    public static final String CONTINUE = "continue";
    public static final String REPEAT = "repeat";

    private RtcListener listener;
    private static Gson gson;
    DataChannelBean dataChannelBean;
    DataChannel channel;

    private String tempPath;
    private int progress = 0;
    private long fileSize = 0;//文件总大小
    private long downFileSize = 0;//已经下载的文件的大小
    private RandomAccessFile localFile;
    private InputStream inputStream;
    private DCLisenter dcLisenter;
    private long sysTime = -1;

    public void setLisenter(DCLisenter lisenter) {
        this.dcLisenter = lisenter;
    }

    public DataChannelAdapter(RtcListener rtcListener, DataChannel dataChannel) {
        this.gson = new GsonBuilder().create();
        this.listener = rtcListener;
        this.channel = dataChannel;
    }

    @Override
    public void onBufferedAmountChange(long l) {
    }

    @Override
    public void onStateChange() {
    }

    @Override
    public void onMessage(DataChannel.Buffer buffer) {
        ByteBuffer byteBuffer = buffer.data;
        final byte[] bytes = new byte[byteBuffer.capacity()];
        byteBuffer.get(bytes);
        byte[] typeBt = new byte[5];
        System.arraycopy(bytes, 0, typeBt, 0, 5);
        String type = new String(typeBt);
        if (type.contains("ctrl")) {
            int Pos = 24;
            int i = Pos;
            while (!String.valueOf(bytes[i]).equals("0")) {
                i++;
            }
            byte[] data = new byte[i - Pos];
            System.arraycopy(bytes, Pos, data, 0, data.length); //拷贝

            receivingCtrlProcessing(new String(data));

        } else if (type.contains("data")) {
            //数据包长度字符串
            int pos = 8;
            int i = pos;
            while (!String.valueOf(bytes[i]).equals("0")) {
                i++;
            }
            byte[] dataLength = new byte[i - pos];
            System.arraycopy(bytes, pos, dataLength, 0, dataLength.length);
            //MD5
            pos = 24;
            i = pos;
            while (!String.valueOf(bytes[i]).equals("0")) {
                i++;
            }
            byte[] md5Byte = new byte[i - pos];
            System.arraycopy(bytes, pos, md5Byte, 0, md5Byte.length);
            byte[] data = new byte[Integer.parseInt(new String(dataLength))];

            System.arraycopy(bytes, 1024, data, 0, data.length);
            receivingDataProcessing(data);

        }
    }


    private void receivingDataProcessing(byte[] data) {
        File file = new File(tempPath);
        long tempSize = 0;
        try {
            if (file.exists()) {
                downFileSize = file.length();
                fileSize = dataChannelBean.getLength();
                tempSize = downFileSize;
                localFile = new RandomAccessFile(tempPath, "rwd");
                localFile.seek(downFileSize);
            } else {
                localFile = new RandomAccessFile(tempPath, "rwd");
                downFileSize = 0;
                fileSize = dataChannelBean.getLength();
            }
            inputStream = new ByteArrayInputStream(data);
            byte[] buffer = new byte[1024 * 16];
            int length = -1;
            while ((length = inputStream.read(buffer)) != -1) {
                localFile.write(buffer, 0, length);
                downFileSize += length;
                int nowProgress = (int) ((100 * downFileSize) / fileSize);
                long currentTime = System.currentTimeMillis();
                if (dcLisenter != null && ((currentTime - sysTime) > 500) && (nowProgress > progress)) {//增加时间间隔判断，降低通信频率
                    progress = nowProgress;
                    sysTime = currentTime;
                    dcLisenter.onProgress(dataChannelBean, downFileSize);
                }
            }

            if (dataChannelBean != null) {
                sendPackage(dataChannelBean.getName(), dataChannelBean.getLength());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void receivingCtrlProcessing(String data) {
        dataChannelBean = gson.fromJson(data, DataChannelBean.class);
        switch (dataChannelBean.getData()) {
            case BEGIN: {
//                agreeTransferFile(dataChannelBean.getName(), dataChannelBean.getLength());
                break;
            }
            case COMPLETE: {
                sendComplete(dataChannelBean.getName(), dataChannelBean.getLength());
//                copyRenameFile2(tempPath, FileHelper.getDowloadFilePath() + File.separator + dataChannelBean.getName().replace(FileHelper.POSTFIX, ""));
                break;
            }
        }
    }

    private int copyTime = 0;
    /**
     * 拷贝文件  file.renameTo相对于FileInputStream方式更节省时间
     *
     * @param tempPath
     * @param newpath
     */
    private void copyRenameFile2(String tempPath, String newpath) {
        newpath = isDuplicatName(newpath);
        File file = new File(tempPath);
        if (file.exists()) {
            boolean b = file.renameTo(new File(newpath));
            if (!b && copyTime < 5) {
                copyTime++;
                copyRenameFile2(tempPath, newpath);
            } else if (b) {
                if (dcLisenter != null) {
                    dcLisenter.COMPLETED(tempPath, "拷贝完成");
                }
            }
            copyTime = 0;
        }
    }

    private String isDuplicatName(String path){
        File file = new File(path);
        if (file.exists()) {
            StringBuilder builder = new StringBuilder(path);
            builder.insert(builder.lastIndexOf("."),"(1)");
            path = builder.toString();
          return isDuplicatName(path);
        }
        return path;
    }

//    /**
//     * 拷贝文件
//     *
//     * @param oldPath
//     * @param newPath
//     */
//    private void copyRenameFile(String oldPath, String newPath) {
//        boolean copy = FileHelper.copyFile(oldPath, newPath);
//
//        if (!copy && copyTime < 5) {
//            copyTime++;
//            copyRenameFile(oldPath, newPath);
//        } else if (copy) {
//            File file = new File(tempPath);
//            file.delete();
//            if (dcLisenter != null) {
//                dcLisenter.COMPLETED(oldPath, "拷贝完成");
//            }
//        }
//        copyTime = 0;
//    }

  /*  private void agreeTransferFile(String name, long length) {
        DataChannelBean bean = new DataChannelBean(RECEIVE_RESP, BEGIN, name, length);
        if (listener != null) {
            listener.onMsgAskDialog(BEGIN, name, length);
        }
        tempPath = FileHelper.getTempDirPath();

        if (tempPath.isEmpty()) {
            return;
        }
        File file = new File(tempPath);
        if (!file.exists()) {
            file.mkdirs();
        }

        tempPath = tempPath + File.separator + name;
        File temp = new File(tempPath);
        if (temp.exists()) {
            temp.delete();
        }

        bean.setPath(tempPath);
        if (dcLisenter != null) {
            dcLisenter.onStart(bean);
        }
        sendDataChannel(bean, "ctrl");
    }*/

    private void sendPackage(String name, long length) {
        DataChannelBean bean = new DataChannelBean(RECEIVE_RESP, CONTINUE, name, length);
        sendDataChannel(bean, "ctrl");
    }

    private void resendPackage(String name, long length) {
        DataChannelBean bean = new DataChannelBean(RECEIVE_RESP, CONTINUE, name, length);
        sendDataChannel(bean, "ctrl");
    }

    private void sendComplete(String name, long length) {
        DataChannelBean bean = new DataChannelBean(RECEIVE_RESP, COMPLETE, name, length);
        sendDataChannel(bean, "ctrl");
        if (dcLisenter != null) {
            dcLisenter.onSuccess(bean, "完成传输正在拷贝文件");
        }
    }

    /**
     * 发送消息
     *
     * @param bean
     * @param type
     */
    private void sendDataChannel(DataChannelBean bean, String type) {
        if (gson != null) {
            String message = gson.toJson(bean) + "0";
            byte[] msgByte = message.getBytes();
            byte[] headByte = getHeadByte(type);
            byte[] arry = concat(headByte, msgByte);
            DataChannel.Buffer buffer = new DataChannel.Buffer(ByteBuffer.wrap(arry), false);
            this.channel.send(buffer);
        } else {
            gson = new GsonBuilder().create();
            if (gson != null) {
                sendDataChannel(bean, type);
            }
        }
    }

    /**
     * 设置头部[0,24]字节控制信息
     *
     * @param type
     * @return
     */
    private byte[] getHeadByte(String type) {
        byte[] bytes = type.getBytes();
        byte[] headByte = new byte[24];
        System.arraycopy(bytes, 0, headByte, 0, bytes.length);
        return headByte;
    }

    /**
     * 合并两个byte[]
     *
     * @param first
     * @param second
     * @param <T>
     * @return
     */
    public static <T> byte[] concat(byte[] first, byte[] second) {
        byte[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    public interface DCLisenter {
        /**
         * (开始传输文件)
         */
        void onStart(DataChannelBean bean);

        /**
         * (文件传输进度情况)
         *
         * @param bean 传输任务对象
         */
        void onProgress(DataChannelBean bean, long currentSize);

        /**
         * (文件传输失败)
         *
         * @param bean 传输任务对象
         */
        void onError(DataChannelBean bean, String msg);

        /**
         * (文件传输成功)
         *
         * @param bean 传输任务对象
         */
        void onSuccess(DataChannelBean bean, String msg);

        /**
         * (文件传输成功)
         *
         * @param newPath 新的路径
         */
        void COMPLETED(String newPath, String msg);
    }

}
