package cn.sd5g.appas.android.basis;

import android.os.Message;

public class RemoteService {
    private final String serviceCode;
    private String message = null;
    private boolean result = false;

    public RemoteService(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public Message execByMessage(int messageId) {
        Message msg = new Message();
        msg.what = messageId;
        msg.obj = this.exec();
        return msg;
    }

    public RemoteService exec() {
        return this;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public String getMessage() {
        return message;
    }

    public boolean isOk() {
        return result;
    }
}
