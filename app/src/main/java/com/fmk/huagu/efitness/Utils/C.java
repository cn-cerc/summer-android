package com.fmk.huagu.efitness.Utils;

/**
 * Created by huagu on 2016/11/2.
 */

public class C {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native static String stringFromJNI();

}
