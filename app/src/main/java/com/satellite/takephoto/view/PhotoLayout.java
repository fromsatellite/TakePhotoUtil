package com.satellite.takephoto.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.satellite.takephoto.R;


public class PhotoLayout extends LinearLayout {
private ImageView imageView,iv_location1;
private TextView tv_location1;
private LinearLayout lay_status;
	public PhotoLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		LayoutInflater inflater= LayoutInflater.from(context);
		View view=inflater.inflate(R.layout.view_lay_photo, this);
		imageView=(ImageView) view.findViewById(R.id.iv_take);
		iv_location1=(ImageView) view.findViewById(R.id.iv_location1);
		tv_location1=(TextView) view.findViewById(R.id.tv_location1);
		lay_status=(LinearLayout) view.findViewById(R.id.lay_status);
	}
	public ImageView getImageView() {
		return imageView;
	}
	public void setImageView(ImageView imageView) {
		this.imageView = imageView;
	}
	public TextView getTv_location1() {
		return tv_location1;
	}
	public void setTv_location1(TextView tv_location1) {
		this.tv_location1 = tv_location1;
	}
	public LinearLayout getLay_status() {
		return lay_status;
	}
	public void setLay_status(LinearLayout lay_status) {
		this.lay_status = lay_status;
	}
	public ImageView getIv_location1() {
		return iv_location1;
	}
	public void setIv_location1(ImageView iv_location1) {
		this.iv_location1 = iv_location1;
	}

}
