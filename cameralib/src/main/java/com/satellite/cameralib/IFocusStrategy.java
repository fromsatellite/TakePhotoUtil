package com.satellite.cameralib;

import android.view.MotionEvent;

public abstract interface IFocusStrategy
{
  public abstract void operateFocus();
  
  public abstract void cancelFocus();
  
  public abstract void executeFocus(MotionEvent paramMotionEvent);
  
  public abstract long getFocusEndTime();
}


