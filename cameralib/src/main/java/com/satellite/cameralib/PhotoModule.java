//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.satellite.cameralib;

import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.AutoFocusMoveCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.view.SurfaceHolder;

public class PhotoModule {
    public static final String TAG = "gxd_camera";
    public CameraInstance mCameraHolder = null;
    private Camera mCameraDevice;
    private Parameters mParameters;

    public PhotoModule() {
    }

    public void init() {
        this.mCameraHolder = CameraInstance.instance();
    }

    public Camera openCamera() {
        if (this.mCameraHolder != null) {
            this.mCameraDevice = this.mCameraHolder.open();
            return this.mCameraDevice;
        } else {
            return null;
        }
    }

    public void onCancelAutoFocus() {
        if (this.mCameraDevice != null) {
            if (ApiChecker.AT_LEAST_5) {
                this.mCameraDevice.cancelAutoFocus();
            }

        }
    }

    public void onAutoFocus(AutoFocusCallback autofocuscallback) {
        if (this.mCameraDevice != null) {
            this.mCameraDevice.autoFocus(autofocuscallback);
        }
    }

    public Parameters getParameters() {
        return this.mCameraDevice == null ? null : this.mCameraDevice.getParameters();
    }

    public void setParameters(Parameters params) {
        if (this.mCameraDevice != null) {
            this.mCameraDevice.setParameters(params);
        }
    }

    public void onStartPreview() {
        if (this.mCameraDevice != null) {
            this.mCameraDevice.startPreview();
        }
    }

    public void onStopPreview() {
        if (this.mCameraDevice != null) {
            this.mCameraDevice.stopPreview();
        }
    }

    public void setDisplayOrientation(int orientation) {
        if (this.mCameraDevice != null) {
            if (ApiChecker.AT_LEAST_8) {
                try {
                    this.mCameraDevice.setDisplayOrientation(orientation);
                } catch (Exception var3) {
                    var3.printStackTrace();
                }
            }

        }
    }

    public void setAutoFocusMoveCallBack(AutoFocusMoveCallback autofocusmovecallback) {
        if (this.mCameraDevice != null) {
            if (ApiChecker.HAS_AUTO_FOCUS_MOVE_CALLBACK) {
                this.mCameraDevice.setAutoFocusMoveCallback(autofocusmovecallback);
            }

        }
    }

    public void release() {
        if (this.mCameraHolder != null) {
            this.mCameraHolder.release();
        } else {
            CameraInstance.instance().release();
        }

        this.mCameraDevice = null;
    }

    public void onCapture(ShutterCallback shutter, PictureCallback raw, PictureCallback jpeg) {
        if (this.mCameraDevice != null) {
            try {
                this.mCameraDevice.takePicture(shutter, raw, jpeg);
            } catch (Exception var5) {
                var5.printStackTrace();
            }

        }
    }

    public void setPreviewDisplay(SurfaceHolder holder) {
        if (this.mCameraDevice != null) {
            try {
                this.mCameraDevice.setPreviewDisplay(holder);
            } catch (Exception var3) {
                var3.printStackTrace();
            }

        }
    }

    public void releaseCameraHolder() {
        if (this.mCameraHolder != null) {
            this.mCameraHolder.releaseInstance();
        }

    }
}
