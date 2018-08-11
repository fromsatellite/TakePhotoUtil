//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.satellite.cameralib;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;

public class FocusUI {
    public Context mContext;
    public ImageView mFocusView;
    public Resources mResources;
    public int white_focus_id;
    public int blue_focus_id;

    public FocusUI(Context context, Resources res) {
        this.mContext = context;
        this.mResources = res;
        int camera_focus_view_id = this.mResources.getIdentifier("camera_focus_view", "id", context.getPackageName());
        this.white_focus_id = this.mResources.getIdentifier("white_focus", "drawable", context.getPackageName());
        this.blue_focus_id = this.mResources.getIdentifier("blue_focus", "drawable", context.getPackageName());
        this.mFocusView = (ImageView)((Activity)this.mContext).findViewById(camera_focus_view_id);
    }

    public void clearFocus() {
        if (this.mFocusView != null) {
            this.mFocusView.setVisibility(8);
        }

    }

    public void onFocusStarted() {
        int x = CameraConst.widthPixels / 2;
        int y = CameraConst.heightPixels / 2;
        this.onFocusStarted(x, y);
    }

    public void onFocusStarted(int touch_x, int touch_y) {
        if (this.mFocusView != null) {
            int view_state = this.mFocusView.getVisibility();
            if (view_state == 8 || view_state == 4) {
                this.mFocusView.setVisibility(0);
            }

            LayoutParams lp = (LayoutParams)this.mFocusView.getLayoutParams();
            lp.setMargins(touch_x - (int)(42.0F * CameraConst.density), touch_y - (int)(42.0F * CameraConst.density), 0, 0);
            this.mFocusView.setLayoutParams(lp);
            this.mFocusView.setImageResource(this.white_focus_id);
            this.mFocusView.setVisibility(0);
            this.ScaleOutAnimation(this.mFocusView);
        }
    }

    public void onFocusSucceeded() {
        if (this.mFocusView != null) {
            int view_state = this.mFocusView.getVisibility();
            if (view_state == 0) {
                this.mFocusView.setImageResource(this.blue_focus_id);
            }
        }

    }

    public void onFocusFail() {
    }

    public void ScaleOutAnimation(View view) {
        RotateAnimation animation = new RotateAnimation(0.0F, 180.0F, 1, 0.5F, 1, 0.5F);
        animation.setDuration(500L);
        animation.setAnimationListener(new AnimationListener() {
            public void onAnimationStart(Animation paramAnimation) {
            }

            public void onAnimationRepeat(Animation paramAnimation) {
            }

            public void onAnimationEnd(Animation paramAnimation) {
            }
        });
        view.startAnimation(animation);
    }
}
