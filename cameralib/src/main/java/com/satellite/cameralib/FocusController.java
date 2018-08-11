//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.satellite.cameralib;

import android.content.Context;
import android.os.Handler;
import android.view.MotionEvent;

public class FocusController {
    private IFocusStrategy mFocusStrategy;
    private Context mContext;
    private FocusUI mFocusUI;
    private PhotoModule mCameraModule;
    private CameraControllerManager mCameraController;
    private Handler mHandler;
    public static final int MOVING_AUTO_FOCUS = 1;
    public static final int SENSOR_AUTO_FOCUS = 2;
    public static final int AUTO_FOCUS = 3;

    public FocusController(Context context, PhotoModule cameramodule, FocusUI focusUI, CameraControllerManager cameracontroller, Handler handler) {
        this.mContext = context;
        this.mFocusUI = focusUI;
        this.mCameraModule = cameramodule;
        this.mCameraController = cameracontroller;
        this.mHandler = handler;
    }

    public IFocusStrategy getFocusStrategy(int type) {
        if (this.mFocusStrategy != null) {
            this.mFocusStrategy.cancelFocus();
        }

        switch(type) {
            case 1:
                this.mFocusStrategy = new MovingAutoFocusStrategy(this.mContext, this.mCameraModule, this.mFocusUI, this.mCameraController, this.mHandler);
            case 2:
            default:
                break;
            case 3:
                this.mFocusStrategy = new ManualAutoFocusStrategy(this.mCameraModule, this.mFocusUI, this.mCameraController, this.mHandler);
        }

        return this.mFocusStrategy;
    }

    public void operateFocus() {
        if (this.mFocusStrategy != null) {
            this.mFocusStrategy.operateFocus();
        }

    }

    public void executeFocus(MotionEvent event) {
        if (this.mFocusStrategy != null) {
            this.mFocusStrategy.executeFocus(event);
        }

    }

    public void cancelFocus() {
        if (this.mFocusStrategy != null) {
            this.mFocusStrategy.cancelFocus();
        }

    }

    public long getFocusEndTime() {
        return this.mFocusStrategy != null ? this.mFocusStrategy.getFocusEndTime() : 1100L;
    }
}
