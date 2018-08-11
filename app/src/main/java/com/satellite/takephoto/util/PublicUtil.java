/**
 *  Copyright (c) 2002-2014 AutoNavi, Inc. All rights reserved.
 *
 *  This software is the confidential and proprietary information of AutoNavi, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with AutoNavi.
 */
package com.satellite.takephoto.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PublicUtil {
    private static final String APP_NAME = "TakePhotoTask";

    private static final String PIC = "pics";

    public static String getNowTime() {
        Date date = new Date();
        long iNowTime = date.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return sdf.format(iNowTime);
    }
    
    
    /*
     * 判断是否有网络链接
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 检测是否有网络连接
     * @param ctx
     * @return true 连接可用。 false 连接不可用。
     */
    public static boolean isNetwork(Context ctx) {
        boolean netIsEnabled = true;
        if (checkNetworkInfo(ctx).equals("00")) {
            netIsEnabled = false;
        }
        return netIsEnabled;
    }

    /**
     * 检测网络状态<br>
     * 返回一个"00"形式的字符串 <li>"00":3G无效，WLAN无效 <li>"10":3G有效，WLAN无效 <li>
     * "01":3G无效，WLAN有效 <li>"11":3G有效，WLAN有效
     * @param ctx Context
     */
    public static String checkNetworkInfo(Context ctx) {
        String g3 = "0";
        String wlan = "0";
        ConnectivityManager conMan = null;
        try {
            conMan = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        } catch (Exception e) {
//            LogUtil.e(e.toString());
        }
        if (conMan == null) return "00";
        State mobile = null;
        State wifi = null;
        try {
            // mobile 3G Data Network
            mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        } catch (Exception e) {
//            LogUtil.e("平板，无3G网模块");
        }
        try {
            // wifi
            wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        } catch (Exception e) {
//            LogUtil.e("获取wifi状态失败，请检查权限");
        }
        if (mobile == State.CONNECTED || mobile == State.CONNECTING) g3 = "1";
        if (wifi == State.CONNECTED || wifi == State.CONNECTING) wlan = "1";
        // 如果3G网络和wifi网络都未连接，且不是处于正在连接状态 则进入Network Setting界面 由用户配置网络连接
        // ctx.startActivity(new
        // Intent(Settings.ACTION_WIRELESS_SETTINGS));//进入无线网络配置界面
        return g3 + wlan;
    }

    private static boolean hasExternalStorage() {
        boolean bReturn = false;
        String strStatus = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(strStatus)) bReturn = true;
        return bReturn;
    }

    public static String getRootDirectory() {
        String str = "";
        if (hasExternalStorage() == true) {
            str = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
                    + APP_NAME;

            File file = new File(str);
            if (!file.exists()) file.mkdir();
        }
        return str;
    }

    /** 删除并清空图片以及文件路径 */
    public static void deleteAndClearStation(ArrayList<Bitmap> bitmaps, ArrayList<String> mfiles,
                                             Bitmap bitmap3, String fileName3, boolean isdel) {
        if (isdel) {
            for (String filename : mfiles) {
                if (!TextUtils.isEmpty(filename)) new File(filename).delete();
            }
            if (!TextUtils.isEmpty(fileName3)) {
                new File(fileName3).delete();
            }
        } else {

        }
        if (bitmaps != null) {
            if (bitmaps.size() > 0) {
                for (Bitmap bitmap : bitmaps) {
                    if (bitmap != null && !bitmap.isRecycled()) {
                        bitmap.recycle();
                        bitmap = null;
                    }
                }
                bitmaps.clear();
                mfiles.clear();
            }
        }
        if (bitmap3 != null && !bitmap3.isRecycled()) {
            bitmap3.recycle();
            bitmap3 = null;
        }
        fileName3 = null;
    }

    private static long lastClickTime;

    /**
     * 判断是否为连击
     * @return
     */
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 300) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    public static void deleteFolder(File file) {
        if (!file.exists()) {
            return;
        }
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                file.delete();
                return;
            }
            for (int i = 0; i < childFiles.length; i++) {
                deleteFolder(childFiles[i]);
            }
            file.delete();
        }
    }

    public static void deleteFile(String filename) {
        if (!TextUtils.isEmpty(filename)) {
            File file = new File(filename);
            if (file != null && file.exists()) {
                file.delete();
            }
        }
    }

    public static void deleteFiles(ArrayList<String> mfiles) {
        if (mfiles != null) {
            for (int i = 0; i < mfiles.size(); i++) {
                if (!TextUtils.isEmpty(mfiles.get(i))) {
                    File file = new File(mfiles.get(i));
                    if (file != null && file.exists()) {
                        file.delete();
                    }
                }
            }
        }
    }

    /**
     * 获取Imei
     * @param context
     * @return
     */
    public static String getIMei(Context context) {
        try {
            String imei = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE))
                    .getDeviceId();
            return imei;
        } catch (Exception e) {
            return "0000000000";
        }
    }

    /**
     * 获取手机型号
     * @param context
     * @return
     */
    public static String getMobileModel(Context context) {
        try {
            String model = android.os.Build.MODEL; // 手机型号
            return model;
        } catch (Exception e) {
            return "未知";
        }
    }

    /**
     * 获取手机品牌
     * @param context
     * @return
     */
    public static String getMobileBrand(Context context) {
        try {
            String brand = android.os.Build.BRAND+"_"+android.os.Build.MODEL; // android系统版本号
            return brand;
        } catch (Exception e) {
            return "未知";
        }
    }

    /**
     * 获取手机系统版本
     * @param context
     * @return
     */
    public static String getMobileOSVersion(Context context) {
        try {
            String release = android.os.Build.VERSION.RELEASE; // android系统版本号
            return release;
        } catch (Exception e) {
            return "未知";
        }
    }

    /**
     * 获取版本号
     * @param context
     * @return
     */
    public static String getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取版本号
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            int version = info.versionCode;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static String getSimCardInfo(Context context) {

        try {
            TelephonyManager telMgr = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            return telMgr.getSimOperatorName();
        } catch (Exception e) {
            return "未知";
        }
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static double getDistance(double x1, double y1, double x2, double y2) {
        double latRadians1 = y1 * (Math.PI / 180);
        double latRadians2 = y2 * (Math.PI / 180);
        double latRadians = latRadians1 - latRadians2;
        double lngRadians = x1 * (Math.PI / 180) - x2 * (Math.PI / 180);
        double f = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(latRadians / 2), 2)
                + Math.cos(latRadians1) * Math.cos(latRadians2)
                * Math.pow(Math.sin(lngRadians / 2), 2)));
        return f * 6378137;
    }

    public static String getPicsPath(String userName, String taskId, String myId) {
        String baseFile = PublicUtil.getRootDirectory() + File.separator + userName + "_" + taskId
                + "_" + myId + "" + File.separator;
        String picsPath = baseFile + "pics";
        File file = new File(picsPath+ File.separator);
        if (!file.exists()) {
            file.mkdirs();
        }
        return picsPath;
    }

    public static String getPicsPath() {
        String baseFile = PublicUtil.getRootDirectory() + File.separator;
        String picsPath = baseFile + "pics";
        File file = new File(picsPath+ File.separator);
        if (!file.exists()) {
            file.mkdirs();
        }
        return picsPath;
    }

    public static String getPicCode(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return "-1";
        }
        return fileName.substring(fileName.lastIndexOf("/") + 1);

    }

    public static String getInVaidTime(long grabTime) {
        if (grabTime < 1000) {
            return "----";
        }
        long twoDay = 1000 * 60 * 60 * 24 * 2;//两天
        SimpleDateFormat df = new SimpleDateFormat("YYYY-MM-dd HH");
        return df.format(new Date(grabTime + twoDay));
    }
    public static boolean isOpenGps(Context ctx) {
    	try {
			LocationManager locationManager = (LocationManager) ctx
					.getSystemService(Context.LOCATION_SERVICE);
			return locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);
		} catch (Exception e) {
			return false;
		}
    }
    public static double parsentStrToDouble(String data){
    	if(TextUtils.isEmpty(data)||data.equals("")){
    		return 0;
    	}
    	return Double.parseDouble(data);
    } 
    /** 编码 */
//    public static String encode(String des) {
//    	  byte[] b = com.autonavi.lib.security.Cryptor.RC4Crypt(des.getBytes());
//          byte[] base64Byte = Base64.encode(b);
//          String baseString = new String(base64Byte);
//          return baseString;
//    }

    /** 解码 */
//    public static String decode(String des) {
//
//    	byte[] base64Byte = Base64.decode(des.getBytes());
//    	byte[] b = com.autonavi.lib.security.Cryptor.RC4Crypt(base64Byte);
//    	String decodeString = new String(b);
//        return decodeString;
//
//    }
    /**
     * 偏转通用接口
     * @param longitude
     * @param latitude
     * @return 偏转结果
     */
//    public static double[] gpsinfoOffset(double longitude,double latitude){
//    	IntValue longtitude_ = new IntValue((int) (longitude * 100000));
//		IntValue latitude_ = new IntValue((int) (latitude * 100000));
//		boolean isOK = Coord.getInstance().gpsinfoOffset(longtitude_,
//				latitude_);
//		double longitude_res = longtitude_.value / 100000d;
//		double latitude_res = latitude_.value / 100000d;
//		double lonlat[] = new double[2];
//		lonlat[0] = longitude_res;
//		lonlat[1] = latitude_res;
//		return lonlat;
//    }
}
