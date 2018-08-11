//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.satellite.cameralib;

import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;

public class CameraInstance {
    public static final String TAG = "gxd_camera";
    private Camera mCameraDevice;
    private boolean mCameraOpened;
    private final int mNumberOfCameras;
    private int mCameraId = -1;
    private int mBackCameraId = -1;
    private int mFrontCameraId = -1;
    private final CameraInfo[] mInfo;
    private Parameters mParameters;
    private static CameraInstance sHolder;

    public static synchronized CameraInstance instance() {
        if (sHolder == null) {
            sHolder = new CameraInstance();
        }

        return sHolder;
    }

    private CameraInstance() {
        if (ApiChecker.HAS_GET_CAMERA_NUMBER) {
            this.mNumberOfCameras = 1;
            this.mBackCameraId = 0;
            this.mFrontCameraId = 1;
            this.mInfo = null;
        } else {
            this.mNumberOfCameras = Camera.getNumberOfCameras();
            this.mInfo = new CameraInfo[this.mNumberOfCameras];

            int i;
            for(i = 0; i < this.mNumberOfCameras; ++i) {
                this.mInfo[i] = new CameraInfo();
                Camera.getCameraInfo(i, this.mInfo[i]);
            }

            for(i = 0; i < this.mNumberOfCameras; ++i) {
                if (this.mBackCameraId == -1 && this.mInfo[i].facing == 0) {
                    this.mBackCameraId = i;
                } else if (this.mFrontCameraId == -1 && this.mInfo[i].facing == 1) {
                    this.mFrontCameraId = i;
                }
            }
        }

    }

    public synchronized Camera open() {
        if (this.mCameraDevice != null && this.mCameraId != this.mBackCameraId) {
            this.mCameraDevice.release();
            this.mCameraDevice = null;
            this.mCameraId = -1;
        }

        if (this.mCameraDevice == null) {
            if (ApiChecker.AT_LEAST_10) {
                this.mCameraDevice = Camera.open(this.mBackCameraId);
            } else {
                this.mCameraDevice = Camera.open();
            }

            if (this.mCameraDevice == null) {
                return null;
            }

            this.mCameraId = this.mBackCameraId;
        }

        this.mCameraOpened = true;
        return this.mCameraDevice;
    }

    public int getBackCameraId() {
        return this.mBackCameraId;
    }

    public int getFrontCameraId() {
        return this.mFrontCameraId;
    }

    public synchronized void release() {
        if (this.mCameraDevice != null) {
            this.strongRelease();
        }
    }

    public synchronized void strongRelease() {
        if (this.mCameraDevice != null) {
            this.mCameraOpened = false;
            this.mCameraDevice.release();
            this.mCameraDevice = null;
            this.mParameters = null;
            this.mCameraId = -1;
        }
    }

    public void releaseInstance() {
        sHolder = null;
    }
}
