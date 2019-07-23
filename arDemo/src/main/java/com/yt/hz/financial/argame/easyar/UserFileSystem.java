package com.yt.hz.financial.argame.easyar;

import android.util.Log;

import java.io.File;

import cn.easyar.IFileSystem;

/**
 * Created by rssqi on 2018/3/28.
 */

public class UserFileSystem extends IFileSystem{

    private String TAG = "EasyAR";
    private String Schema = "user://";
    private String mRootDir = null;

    private boolean check(String filename)
    {
        if(mRootDir.isEmpty())
        {
            Log.i(TAG, "UserFileSystem RootDirNotExist");
            return false;
        }
        if(filename.isEmpty())
            return false;
        return true;
    }
    @Override
    public boolean fileExist(String filename) {
        if(false == check(filename))
            return false;

        return true;
    }

    @Override
    public String convertUserToAbsolute(String filename) {
        if(false == check(filename))
            return null;

        if(!filename.startsWith(Schema))
        {
            Log.i(TAG, "UserFileSystem: filename " + filename + " not valid!");
            return null;
        }

        String name = mRootDir + "/" + filename.substring(Schema.length());

        File f = new File(name);
        if(!f.exists() || f.isDirectory()) {
            Log.i(TAG, "filename not exist: " + filename);
            return null;
        }
        return name;
    }

    //so all user://xxx.png will be rootDir/xxx.png
    public void setUserRootDir(String rootDir)
    {
        if(rootDir.endsWith("/"))
        {
            //not accept "/"
            mRootDir = rootDir.substring(0, rootDir.length() -1);
        }else
        {
            mRootDir = rootDir;
        }

        if(mRootDir.isEmpty())
        {
            Log.i(TAG, "UserRootDir empty!");
            return;
        }

        File f = new File(mRootDir);
        if(!f.exists() || !f.isDirectory()) {
            Log.i(TAG, "UserRootDir not valid " + rootDir);
        }
    }

    public void clearCache()
    {
        throw new RuntimeException("clearCache not implemented");
    }
}
