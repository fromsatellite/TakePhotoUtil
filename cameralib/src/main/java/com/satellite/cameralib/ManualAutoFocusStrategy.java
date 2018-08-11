//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.satellite.cameralib;

import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.os.Handler;
import android.view.MotionEvent;

import com.satellite.cameralib.CameraControllerManager.CameraState;
import com.satellite.cameralib.CameraControllerManager.CommandEvent;

public class ManualAutoFocusStrategy implements IFocusStrategy {
    private static final String TAG = "gxd_camera";
    public PhotoModule mCameraModule;
    private FocusUI mfocusUI;
    private long mFocusEndTime;
    private CameraControllerManager mCameraController;
    private int touch_x;
    private int touch_y;
    private MotionEvent mMotionEvent;
    private Handler mHandler;
    private AutoFocusCallback autoFocusCallback = new AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            ManualAutoFocusStrategy.this.mHandler.removeMessages(4);
            if (success) {
                ManualAutoFocusStrategy.this.mFocusEndTime = System.currentTimeMillis();
                if (ManualAutoFocusStrategy.this.mCameraController.getCommandEvent() == CommandEvent.CLICK_TAKE_PIC) {
                    ManualAutoFocusStrategy.this.mfocusUI.clearFocus();
                    ManualAutoFocusStrategy.this.takePicInSuccessAutoFocus();
                } else if (ManualAutoFocusStrategy.this.mCameraController.getCommandEvent() == CommandEvent.TOUCH_SCREEN) {
                    ManualAutoFocusStrategy.this.showAutoFocusSucess();
                    if (ManualAutoFocusStrategy.this.mCameraController.isTouchTakingPic()) {
                        ManualAutoFocusStrategy.this.takePicInSuccessAutoFocus();
                    } else {
                        ManualAutoFocusStrategy.this.mCameraController.setCameraState(CameraState.IDLE);
                        if (ManualAutoFocusStrategy.this.mCameraController.getIsSupportContinuousFocus()) {
                            ManualAutoFocusStrategy.this.mCameraController.restoreContinuousFocus();
                        }
                    }
                } else if (ManualAutoFocusStrategy.this.mCameraController.getCommandEvent() == CommandEvent.FIRST_IN_FOCUS) {
                    ManualAutoFocusStrategy.this.showAutoFocusSucess();
                    ManualAutoFocusStrategy.this.mCameraController.setCameraState(CameraState.IDLE);
                    if (ManualAutoFocusStrategy.this.mCameraController.getIsSupportContinuousFocus()) {
                        ManualAutoFocusStrategy.this.mCameraController.restoreContinuousFocus();
                    }
                }
            } else {
                ManualAutoFocusStrategy.this.mHandler.sendEmptyMessageDelayed(4, 500L);
                ManualAutoFocusStrategy.this.takePicInFailedAutoFocus();
            }

        }
    };

    public ManualAutoFocusStrategy(PhotoModule cameramodule, FocusUI focusUI, CameraControllerManager cameraController, Handler handler) {
        this.mCameraModule = cameramodule;
        this.mfocusUI = focusUI;
        this.mCameraController = cameraController;
        this.mHandler = handler;
    }

    public void operateFocus() {
    }

    public void cancelFocus() {
        if (!this.mCameraController.getPicTaked()) {
            if (this.mCameraModule != null) {
                this.mCameraModule.onCancelAutoFocus();
            }

        }
    }

    public void executeFocus(MotionEvent event) {
        if (!this.mCameraController.getPicTaked() && this.mCameraModule != null) {
            this.mMotionEvent = event;
            if (this.isSupportAutoFocus()) {
                if (this.mCameraController.getCameraState() == CameraState.AUTO_FOCUSING) {
                    this.cancelFocus();
                    this.setAutoFocus();
                } else if (this.mCameraController.getCameraState() == CameraState.IDLE) {
                    this.setAutoFocus();
                } else if (this.mCameraController.getCameraState() == CameraState.TAKING_PICTURE) {
                    return;
                }
            }

        }
    }

    private void setAutoFocus() {
        Parameters parameters = this.mCameraController.getCurrentParameters();
        parameters.setFocusMode("auto");
        this.mCameraController.trySetParameters(parameters);
        this.mCameraController.setCameraState(CameraState.AUTO_FOCUSING);

        try {
            this.mCameraModule.onAutoFocus(this.autoFocusCallback);
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }

    public long getFocusEndTime() {
        return this.mFocusEndTime;
    }

    private void takePicInSuccessAutoFocus() {
        if (this.mCameraController.getCameraState() != CameraState.TAKING_PICTURE) {
            this.mCameraController.setCameraState(CameraState.TAKING_PICTURE);
            this.mCameraController.takePicture();
        }
    }

    private void takePicInFailedAutoFocus() {
        if ((!this.mCameraController.isTouchTakingPic() || this.mCameraController.getCommandEvent() != CommandEvent.TOUCH_SCREEN) && this.mCameraController.getCommandEvent() != CommandEvent.CLICK_TAKE_PIC) {
            this.mCameraController.setCameraState(CameraState.IDLE);
            if (this.mCameraController.getIsSupportContinuousFocus()) {
                this.mCameraController.restoreContinuousFocus();
            }
        } else {
            if (this.mCameraController.getCameraState() == CameraState.TAKING_PICTURE) {
                return;
            }

            this.mCameraController.setCameraState(CameraState.TAKING_PICTURE);
            this.mCameraController.takePicture();
        }

    }

    private void showAutoFocusSucess() {
        if (this.mMotionEvent == null) {
            this.mfocusUI.onFocusStarted();
        }

        this.mfocusUI.onFocusSucceeded();
        this.mHandler.sendEmptyMessageDelayed(4, 500L);
    }

    private boolean isSupportAutoFocus() {
        boolean isSupportAutoFocus = this.mCameraController.getCurrentParameters().getSupportedFocusModes().contains("auto");
        return isSupportAutoFocus;
    }
}
