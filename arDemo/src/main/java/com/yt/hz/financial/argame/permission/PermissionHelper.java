package com.yt.hz.financial.argame.permission;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rzm on 2017/8/19.
 */

public class PermissionHelper {

    private final Object mObject;
    private int mRequestCode = 0;
    private String[] mRequestPermissions;
    private static List<String> mUnGrantedPermissions;

    public PermissionHelper(Object object) {
        this.mObject = object;
    }

    //在activity中使用
    public static PermissionHelper with(Activity activity) {
        return new PermissionHelper(activity);
    }

    //在fragment中使用
    public static PermissionHelper with(Fragment fragment) {
        return new PermissionHelper(fragment);
    }

    public PermissionHelper requestCode(int requestCode) {
        this.mRequestCode = requestCode;
        return this;
    }

    public PermissionHelper requestPermissions(String... permissions) {
        this.mRequestPermissions = permissions;
        return this;
    }

    public void request() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // 6.0以下直接执行方法  反射获取执行方法,这个方法是申请权限的回调方法，成功或失败,方法的名字不确定
            // 用注解的方式给方法打一个标记，然后通过反射去执行
            //(6.0以下的这个方法其实可以不去执行，直接配置文件设置以下就可以了，不过这样显得更规范，用与不用看个人喜好)
            executeBellow6();
        }else{
            executeOver6();
        }
    }

    private static Activity getContext(Object object) {
        Activity context = null;
        if (object instanceof Activity) {
            context = ((Activity) object);
        } else if (object instanceof Fragment) {
            context = ((Fragment) object).getActivity();
        }
        return context;
    }

    private void executeOver6() {
        //获取没有授权权限
        List<String> deniedPermissions = getDeniedPermissions(mObject, mRequestPermissions);
        if (deniedPermissions != null && deniedPermissions.size() == 0) {
            //全部授权过
            executeSucceedMethod(mObject, mRequestCode);
        } else {
            //如果没有授予就申请权限
            requestListPermissions(mObject, deniedPermissions, mRequestCode);
        }
    }

    /**
     * @param object
     * @param deniedPermissions
     * @param requestCode
     */
    private static void requestListPermissions(Object object, List<String> deniedPermissions, int requestCode) {
        ActivityCompat.requestPermissions(getContext(object), deniedPermissions.toArray(new String[deniedPermissions.size()]), requestCode);
    }

    private void executeBellow6() {
        if (mObject == null || mRequestCode == 0)
            return;
        executeSucceedMethod(mObject, mRequestCode);
    }

    private static void executeSucceedMethod(Object object, long requestCode) {
        Method[] methods = object.getClass().getDeclaredMethods();
        for (Method method : methods) {
            // 获取申请成功的标记
            PermissionSucceed permissionSucceed = method.getAnnotation(PermissionSucceed.class);
            if (permissionSucceed != null) {
                int code = permissionSucceed.requestCode();
                //根据请求码和标记找到对应的方法
                if (requestCode == code) {
                    try {
                        // 反射执行方法  第一个是传该方法是属于哪个类   第二个参数是反射方法的参数
                        method.setAccessible(true);
                        method.invoke(object, new Object[]{});
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 用户拒绝了权限但是没有永久拒绝（未勾选不再提醒）
     * @param object
     * @param requestCode
     */
    private static void executeDeniedMethod(Object object, long requestCode) {
        Method[] methods = object.getClass().getDeclaredMethods();
        for (Method method : methods) {
            PermissionDenied permissionFailed = method.getAnnotation(PermissionDenied.class);
            if (permissionFailed != null) {
                int code = permissionFailed.requestCode();
                if (code == requestCode) {
                    try {
                        // 反射执行方法  第一个是传该方法是属于哪个类   第二个参数是反射方法的参数
                        method.setAccessible(true);
                        method.invoke(object, new Object[]{});
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    /**
     * 用户拒绝了权限并且永久拒绝（勾选不再提醒）
     * @param object
     * @param requestCode
     */
    private static void executePermanentDeniedMethod(Object object, long requestCode) {
        Method[] methods = object.getClass().getDeclaredMethods();
        for (Method method : methods) {
            PermissionPermanentDenied permissionPermanentDenied = method.getAnnotation(PermissionPermanentDenied.class);
            if (permissionPermanentDenied != null) {
                int code = permissionPermanentDenied.requestCode();
                if (code == requestCode) {
                    try {
                        // 反射执行方法  第一个是传该方法是属于哪个类   第二个参数是反射方法的参数
                        method.setAccessible(true);
                        method.invoke(object, new Object[]{});
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private static List<String> getDeniedPermissions(Object mObject, String[] requestPermissions) {
        mUnGrantedPermissions = new ArrayList<String>();
        Context context = getContext(mObject);
        if (context != null) {
            for (String permission : requestPermissions) {
                if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    mUnGrantedPermissions.add(permission);
                }
            }
        }
        return mUnGrantedPermissions;
    }

    /**
     * 处理申请权限的回调
     *
     * shouldShowRequestPermissionRationale
     * 1   第一次进来这个返回值为false
     * 2   当用户点击了拒绝权限而没有选择不再提示，返回值为true
     * 3   当用户点击了拒绝权限并且选择了不再提示，返回值为false
     */
    public static void requestPermissionsResult(Object object, int requestCode,
                                                String[] permissions, int[] grantResults) {
        if (verifyPermissions(grantResults)) {//有权限
            executeSucceedMethod(object, requestCode);
        } else {

            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                    boolean showRequestPermission = ActivityCompat.shouldShowRequestPermissionRationale(getContext(object), permissions[i]);
                    if (showRequestPermission) {
                        //拒绝权限而没有选择不再提示
                        executeDeniedMethod(object,requestCode);
                    } else {
                        //拒绝权限并且选择了不再提示
                        executePermanentDeniedMethod(object,requestCode);
                    }
                }
            }

        }

    }

    /**
     * 判断请求权限是否成功
     *
     * @param grantResults
     * @return
     */
    public static boolean verifyPermissions(int[] grantResults) {
        if (grantResults.length < 1) {
            return false;
        }

        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

}
