package com.yt.hz.financial.argame.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by admin on 2019/1/11.
 */

public class FileUtil {

    public static void copyAssetsToSdcard(String assestsName,String sdCardPath, InputStream inputStream,LoaderEZPLisener loaderEZPLisener){
        File file=new File(sdCardPath);
        if(!file.exists()){
            file.mkdirs();
        }
        File storageFile=new File(sdCardPath+ "/"+assestsName);

        boolean mIsCopyed = copyZipToSDCard(storageFile, inputStream);
        if (mIsCopyed) {
            loaderEZPLisener.onSucess(storageFile.getAbsolutePath());
        }else{
            loaderEZPLisener.fail();
        }
    }

    public static boolean copyZipToSDCard(File file, InputStream input){
        boolean isLoadAR=false;
        isLoadAR=assetsDataToSD(file.getAbsolutePath(),input);
        return isLoadAR;
    }
    private static boolean assetsDataToSD(String fileName, InputStream input) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(fileName);
            inputStream = input;
            byte[] buffer = new byte[4 * 1024];
            int i = 0;
            while ((i = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, i);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }finally {
            try {
                if(outputStream!=null){
                    outputStream.flush();
                    outputStream.close();
                }
                if(inputStream!=null){
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}
