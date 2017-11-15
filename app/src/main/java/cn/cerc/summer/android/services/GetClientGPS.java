package cn.cerc.summer.android.services;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;

import org.json.JSONObject;

import cn.cerc.summer.android.forms.JavaScriptService;

/**
 * Created by Jason<sz9214e@qq.com> on 2017/8/9.
 */

public class GetClientGPS implements JavaScriptService {

    @Override
    public void execute(Context context, JSONObject request, JSONObject response) throws Exception {
        LocationManager locationManager = (LocationManager) context.getSystemService(context.getApplicationContext().LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);//低精度，如果设置为高精度，依然获取不了location。
        criteria.setAltitudeRequired(false);//不要求海拔
        criteria.setBearingRequired(false);//不要求方位
        criteria.setCostAllowed(true);//允许有花费
        criteria.setPowerRequirement(Criteria.POWER_LOW);//低功耗

        //从可用的位置提供器中，匹配以上标准的最佳提供器
        String locationProvider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(context.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            response.put("result", false);
            response.put("message", "没有权限，定位失败！");
            return;
        }
        Location location = locationManager.getLastKnownLocation(locationProvider);
        if (location != null) {
            //不为空,显示地理位置经纬度
            response.put("result", false);
            response.put("message", "定位成功");
            response.put("longitude", location.getLongitude());
            response.put("latitude", location.getLatitude());
        }else {
            response.put("result", false);
            response.put("message", "坐标获取失败");
        }
        return ;
    }
}
