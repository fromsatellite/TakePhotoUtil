//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.satellite.cameralib;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

public class CameraInterface {
    public CameraInterface() {
    }

    public static void setCameraPictureSize(int maxSize) {
        CameraConst.MAX_PICTURE_SIZE = maxSize;
    }

    public static void setCameraFloder(String path) {
        CameraConst.FOLDER_NAME = path;
    }

    public static void setPictureCompressQuality(int quality) {
        CameraConst.PICTURE_QUALITY = quality;
    }

    public static void setPictrueCompressSize(int size) {
        CameraConst.MAX_PICTURE_COMPRESS_SIZE_VALUE = size;
    }

    public static void setIsPhotoCompress(boolean isCompress) {
        CameraConst.IS_PICTURE_COMPRESSED = isCompress;
    }

    public static void setIsHasFlashFunction(boolean isHas) {
        CameraConst.IS_HAS_FLASH = isHas;
    }

    public static void setIsHasTouchCaptureFunction(boolean isHas) {
        CameraConst.IS_HAS_TOUCH_CAPTURE = isHas;
    }

    public static void setIsHasVolumeKeyFunction(boolean isHas) {
        CameraConst.IS_HAS_VOLUME_ZOOM = isHas;
    }

    public static void setCameraCapturePermisson(boolean isHas, String reason) {
        CameraConst.IS_HAS_CAPTURE_PERMISSION = isHas;
        CameraConst.PERMISSION_REASON = reason;
    }

    public static void setOnCaptureButtonClickListener(CameraInterface.onCaptureButtonClickListener listener) {
        CameraConst.CAPTURE_LISTENER = listener;
    }

    public static String getPicturePathByURI(Uri uri) {
        return uri == null ? null : uri.toString().substring(uri.toString().indexOf("///") + 2);
    }

    public static void showCameraActivityForResult(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, CameraActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    public interface onCaptureButtonClickListener {
        void onCapture();
    }
}
