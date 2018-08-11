//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.satellite.cameralib.photocompress;

import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.text.TextUtils;

import com.satellite.cameralib.CameraConst;

import java.io.File;
import java.io.IOException;

public class PhotoUtil {
    public static int COMPRESS_VALUE = 0;

    public PhotoUtil() {
    }

    public static void compresImageByBitmap(String filePath, int quality, int maxPictureSize) {
        int degree = ImageUtil.readPictureDegree(filePath);
        int index = CameraConst.widthPixels > CameraConst.heightPixels ? CameraConst.widthPixels : CameraConst.heightPixels;
        int compressValue = COMPRESS_VALUE;
        if (compressValue > 2 || compressValue < 0) {
            boolean var7 = false;
        }

        Bitmap newbitmap = ImageUtil.rotaingImageView(degree, ImageUtil.image_Compression_ByCompressValue(filePath, index, maxPictureSize));
        ImageUtil.compressBmpToFile(newbitmap, filePath, quality);
        newbitmap.recycle();
    }

    public static boolean setPicOritation(String fp, int degree) {
        try {
            ExifInterface exifInterface = new ExifInterface(fp);
            exifInterface.setAttribute("Orientation", String.valueOf(degree));
            exifInterface.saveAttributes();
            return true;
        } catch (IOException var3) {
            var3.printStackTrace();
            return false;
        }
    }

    public static int shootFileExist(String firstFile, String secondFile) {
//        int isExist = false;
        int isExist;
        if (!TextUtils.isEmpty(firstFile) && !TextUtils.isEmpty(secondFile)) {
            if (isFileExist(firstFile)) {
                if (isFileExist(secondFile)) {
                    isExist = 2;
                } else {
                    isExist = 1;
                }
            } else if (isFileExist(secondFile)) {
                isExist = 3;
            } else {
                isExist = 0;
            }
        } else if (!TextUtils.isEmpty(firstFile) && TextUtils.isEmpty(secondFile)) {
            if (isFileExist(firstFile)) {
                isExist = 1;
            } else {
                isExist = 0;
            }
        } else {
            isExist = 0;
        }

        return isExist;
    }

    public static int shootFileExist(String firstFile, String secondFile, String thirdFile) {
        int isExist;
//        byte isExist;
        if (!TextUtils.isEmpty(firstFile) && !TextUtils.isEmpty(secondFile) && !TextUtils.isEmpty(thirdFile)) {
            if (isFileExist(firstFile)) {
                if (isFileExist(secondFile)) {
                    if (isFileExist(thirdFile)) {
                        isExist = 4;
                    } else {
                        isExist = 2;
                    }
                } else if (isFileExist(thirdFile)) {
                    isExist = 7;
                } else {
                    isExist = 1;
                }
            } else if (isFileExist(secondFile)) {
                if (isFileExist(thirdFile)) {
                    isExist = 6;
                } else {
                    isExist = 3;
                }
            } else if (isFileExist(thirdFile)) {
                isExist = 5;
            } else {
                isExist = 0;
            }
        } else if (!TextUtils.isEmpty(firstFile) && !TextUtils.isEmpty(secondFile)) {
            if (isFileExist(firstFile)) {
                if (isFileExist(secondFile)) {
                    isExist = 2;
                } else {
                    isExist = 1;
                }
            } else if (isFileExist(secondFile)) {
                isExist = 3;
            } else {
                isExist = 0;
            }
        } else if (!TextUtils.isEmpty(firstFile) && TextUtils.isEmpty(secondFile)) {
            if (isFileExist(firstFile)) {
                isExist = 1;
            } else {
                isExist = 0;
            }
        } else {
            isExist = 0;
        }

        return isExist;
    }

    public static boolean isFileExist(String filePath) {
        boolean isExist = false;
        if (TextUtils.isEmpty(filePath)) {
            return isExist;
        } else {
            File file = new File(filePath);
            if (file.exists() && file.isFile()) {
                isExist = true;
            }

            return isExist;
        }
    }
}
