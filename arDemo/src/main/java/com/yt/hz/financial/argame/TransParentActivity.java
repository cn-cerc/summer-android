package com.yt.hz.financial.argame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class TransParentActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new TextView(this));
        String a = getIntent().getStringExtra("type");
        //4 代表申请办卡 0-上，1-下，2-左，3-右
        if (a!=null){
            Intent intent = new Intent();
            switch (a){
                case "1":
                    intent.putExtra("json","{\"errorMessage\":\"外部调用任务成功\",\"errorCode\":\"0\",\"userAddBonus\":\"20\"}");
                    setResult(RESULT_OK,intent);
                    break;
                case "2":
                    intent.putExtra("json","{\n" +
                            "\t\"errorMessage\": null,\n" +
                            "\t\"errorCode\": \"0\",\n" +
                            "\t\"userFinishList\": [{\n" +
                            "\t\t\"orderId\": \"1\",\n" +
                            "\t\t\"nikeName\": \"户名106916356\",\n" +
                            "\t\t\"nums\": \"32\"\n" +
                            "\t}, {\n" +
                            "\t\t\"orderId\": \"2\",\n" +
                            "\t\t\"nikeName\": \"户名106916026\",\n" +
                            "\t\t\"nums\": \"32\"\n" +
                            "\t}, {\n" +
                            "\t\t\"orderId\": \"3\",\n" +
                            "\t\t\"nikeName\": \"谢兴未\",\n" +
                            "\t\t\"nums\": \"23\"\n" +
                            "\t}, {\n" +
                            "\t\t\"orderId\": \"4\",\n" +
                            "\t\t\"nikeName\": null,\n" +
                            "\t\t\"nums\": \"20\"\n" +
                            "\t}, {\n" +
                            "\t\t\"orderId\": \"5\",\n" +
                            "\t\t\"nikeName\": \"户名106913329\",\n" +
                            "\t\t\"nums\": \"18\"\n" +
                            "\t}, {\n" +
                            "\t\t\"orderId\": \"6\",\n" +
                            "\t\t\"nikeName\": \"户名106913329\",\n" +
                            "\t\t\"nums\": \"18\"\n" +
                            "\t}, {\n" +
                            "\t\t\"orderId\": \"7\",\n" +
                            "\t\t\"nikeName\": \"户名106913329\",\n" +
                            "\t\t\"nums\": \"18\"\n" +
                            "\t}, {\n" +
                            "\t\t\"orderId\": \"8\",\n" +
                            "\t\t\"nikeName\": \"户名106913329\",\n" +
                            "\t\t\"nums\": \"18\"\n" +
                            "\t}, {\n" +
                            "\t\t\"orderId\": \"9\",\n" +
                            "\t\t\"nikeName\": \"户名106913329\",\n" +
                            "\t\t\"nums\": \"18\"\n" +
                            "\t}, {\n" +
                            "\t\t\"orderId\": \"10\",\n" +
                            "\t\t\"nikeName\": \"户名106913329\",\n" +
                            "\t\t\"nums\": \"18\"\n" +
                            "\t}, {\n" +
                            "\t\t\"orderId\": \"11\",\n" +
                            "\t\t\"nikeName\": \"户名106913329\",\n" +
                            "\t\t\"nums\": \"18\"\n" +
                            "\t}, {\n" +
                            "\t\t\"orderId\": \"12\",\n" +
                            "\t\t\"nikeName\": \"户名106913329\",\n" +
                            "\t\t\"nums\": \"18\"\n" +
                            "\t}, {\n" +
                            "\t\t\"orderId\": \"13\",\n" +
                            "\t\t\"nikeName\": \"户名106913329\",\n" +
                            "\t\t\"nums\": \"18\"\n" +
                            "\t}, {\n" +
                            "\t\t\"orderId\": \"14\",\n" +
                            "\t\t\"nikeName\": \"户名106913329\",\n" +
                            "\t\t\"nums\": \"18\"\n" +
                            "\t}]\n" +
                            "}");
                    setResult(RESULT_OK,intent);
                    break;
            }
        }
        finish();
    }
}
