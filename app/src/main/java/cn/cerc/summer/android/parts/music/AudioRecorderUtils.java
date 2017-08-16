package cn.cerc.summer.android.parts.music;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by roc on 2017/8/14.
 * 管理录音操作的类
 */

public class AudioRecorderUtils {

    // 最大录音时长1000*60*10
    private final int MAX_LENGTH = 1000 * 60 * 60 * 5;
    //音频输入-麦克风
    private final static int AUDIO_INPUT = MediaRecorder.AudioSource.MIC;
    //采用频率
    //44100是目前的标准，但是某些设备仍然支持22050，16000，11025
    //采样频率一般共分为22.05KHz、44.1KHz、48KHz三个等级
    private final static int AUDIO_SAMPLE_RATE = 16000;
    //声道 单声道
    private final static int AUDIO_CHANNEL = AudioFormat.CHANNEL_IN_MONO;
    //编码
    private final static int AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    //缓冲区字节大小
    private int bufferSizeInBytes = 0;
    //文件名
    private String fileName;
    //录音文件
    private List<String> listFileName = new ArrayList<>();

    //录音对象
    private AudioRecord audioRecord;
    //录音状态
    private Status status = Status.STATUS_NO_READY;

    public Status getStatus() {
        return status;
    }

    public String getFileName() {
        return fileName;
    }

    public enum Status {
        //未开始
        STATUS_NO_READY,
        //预备
        STATUS_READY,
        //录音
        STATUS_START,
        //暂停
        STATUS_PAUSE,
        //停止
        STATUS_STOP
    }

    public AudioRecorderUtils() {

    }

    public void createAudio(String fileName, int audioSource, int sampleRateInHz, int channelConfig, int audioFormat) {
        this.fileName = fileName;
        // 获得缓冲区字节大小
        bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, channelConfig);
        audioRecord = new AudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat, bufferSizeInBytes);
    }

    public void createDefaultAudio(String fileName) {
        this.fileName = fileName;
        this.status = Status.STATUS_READY;
        // 获得缓冲区字节大小
        bufferSizeInBytes = AudioRecord.getMinBufferSize(AUDIO_SAMPLE_RATE, AUDIO_CHANNEL, AUDIO_ENCODING);
        audioRecord = new AudioRecord(AUDIO_INPUT, AUDIO_SAMPLE_RATE, AUDIO_CHANNEL, AUDIO_ENCODING, bufferSizeInBytes);
    }

    /**
     * 开始录音
     */
    public void startRecorder() {
        Log.e("AudioRecorder","开始录音");
        if (status == Status.STATUS_NO_READY || TextUtils.isEmpty(fileName)) {
            throw new IllegalStateException("录音尚未初始化,请检查是否禁止了录音权限~");
        }
        if (status == Status.STATUS_START) {
            throw new IllegalStateException("录音尚未初始化,请检查是否禁止了录音权限~");
        }
        Log.e("AudioRecorder", "===startRecord===" + audioRecord.getState());
        //开始录音
        audioRecord.startRecording();

        //通过线程写入文件
        new Thread(new Runnable() {
            @Override
            public void run() {
                writeDataTOFile();
            }
        }).start();
    }

    /**
     * 暂停录音
     */
    public void pauseRecord() {
        Log.e("AudioRecorder","暂停录音");
        if (status != Status.STATUS_START) {
            throw new IllegalStateException("没有在录音");
        } else {
            audioRecord.stop();
            status = Status.STATUS_PAUSE;
        }
    }

    /**
     * 停止录音
     */
    public void stopRecord() {
        Log.e("AudioRecorder","停止录音");
        if (status == Status.STATUS_NO_READY || status == Status.STATUS_READY) {
            throw new IllegalStateException("录音尚未开始");
        } else {
            audioRecord.stop();
            status = Status.STATUS_STOP;
            release();
        }
    }

    /**
     * 释放资源
     */
    public void release() {
        Log.e("AudioRecorder","释放资源");
        //假如有暂停录音
        try {
            if (listFileName.size() > 0) {
                List<String> filePaths = new ArrayList<>();
                for (String fileName : listFileName) {
                    filePaths.add(RecorderFileUtils.getPcmFileAbsolutePath(fileName));
                }
                //清除
                listFileName.clear();
                //将多个pcm文件转化为wav文件
                mergePCMFilesToWAVFile(filePaths);
            } else {
                //这里由于只要录音过filesName.size都会大于0,没录音时fileName为null
                //会报空指针 NullPointerException
                // 将单个pcm文件转化为wav文件
                //Log.e("AudioRecorder", "=====makePCMFileToWAVFile======");
                //makePCMFileToWAVFile();
            }
        } catch (IllegalStateException e) {
            throw new IllegalStateException(e.getMessage());
        }

        if (audioRecord != null) {
            audioRecord.release();
            audioRecord = null;
        }

        status = Status.STATUS_NO_READY;
    }

    /**
     * 将音频信息写入文件
     */
    private void writeDataTOFile() {
        // new一个byte数组用来存一些字节数据，大小为缓冲区大小
        byte[] audiodata = new byte[bufferSizeInBytes];

        FileOutputStream outputStream = null;
        int readsize = 0;
        try {
            String currentFileName = fileName;
            if (status == Status.STATUS_PAUSE) {
                //当前状态为暂停录音的话，将文件名后面加个数字,防止重名文件内容被覆盖
                currentFileName += listFileName.size();
            }
            //添加到录音文件集合
            listFileName.add(currentFileName);
            File file = new File(RecorderFileUtils.getPcmFileAbsolutePath(currentFileName));
            if (file.exists()) {
                file.delete();
            }
            // 建立一个可存取字节的文件
            outputStream = new FileOutputStream(file);
        } catch (IllegalStateException e) {
            Log.e("AudioRecorder", e.getMessage());
            throw new IllegalStateException(e.getMessage());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //将录音状态设置成正在录音状态
        status = Status.STATUS_START;
        //通过死循环一直写入数据
        while (status == Status.STATUS_START) {
            Log.e("AudioRecorder", "写入数据");
            readsize = audioRecord.read(audiodata, 0, bufferSizeInBytes);
            if (AudioRecord.ERROR_INVALID_OPERATION != readsize && outputStream != null) {
                try {
                    outputStream.write(audiodata);
                } catch (IOException e) {
                    Log.e("AudioRecorder", e.getMessage());
                }
            }
        }
        try {
            if (outputStream != null) {
                outputStream.close();// 关闭写入流
            }
        } catch (IOException e) {
            Log.e("AudioRecorder", e.getMessage());
        }
    }

    /**
     * 将pcm合并成wav
     *
     * @param filePaths
     */
    private void mergePCMFilesToWAVFile(final List<String> filePaths) {
        Log.e("AudioRecorder","录音完成，pcm合并成wav");
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (RecorderPcmToWav.mergePCMFilesToWAVFile(filePaths, RecorderFileUtils.getWavFileAbsolutePath(fileName))) {
                    Log.e("AudioRecorder", "合并完成");
                    //操作成功

                    Log.e(getClass().getSimpleName(), "开始上传");
                    String path = RecorderFileUtils.getWavFileAbsolutePath(getFileName());
                    File file = new File(path);
                    try {
                        FileInputStream fileInputStream = new FileInputStream(file);
                        new ArrivateUpload(FrmCaptureMusic.url, fileInputStream, getFileName()).start();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    //操作失败
                    Log.e("AudioRecorder", "mergePCMFilesToWAVFile fail");
                    throw new IllegalStateException("mergePCMFilesToWAVFile fail");
                }
            }
        }).start();
    }

    /**
     * 将单个pcm文件转化为wav文件
     */
    private void makePCMFileToWAVFile() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (RecorderPcmToWav.makePCMFileToWAVFile(RecorderFileUtils.getPcmFileAbsolutePath(fileName), RecorderFileUtils.getWavFileAbsolutePath(fileName), true)) {
                    //操作成功
                } else {
                    //操作失败
                    Log.e("AudioRecorder", "makePCMFileToWAVFile fail");
                    throw new IllegalStateException("makePCMFileToWAVFile fail");
                }
                fileName = null;
            }
        }).start();
    }
}
