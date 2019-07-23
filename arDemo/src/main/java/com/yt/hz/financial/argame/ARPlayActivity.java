package com.yt.hz.financial.argame;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easy.occlient.LoaderEZPLisener;
import com.easy.occlient.OCARAsset;
import com.easy.occlient.OCARBinding;
import com.easy.occlient.OCClient;
import com.easy.occlient.OCUtil;
import com.easy.occlient.net.AsyncCallback;
import com.google.gson.Gson;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.LexiconListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.iflytek.cloud.util.ContactManager;
import com.iflytek.cloud.util.ResourceUtil;
import com.yt.hz.financial.argame.bean.AnswerBean;
import com.yt.hz.financial.argame.easyar.MessageID;
import com.yt.hz.financial.argame.easyar.SamplePlayerViewWrapper;
import com.yt.hz.financial.argame.permission.PermissionDenied;
import com.yt.hz.financial.argame.permission.PermissionHelper;
import com.yt.hz.financial.argame.permission.PermissionPermanentDenied;
import com.yt.hz.financial.argame.permission.PermissionSucceed;
import com.yt.hz.financial.argame.util.FucUtil;
import com.yt.hz.financial.argame.util.IatSettings;
import com.yt.hz.financial.argame.util.JsonParser;
import com.yt.hz.financial.argame.util.TtsSettings;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;

import cn.easyar.EasyARDictionary;
import cn.easyar.Message;
import cn.easyar.MessageClient;
import cn.easyar.PlayerView;
import cn.easyar.player.OnReceivedCallback;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class ARPlayActivity extends Activity implements View.OnClickListener, SamplePlayerViewWrapper.RecorderLisener {
    private final String TAG = "LoaderPreloadIDActivity";
    private final String SERVER_ADDRESS = "https://aroc-cn1.easyar.com/";
    private final String OCKEY = "0560a9dab192c15b15d7921c5091dc55";
    private final String OCSCRET = "3314f279e3b94acefeb904794aadbc2937c11cb8714572f40ee5c6ebf9f52421";
    private String mArID ="a212e23f-8193-4482-8e71-2d7d0309bb64";
    private SamplePlayerViewWrapper mPlayerView = null;
    MessageClient mTheMessageClient = null;
    private OCClient mOcClient;
    private final HashMap<String, OCARBinding> mTheARBindingsDict = new HashMap<>();
    private final int PERMISSION_CODE = 1;
    private Button mButtonRecorder;
    private ImageView mIvSnapShot;

    private Button btnSearch;
    private RelativeLayout rlLocation;
    private TextView tvLoactionName,tvLocationDetail;

    // 语音听写对象
    private SpeechRecognizer mIat;
    // 语音听写UI
    private RecognizerDialog mIatDialog;
    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();

    private EditText mResultText;
    private EditText showContacts;
    private Toast mToast;
    private SharedPreferences mSharedPreferences;
    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_CLOUD;

    private boolean mTranslateEnable = false;
    private String resultType = "json";

    private boolean cyclic = false;//音频流识别是否循环调用

    private StringBuffer buffer = new StringBuffer();

    Handler han = new Handler(){

        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x001) {
                executeStream();
            }
        }
    };

    private RelativeLayout rlPlayAppear,rlPlayDisappear,rlPlayVoice,rlPlayVideo;
    private RelativeLayout rlPlay,rlRecord;
    private ImageView ivTishi,ivShuohua;
    private CircleProgressBar cpbProgressbar;
    private boolean isRecording = false;
    private Handler handler = new Handler();
    private TextView tvAnswerWord;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar_play);
        Intent intent = getIntent();
        //mArID = intent.getStringExtra("id");
        ViewGroup previewGroup = (ViewGroup) findViewById(R.id.preview);

        mPlayerView = new SamplePlayerViewWrapper(this);
        previewGroup.addView(
                mPlayerView.getPlayerView(),
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
        requestPermission();

        ivShuohua = findViewById(R.id.iv_shuohua);
        rlPlay = findViewById(R.id.rl_play);
        rlRecord = findViewById(R.id.rl_record);
        rlRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRecording) {
                    isRecording = true;
                    startRecordTime();
                    mPlayerView.optionRecorde();
                    //showTip("开始录屏");
                }else {
                    stopRecord();
                }
            }
        });
        findViewById(R.id.iv_fanhui).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rlRecord.getVisibility()==View.VISIBLE){
                    stopRecord();
                    return;
                }
                finish();
            }
        });
        ivTishi = findViewById(R.id.iv_tishi);
        cpbProgressbar = findViewById(R.id.cpb_recording);
        tvAnswerWord = findViewById(R.id.tv_answer_word);

        rlPlayAppear = findViewById(R.id.rl_play_appear);
        rlPlayDisappear = findViewById(R.id.rl_play_disappear);
        rlPlayVoice = findViewById(R.id.rl_play_voice);
        rlPlayVideo = findViewById(R.id.rl_play_video);
        rlPlayVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlPlay.setVisibility(View.GONE);
                rlRecord.setVisibility(View.VISIBLE);
            }
        });
        rlPlayAppear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlPlayAppear.setVisibility(View.GONE);
                rlPlayDisappear.setVisibility(View.VISIBLE);
                EasyARDictionary theBody = new EasyARDictionary();
                theBody.setString("active","y");
                mTheMessageClient.send(new Message(MessageID.active.getId(), theBody));
            }
        });

        rlPlayDisappear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlPlayDisappear.setVisibility(View.GONE);
                rlPlayAppear.setVisibility(View.VISIBLE)
                ;EasyARDictionary theBody = new EasyARDictionary();
                theBody.setString("active","n");
                mTheMessageClient.send(new Message(MessageID.active.getId(), theBody));
            }
        });
        rlPlayVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivShuohua.setVisibility(View.VISIBLE);
                //识别
                buffer.setLength(0);
                mResultText.setText(null);// 清空显示内容
                mIatResults.clear();
                // 设置参数
                setParam();
                boolean isShowDialog = mSharedPreferences.getBoolean(
                        getString(R.string.pref_key_iat_show), true);
                if (false) {
                    // 显示听写对话框
                    mIatDialog.setListener(mRecognizerDialogListener);
                    mIatDialog.show();
                    showTip(getString(R.string.text_begin));
                } else {
                    // 不显示听写对话框
                    ret = mIat.startListening(mRecognizerListener);
                    if (ret != ErrorCode.SUCCESS) {
                        showTip("听写失败,错误码：" + ret+",请点击网址https://www.xfyun.cn/document/error-code查询解决方案");
                    } else {
                        showTip(getString(R.string.text_begin));
                    }
                }

            }
        });
        // 初始化识别无UI识别对象
        // 使用SpeechRecognizer对象，可根据回调消息自定义界面；
        mIat = SpeechRecognizer.createRecognizer(ARPlayActivity.this, mInitListener);

        mSharedPreferences = getSharedPreferences(IatSettings.PREFER_NAME,
                Activity.MODE_PRIVATE);

        mResultText = new EditText(this);

        texts = getResources().getString(R.string.text_tts_source);
        // 初始化合成对象
        mTts = SpeechSynthesizer.createSynthesizer(ARPlayActivity.this, mTtsInitListener);

        // 云端发音人名称列表
        mCloudVoicersEntries = getResources().getStringArray(R.array.voicer_cloud_entries);
        mCloudVoicersValue = getResources().getStringArray(R.array.voicer_cloud_values);

        mSharedPreferences = getSharedPreferences(TtsSettings.PREFER_NAME, MODE_PRIVATE);
        mToast = Toast.makeText(this,"",Toast.LENGTH_SHORT);
    }

    private void stopRecord(){
        time = 0;
        cpbProgressbar.setProgress(0);
        isRecording = false;
        mPlayerView.optionRecorde();
        rlRecord.setVisibility(View.GONE);
        ivTishi.setVisibility(View.VISIBLE);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                rlPlay.setVisibility(View.VISIBLE);
                ivTishi.setVisibility(View.GONE);
            }
        },1000);
    }

    private int time = 0;
    private void startRecordTime(){
        cpbProgressbar.setProgress(time);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                time++;
                if (time>30){
                    stopRecord();
                    return;
                }
                if (isRecording)
                startRecordTime();
            }
        },1000);
    }
    /**
     * 合成回调监听。
     */
    private SynthesizerListener mTtsListener = new SynthesizerListener() {

        @Override
        public void onSpeakBegin() {
            showTip("开始播放");
        }

        @Override
        public void onSpeakPaused() {
            showTip("暂停播放");
        }

        @Override
        public void onSpeakResumed() {
            showTip("继续播放");
        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos,
                                     String info) {
            // 合成进度
            mPercentForBuffering = percent;
            showTip(String.format(getString(R.string.tts_toast_format),
                    mPercentForBuffering, mPercentForPlaying));
        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            // 播放进度
            mPercentForPlaying = percent;
            showTip(String.format(getString(R.string.tts_toast_format),
                    mPercentForBuffering, mPercentForPlaying));

            SpannableStringBuilder style=new SpannableStringBuilder(texts);
            Log.e(TAG,"beginPos = "+beginPos +"  endPos = "+endPos);
            style.setSpan(new BackgroundColorSpan(Color.RED),beginPos,endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            //((EditText) findViewById(R.id.tts_text)).setText(style);
        }

        @Override
        public void onCompleted(SpeechError error) {
            if (error == null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvAnswerWord.setVisibility(View.GONE);
                    }
                });
                showTip("播放完成");
            } else if (error != null) {
                showTip(error.getPlainDescription(true));
            }
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}

            //当设置SpeechConstant.TTS_DATA_NOTIFY为1时，抛出buf数据
			/*if (SpeechEvent.EVENT_TTS_BUFFER == eventType) {
						byte[] buf = obj.getByteArray(SpeechEvent.KEY_EVENT_TTS_BUFFER);
						Log.e("MscSpeechLog", "buf is =" + buf);
					}*/

        }
    };

    /**
     * 初始化监听。
     */
    private InitListener mTtsInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            Log.d(TAG, "InitListener init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                showTip("初始化失败,错误码："+code+",请点击网址https://www.xfyun.cn/document/error-code查询解决方案");
            } else {
                // 初始化成功，之后可以调用startSpeaking方法
                // 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
                // 正确的做法是将onCreate中的startSpeaking调用移至这里
            }
        }
    };
    // 语音合成对象
    private SpeechSynthesizer mTts;

    // 默认发音人
    private String voicer = "xiaoyan";

    private String[] mCloudVoicersEntries;
    private String[] mCloudVoicersValue ;
    String texts = "";

    // 缓冲进度
    private int mPercentForBuffering = 0;
    // 播放进度
    private int mPercentForPlaying = 0;
    /**
     * 参数设置
     * @return
     */
    private void setParam2(){
        // 清空参数
        mTts.setParameter(SpeechConstant.PARAMS, null);
        // 根据合成引擎设置相应参数
        if(mEngineType.equals(SpeechConstant.TYPE_CLOUD)) {
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
            //支持实时音频返回，仅在synthesizeToUri条件下支持
            //mTts.setParameter(SpeechConstant.TTS_DATA_NOTIFY, "1");
            // 设置在线合成发音人
            mTts.setParameter(SpeechConstant.VOICE_NAME, voicer);
            //设置合成语速
            mTts.setParameter(SpeechConstant.SPEED, mSharedPreferences.getString("speed_preference", "50"));
            //设置合成音调
            mTts.setParameter(SpeechConstant.PITCH, mSharedPreferences.getString("pitch_preference", "50"));
            //设置合成音量
            mTts.setParameter(SpeechConstant.VOLUME, mSharedPreferences.getString("volume_preference", "50"));
        }else {
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
            mTts.setParameter(SpeechConstant.VOICE_NAME, "");

        }
        //设置播放器音频流类型
        mTts.setParameter(SpeechConstant.STREAM_TYPE, mSharedPreferences.getString("stream_preference", "3"));
        // 设置播放合成音频打断音乐播放，默认为true
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "pcm");
        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/tts.pcm");
    }


    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                showTip("初始化失败，错误码：" + code+",请点击网址https://www.xfyun.cn/document/error-code查询解决方案");
            }
        }
    };

    /**
     * 上传联系人/词表监听器。
     */
    private LexiconListener mLexiconListener = new LexiconListener() {

        @Override
        public void onLexiconUpdated(String lexiconId, SpeechError error) {
            if (error != null) {
                showTip(error.toString());
            } else {
                showTip(getString(R.string.text_upload_success));
            }
        }
    };

    /**
     * 听写监听器。
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            showTip("开始说话");
        }

        @Override
        public void onError(SpeechError error) {
            // Tips：
            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
            if(mTranslateEnable && error.getErrorCode() == 14002) {
                showTip( error.getPlainDescription(true)+"\n请确认是否已开通翻译功能" );
            } else {
                showTip(error.getPlainDescription(true));
            }
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            showTip("结束说话");
        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            Log.d(TAG, results.getResultString());
            if (resultType.equals("json")) {
                if( mTranslateEnable ){
                    printTransResult( results );
                }else{
                    printResult(results);
                }
            }else if(resultType.equals("plain")) {
                buffer.append(results.getResultString());
                mResultText.setText(buffer.toString());
                mResultText.setSelection(mResultText.length());
                word = buffer.toString();
                requestMsg();
                //showTip(buffer.toString());
            }

            if (isLast & cyclic) {
                // TODO 最后的结果
                android.os.Message message = android.os.Message.obtain();
                message.what = 0x001;
                han.sendMessageDelayed(message,100);
            }
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            //showTip("当前正在说话，音量大小：" + volume);
            Log.d(TAG, "返回音频数据："+data.length);
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }
    };

    private String word="";
    private String answerword = "";
    private void requestMsg(){
        new Thread(new Runnable() {
            @Override
            public void run() {

                OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象。
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");//数据类型为json格式，
                String jsonStr = "{\n" +
                        "  \"data\":{\n" +
                        "             \"content\": [\n" +
                        "                {\n" +
                        "                  \"data\": \""+word+"\"\n" +
                        "                }\n" +
                        "              ],\n" +
                        "             \"userInfo\": {\n" +
                        "                \"uniqueId\": \"uniqueId\"\n" +
                        "              }\n" +
                        "          },\n" +
                        "  \"key\":\"d8336a2db103406d8163dee0bf410f74\",\n" +
                        "  \"timestamp\":\""+System.currentTimeMillis()+"\"\n" +
                        "}";//json数据.
                RequestBody body = RequestBody.create(JSON, jsonStr);
                Request request = new Request.Builder()
                        .url("http://api.turingos.cn/turingos/api/v2")
                        .post(body)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        //Log.e("yuan response",response.body().string());
                        answerword = "";
                        Gson gson = new Gson();
                        AnswerBean answerBean =  gson.fromJson(response.body().string(), AnswerBean.class);
                        if (answerBean!=null&&answerBean.getResults()!=null&&answerBean.getResults().size()>0){
                            for (AnswerBean.ResultsBean resultsBean:answerBean.getResults()){
                                answerword = answerword+resultsBean.getValues().getText();
                                speak();
                            }
                        }
                    }
                });

            }
        }).start();
    }

    private void speak(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ivShuohua.setVisibility(View.GONE);
                tvAnswerWord.setText(answerword);
                tvAnswerWord.setVisibility(View.VISIBLE);
            }
        });
        //播放
        setParam2();
        int code = mTts.startSpeaking(answerword, mTtsListener);

        /*String path = Environment.getExternalStorageDirectory()+"/tts.pcm";
        int code = mTts.synthesizeToUri(text, path, mTtsListener);*/

        if (code != ErrorCode.SUCCESS) {
            showTip("语音合成失败,错误码: " + code+",请点击网址https://www.xfyun.cn/document/error-code查询解决方案");
        }
    }
    private void printResult(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());

        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);

        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }

        mResultText.setText(resultBuffer.toString());
        //showTip(resultBuffer.toString());
        word = resultBuffer.toString();
        requestMsg();
        mResultText.setSelection(mResultText.length());
    }

    /**
     * 听写UI监听器
     */
    private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
        public void onResult(RecognizerResult results, boolean isLast) {
            if( mTranslateEnable ){
                printTransResult( results );
            }else{
                printResult(results);
            }

        }

        /**
         * 识别回调错误.
         */
        public void onError(SpeechError error) {
            if(mTranslateEnable && error.getErrorCode() == 14002) {
                showTip( error.getPlainDescription(true)+"\n请确认是否已开通翻译功能" );
            } else {
                showTip(error.getPlainDescription(true));
            }
        }

    };

    /**
     * 获取联系人监听器。
     */
    private ContactManager.ContactListener mContactListener = new ContactManager.ContactListener() {

        @Override
        public void onContactQueryFinish(final String contactInfos, boolean changeFlag) {
            // 注：实际应用中除第一次上传之外，之后应该通过changeFlag判断是否需要上传，否则会造成不必要的流量.
            // 每当联系人发生变化，该接口都将会被回调，可通过ContactManager.destroy()销毁对象，解除回调。
            // if(changeFlag) {
            // 指定引擎类型
            runOnUiThread(new Runnable() {
                public void run() {
                    //showContacts.setText(contactInfos);
                }
            });

            mIat.setParameter(SpeechConstant.ENGINE_TYPE,SpeechConstant.TYPE_CLOUD);
            mIat.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
            ret = mIat.updateLexicon("contact", contactInfos, mLexiconListener);
            if (ret != ErrorCode.SUCCESS) {
                showTip("上传联系人失败：" + ret);
            }
        }
    };

    private void showTip(final String str) {
        mToast.setText(str);
        mToast.show();
    }

    /**
     * 参数设置
     *
     * @return
     */
    public void setParam() {
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);

        // 设置听写引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, resultType);

        this.mTranslateEnable = mSharedPreferences.getBoolean( this.getString(R.string.pref_key_translate), false );
        if( mTranslateEnable ){
            Log.i( TAG, "translate enable" );
            mIat.setParameter( SpeechConstant.ASR_SCH, "1" );
            mIat.setParameter( SpeechConstant.ADD_CAP, "translate" );
            mIat.setParameter( SpeechConstant.TRS_SRC, "its" );
        }

        String lag = mSharedPreferences.getString("iat_language_preference",
                "mandarin");
        if (lag.equals("en_us")) {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
            mIat.setParameter(SpeechConstant.ACCENT, null);

            if( mTranslateEnable ){
                mIat.setParameter( SpeechConstant.ORI_LANG, "en" );
                mIat.setParameter( SpeechConstant.TRANS_LANG, "cn" );
            }
        } else {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            // 设置语言区域
            mIat.setParameter(SpeechConstant.ACCENT, lag);

            if( mTranslateEnable ){
                mIat.setParameter( SpeechConstant.ORI_LANG, "cn" );
                mIat.setParameter( SpeechConstant.TRANS_LANG, "en" );
            }
        }
        //此处用于设置dialog中不显示错误码信息
        //mIat.setParameter("view_tips_plain","false");

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, mSharedPreferences.getString("iat_vadbos_preference", "4000"));

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, mSharedPreferences.getString("iat_vadeos_preference", "1000"));

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, mSharedPreferences.getString("iat_punc_preference", "1"));

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        mIat.setParameter(SpeechConstant.AUDIO_FORMAT,"wav");
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/iat.wav");
    }

    private void printTransResult (RecognizerResult results) {
        String trans  = JsonParser.parseTransResult(results.getResultString(),"dst");
        String oris = JsonParser.parseTransResult(results.getResultString(),"src");

        if( TextUtils.isEmpty(trans)||TextUtils.isEmpty(oris) ){
            showTip( "解析结果失败，请确认是否已开通翻译功能。" );
        }else{
            mResultText.setText( "原始语言:\n"+oris+"\n目标语言:\n"+trans );
        }

    }

    int ret = 0; // 函数调用返回值
    //执行音频流识别操作
    private void executeStream() {
        buffer.setLength(0);
        mResultText.setText(null);// 清空显示内容
        mIatResults.clear();
        // 设置参数
        setParam();
        // 设置音频来源为外部文件
        mIat.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");
        // 也可以像以下这样直接设置音频文件路径识别（要求设置文件在sdcard上的全路径）：
        // mIat.setParameter(SpeechConstant.AUDIO_SOURCE, "-2");
        //mIat.setParameter(SpeechConstant.ASR_SOURCE_PATH, "sdcard/XXX/XXX.pcm");
        ret = mIat.startListening(mRecognizerListener);
        if (ret != ErrorCode.SUCCESS) {
            showTip("识别失败,错误码：" + ret+",请点击网址https://www.xfyun.cn/document/error-code查询解决方案");
        } else {
            byte[] audioData = FucUtil.readAudioFile(ARPlayActivity.this, "iattest.wav");

            if (null != audioData) {
                showTip(getString(R.string.text_begin_recognizer));
                // 一次（也可以分多次）写入音频文件数据，数据格式必须是采样率为8KHz或16KHz（本地识别只支持16K采样率，云端都支持），
                // 位长16bit，单声道的wav或者pcm
                // 写入8KHz采样的音频时，必须先调用setParameter(SpeechConstant.SAMPLE_RATE, "8000")设置正确的采样率
                // 注：当音频过长，静音部分时长超过VAD_EOS将导致静音后面部分不能识别。
                // 音频切分方法：FucUtil.splitBuffer(byte[] buffer,int length,int spsize);
                mIat.writeAudio(audioData, 0, audioData.length);

                mIat.stopListening();
            } else {
                mIat.cancel();
                showTip("读取音频流失败");
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (mPlayerView != null) {
            mPlayerView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mPlayerView != null) {
            mPlayerView.onPause();
        }
    }
    @Override
    public void finish() {
        super.finish();

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayerView!=null){
            mPlayerView.dispose();
        }
        if( null != mIat ){
            // 退出时释放连接
            mIat.cancel();
            mIat.destroy();
        }
        if( null != mTts ){
            mTts.stopSpeaking();
            // 退出时释放连接
            mTts.destroy();
        }
    }
    private void conectionOCClient() {
        mOcClient = new OCClient();
        mOcClient.setServerAddress(SERVER_ADDRESS);
        mOcClient.setServerAccessKey(OCKEY, OCSCRET);

        loadARID();
    }
    public void loadARID(){

        mOcClient.loadARAsset(mArID, new AsyncCallback<OCARAsset>() {
            @Override
            public void onSuccess(OCARAsset asset) {
                System.out.println("yanjin---loadARAsset---"+asset.toString());
                mTheMessageClient = MessageClient.create(mPlayerView.getPlayerView(), "Native", new ARPlayActivity.OnReceivedCallbackImp());
                mTheMessageClient.setDest("TS");
                final String assetLocalAbsolutePath = asset.getLocalAbsolutePath();
                        mPlayerView.loadPackage(assetLocalAbsolutePath, new PlayerView.OnLoadPackageFinish() {
                            @Override
                            public void onFinish() {

                            }
                        });

            }

            @Override
            public void onFail(Throwable t) {

            }

            @Override
            public void onProgress(String taskName, final float progress) {
                if (null != taskName && taskName.equals("download")) {
                    //显示进度圈
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //mArScanView.setProgress(progress);
                        }
                    });
                }
            }
        });
    }
    @Override
    public void onClick(View view) {
    }


    @Override
    public void recorderSetText(String text) {
        //mButtonRecorder.setText(text);
        showTip(text);
    }

    @Override
    public void recorderSetEnabled(boolean flag) {
        //mButtonRecorder.setEnabled(flag);
        showTip(""+flag);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private class OnReceivedCallbackImp implements OnReceivedCallback {
        @Override
        public void onReceived(String s, final Message message) {
            if (MessageID.MSG_ID_FOUNDTARGET.getId() == message.getId()) {

            } else if (MessageID.LoadFinish.getId() == message.getId()) {
                log("LoadFinish");

            }else if (MessageID.TargetFound.getId() == message.getId()) {
                log("TargetFound");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    }
                });

            }else {
                Log.i(TAG, String.format("消息: error message: %d", message.getId()));
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ARPlayActivity.this,message.getId()+"",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void requestPermission() {
        PermissionHelper.with(this).requestCode(PERMISSION_CODE).requestPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA
        ).request();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionHelper.requestPermissionsResult(this, requestCode, permissions, grantResults);
    }
    public void log(String msg){
        Log.e("yuan",msg);
    }
    @PermissionDenied(requestCode = PERMISSION_CODE)
    private void onPermissionDenied() {
        Toast.makeText(this, "您拒绝了开启权限,可去设置界面打开", Toast.LENGTH_SHORT).show();
    }


    @PermissionPermanentDenied(requestCode = PERMISSION_CODE)
    private void onPermissionPermanentDenied() {
        Toast.makeText(this, "您选择了永久拒绝,可在设置界面重新打开", Toast.LENGTH_SHORT).show();
    }

    @PermissionSucceed(requestCode = PERMISSION_CODE)
    private void onPermissionSuccess() {
        try {
            startAR();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void creditBillJson(String json){
        EasyARDictionary theBody = new EasyARDictionary();
        theBody.setString("json",json);
        mTheMessageClient.send(new Message(MessageID.creditBillJson.getId(), theBody));
    }
    private void startAR() throws IOException {


        OCUtil.getInstent().loaderEZP(OCUtil.getInstent().EZP_NAME,getAssets().open(OCUtil.getInstent().EZP_NAME),new LoaderEZPLisener() {
            @Override
            public void onSucess(String path) {
                loadAr(path);
                mPlayerView.getReadyRecorder();
            }

            @Override
            public void fail() {

            }
        });
//        OCUtil.getInstent().loaderEZP(OCUtil.getInstent().EZP_NAME,getAssets().open(OCUtil.getInstent().EZP_NAME),new LoaderEZPLisener() {
//            @Override
//            public void onSucess(String path) {
//                loadAr(path);
//
//            }
//
//            @Override
//            public void fail() {
//
//            }
//        });
    }

    public void loadAr(String path) {
        mPlayerView.loadPackage(path, new PlayerView.OnLoadPackageFinish() {
            @Override
            public void onFinish() {
                conectionOCClient();
            }

        });
    }
//    public void loadAr(String path) {
//        mPlayerView.loadPackage(path, new PlayerView.OnLoadPackageFinish() {
//            @Override
//            public void onFinish() {
//                conectionOCClient();
//            }
//
//        });
//    }

}
