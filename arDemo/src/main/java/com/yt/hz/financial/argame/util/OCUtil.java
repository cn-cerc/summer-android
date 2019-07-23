package com.yt.hz.financial.argame.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class OCUtil {
    public String TAG = "OCUtil";
    public String EZP_NAME= "environment.ezp";
    private static OCUtil ocUtil;
    private static Object object=new Object();
    public static synchronized OCUtil getInstent(){
        synchronized (object){
            if(ocUtil==null){
                ocUtil=new OCUtil();
            }
            return  ocUtil;
        }
    }

    public  boolean copyZipToSDCard(File file, InputStream input){
        boolean isLoadAR=false;
        isLoadAR=assetsDataToSD(file.getAbsolutePath(),input);
        return isLoadAR;
    }
    private  boolean assetsDataToSD(String fileName, InputStream input) {
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

    /**
     *
     *
     * @param inputStream assets的流
     * @param loaderEZPLisener 监听
     */
    public  void loaderEZP(String path, InputStream inputStream, LoaderEZPLisener loaderEZPLisener){
        File file=new File(path);
        if(!file.exists()){
            file.mkdirs();
        }
        File storageFile=new File(path+ "/"+EZP_NAME);

        boolean mIsCopyed = copyZipToSDCard(storageFile, inputStream);
        if (mIsCopyed) {
            loaderEZPLisener.onSucess(storageFile.getAbsolutePath());
        }else{
            loaderEZPLisener.fail();
        }
    }
    public byte[] readFile(File file) {
        // 需要读取的文件，参数是文件的路径名加文件名
        if (file.isFile()) {
            // 以字节流方法读取文件

            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                // 设置一个，每次 装载信息的容器
                byte[] buffer = new byte[1024];
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                // 开始读取数据
                int len = 0;// 每次读取到的数据的长度
                while ((len = fis.read(buffer)) != -1) {// len值为-1时，表示没有数据了
                    // append方法往sb对象里面添加数据
                    outputStream.write(buffer, 0, len);
                }
                // 输出字符串
                return outputStream.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("文件不存在");
        }
        return null;
    }
}
