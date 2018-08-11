//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.satellite.cameralib;

import android.os.Build.VERSION;

public class ApiChecker {
    public static final boolean AT_LEAST_16;
    public static final boolean AT_LEAST_14;
    public static final boolean AT_LEAST_11;
    public static final boolean AT_LEAST_10;
    public static final boolean AT_LEAST_8;
    public static final boolean AT_LEAST_5;
    public static final boolean HAS_AUTO_FOCUS_MOVE_CALLBACK;
    public static final boolean HAS_DISPLAY_LISTENER;
    public static final boolean HAS_GET_CAMERA_NUMBER;
    public static final boolean HAS_HIDEYBARS;

    static {
        AT_LEAST_16 = VERSION.SDK_INT >= 16;
        AT_LEAST_14 = VERSION.SDK_INT >= 14;
        AT_LEAST_11 = VERSION.SDK_INT >= 11;
        AT_LEAST_10 = VERSION.SDK_INT >= 10;
        AT_LEAST_8 = VERSION.SDK_INT >= 8;
        AT_LEAST_5 = VERSION.SDK_INT >= 5;
        HAS_AUTO_FOCUS_MOVE_CALLBACK = VERSION.SDK_INT >= 16;
        HAS_DISPLAY_LISTENER = VERSION.SDK_INT >= 17;
        HAS_GET_CAMERA_NUMBER = VERSION.SDK_INT < 9;
        HAS_HIDEYBARS = VERSION.SDK_INT >= 19 || "KeyLimePie".equals(VERSION.CODENAME);
    }

    public ApiChecker() {
    }
}
