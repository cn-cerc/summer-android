package cn.cerc.summer.android.parts.music;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by roc on 2017/8/15.
 * 管理录音文件的类
 */

public class RecorderFileUtils {

    private static String rootPath = "Record";
    //原始文件(不能播放)
    private final static String AUDIO_PCM_BASEPATH = "/" + rootPath + "/pcm/";
    //可播放的高质量音频文件
    private final static String AUDIO_WAV_BASEPATH = "/" + rootPath + "/wav/";

    /**
     * 根据文件名获取pcm文件的路径
     *
     * @param fileName 文件名
     */
    public static String getPcmFileAbsolutePath(String fileName) {
        //.pcm文件路径
        String pcmFilePath = "";

        if (TextUtils.isEmpty(fileName)) {
            Log.e("FileUtils", "fileName isEmpty");
            throw new NullPointerException("fileName isEmpty");
        }
        if (!isSdcardExit()) {
            Log.e("FileUtils", "sd card no found");
            throw new IllegalStateException("sd card no found");
        } else {
            //判断文件名是否含有.pcm后缀
            if (!fileName.endsWith(".pcm")) {
                fileName += ".pcm";
            }
            //文件夹路径
            String fileBasePath = Environment.getExternalStorageDirectory().getAbsolutePath() + AUDIO_PCM_BASEPATH;
            File file = new File(fileBasePath);
            Log.e("FileUtils", file.getAbsolutePath());
            //判断是否存在该文件路劲，不存在则创建
            if (!file.exists()) {
                //创建路径
                file.mkdirs();
            }
            pcmFilePath = fileBasePath + fileName;
        }
        return pcmFilePath;
    }

    /**
     * 根据文件名获取wav文件的路径
     *
     * @param fileName 文件名
     */
    public static String getWavFileAbsolutePath(String fileName) {
        //.pcm文件路径
        String wavFilePath = "";

        if (TextUtils.isEmpty(fileName)) {
            Log.e("FileUtils", "fileName isEmpty");
            throw new NullPointerException("fileName isEmpty");
        }
        if (!isSdcardExit()) {
            Log.e("FileUtils", "sd card no found");
            throw new IllegalStateException("sd card no found");
        } else {
            //判断文件名是否含有.wav后缀
            if (!fileName.endsWith(".wav")) {
                fileName += ".wav";
            }
            //文件夹路径
            String fileBasePath = Environment.getExternalStorageDirectory().getAbsolutePath() + AUDIO_WAV_BASEPATH;
            File file = new File(fileBasePath);
            Log.e("FileUtils", file.getAbsolutePath());
            //判断是否存在该文件路劲，不存在则创建
            if (!file.exists()) {
                //创建路径
                file.mkdirs();
            }
            wavFilePath = fileBasePath + fileName;
        }
        return wavFilePath;
    }

    /**
     * 判断是否有外部存储设备sdcard
     *
     * @return true | false
     */
    public static boolean isSdcardExit() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }

    /**
     * 获取全部pcm文件列表
     *
     * @return
     */
    public static List<File> getPcmFiles() {
        List<File> list = new ArrayList<>();
        String fileBasePath = Environment.getExternalStorageDirectory().getAbsolutePath() + AUDIO_PCM_BASEPATH;

        File rootFile = new File(fileBasePath);
        if (!rootFile.exists()) {
        } else {

            File[] files = rootFile.listFiles();
            for (File file : files) {
                list.add(file);
            }

        }
        return list;

    }

    /**
     * 获取全部wav文件列表
     *
     * @return
     */
    public static List<File> getWavFiles() {
        List<File> list = new ArrayList<>();
        String fileBasePath = Environment.getExternalStorageDirectory().getAbsolutePath() + AUDIO_WAV_BASEPATH;

        File rootFile = new File(fileBasePath);
        if (!rootFile.exists()) {
        } else {
            File[] files = rootFile.listFiles();
            for (File file : files) {
                list.add(file);
            }

        }
        return list;
    }
}
