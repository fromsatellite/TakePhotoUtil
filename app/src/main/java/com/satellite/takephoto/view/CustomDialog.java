/**
 *  Copyright (c) 2002-2014 AutoNavi, Inc. All rights reserved.
 *
 *  This software is the confidential and proprietary information of AutoNavi, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with AutoNavi.
 */
package com.satellite.takephoto.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface.OnCancelListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.satellite.takephoto.R;



//import com.autonavi.busstationcollection.ConApplication;
//import com.autonavi.busstationcollection.R;
//import com.autonavi.busstationcollection.common.BaseActivity;
//import com.autonavi.busstationcollection.utils.PrefUtils;

/**
 * 自定义的dialog
 * @author wang.yuchao
 * @since 2014-11-7
 */
public class CustomDialog {

    /**
     * 过段时间消失弹框:三个控件：一个图片，两行文字，类似toast
     */
//    public static void showCustomDialog(Context context, long delayMillis, int resId,
//            String textBigSize, String textSmallSize) {
//        View view = LayoutInflater.from(context).inflate(R.layout.dialog_custom_toast, null);
//        ImageView ivHint = (ImageView) view.findViewById(R.id.iv_hint);
//        TextView tvHintBig = (TextView) view.findViewById(R.id.tv_hint_big);
//        TextView tvHintSmall = (TextView) view.findViewById(R.id.tv_hint_small);
//        ivHint.setImageResource(resId);
//        tvHintBig.setText(textBigSize);
//        tvHintSmall.setText(textSmallSize);
//        final Dialog dialog = new Dialog(context, R.style.custom_dialog);
//        Window window = dialog.getWindow();
//        window.setGravity(Gravity.CENTER);
//        window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(view);
//        dialog.setCancelable(true);
//        dialog.show();
//        view.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                dialog.cancel();
//            }
//        }, delayMillis);
//    }

    /**
     * 过段时间消失弹框:三个控件：一个图片，两行文字：消失后触发事件
     */
//    public static void showCustomDialog(final BaseActivity activity, long delayMillis, int resId,
//            String textBigSize, String textSmallSize, OnCancelListener onCancelListener) {
//        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_custom_toast, null);
//        ImageView ivHint = (ImageView) view.findViewById(R.id.iv_hint);
//        TextView tvHintBig = (TextView) view.findViewById(R.id.tv_hint_big);
//        TextView tvHintSmall = (TextView) view.findViewById(R.id.tv_hint_small);
//        ivHint.setImageResource(resId);
//        tvHintBig.setText(textBigSize);
//        tvHintSmall.setText(textSmallSize);
//        final Dialog dialog = new Dialog(activity, R.style.custom_dialog);
//        Window window = dialog.getWindow();
//        window.setGravity(Gravity.CENTER);
//        window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(view);
//        dialog.setCancelable(true);
//        dialog.setOnCancelListener(onCancelListener);
//        if (activity != null && !activity.isFinishing()) dialog.show();
//        view.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (activity != null && !activity.isFinishing()) dialog.cancel();
//            }
//        }, delayMillis);
//    }

    /**
     * 显示一个弹框，自定义消失与显示,类似progressdialog
     */
//    public static Dialog createCustomProgressDialog(Context context, String strHint,
//            boolean cancelable, OnCancelListener onCancelListener) {
//        View view = LayoutInflater.from(context).inflate(R.layout.dialog_custom_toast, null);
//        ImageView ivHint = (ImageView) view.findViewById(R.id.iv_hint);
//        TextView tvHintBig = (TextView) view.findViewById(R.id.tv_hint_big);
//        TextView tvHintSmall = (TextView) view.findViewById(R.id.tv_hint_small);
//        ivHint.setVisibility(View.GONE);
//        tvHintBig.setVisibility(View.GONE);
//        tvHintSmall.setText(strHint);
//        final Dialog dialog = new Dialog(context, R.style.custom_dialog);
//        Window window = dialog.getWindow();
//        window.setGravity(Gravity.CENTER);
//        window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(view);
//        dialog.setCancelable(cancelable);
//        dialog.setOnCancelListener(onCancelListener);
//        return dialog;
//    }

    /**
     * 弹出对话框：一个按钮，一个提示
     */
//    public static Dialog createCustomDialog(Context context, String strHint,
//            OnClickListener confirmListener, String strConfirm, boolean cancelable,
//            OnCancelListener onCancelListener) {
//        View view = LayoutInflater.from(context).inflate(R.layout.dialog_custom_buttons, null);
//        TextView tvHint = (TextView) view.findViewById(R.id.tv_hint);
//        Button btnConfirm = (Button) view.findViewById(R.id.btn_confirm);
//        Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);
//        View viewline = view.findViewById(R.id.view_line);
//        viewline.setVisibility(View.GONE);
//        btnCancel.setVisibility(View.GONE);
//        tvHint.setText(strHint);
//        btnConfirm.setText(strConfirm);
//        btnConfirm.setOnClickListener(confirmListener);
//        final Dialog dialog = new Dialog(context, R.style.custom_dialog);
//        Window window = dialog.getWindow();
//        window.setGravity(Gravity.CENTER);
//        window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(view);
//        dialog.setCancelable(cancelable);
//        dialog.setOnCancelListener(onCancelListener);
//        return dialog;
//    }
    /**
     * 弹出对话框：一个按钮，一个提示
     */
//    public static Dialog createOneButtonDialog(Context context, String strHint,String buttonName,
//    		OnClickListener confirmListener,  boolean cancelable) {
//    	View view = LayoutInflater.from(context).inflate(R.layout.dialog_custom_button, null);
//    	TextView tvHint = (TextView) view.findViewById(R.id.tv_hint);
//    	Button btnConfirm = (Button) view.findViewById(R.id.btn_confirm);
//    	tvHint.setText(strHint);
//    	btnConfirm.setText(buttonName);
//    	btnConfirm.setOnClickListener(confirmListener);
//    	final Dialog dialog = new Dialog(context, R.style.custom_dialog);
//    	Window window = dialog.getWindow();
//    	window.setGravity(Gravity.CENTER);
//    	window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
//    	dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//    	dialog.setContentView(view);
//    	dialog.setCancelable(cancelable);
//    	return dialog;
//    }

    /**
     * 弹出对话框：两个按钮，一个提示
     */
    public static Dialog createCustomDialog(Activity context, String strHint,
                                            OnClickListener confirmListener, String strConfirm, OnClickListener onCancel,
                                            String strCancel, boolean cancelable, OnCancelListener onCancelListener) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_custom_buttons, null);
        TextView tvHint = (TextView) view.findViewById(R.id.tv_hint);
        Button btnConfirm = (Button) view.findViewById(R.id.btn_confirm);
        Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);
        tvHint.setText(strHint);
        btnConfirm.setText(strConfirm);
        btnCancel.setText(strCancel);
        btnCancel.setOnClickListener(onCancel);
        btnConfirm.setOnClickListener(confirmListener);
        final Dialog dialog = new Dialog(context, R.style.custom_dialog);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        dialog.setCancelable(cancelable);
        dialog.setOnCancelListener(onCancelListener);
        return dialog;
    }

//    public static ProgressDialog showLoginDialog(BaseActivity activity, String msg,
//            OnCancelListener listener) {
//        ProgressDialog progressDialog = new ProgressDialog(activity);
//        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        progressDialog.setIndeterminate(false);
//        progressDialog.setCancelable(true);
//        progressDialog.setMessage(msg);
//        progressDialog.setOnCancelListener(listener);
//        if (progressDialog != null && !progressDialog.isShowing() && !activity.isFinishing()) {
//            progressDialog.show();
//        }
//        return progressDialog;
//    }
    /**
     * 弹出对话框：提交数据 不可修改提示框
     */
//    public static Dialog createCommitDialog(Activity context,OnClickListener confirmListener,  OnClickListener onCancel) {
//        View view = LayoutInflater.from(context).inflate(R.layout.dialog_commit_task, null);
//        Button btnConfirm = (Button) view.findViewById(R.id.btn_confirm);
//        Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);
//        final CheckBox cb_no_alert = (CheckBox) view.findViewById(R.id.cb_no_alert);
//        btnCancel.setOnClickListener(onCancel);
//        btnConfirm.setOnClickListener(confirmListener);
//        final Dialog dialog = new Dialog(context, R.style.custom_dialog);
//        Window window = dialog.getWindow();
//        window.setGravity(Gravity.CENTER);
//        window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//
//        dialog.setOnCancelListener(new OnCancelListener() {
//			@Override
//			public void onCancel(DialogInterface dialog) {
//                if(cb_no_alert.isChecked()){
//					PrefUtils.setCurrentUserVersionCommit(ConApplication.context, PrefUtils.getUser(ConApplication.context));
//				 }
//			}
//		});
//        dialog.setContentView(view);
//        return dialog;
//    }
}
