//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.satellite.cameralib;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class CameraFocusView extends View {
    private Paint mOvalPaint;
    private int mStrokeWidth = 2;
    private int padding = 3;
    private int mOval_l;
    private int mOval_t;
    private int mOval_r;
    private int mOval_b;

    public CameraFocusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initRocketView();
    }

    private void initRocketView() {
        this.mOvalPaint = new Paint();
        this.mOvalPaint.setAntiAlias(true);
        this.mOvalPaint.setColor(-1);
        this.mOvalPaint.setStyle(Style.STROKE);
        this.mOvalPaint.setStrokeWidth((float)this.mStrokeWidth);
        this.setPadding(this.padding, this.padding, this.padding, this.padding);
    }

    public void changeRecColor(boolean focused) {
        if (focused) {
            this.mOvalPaint.setColor(-16711936);
        } else {
            this.mOvalPaint.setColor(-1);
        }

    }

    public void setOvalRect(int l, int t, int r, int b) {
        this.mOval_l = l + this.padding;
        this.mOval_t = t + this.padding;
        this.mOval_r = r;
        this.mOval_b = b;
        this.requestLayout();
        this.invalidate();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(0);
        RectF re11 = new RectF((float)this.mOval_l, (float)this.mOval_t, (float)this.mOval_r, (float)this.mOval_b);
        canvas.drawRect(re11, this.mOvalPaint);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.setMeasuredDimension(this.measureWidth(widthMeasureSpec), this.measureHeight(heightMeasureSpec));
    }

    private int measureWidth(int measureSpec) {
//        int result = false;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        int result;
        if (specMode == 1073741824) {
            result = specSize;
        } else {
            result = this.mOval_r + this.getPaddingLeft() + this.getPaddingRight();
            if (specMode == -2147483648) {
                result = Math.min(result, specSize);
            }
        }

        return result;
    }

    private int measureHeight(int measureSpec) {
//        int result = false;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        int result;
        if (specMode == 1073741824) {
            result = specSize;
        } else {
            result = this.mOval_b + this.getPaddingTop() + this.getPaddingBottom();
            if (specMode == -2147483648) {
                result = Math.min(result, specSize);
            }
        }

        return result;
    }
}
