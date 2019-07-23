package com.yt.hz.financial.argame.easyar;

/**
 * Created by Sigthp-P-Department on 2018/5/21.
 */

public enum MessageID {
    //发现图片
    MSG_ID_FOUNDTARGET(100),
    //注册图片
    MSG_ID_LOAD_TARGET(101),
    LoadTarget(101),
    LoadedTarget(102),
    ModelAnimFinish(103),
    ButtomOnClick(104),
    ModelCenter(105),

    LoadFinish(1001),//模型加载完成
    TargetFound(1002),//扫图成功
    ClickTarget(1100),//点击目标
    showTakePhotoButton(1003),//传参key：state，value：true/false(String类型)
    setBackFive(1004),//设置返回键为返回5个星球



    changeCamera(2003),//切换摄像头
    creditBillJson(2001),//切换摄像头
    active(1010),//模型显示隐藏
    locationMsg(1200),//发送地图信息
    currentLocation(1250),//发送地图信息
    yOrientation(1300),//发送正北角
    backFive(2004),//原生调内容返回5个星球

    CameraReversal(106),
    VerticalScreenSwitching(107),
    Drawing(200),
    ShowLuoTuoTips(107);
    private int id;

    public int getId() {
        return id;
    }

    MessageID(int i) {
        id = i;
    }
}
