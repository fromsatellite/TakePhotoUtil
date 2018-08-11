//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.satellite.cameralib;

import android.app.Activity;
import android.os.Handler;
import android.view.SurfaceHolder;

public abstract class AbstractCameraControllerManager {
    public static final String TAG = "gxd_camera";
    public Double mLat;
    public Double mLng;
    public Double mTelAddrLat;
    public Double mTelAddrLng;
    public Activity mContext = null;
    public Handler mHandler = null;
    public SurfaceHolder mSurfaceHolder;
    public int mXCaptureDirection;
    public int mYCaptureDirection;
    public int mZCaptureDirection;
    public boolean mIsNeedTracePoint = false;
    public int mXDirection;
    public int mYDirection;
    public int mZDirection;
    public long mDerectionTime;
    public static final long GXDTAOJIN_LOCATION_VALID_TIME = 500L;

    public AbstractCameraControllerManager(Activity mContext, Handler mHandler, SurfaceHolder mSurfaceHolder) {
        this.mContext = mContext;
        this.mHandler = mHandler;
        this.mSurfaceHolder = mSurfaceHolder;
    }
}
