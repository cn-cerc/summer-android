package cn.cerc.summer.android.Utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.huagu.ehealth.R;

import cn.cerc.summer.android.Entity.JSParam;

/**
 * Created by fff on 2016/12/28.
 */

public class SoundUtils extends HardwareJSUtils implements SountPlayerLinter, AudioManager.OnAudioFocusChangeListener, MediaPlayer.OnCompletionListener {

    public JSParam jsp;
    private AudioManager mAm;
    private MediaPlayer player;

    private SoundPlayerStatusLintener spsl;
    private static SoundUtils su;

    /**
     * 获取单例实例，  传递的监听只有第一次传递的有效
     * @param spsl  播放完监听
     * @return  返回当前实例
     */
    public static SoundUtils getInstance(SoundPlayerStatusLintener spsl){
        if (su == null) su = new SoundUtils(spsl);
        return su;
    }

    @Override
    public void setJson(String json) {
        jsp = JSON.parseObject(json,JSParam.class);
    }

    public void setmusic(){
        player = MediaPlayer.create(spsl.getContext(), R.raw.faded);
        player.setLooping(false);
    }


    public SoundUtils(SoundPlayerStatusLintener spsl) {
        this.spsl = spsl;
        player = MediaPlayer.create(spsl.getContext(), R.raw.faded);  // 在res目录下新建raw目录，复制一个test.mp3文件到此目录下。
        player.setLooping(false);
    }

    /**
     * 开始播放
     */
    @Override
    public void startPlayer() {

        mAm = (AudioManager) spsl.getContext().getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        if (mAm.isMusicActive()){
            int result = mAm.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);//语义就是请求声音焦点...,第三个参数：是否长期占有
//            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
            player.start();
        }else player.start();
        player.setOnCompletionListener(this);
    }

    /**
     * 停止播放
     */
    @Override
    public void stopPlayer() {
        player.stop();
    }

    /**
     * 暂停播放
     */
    @Override
    public void pausePlayer() {
        player.pause();
    }

    /**
     * 获取当前是够播放
     * @return   isPlaying
     */
    @Override
    public boolean getPlayerStatus() {
        return player.isPlaying();
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        Log.e("SoundUtils", "focusChange: " + focusChange);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (player != null && player.isPlaying()) player.pause();
        mAm.abandonAudioFocus(this);
        spsl.Completion();
    }

    public interface SoundPlayerStatusLintener{
        void Completion();

        Context getContext();
    }

}

interface SountPlayerLinter{

    void startPlayer();

    void stopPlayer();

    void pausePlayer();

    boolean getPlayerStatus();
}


