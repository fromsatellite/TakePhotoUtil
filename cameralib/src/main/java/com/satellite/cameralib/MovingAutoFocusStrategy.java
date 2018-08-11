//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.satellite.cameralib;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusMoveCallback;
import android.hardware.Camera.Parameters;
import android.os.Handler;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.satellite.cameralib.CameraControllerManager.CameraState;
import com.satellite.cameralib.CameraControllerManager.CommandEvent;

public class MovingAutoFocusStrategy implements IFocusStrategy {
    private static final String TAG = "gxd_camera";
    public PhotoModule mCameraModule;
    private boolean mPreviousMoving;
    private FocusUI mfocusUI;
    private long mFocusEndTime;
    private Context mContext;
    private CameraControllerManager mCameraController;
    private Handler mHandler;

    public MovingAutoFocusStrategy(Context context, PhotoModule cameramodule, FocusUI focusUI, CameraControllerManager cameraController, Handler mHandler) {
        this.mCameraModule = cameramodule;
        this.mfocusUI = focusUI;
        this.mContext = context;
        this.mCameraController = cameraController;
        this.mHandler = mHandler;
    }

    public void operateFocus() {
        if (!this.mCameraController.getPicTaked() && this.mCameraModule != null) {
            if (this.mCameraController.getIsSupportContinuousFocus()) {
                Parameters parameters = this.mCameraController.getCurrentParameters();
                if (parameters != null) {
                    try {
                        parameters.setFocusMode("continuous-picture");
                        this.mCameraController.trySetParameters(parameters);
                        MovingAutoFocusStrategy.MyAutoFocusMoveCallback autofocusmovecallback =
                                new MovingAutoFocusStrategy.MyAutoFocusMoveCallback();
                        this.mCameraModule.setAutoFocusMoveCallBack(autofocusmovecallback);
                    } catch (Exception var3) {
                        var3.printStackTrace();
                    }
                }

            }
        }
    }

    public void cancelFocus() {
        if (!this.mCameraController.getPicTaked() && this.mCameraModule != null) {
            this.mCameraModule.setAutoFocusMoveCallBack((AutoFocusMoveCallback)null);
        }
    }

    public long getFocusEndTime() {
        return this.mFocusEndTime;
    }

    private boolean isUsePicPreviewLayoutShow() {
        RelativeLayout mUsePicPreviewLayout = (RelativeLayout)((Activity)this.mContext).findViewById(CameraActivity.RES_ID_USEPICTURE);
        return mUsePicPreviewLayout.getVisibility() == 0;
    }

    public void executeFocus(MotionEvent event) {
    }

    private final class MyAutoFocusMoveCallback implements AutoFocusMoveCallback {
        private MyAutoFocusMoveCallback() {
        }

        public void onAutoFocusMoving(boolean moving, Camera camera) {
            MovingAutoFocusStrategy.this.mCameraController.setCameraState(CameraState.IDLE);
            if (moving != MovingAutoFocusStrategy.this.mPreviousMoving) {
                if (moving && !MovingAutoFocusStrategy.this.mPreviousMoving) {
                    if (!MovingAutoFocusStrategy.this.isUsePicPreviewLayoutShow()) {
                        MovingAutoFocusStrategy.this.mHandler.postDelayed(new Runnable() {
                            public void run() {
                                MovingAutoFocusStrategy.this.mfocusUI.onFocusStarted();
                            }
                        }, 0L);
                        MovingAutoFocusStrategy.this.mHandler.postDelayed(new Runnable() {
                            public void run() {
                                if (MovingAutoFocusStrategy.this.mCameraController.getCameraState() == CameraState.IDLE && MovingAutoFocusStrategy.this.mCameraController.getCommandEvent() == CommandEvent.IDLE) {
                                    MovingAutoFocusStrategy.this.mfocusUI.clearFocus();
                                }

                            }
                        }, 1000L);
                    }
                } else if (!moving) {
                    MovingAutoFocusStrategy.this.mfocusUI.onFocusSucceeded();
                    MovingAutoFocusStrategy.this.mHandler.postDelayed(new Runnable() {
                        public void run() {
                            MovingAutoFocusStrategy.this.mfocusUI.clearFocus();
                        }
                    }, 500L);
                    MovingAutoFocusStrategy.this.mFocusEndTime = System.currentTimeMillis();
                }

                MovingAutoFocusStrategy.this.mPreviousMoving = moving;
            }
        }
    }
}
