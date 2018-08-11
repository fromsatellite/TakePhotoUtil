//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.satellite.cameralib;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

public class VerticalSeekBar extends SeekBar {
    public VerticalSeekBar(Context context) {
        super(context);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(h, w, oldh, oldw);
    }

    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        this.setMeasuredDimension(this.getMeasuredHeight(), this.getMeasuredWidth());
    }

    protected void onDraw(Canvas c) {
        c.rotate(-90.0F);
        c.translate((float)(-this.getHeight()), 0.0F);
        super.onDraw(c);
    }

    public synchronized void setProgress(int progress) {
        super.setProgress(progress);
        this.onSizeChanged(this.getWidth(), this.getHeight(), 0, 0);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (!this.isEnabled()) {
            return false;
        } else {
            switch(event.getAction()) {
                case 0:
                case 1:
                case 2:
                    this.setProgress(this.getMax() - (int)((float)this.getMax() * event.getY() / (float)this.getHeight()));
                    this.onSizeChanged(this.getWidth(), this.getHeight(), 0, 0);
                case 3:
                default:
                    return true;
            }
        }
    }
}
