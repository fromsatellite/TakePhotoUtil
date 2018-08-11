//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.satellite.cameralib;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGestureListener;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.satellite.cameralib.CameraControllerManager.CameraState;
import com.satellite.cameralib.CameraControllerManager.CommandEvent;
import com.satellite.cameralib.CameraSettingsMenu.OnSettingChangeListener;
import com.satellite.cameralib.photocompress.ImageUtil;

import java.util.List;

public class CameraActivity extends Activity implements OnClickListener, Callback, OnGestureListener {
    private static final String TAG = "gxd_camera";
    private Button mRetakeBtn;
    private Button mUsePicBtn;
    private ImageView ivSettingsMenu;
    private ImageView mShowCameraPic;
    private TextView cancleCameraBtn;
    private TextView captureBtn;
    private RelativeLayout cancleCameraBtnLayout;
    private RelativeLayout mUsePicPreviewLayout;
    private VerticalSeekBar mZoomSeekBar;
    private SurfaceView surfaceSv;
    private Context mContext;
    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;
    private int mZoomProgress;
    public boolean mIsNeedTracePoint = false;
    private CameraSettingsMenu cameraSettingsMenu;
    private Toast mToast;
    public static final String MSG_SHOOTED_TYPE = "mShootedTypeFlag";
    public static final long GXDTAOJIN_LOCATION_VALID_TIME = 500L;
    public View CameraFocusView;
    public int XTouch;
    public int YTouch;
    private boolean mPausing;
    private int mMaxZoom;
    private boolean isVolumeTakePicture;
    private GestureDetector mGesture = null;
    private CameraActivity.SurfaceViewScaleGestureListener mScaleGestureListener = null;
    private ScaleGestureDetector mScaleDetector = null;
    private final float VOLUM_ZOOM_RATIO = 0.25F;
    private final float ZERO = 1.0F;
    private CameraControllerManager mCameraController = null;
    private boolean isOpenCameraError = false;
    private boolean isEverStartCamera = false;
    private Resources mResouse = null;
    public static int RES_ID_USEPICTURE;
    public static final int MSG_UPDATE_UI = 1;
    public static final int MSG_UPDATE_CAMERA_UI = 2;
    public static final int MSG_SET_MOVING_FOCUS_STRATEGY = 3;
    public static final int MSG_HIDE_FOCUS_UI = 4;
    public static final int MSG_RETAKE_PICTURE = 5;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case 1:
                    CameraActivity.this.attachAndShowCameraPic();
                    break;
                case 2:
                    CameraActivity.this.updateCameraUI();
                    break;
                case 3:
                    if (!CameraActivity.this.mPausing) {
                        CameraActivity.this.mCameraController.hideFocusView();
                        CameraActivity.this.mCameraController.setMovingAutoFocusStrategy();
                    }
                    break;
                case 4:
                    if (!CameraActivity.this.mPausing) {
                        CameraActivity.this.mCameraController.hideFocusView();
                    }
                    break;
                case 5:
                    if (!CameraActivity.this.mPausing) {
                        CameraActivity.this.mUsePicPreviewLayout.setVisibility(8);
                        if (CameraActivity.this.mCamera != null) {
                            CameraActivity.this.updateCameraUI();
                        }

                        CameraActivity.this.mCameraController.setPicTaked(false);
                        CameraActivity.this.mCameraController.setCommandEvent(CommandEvent.IDLE);
                        CameraActivity.this.mCameraController.setCameraState(CameraState.IDLE);
                        CameraActivity.this.initCameraState(true);
                    }
            }

        }
    };

    public CameraActivity() {
    }

    public void surfaceCreated(SurfaceHolder holder) {
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (this.mSurfaceHolder.getSurface() != null) {
            try {
                this.mSurfaceHolder = holder;
                if (this.mCamera == null) {
                    return;
                }

                if (this.mPausing || this.isFinishing()) {
                    return;
                }

                if (this.mCameraController.isPreviewing() && holder.isCreating()) {
                    this.mCameraController.setStartPreview(this.mCamera, holder);
                } else {
                    this.mCameraController.restartPreview(this.mCamera, this.mSurfaceHolder);
                }

                if (this.mCameraController.isStart_preview_failed()) {
                    this.showToast("预览失败，请稍后重试");
                    this.finish();
                    return;
                }

                if (this.surfaceSv != null) {
                    this.surfaceSv.setEnabled(true);
                }

                if (this.mZoomSeekBar != null && this.captureBtn != null && this.cancleCameraBtn != null) {
                    this.mZoomSeekBar.setVisibility(0);
                    this.captureBtn.setVisibility(0);
                    int camera_cancle_btn = this.mResouse.getIdentifier("camera_cancle_btn", "drawable", this.getPackageName());
                    this.cancleCameraBtn.setBackgroundResource(camera_cancle_btn);
                }

                this.initCameraState(false);
            } catch (Exception var6) {
                var6.printStackTrace();
            }

        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        holder = null;
        this.surfaceSv = null;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this;
        this.requestWindowFeature(1);
        this.getWindow().setFlags(1024, 1024);
        this.mResouse = this.getResources();
        int activity_camera_id = this.mResouse.getIdentifier("activity_camera", "layout", this.getPackageName());
        this.setContentView(activity_camera_id);
        this.initView();
        this.initSurfaceHolder();
        this.setDensity();
        this.mCameraController = new CameraControllerManager(this, this.mHandler, this.mSurfaceHolder, this.mResouse);
        this.mCameraController.startOrientationEventListener();
        this.mGesture = new GestureDetector(this, new CameraActivity.SurfaceViewGestureListener());
        this.mScaleGestureListener = new CameraActivity.SurfaceViewScaleGestureListener();
        this.mScaleDetector = new ScaleGestureDetector(this, this.mScaleGestureListener);
        this.isVolumeTakePicture = this.mCameraController.isVolumeKeyTakePicture();
    }

    public void onResume() {
        super.onResume();
        this.mPausing = false;
        if (this.mCamera == null) {
            this.mCamera = this.mCameraController.openCameraAndSetParameters();
        }

        if (this.mCamera == null) {
            this.isOpenCameraError = true;
            this.showToast("相机故障，请稍后重试");
            this.finish();
        } else {
            if (this.cameraSettingsMenu.getFlashlightOn() == 1) {
                this.mCameraController.setCameraFlash(true);
            } else if (this.cameraSettingsMenu.getFlashlightOn() == 0) {
                this.mCameraController.setCameraFlash(false);
            }

            if (this.isEverStartCamera) {
                this.updateCameraUI();
            }

            this.mMaxZoom = this.mCameraController.getMaxCameraZoom();
            this.mZoomSeekBar.setMax(this.mMaxZoom);
            this.mCameraController.enableOrientationEventListener();
        }
    }

    public void onPause() {
        super.onPause();
        if (this.isOpenCameraError) {
            this.mCameraController.disableOrientationEventListener();
        } else {
            this.mPausing = true;
            this.removeAllMessageInHandlerQueue();
            this.mCameraController.cancelAutoFocusStrategy();
            this.mCameraController.stopAndReleaseCamera();
            this.mCameraController.disableOrientationEventListener();
            this.mCamera = null;
            this.isEverStartCamera = true;
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (this.isOpenCameraError) {
            this.isOpenCameraError = false;
            this.mCameraController.stopAndReleaseCamera();
        } else {
            this.mCameraController.stopAndReleaseCamera();
            if (this.mShowCameraPic != null) {
                this.mShowCameraPic.setImageBitmap((Bitmap)null);
            }

            this.mHandler = null;
            this.mResouse = null;
            this.isEverStartCamera = false;
            this.resetCameraConst();
        }
    }

    private void touchScreen(MotionEvent event) {
        if (this.mCamera != null) {
            this.mHandler.removeMessages(3);
            this.mHandler.removeMessages(4);
            this.mHandler.postDelayed(new Runnable() {
                public void run() {
                    CameraActivity.this.mCameraController.startAndShowFocusView(CameraActivity.this.XTouch, CameraActivity.this.YTouch);
                }
            }, 0L);
            if (this.mCameraController.getCameraState() == CameraState.AUTO_FOCUSING) {
                this.mCameraController.cancelAutoFocusStrategy();
            }

            if (this.mCameraController.getCameraState() != CameraState.TAKING_PICTURE) {
                this.mCameraController.executeAutoFocusStrategy(event);
            }
        }
    }

    private void clickTakeBtn() {
        if (!CameraConst.IS_HAS_CAPTURE_PERMISSION) {
            this.showToast(CameraConst.PERMISSION_REASON);
        } else {
            if (CameraConst.CAPTURE_LISTENER != null) {
                CameraConst.CAPTURE_LISTENER.onCapture();
            }

            this.mHandler.removeMessages(3);
            this.mCameraController.capture();
        }
    }

    private void initView() {
        int camera_cancle_btn = this.mResouse.getIdentifier("camera_cancle_btn", "id", this.getPackageName());
        int camera_ok_btn = this.mResouse.getIdentifier("camera_ok_btn", "id", this.getPackageName());
        int id_switch_camera_btn = this.mResouse.getIdentifier("id_switch_camera_btn", "id", this.getPackageName());
        int id_capture_btn = this.mResouse.getIdentifier("id_capture_btn", "id", this.getPackageName());
        int id_cancle_btn_layout = this.mResouse.getIdentifier("id_cancle_btn_layout", "id", this.getPackageName());
        int ivSettingsMenu_id = this.mResouse.getIdentifier("ivSettingsMenu", "id", this.getPackageName());
        int usepic_layout = this.mResouse.getIdentifier("usepic_layout", "id", this.getPackageName());
        int camera_pic_activity = this.mResouse.getIdentifier("camera_pic_activity", "id", this.getPackageName());
        int zoom_seekbar_def = this.mResouse.getIdentifier("zoom_seekbar_def", "id", this.getPackageName());
        int id_area_sv = this.mResouse.getIdentifier("id_area_sv", "id", this.getPackageName());
        RES_ID_USEPICTURE = usepic_layout;
        this.mRetakeBtn = (Button)this.findViewById(camera_cancle_btn);
        this.mUsePicBtn = (Button)this.findViewById(camera_ok_btn);
        this.cancleCameraBtn = (TextView)this.findViewById(id_switch_camera_btn);
        this.captureBtn = (Button)this.findViewById(id_capture_btn);
        this.cancleCameraBtnLayout = (RelativeLayout)this.findViewById(id_cancle_btn_layout);
        this.ivSettingsMenu = (ImageView)this.findViewById(ivSettingsMenu_id);
        this.mUsePicPreviewLayout = (RelativeLayout)this.findViewById(usepic_layout);
        this.mShowCameraPic = (ImageView)this.mUsePicPreviewLayout.findViewById(camera_pic_activity);
        this.mZoomSeekBar = (VerticalSeekBar)this.findViewById(zoom_seekbar_def);
        this.mUsePicPreviewLayout.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            }
        });
        this.cameraSettingsMenu = new CameraSettingsMenu(this, new OnSettingChangeListener() {
            public void onSettingChange(int settingType, boolean on) {
                if (settingType == 1) {
                    CameraActivity.this.mCameraController.setCameraFlash(on);
                } else if (settingType != 2 && settingType == 3) {
                    CameraActivity.this.isVolumeTakePicture = on;
                }

            }
        }, this.mCameraController, this.mResouse);
        this.ivSettingsMenu.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                boolean supportFlashlight = true;
                List<String> flashModes = CameraActivity.this.mCameraController.getCurrentParameters().getSupportedFlashModes();
                if (flashModes == null) {
                    supportFlashlight = false;
                }

                CameraActivity.this.cameraSettingsMenu.showMenu(v, supportFlashlight, CameraActivity.this.mCameraController.isSupportAutoFocus());
            }
        });
        this.surfaceSv = (SurfaceView)this.findViewById(id_area_sv);
        this.surfaceSv.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (CameraActivity.this.mCameraController.getCurrentParameters().isZoomSupported() && ApiChecker.AT_LEAST_8) {
                    CameraActivity.this.mScaleDetector.onTouchEvent(event);
                }

                CameraActivity.this.mGesture.onTouchEvent(event);
                return true;
            }
        });
        SurfaceHolder surfaceHolder = this.surfaceSv.getHolder();
        if (!ApiChecker.AT_LEAST_11) {
            surfaceHolder.setType(3);
        }

        this.mZoomSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            public void onStopTrackingTouch(SeekBar paramSeekBar) {
            }

            public void onStartTrackingTouch(SeekBar paramSeekBar) {
            }

            public void onProgressChanged(SeekBar paramSeekBar, int paramInt, boolean paramBoolean) {
                if (CameraActivity.this.mCamera != null) {
                    CameraActivity.this.mZoomProgress = paramSeekBar.getProgress();
                    if (CameraActivity.this.mZoomProgress >= 0 && CameraActivity.this.mZoomProgress < CameraActivity.this.mMaxZoom - 1) {
                        CameraActivity.this.mCameraController.setCameraZoom(CameraActivity.this.mZoomProgress);
                    } else {
                        CameraActivity.this.mCameraController.setCameraZoom(CameraActivity.this.mMaxZoom - 1);
                    }

                }
            }
        });
        this.captureBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                CameraActivity.this.clickTakeBtn();
            }
        });
        this.cancleCameraBtnLayout.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                CameraActivity.this.mCameraController.hideFocusView();
                CameraActivity.this.mCameraController.deletePicFile();
                CameraActivity.this.finish();
            }
        });
        this.mRetakeBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                CameraActivity.this.mUsePicPreviewLayout.setVisibility(8);
                if (CameraActivity.this.mCamera != null) {
                    CameraActivity.this.updateCameraUI();
                }

                CameraActivity.this.mCameraController.setPicTaked(false);
                CameraActivity.this.mCameraController.setCommandEvent(CommandEvent.IDLE);
                CameraActivity.this.mCameraController.setCameraState(CameraState.IDLE);
                CameraActivity.this.initCameraState(true);
            }
        });
        this.mUsePicBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                try {
                    CameraActivity.this.mCameraController.returnResult();
                } catch (Exception var3) {
                    var3.printStackTrace();
                }

            }
        });
    }

    public void updateCameraUI() {
        if (this.mCameraController != null) {
            this.mCameraController.setStartPreview(this.mCamera, this.mSurfaceHolder);
        }

        if (this.mCameraController.isStart_preview_failed()) {
            this.showToast("预览失败，请稍后重试");
            this.finish();
        } else {
            if (this.surfaceSv != null) {
                this.surfaceSv.setEnabled(true);
            }

            this.mZoomSeekBar.setVisibility(0);
            this.captureBtn.setVisibility(0);
            int camera_cancle_btn = this.mResouse.getIdentifier("camera_cancle_btn", "drawable", this.getPackageName());
            this.cancleCameraBtn.setBackgroundResource(camera_cancle_btn);
        }
    }

    private void initSurfaceHolder() {
        this.mSurfaceHolder = this.surfaceSv.getHolder();
        this.mSurfaceHolder.setKeepScreenOn(true);
        this.mSurfaceHolder.addCallback(this);
    }

    private void removeAllMessageInHandlerQueue() {
        if (this.mHandler != null) {
            this.mHandler.removeMessages(1);
            this.mHandler.removeMessages(2);
            this.mHandler.removeMessages(3);
            this.mHandler.removeMessages(4);
            this.mHandler.removeMessages(5);
        }

    }

    private void initCameraState(boolean retake) {
        this.mCameraController.setCommandEvent(CommandEvent.FIRST_IN_FOCUS);
        this.mCameraController.setCameraState(CameraState.IDLE);
        this.mCameraController.setPicTaked(false);
        int delay = 700;
        if (retake) {
            delay = 300;
        }

        if (this.mCameraController.isSupportAutoFocus() && this.mUsePicPreviewLayout.getVisibility() != 0 && this.mCameraController.getCommandEvent() == CommandEvent.FIRST_IN_FOCUS) {
            this.mHandler.postDelayed(new Runnable() {
                public void run() {
                    if (!CameraActivity.this.mPausing && CameraActivity.this.mCameraController.getCommandEvent() == CommandEvent.FIRST_IN_FOCUS && CameraActivity.this.mCamera != null) {
                        CameraActivity.this.mCameraController.executeAutoFocus();
                    }

                }
            }, (long)delay);
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case 20:
                if (resultCode == 22) {
                    this.mCameraController.returnResult();
                }
            default:
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == 2) {
            if (this.mCamera != null) {
                this.mCameraController.setDisplayOrientation(0);
            }
        } else if (this.mCamera != null) {
            this.mCameraController.setDisplayOrientation(90);
        }

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 4) {
            this.mCameraController.deletePicFile();
        } else if (keyCode == 27 && event.getAction() == 0) {
            if (this.mUsePicPreviewLayout.getVisibility() != 0) {
                this.clickTakeBtn();
            }
        } else {
            if (keyCode == 24) {
                if (!this.isVolumeTakePicture) {
                    if (this.mCameraController.getCurrentParameters().isZoomSupported()) {
                        this.mZoomProgress = (int)((float)this.mZoomProgress + (float)this.mMaxZoom * 0.25F);
                        if (this.mZoomProgress <= 0) {
                            this.mZoomProgress = 0;
                        } else if (this.mZoomProgress >= this.mMaxZoom) {
                            this.mZoomProgress = this.mMaxZoom;
                        }

                        this.mCameraController.setCameraZoom(this.mZoomProgress);
                        this.mZoomSeekBar.setProgress(this.mZoomProgress);
                        return true;
                    }
                } else if (this.mUsePicPreviewLayout.getVisibility() != 0) {
                    this.clickTakeBtn();
                    return true;
                }

                return true;
            }

            if (keyCode == 25) {
                if (!this.isVolumeTakePicture) {
                    if (this.mCameraController.getCurrentParameters().isZoomSupported()) {
                        this.mZoomProgress = (int)((float)this.mZoomProgress - (float)this.mMaxZoom * 0.25F);
                        if (this.mZoomProgress <= 0) {
                            this.mZoomProgress = 0;
                        } else if (this.mZoomProgress >= this.mMaxZoom) {
                            this.mZoomProgress = this.mMaxZoom;
                        }

                        this.mCameraController.setCameraZoom(this.mZoomProgress);
                        this.mZoomSeekBar.setProgress(this.mZoomProgress);
                        return true;
                    }
                } else if (this.mUsePicPreviewLayout.getVisibility() != 0) {
                    this.clickTakeBtn();
                    return true;
                }

                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    public void attachAndShowCameraPic() {
        this.mUsePicPreviewLayout.setVisibility(0);
        int width_pixels = CameraConst.widthPixels;
        int height_pixels = CameraConst.heightPixels;
        int width;
        int height;
        if (width_pixels > height_pixels) {
            height = width_pixels;
            width = height_pixels;
        } else {
            width = width_pixels;
            height = height_pixels;
        }

        Bitmap bitmap = ImageUtil.getNormalBitmap(this.mCameraController.getPicFilePath(), width, height);
        if (bitmap != null) {
            this.mShowCameraPic.setImageBitmap(bitmap);
        }

    }

    public void onGestureStarted(GestureOverlayView overlay, MotionEvent event) {
    }

    public void onGesture(GestureOverlayView overlay, MotionEvent event) {
    }

    public void onGestureEnded(GestureOverlayView overlay, MotionEvent event) {
    }

    public void onGestureCancelled(GestureOverlayView overlay, MotionEvent event) {
    }

    public void onClick(View v) {
    }

    public boolean onTouchEvent(MotionEvent event) {
        event.getAction();
        return true;
    }

    public void showToast(String msg) {
        if (this.mToast == null) {
            this.mToast = Toast.makeText(this, msg, 0);
        } else {
            this.mToast.setText(msg);
            this.mToast.setDuration(0);
        }

        this.mToast.show();
    }

    public void setDensity() {
        DisplayMetrics metric = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(metric);
        CameraConst.density = metric.density;
        CameraConst.heightPixels = metric.heightPixels;
        CameraConst.widthPixels = metric.widthPixels;
    }

    public void resetCameraConst() {
        CameraConst.FOLDER_NAME = "/CameraDemo/Image/";
        CameraConst.MAX_PICTURE_SIZE = 1920;
        CameraConst.MAX_PICTURE_COMPRESS_SIZE_VALUE = 1280;
        CameraConst.PICTURE_QUALITY = 92;
        CameraConst.IS_HAS_FLASH = true;
        CameraConst.IS_HAS_TOUCH_CAPTURE = true;
        CameraConst.IS_HAS_VOLUME_ZOOM = true;
        CameraConst.IS_HAS_CAPTURE_PERMISSION = true;
        CameraConst.PERMISSION_REASON = "";
        CameraConst.CAPTURE_LISTENER = null;
        CameraConst.IS_PICTURE_COMPRESSED = true;
    }

    public interface REQUEST_CODE {
        int REQUEST_CODE_SHOW_PICTURE = 20;
        int REQUEST_CODE_NEWPOI_PAGE = 21;
        int REQUEST_CODE_RETAKE = 22;
    }

    class SurfaceViewGestureListener extends SimpleOnGestureListener {
        SurfaceViewGestureListener() {
        }

        public boolean onDown(MotionEvent event) {
            return super.onDown(event);
        }

        public boolean onSingleTapUp(MotionEvent event) {
            if (!CameraActivity.this.mCameraController.isSupportAutoFocus()) {
                return false;
            } else if (!ApiChecker.AT_LEAST_14) {
                return false;
            } else {
                CameraActivity.this.XTouch = (int)event.getX();
                CameraActivity.this.YTouch = (int)event.getY();

                try {
                    if (CameraActivity.this.mCameraController.getCommandEvent() != CommandEvent.CLICK_TAKE_PIC) {
                        CameraActivity.this.mCameraController.setCommandEvent(CommandEvent.TOUCH_SCREEN);
                        CameraActivity.this.touchScreen(event);
                    }
                } catch (Exception var3) {
                    var3.printStackTrace();
                }

                return super.onSingleTapUp(event);
            }
        }
    }

    class SurfaceViewScaleGestureListener implements OnScaleGestureListener {
        SurfaceViewScaleGestureListener() {
        }

        public boolean onScale(ScaleGestureDetector detector) {
            float scale = detector.getScaleFactor();
            if (!Float.isNaN(scale) && !Float.isInfinite(scale)) {
                if (scale < 1.0F) {
                    CameraActivity.this.mZoomProgress = CameraActivity.this.mZoomProgress - 1;
                } else {
                    CameraActivity.this.mZoomProgress = CameraActivity.this.mZoomProgress + 1;
                }

                if (CameraActivity.this.mZoomProgress <= 0) {
                    CameraActivity.this.mZoomProgress = 0;
                } else if (CameraActivity.this.mZoomProgress >= CameraActivity.this.mMaxZoom) {
                    CameraActivity.this.mZoomProgress = CameraActivity.this.mMaxZoom;
                }

                CameraActivity.this.mCameraController.setCameraZoom(CameraActivity.this.mZoomProgress);
                CameraActivity.this.mZoomSeekBar.setProgress(CameraActivity.this.mZoomProgress);
                return true;
            } else {
                return false;
            }
        }

        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        public void onScaleEnd(ScaleGestureDetector detector) {
            if (!CameraActivity.this.mPausing && CameraActivity.this.mCameraController.getIsSupportContinuousFocus() && CameraActivity.this.mCameraController.getCameraState() == CameraState.IDLE) {
                CameraActivity.this.mHandler.removeMessages(3);
                CameraActivity.this.mHandler.sendEmptyMessageDelayed(3, 700L);
            }

        }
    }
}
