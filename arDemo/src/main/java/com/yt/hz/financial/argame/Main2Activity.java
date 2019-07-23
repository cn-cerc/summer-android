package com.yt.hz.financial.argame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Main2Activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        findViewById(R.id.btn_enter_ar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main2Activity.this, LoaderPreloadIDActivity.class);
                intent.putExtra("id","add3a6de-cd7b-4650-9d8c-99d87add0509");
                intent.putExtra("desc","hh");
                startActivity(intent);
            }
        });
    }
}
