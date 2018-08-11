package com.satellite.takephoto;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.satellite.cameralib.CameraInterface;
import com.satellite.cameralib.CameraInterface.onCaptureButtonClickListener;
import com.autonavi.photosdk.entity.PhotoParams;
import com.autonavi.photosdk.entity.PhotoResult;
import com.autonavi.photosdk.interfaces.PhotoCreatedCallBack;
import com.autonavi.photosdk.utils.FilesUtils;
import com.satellite.takephoto.util.Code;
import com.satellite.takephoto.util.PublicUtil;

import java.io.File;

//import com.umeng.analytics.MobclickAgent;

public abstract class BasePhotoActivity extends FragmentActivity {

	private PhotoParams currentPhotoParams;

	/*当前的照片类型*/
	public int currentPhotoType=-1;

	private Dialog gpsDialog = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	if(savedInstanceState!=null){
		currentPhotoParams=savedInstanceState.getParcelable("PhotoParams");
		currentPhotoType=savedInstanceState.getInt("currentPhotoType", -1);
		
	}
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
	   if(outState!=null){
	    	outState.putParcelable("PhotoParams", currentPhotoParams);
	    	outState.putInt("currentPhotoType", currentPhotoType);
	    }
		super.onSaveInstanceState(outState);
	}

	/**
	 * 开始拍照
	 *
	 */
	public synchronized void startTakePhoto(PhotoParams params){
		currentPhotoParams=params;
		try {
			if(TextUtils.isEmpty(params.fileName)){
				Toast.makeText(this, "照片名称不能为空", 1).show();
				return;
			}
			//   Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			if(TextUtils.isEmpty(params.path)){
				params.path= FilesUtils.getRootDirectory();
			}else{
				File fdir=new File(params.path);
				if(!fdir.exists()){
					fdir.mkdir();
				}
			}
//   	    String url=params.path+File.separator+params.fileName;
//        Uri uri = Uri.fromFile(new File(url));
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//        startActivityForResult(intent, Code.REQ_Camera_CODE);

			CameraInterface.setCameraFloder(params.path);//设置照片存放路径
			CameraInterface.setCameraPictureSize(1920);//设置图片大小
			CameraInterface.setOnCaptureButtonClickListener(new onCaptureButtonClickListener() {
				@Override
				public void onCapture() {
					//这个地方获取角度 定位等
				}
			}); //设置监听相机中拍照键的回调函数
			/**里面有很多设置，详细请查看“CameraInterface说明文档”**/
			/*启动相机*/
			CameraInterface.showCameraActivityForResult(this, Code.REQ_Camera_CODE);
		} catch (Exception e) {
			Toast.makeText(this, "启动相机失败!", 1).show();
			e.printStackTrace();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, intent);
		if(requestCode== Code.REQ_LookPhotoActivity_CODE
				||requestCode>10000
				||resultCode==Code.RES_StopTaskInfoActivity_CODE
				||resultCode!= Activity.RESULT_OK){
			onCannelTakePhoto();
			return;
		}
		if (resultCode != Activity.RESULT_OK||requestCode!=Code.REQ_Camera_CODE) { //拍照时候用户点击了确定
			PhotoResult r=new PhotoResult();
			r.setStatus(-6);
			r.setMessage("用户取消拍照了");
			onTakePhotoResult(r);
			return;
		}
		if(currentPhotoParams==null){
			PhotoResult r = new PhotoResult();
			r.setStatus(-5);
			r.setMessage("参数为空");
			onTakePhotoResult(r);
		} else {
			Uri uri = intent.getData();
			//这个是全路径
			String filePath = CameraInterface.getPicturePathByURI(uri); //通过CameraInterface来得到文件路径
			currentPhotoParams.allPath = filePath;

			FilesUtils.createSmallPhoto(currentPhotoParams,BasePhotoActivity.this,new PhotoCreatedCallBack(){
				@Override
				public void callBack(PhotoResult result) {
					if(result.getStatus()<0|| TextUtils.isEmpty(result.getSourceFileName())|| TextUtils.isEmpty(result.getSmallFileName())){
						onCannelTakePhoto();
					} else {
						//判断对应的文件是否真的生成啦
						File big = new File(result.getSourceFileName());
						if(!big.exists() || big.length()<1024){
							onCannelTakePhoto();
						} else {
							//判断小图是否存在
							File small = new File(result.getSmallFileName());
							if(!small.exists() || small.length()<1024){
								onCannelTakePhoto();
							} else {
								onTakePhotoResult(result);
							}
						}
					}
				}
			});

		}

	}

	public abstract void onTakePhotoResult(PhotoResult result);

	public abstract void onCannelTakePhoto();

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	    boolean isOpenGps = PublicUtil.isOpenGps(this);
//	    if (isOpenGps == false) {
//	            if (gpsDialog == null) {
//	                gpsDialog = CustomDialog.createCustomDialog(this,"请开启GPS",
//	                        new OnClickListener() {
//	                            @Override
//	                            public void onClick(View v) {
//	                                //确定
//	                                if (!isFinishing()) {
//	                                    gpsDialog.dismiss();
//	                                }
//	                                Intent intent = new Intent();
//	                                intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//	                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//	                                startActivity(intent);
//	                            }
//	                        }, "确定", new OnClickListener() {
//	                            @Override
//	                            public void onClick(View v) {
//	                                //取消
//	                                if (!isFinishing()) {
//	                                    gpsDialog.cancel();
//	                                }
//	                            }
//	                        }, "取消", true, new OnCancelListener() {
//	                            @Override
//	                            public void onCancel(DialogInterface dialog) {
//	                                //取消
//	                                if (!isFinishing()) {
//	                                    gpsDialog.cancel();
//	                                }
//	                                finish();
//	                            }
//	                        });
//	            }
//	            if (gpsDialog != null && !isFinishing()) gpsDialog.show();
//	        }
	}
}
