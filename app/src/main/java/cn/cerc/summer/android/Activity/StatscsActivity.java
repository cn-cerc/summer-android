package cn.cerc.summer.android.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.huagu.ehealth.R;

import cn.cerc.summer.android.View.StatscsView;
/**
 * Created by admin on 2017/4/7.
 */

public class StatscsActivity extends Activity implements
        OnSeekBarChangeListener {


    private SeekBar seekBar;

    private StatscsView statscsView;

    public StatscsActivity() {
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.histogram);

        seekBar = (SeekBar) this.findViewById(R.id.seekBar);

        statscsView = (StatscsView) this.findViewById(R.id.statscsView1);

        // seekerBar
        seekBar.setOnSeekBarChangeListener(this);

    }

    private int[] lastData0 = new int[] { 70000, 10000, 20000, 40000, 50000,
            80000, 40000 };
    private int[] thisData0 = new int[] { 40000, 10000, 10000, 20000, 30000,
            50000, 30000 };

    private int[] lastData1 = new int[] { 70000, 60000, 60000, 40000, 50000,
            80000, 80000 };
    private int[] thisData1 = new int[] { 40000, 30000, 30000, 20000, 30000,
            50000, 30000 };

    private int[] lastData2 = new int[] { 70000, 50000, 70000, 80000, 80000,
            80000, 70000 };
    private int[] thisData2 = new int[] { 40000, 10000, 40000, 40000, 30000,
            40000, 10000 };

    private int[] lastData3 = new int[] { 70000, 80000, 70000, 40000, 50000,
            80000, 40000 };
    private int[] thisData3 = new int[] { 10000, 10000, 10000, 20000, 30000,
            10000, 30000 };

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
        // TODO Auto-generated method stub

        int cc = progress / 4;

        switch (cc) {
            case 0:
                statscsView.updateThisData(lastData0);
                statscsView.updateLastData(thisData0);

                break;
            case 1:
                statscsView.updateThisData(lastData1);
                statscsView.updateLastData(thisData1);

                break;
            case 2:
                statscsView.updateThisData(lastData2);
                statscsView.updateLastData(thisData2);

                break;
            case 3:
                statscsView.updateThisData(lastData3);
                statscsView.updateLastData(thisData3);

                break;

            default:
                break;
        }


    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }






}
