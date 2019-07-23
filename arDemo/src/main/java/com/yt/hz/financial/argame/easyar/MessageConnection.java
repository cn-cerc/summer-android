package com.yt.hz.financial.argame.easyar;

import android.util.Log;

import cn.easyar.BufferVariant;
import cn.easyar.BufferVariantType;
import cn.easyar.EasyARDictionary;
import cn.easyar.Message;
import cn.easyar.MessageClient;

/**
 * Created by Sigthp-P-Department on 2018/5/30.
 * 信息封装类
 */

public class MessageConnection {

    private static MessageConnection messageConnection;
    private static Object object=new Object();

    public static synchronized MessageConnection getInstent(){
        synchronized (object){
            if(messageConnection==null){
                messageConnection=new MessageConnection();
            }
            return  messageConnection;
        }
    }

    /**
     * 发送图片数据到本地ezp，为识别做好准备
     * @param target
     * @param messageClient
     */
    public void loadTaget(byte[] target, String id, MessageClient messageClient){
        EasyARDictionary theBody = new EasyARDictionary();
        theBody.setString("targetId", id);
        theBody.setBufferVariant("image", new BufferVariant(target, target.length, BufferVariantType.Image));
        messageClient.send(new Message(MessageID.MSG_ID_LOAD_TARGET.getId(), theBody));
        Log.d("easyar","loadTaget---------target--"+target.length);
    }

    /**
     * 发送信息
     * @param id
     * @param value
     * @param messageClient
     */
    public void sendMessage(int id, String value, MessageClient messageClient){
        EasyARDictionary theBody = new EasyARDictionary();
        theBody.setString("key", value);
        messageClient.send(new Message(id, theBody));
    }

}
