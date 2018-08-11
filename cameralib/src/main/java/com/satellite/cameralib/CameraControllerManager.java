//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.satellite.cameralib;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.widget.Toast;

import com.satellite.cameralib.photocompress.PhotoUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

public class CameraControllerManager extends AbstractCameraControllerManager {
    public static final String TAG = "gxd_camera";
    private Camera mCamera;
    private PhotoModule mPhotoModule;
    private Parameters mParameters = null;
    private boolean mSupportContinuousFocus;
    private boolean mSupportAutoFocus;
    private boolean mPreviewing = false;
    private static final int MEDIA_TYPE_IMAGE = 1;
    private static final int DEFAULT_VALUE = -2;
    private static String mFilePath;
    private FocusUI mFocusUI;
    private Toast mToast;
    private boolean mCanTakePicture = true;
    private String mReason;
    private File mPictureFile;
    private byte[] picData;
    private CameraControllerManager.CameraState cameraState;
    private CameraControllerManager.CommandEvent commandEvent;
    private boolean picTaked;
    private long mCaptureTime;
    private int mOrientation;
    private int mZoomProgress;
    private boolean start_preview_failed;
    private OrientationEventListener mOrientationListener;
    private FocusController mFocusManager;
    PictureCallback picturecallbck;

    public CameraControllerManager(Activity mContext, Handler mHandler, SurfaceHolder mSurfaceHolder, Resources res) {
        super(mContext, mHandler, mSurfaceHolder);
        this.cameraState = CameraControllerManager.CameraState.IDLE;
        this.commandEvent = CameraControllerManager.CommandEvent.FIRST_IN_FOCUS;
        this.picTaked = false;
        this.start_preview_failed = false;
        this.picturecallbck = new PictureCallback() {
            public void onPictureTaken(byte[] data, Camera camera) {
                CameraControllerManager.this.picData = data;
                CameraControllerManager.this.handlePicData();
            }
        };
        this.mPhotoModule = new PhotoModule();
        this.mPhotoModule.init();
        this.mFocusUI = new FocusUI(mContext, res);
        this.mFocusManager = new FocusController(mContext, this.mPhotoModule, this.mFocusUI, this, mHandler);
    }

    public Camera openCameraAndSetParameters() {
        if (!this.checkCameraHardware(this.mContext)) {
            return null;
        } else {
            if (this.mCamera == null) {
                try {
                    this.mCamera = this.mPhotoModule.openCamera();
                } catch (Exception var3) {
                    var3.printStackTrace();
                    return null;
                }

                if (this.mCamera != null) {
                    try {
                        this.setCommenParametersFirstTime();
                    } catch (Exception var2) {
                        var2.printStackTrace();
                        return null;
                    }
                }
            }

            return this.mCamera;
        }
    }

    public void restartPreview(Camera camera, SurfaceHolder surfaceholder) {
        if (camera != null) {
            this.setStartPreview(camera, surfaceholder);
        }

    }

    private boolean checkCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature("android.hardware.camera");
    }

    public void stopAndReleaseCamera() {
        try {
            if (this.mCamera != null) {
                this.mCamera.setPreviewCallback((PreviewCallback)null);
                if (this.mPreviewing) {
                    this.stopPreview();
                }

                if (this.mPhotoModule != null) {
                    this.mPhotoModule.release();
                }

                this.mCamera = null;
            }
        } catch (Exception var2) {
            if (this.mPhotoModule != null) {
                this.mPhotoModule.releaseCameraHolder();
            }

            var2.printStackTrace();
        }

    }

    public void setStartPreview(Camera camera, SurfaceHolder holder) {
        if (camera != null && holder != null) {
            this.mPhotoModule.setPreviewDisplay(holder);
            int mOrientationDegree = this.getPreviewDegree(this.mContext);
            if (ApiChecker.AT_LEAST_11) {
                this.mPhotoModule.setDisplayOrientation(mOrientationDegree);
            } else {
                this.setMyDisplayOrientation(camera, mOrientationDegree);
            }

            this.startPreview();
        }

    }

    public void startPreview() {
        if (this.mPhotoModule != null) {
            try {
                if (this.mPreviewing) {
                    this.stopPreview();
                }

                this.mPhotoModule.onStartPreview();
                this.mPreviewing = true;
            } catch (Exception var2) {
                var2.printStackTrace();
                this.start_preview_failed = true;
            }
        }

    }

    public boolean isStart_preview_failed() {
        return this.start_preview_failed;
    }

    public void stopPreview() {
        if (this.mPhotoModule != null) {
            this.mPhotoModule.onStopPreview();
            this.mPreviewing = false;
        }

    }

    public void setPreviewDisplay(SurfaceHolder holder) {
        this.mPhotoModule.setPreviewDisplay(holder);
    }

    public int getPreviewDegree(Activity activity) {
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degree = 0;
        switch(rotation) {
            case 0:
                degree = 90;
                break;
            case 1:
                degree = 0;
                break;
            case 2:
                degree = 270;
                break;
            case 3:
                degree = 180;
        }

        return degree;
    }

    protected void setMyDisplayOrientation(Camera camera, int angle) {
        try {
            Method downPolymorphic = camera.getClass().getMethod("setDisplayOrientation", Integer.TYPE);
            if (downPolymorphic != null) {
                downPolymorphic.invoke(camera, angle);
            }
        } catch (Exception var5) {
            var5.printStackTrace();
        }

    }

    private boolean isSupportContinuousFocus() {
        if (ApiChecker.AT_LEAST_14 && this.mParameters != null) {
            boolean isSupportContinuousFocus = this.mParameters.getSupportedFocusModes().contains("continuous-picture");
            return ApiChecker.HAS_AUTO_FOCUS_MOVE_CALLBACK && isSupportContinuousFocus;
        } else {
            return false;
        }
    }

    public boolean isSupportAutoFocus() {
        return this.mParameters != null ? this.mParameters.getSupportedFocusModes().contains("auto") : false;
    }

    @TargetApi(9)
    public void setCommenParametersFirstTime() {
        this.mParameters = this.getCurrentParameters();
        this.mParameters.setPictureFormat(256);
        this.mParameters.setJpegQuality(100);
        List<String> focusModes = this.mParameters.getSupportedFocusModes();
        if (focusModes != null && focusModes.size() > 0) {
            if (this.isSupportContinuousFocus()) {
                this.mSupportContinuousFocus = true;
            }

            if (this.isSupportAutoFocus()) {
                this.mSupportAutoFocus = true;
            }
        }

        List<Size> supportPreviewSize = this.mParameters.getSupportedPreviewSizes();
        int width = 0;
        int height = 0;
        Iterator var6 = supportPreviewSize.iterator();

        while(var6.hasNext()) {
            Size previewsize = (Size)var6.next();
            if (previewsize.width >= width && previewsize.width < 1281 && previewsize.height < 1281) {
                width = previewsize.width;
                height = previewsize.height;
            }
        }

        try {
            this.mParameters.setPreviewSize(width, height);
        } catch (Exception var11) {
            var11.printStackTrace();
        }

        List<Size> supportPicSize = this.mParameters.getSupportedPictureSizes();
        int picturesize_width = 0;
        int picturesize_height = 0;
        Iterator var9 = supportPicSize.iterator();

        while(var9.hasNext()) {
            Size size = (Size)var9.next();
            if (size.width >= picturesize_width && size.width <= CameraConst.MAX_PICTURE_SIZE && size.height <= CameraConst.MAX_PICTURE_SIZE) {
                picturesize_width = size.width;
                picturesize_height = size.height;
            }
        }

        try {
            this.mParameters.setPictureSize(picturesize_width, picturesize_height);
        } catch (Exception var10) {
            var10.printStackTrace();
        }

        this.trySetParameters(this.mParameters);
    }

    public void trySetParameters(Parameters mParameters) {
        if (!this.picTaked) {
            try {
                this.mPhotoModule.setParameters(mParameters);
            } catch (Exception var3) {
                var3.printStackTrace();
            }

        }
    }

    public void capture() {
        if (!this.mCanTakePicture) {
            this.showToast(this.mReason);
        } else {
            this.hideFocusView();
            if (!this.picTaked && this.commandEvent != CameraControllerManager.CommandEvent.CLICK_TAKE_PIC) {
                if (this.cameraState != CameraControllerManager.CameraState.TAKING_PICTURE) {
                    this.commandEvent = CameraControllerManager.CommandEvent.CLICK_TAKE_PIC;
                    if (this.cameraState == CameraControllerManager.CameraState.IDLE) {
                        if (this.isSupportAutoFocus() && System.currentTimeMillis() - this.mFocusManager.getFocusEndTime() > 1000L) {
                            this.executeAutoFocus();
                        } else {
                            this.cameraState = CameraControllerManager.CameraState.TAKING_PICTURE;
                            this.takePicture();
                        }
                    } else if (this.cameraState == CameraControllerManager.CameraState.AUTO_FOCUSING) {
                        return;
                    }

                }
            }
        }
    }

    public void returnResult() {
        Intent intent = new Intent();
        if (this.mPictureFile != null) {
            if (!TextUtils.isEmpty(mFilePath) && CameraConst.IS_PICTURE_COMPRESSED) {
                PhotoUtil.compresImageByBitmap(mFilePath, CameraConst.PICTURE_QUALITY, CameraConst.MAX_PICTURE_COMPRESS_SIZE_VALUE);
            }

            intent.setData(Uri.fromFile(this.mPictureFile));
        }

        this.mContext.setResult(-1, intent);
        this.mContext.finish();
    }

    public void deletePicFile() {
        if (this.mPictureFile != null) {
            this.mPictureFile.delete();
        }

    }

    public String getPicFilePath() {
        return mFilePath != null ? mFilePath : null;
    }

    public File getPicFile() {
        return this.mPictureFile != null ? this.mPictureFile : null;
    }

    public boolean isPreviewing() {
        return this.mPreviewing;
    }

    public Parameters getCurrentParameters() {
        try {
            if (this.mParameters == null) {
                return this.mPhotoModule.getParameters();
            }
        } catch (Exception var2) {
            var2.printStackTrace();
        }

        return this.mParameters;
    }

    @SuppressLint({"SimpleDateFormat"})
    private File getOutputMediaFile(int type) {
        String mediaStorageStr = this.getSDPath() + File.separator + CameraConst.FOLDER_NAME + File.separator;
        File dir = new File(mediaStorageStr);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File mediaStorageDir = new File(mediaStorageStr);
        if (mediaStorageStr.startsWith("G", 1)) {
            return null;
        } else if (type == 1) {
            File mediaFile = new File(mediaStorageDir.getPath() + "/" + System.currentTimeMillis() + ".jpg");
            mFilePath = mediaFile.toString();
            return mediaFile;
        } else {
            return null;
        }
    }

    public String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals("mounted");
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();
            return sdDir.toString();
        } else {
            return "";
        }
    }

    private void handlePicData() {
        this.mPictureFile = this.getOutputMediaFile(1);
        if (this.mPictureFile != null) {
            this.setPicOritation(this.mPictureFile.toString(), this.mOrientation);
        }

        if (this.mPictureFile == null) {
            this.showToast("请检查您的SD卡");
            if (this.mCamera != null) {
                this.mHandler.sendEmptyMessage(2);
                this.picTaked = false;
            }

        } else {
            try {
                FileOutputStream fos = new FileOutputStream(this.mPictureFile);
                if (fos != null && this.picData != null) {
                    fos.write(this.picData);
                    fos.close();
                } else {
                    this.showToast("拍照失败，请重试");
                    this.mHandler.sendEmptyMessage(2);
                }

                Message delayMsg = this.mHandler.obtainMessage(1);
                this.mHandler.sendMessage(delayMsg);
            } catch (Exception var3) {
                this.mHandler.post(new Runnable() {
                    public void run() {
                        CameraControllerManager.this.showToast("手机存储空间不足");
                    }
                });
                if (this.mCamera != null) {
                    this.mHandler.sendEmptyMessage(2);
                }

                this.setCommandEvent(CameraControllerManager.CommandEvent.IDLE);
                this.setCameraState(CameraControllerManager.CameraState.IDLE);
                this.picTaked = false;
            }

        }
    }

    public void takePicture() {
        if (!this.picTaked) {
            this.mParameters = this.getCurrentParameters();
            this.mParameters.setRotation(this.mOrientation);
            this.trySetParameters(this.mParameters);
            this.setZoom();
            this.getOriontationParams();
            this.picTaked = true;
            this.mPhotoModule.onCapture((ShutterCallback)null, (PictureCallback)null, this.picturecallbck);
        }
    }

    private void setZoom() {
        if (!this.picTaked) {
            this.mParameters = this.getCurrentParameters();
            if (this.mParameters.isZoomSupported()) {
                if (this.mZoomProgress < 0) {
                    this.mZoomProgress = 0;
                }

                this.mParameters.setZoom(this.mZoomProgress);
                this.trySetParameters(this.mParameters);
            }

        }
    }

    public int getMaxCameraZoom() {
        int maxZoom = 0;
        if (this.picTaked) {
            return 0;
        } else {
            this.mParameters = this.getCurrentParameters();
            if (this.mParameters.isZoomSupported()) {
                maxZoom = this.mParameters.getMaxZoom();
            }

            return maxZoom;
        }
    }

    private void getOriontationParams() {
        this.mCaptureTime = System.currentTimeMillis();
        boolean isInValidTime = this.mCaptureTime - this.mDerectionTime < 500L;
        if (isInValidTime) {
            this.mXCaptureDirection = this.mXDirection;
            this.mYCaptureDirection = this.mYDirection;
            this.mZCaptureDirection = this.mZDirection;
        } else {
            this.mXCaptureDirection = -1;
            this.mYCaptureDirection = -1;
            this.mZCaptureDirection = -1;
        }

    }

    public void startOrientationEventListener() {
        this.mOrientationListener = new OrientationEventListener(this.mContext) {
            public void onOrientationChanged(int rotation) {
                if ((rotation <= 325 || rotation >= 360) && (rotation <= 0 || rotation >= 45)) {
                    if (rotation > 45 && rotation < 135) {
                        CameraControllerManager.this.mOrientation = 180;
                    } else if (rotation > 135 && rotation < 225) {
                        CameraControllerManager.this.mOrientation = 270;
                    } else if (rotation > 225 && rotation < 315) {
                        CameraControllerManager.this.mOrientation = 0;
                    }
                } else {
                    CameraControllerManager.this.mOrientation = 90;
                }

            }
        };
    }

    public void enableOrientationEventListener() {
        if (this.mOrientationListener != null) {
            this.mOrientationListener.enable();
        }

    }

    public void disableOrientationEventListener() {
        if (this.mOrientationListener != null) {
            this.mOrientationListener.disable();
        }

    }

    public boolean isTouchTakingPic() {
        SharedPreferences sharedPre = this.mContext.getSharedPreferences("SharedPreferences", 0);
        boolean touchTake = sharedPre.getBoolean("touchTake", false);
        return touchTake;
    }

    public boolean isVolumeKeyTakePicture() {
        SharedPreferences sharedPre = this.mContext.getSharedPreferences("SharedPreferences", 0);
        boolean touchTake = sharedPre.getBoolean("volumeKeyTakePic", false);
        return touchTake;
    }

    public CameraControllerManager.CommandEvent getCommandEvent() {
        return this.commandEvent;
    }

    public void setCommandEvent(CameraControllerManager.CommandEvent command) {
        this.commandEvent = command;
    }

    public CameraControllerManager.CameraState getCameraState() {
        return this.cameraState;
    }

    public void setCameraState(CameraControllerManager.CameraState state) {
        this.cameraState = state;
    }

    public void setPicTaked(boolean istaked) {
        this.picTaked = istaked;
    }

    public boolean getPicTaked() {
        return this.picTaked;
    }

    public void setDisplayOrientation(int orientation) {
        if (this.mPhotoModule != null) {
            this.mPhotoModule.setDisplayOrientation(orientation);
        }

    }

    public void setCameraZoom(int zoom) {
        this.mZoomProgress = zoom;
        this.setZoom();
    }

    public void executeAutoFocus() {
        try {
            this.executeAutoFocusStrategy((MotionEvent)null);
        } catch (Exception var2) {
            var2.printStackTrace();
        }

    }

    public void cancelAutoFocusStrategy() {
        try {
            if (this.mFocusManager != null) {
                this.mFocusManager.cancelFocus();
            }
        } catch (Exception var2) {
            var2.printStackTrace();
        }

    }

    public void setMovingAutoFocusStrategy() {
        if (this.mCamera != null) {
            if (this.mFocusManager != null) {
                this.mFocusManager.getFocusStrategy(1);
                this.mFocusManager.operateFocus();
            }

        }
    }

    public void executeAutoFocusStrategy(MotionEvent event) {
        try {
            if (this.mCamera == null) {
                return;
            }

            if (this.mFocusManager != null) {
                this.mFocusManager.getFocusStrategy(3);
                this.mFocusManager.executeFocus(event);
            }
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }

    public void restoreContinuousFocus() {
        if (this.getCameraState() == CameraControllerManager.CameraState.IDLE) {
            this.mHandler.sendEmptyMessageDelayed(3, 2000L);
        }

    }

    public void hideFocusView() {
        if (this.mFocusUI != null) {
            this.mFocusUI.clearFocus();
        }

    }

    public void startAndShowFocusView(int x, int y) {
        if (this.mFocusUI != null) {
            this.mFocusUI.onFocusStarted(x, y);
        }

    }

    public void setCameraFlash(boolean on) {
        if (!this.picTaked) {
            this.mParameters = this.getCurrentParameters();
            List<String> flashModes = this.mParameters.getSupportedFlashModes();
            if (flashModes != null) {
                if (on) {
                    this.mParameters.setFlashMode("on");
                } else {
                    this.mParameters.setFlashMode("off");
                }

                this.trySetParameters(this.mParameters);
            }
        }
    }

    public boolean getIsSupportContinuousFocus() {
        return this.mSupportContinuousFocus;
    }

    public void showToast(String msg) {
        if (this.mToast == null) {
            this.mToast = Toast.makeText(this.mContext, msg, 0);
        } else {
            this.mToast.setText(msg);
            this.mToast.setDuration(0);
        }

        this.mToast.show();
    }

    public boolean setPicOritation(String fp, int degree) {
        try {
            ExifInterface exifInterface = new ExifInterface(fp);
            exifInterface.setAttribute("Orientation", String.valueOf(degree));
            exifInterface.saveAttributes();
            return true;
        }
//        catch (IOException var4) {
//            var4.printStackTrace();
//            return false;
//        }
        catch (Exception var4) {
            var4.printStackTrace();
            return false;
        }
    }

    static enum CameraState {
        IDLE,
        AUTO_FOCUSING,
        TAKING_PICTURE;

        private CameraState() {
        }
    }

    static enum CommandEvent {
        IDLE,
        FIRST_IN_FOCUS,
        TOUCH_SCREEN,
        CLICK_TAKE_PIC,
        SENSOR_AUTO_FOCUS;

        private CommandEvent() {
        }
    }
}
