//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.satellite.cameralib;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.PopupWindow;
import android.widget.TextView;

public class CameraSettingsMenu implements OnCheckedChangeListener, OnTouchListener {
    private PopupWindow popupCameraSettings;
    private Context context;
    private CheckBox cbFlashlight;
    private CheckBox cbTouchTake;
    private CheckBox cbVolumeKeyFunction;
    private TextView textView;
    private View btChangePicSize;
    private View layoutFlashlight;
    private View viewDivider;
    private View layoutTouchCapture;
    private View viewDivider2;
    private View layoutVolumeKey;
    public static final String PREF_KEY_FLASHLIGHT = "flushType";
    public static final String PREF_KEY_TOUCH_TAKE = "touchTake";
    public static final String PREF_KEY_DEVELOP_PIC_QUALITY = "developPicQuality";
    public static final String PREF_KEY_IS_VOLUMEKEY_TAKEPIC = "volumeKeyTakePic";
    private int flashlightOn;
    private boolean touchTake;
    private boolean isDevelopPicQuality;
    private boolean isVolumeKeyTakePic;
    private CameraSettingsMenu.OnSettingChangeListener listener;
    private TextView mTextViewPictureSize;
    private Resources resourse;
    public static final int SETTING_FLASHLIGHT = 1;
    public static final int SETTING_DEVELOP_PICTURE_QUALITY = 2;
    public static final int SETTING_VOLUME_KEY_TAKE_PICTURE = 3;
    public static final int SETTING_CHANGE_PICTURE_SIZE = 4;

    public CameraSettingsMenu(Context context) {
        this.context = context;
        this.initPopupCameraSettings();
    }

    public CameraSettingsMenu(Context context, CameraSettingsMenu.OnSettingChangeListener listener, AbstractCameraControllerManager cameracontroller, Resources res) {
        this.context = context;
        this.listener = listener;
        this.resourse = res;
        this.initPopupCameraSettings();
    }

    private void initPopupCameraSettings() {
        if (this.popupCameraSettings == null && this.resourse != null) {
            int camera_settings_layout = this.resourse.getIdentifier("camera_settings", "layout", this.context.getPackageName());
            int cbFlashlight_id = this.resourse.getIdentifier("cbFlashlight", "id", this.context.getPackageName());
            int cbTouchTake_id = this.resourse.getIdentifier("cbTouchTake", "id", this.context.getPackageName());
            int cbvolumekeyfunction_id = this.resourse.getIdentifier("cbvolumekeyfunction", "id", this.context.getPackageName());
            int volumekeytext_id = this.resourse.getIdentifier("volumekeytext", "id", this.context.getPackageName());
            int layoutFlashlight_id = this.resourse.getIdentifier("layoutFlashlight", "id", this.context.getPackageName());
            int viewDivider_id = this.resourse.getIdentifier("viewDivider", "id", this.context.getPackageName());
            int layoutTouchCapture_id = this.resourse.getIdentifier("layoutTouchCapture", "id", this.context.getPackageName());
            int viewDivider2_id = this.resourse.getIdentifier("viewDivider3", "id", this.context.getPackageName());
            int layoutVolumeKey_id = this.resourse.getIdentifier("layoutVolumeKey", "id", this.context.getPackageName());
            View customView = View.inflate(this.context, camera_settings_layout, (ViewGroup)null);
            this.cbFlashlight = (CheckBox)customView.findViewById(cbFlashlight_id);
            this.cbTouchTake = (CheckBox)customView.findViewById(cbTouchTake_id);
            this.cbVolumeKeyFunction = (CheckBox)customView.findViewById(cbvolumekeyfunction_id);
            this.textView = (TextView)customView.findViewById(volumekeytext_id);
            this.layoutFlashlight = customView.findViewById(layoutFlashlight_id);
            this.viewDivider = customView.findViewById(viewDivider_id);
            this.layoutTouchCapture = customView.findViewById(layoutTouchCapture_id);
            this.viewDivider2 = customView.findViewById(viewDivider2_id);
            this.layoutVolumeKey = customView.findViewById(layoutVolumeKey_id);
            SharedPreferences sharedPre = this.context.getSharedPreferences("SharedPreferences", 0);
            this.flashlightOn = sharedPre.getInt("flushType", 0);
            this.touchTake = sharedPre.getBoolean("touchTake", false);
            this.isDevelopPicQuality = sharedPre.getBoolean("developPicQuality", false);
            this.isVolumeKeyTakePic = sharedPre.getBoolean("volumeKeyTakePic", false);
            if (this.flashlightOn == 0) {
                this.cbFlashlight.setChecked(false);
            } else if (this.flashlightOn == 1) {
                this.cbFlashlight.setChecked(true);
            }

            if (this.touchTake) {
                this.cbTouchTake.setChecked(true);
            } else {
                this.cbTouchTake.setChecked(false);
            }

            if (this.isVolumeKeyTakePic) {
                this.textView.setText("音量键功能(拍照)");
                this.cbVolumeKeyFunction.setChecked(true);
            } else {
                this.textView.setText("音量键功能(焦距)");
                this.cbVolumeKeyFunction.setChecked(false);
            }

            this.cbFlashlight.setOnCheckedChangeListener(this);
            this.cbTouchTake.setOnCheckedChangeListener(this);
            this.cbVolumeKeyFunction.setOnCheckedChangeListener(this);
            this.popupCameraSettings = new PopupWindow(customView, -1, -2);
            this.popupCameraSettings.setBackgroundDrawable(new BitmapDrawable());
            this.popupCameraSettings.setOutsideTouchable(true);
            this.popupCameraSettings.setFocusable(true);
        }

    }

    public void showMenu(View anchor, boolean supportFlashlight, boolean supportAutoFocus) {
        if (!supportFlashlight) {
            this.cbFlashlight.setEnabled(false);
        }

        if (!supportAutoFocus) {
            this.cbTouchTake.setEnabled(false);
        }

        if (!CameraConst.IS_HAS_FLASH && this.layoutFlashlight != null && this.viewDivider != null) {
            this.layoutFlashlight.setVisibility(8);
            this.viewDivider.setVisibility(8);
        }

        if (!CameraConst.IS_HAS_TOUCH_CAPTURE && this.layoutTouchCapture != null && this.viewDivider2 != null) {
            this.layoutTouchCapture.setVisibility(8);
            this.viewDivider2.setVisibility(8);
        }

        if (!CameraConst.IS_HAS_VOLUME_ZOOM && this.layoutVolumeKey != null) {
            this.layoutVolumeKey.setVisibility(8);
        }

        this.popupCameraSettings.showAsDropDown(anchor);
    }

    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == this.cbFlashlight) {
            this.setFlashlightOn(isChecked ? 1 : 0);
            if (this.listener != null) {
                this.listener.onSettingChange(1, isChecked);
            }
        } else if (buttonView == this.cbTouchTake) {
            this.setTouchTake(isChecked);
        } else if (buttonView == this.cbVolumeKeyFunction) {
            if (isChecked) {
                this.textView.setText("音量键功能(拍照)");
            } else {
                this.textView.setText("音量键功能(焦距)");
            }

            this.setVolumeKeyTakePic(isChecked);
            if (this.listener != null) {
                this.listener.onSettingChange(3, isChecked);
            }
        }

    }

    private void setVolumeKeyTakePic(boolean isChecked) {
        this.isVolumeKeyTakePic = isChecked;
        SharedPreferences sharedPre = this.context.getSharedPreferences("SharedPreferences", 0);
        Editor editor = sharedPre.edit();
        editor.putBoolean("volumeKeyTakePic", isChecked);
        editor.commit();
    }

    public int getFlashlightOn() {
        return this.flashlightOn;
    }

    private void setFlashlightOn(int flashlightOn) {
        this.flashlightOn = flashlightOn;
        SharedPreferences sharedPre = this.context.getSharedPreferences("SharedPreferences", 0);
        Editor editor = sharedPre.edit();
        editor.putInt("flushType", flashlightOn);
        editor.commit();
    }

    private void setTouchTake(boolean touchTake) {
        this.touchTake = touchTake;
        SharedPreferences sharedPre = this.context.getSharedPreferences("SharedPreferences", 0);
        Editor editor = sharedPre.edit();
        editor.putBoolean("touchTake", touchTake);
        editor.commit();
    }

    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == 1 && this.listener != null) {
            this.listener.onSettingChange(4, true);
        }

        return true;
    }

    public interface OnSettingChangeListener {
        void onSettingChange(int var1, boolean var2);
    }
}
