package com.satellite.takephoto;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.FileObserver;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.autonavi.photosdk.entity.PhotoParams;
import com.autonavi.photosdk.entity.PhotoResult;
import com.autonavi.photosdk.entity.PhotoSize;
import com.autonavi.photosdk.entity.WaterParams;
import com.autonavi.photosdk.utils.FilesUtils;
import com.satellite.takephoto.model.GpsInfo;
import com.satellite.takephoto.model.Photo;
import com.satellite.takephoto.util.PublicUtil;
import com.satellite.takephoto.view.PhotoLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
//import com.autonavi.busstationcollection.common.Code;
//import com.autonavi.busstationcollection.dto.PhotoListDto;
//import com.autonavi.busstationcollection.entity.StopTask;
//import com.autonavi.busstationcollection.utils.GsonUtils;
//import com.autonavi.busstationcollection.utils.UmengUtil;
//import com.autonavi.collection.bus.loaction.MLocationManager;

//import com.autonavi.busstationcollection.interfaces.PhotoListLoadListener;
//import com.autonavi.busstationcollection.ui.LookPhotoActivity;


public class TakePhotoFragment extends Fragment implements OnClickListener,SensorEventListener {
	LinearLayout lay_views;
	ImageView iv_take;
	public int maxPhotoNum=3;
	AQuery query;
	private List<Photo> photoList=new ArrayList<Photo>();
	//PhotoType photoType;
	/**照片类型  0站台 1站桩   2站牌    **/
	public int photoType=-1;
	public int isNeedLocation=0;
	public String path="";
	private final static int imagViewId=00001;
	/**是否会调用 onActivityResult **/
	private boolean isCallForResult=false;
	private boolean isCallTakePhotoResult=false;
	public boolean isPhotoListFinnished=true;
	private boolean isAllowEidt=false;//是否允许编辑
	private boolean isOnDestory=false;//是否允许编辑
	private int width,height;
	private int space;
	//View view;
	//IPhotoListListener photoListListener;
	 private FileObserver observer = null;
	 GpsInfo location;
//	 StopTask task;
//	 PhotoListLoadListener loadedListenner;
	 WaterParams wm=new WaterParams();
	 float angle,direction;
	 float currentAngle,currentDirection;
	 SensorManager sm ;
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if(savedInstanceState!=null){}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		View view=inflater.inflate(R.layout.fragment_take_photo, null);
		lay_views=(LinearLayout) view.findViewById(R.id.lay_views);
		iv_take=(ImageView) view.findViewById(R.id.iv_take);
		iv_take.setOnClickListener(this);
		iv_take.setVisibility(isAllowEidt&&photoList.size()<maxPhotoNum? View.VISIBLE: View.INVISIBLE);
		FragmentActivity a=	getActivity();
		if(query==null){
			if(a!=null){
				query=new AQuery(a);
			}
		}
		
		if(a!=null){
			// 获取传感器管理器
		   sm = (SensorManager) a.getSystemService(Context.SENSOR_SERVICE);
//		   task=PrefUtils.getCurrentTask(a.getApplicationContext());
		   WindowManager wm = a.getWindowManager();
		   int srceenWidth = wm.getDefaultDisplay().getWidth();
		   width= PublicUtil.dip2px(a.getApplicationContext(), 80);
		   height=PublicUtil.dip2px(a.getApplicationContext(), 110);
		   //计算间距
		   space=(srceenWidth-3*width)/4;
		   LinearLayout.LayoutParams layParams=new LinearLayout.LayoutParams(width, width);
		   layParams.leftMargin=space;
		   iv_take.setLayoutParams(layParams);
		}
		if(savedInstanceState!=null){
			isOnDestory=true;//代表被回收过
			isNeedLocation=savedInstanceState.getInt("isNeedLocation");
			photoType=savedInstanceState.getInt("photoType");
			maxPhotoNum=savedInstanceState.getInt("maxPhotoNum");
			isCallForResult=savedInstanceState.getBoolean("isCallForResult");
			isCallTakePhotoResult=savedInstanceState.getBoolean("isCallTakePhotoResult");
			isPhotoListFinnished=savedInstanceState.getBoolean("isPhotoListFinnished");
			isAllowEidt=savedInstanceState.getBoolean("isAllowEidt");
			path=savedInstanceState.getString("path");
			//如果不是 从查看照片回来的  才从本地的缓存中恢复  因为用户可能会删除重拍数据发生了变化
			
			//到底在哪 isPhotoListFinnished更改呢  
//			if(!isCallForResult){
//				String json=savedInstanceState.getString("Dto");
//				PhotoListDto dto=GsonUtils.fromJson(json, PhotoListDto.class);
//				if(dto!=null){
//					List<Photo> list=dto.list;
//					if(list!=null&&list.size()>0){
//						photoList.addAll(list);
//						//恢复
//						initPhotos();
//					}
//				}
//			}
		//  如果是从拍照过来的 要在onTakePhotoResult恢复
			if(!isCallTakePhotoResult){
				Log.i("Test", "json--------");
				isPhotoListFinnished=true;
//				if(loadedListenner!=null){
//					Log.i("Test", "loadedListenner---");
//					loadedListenner.loadedCallBack(photoType);
//				}
			}
           }
		isCallForResult=false;//记得恢复默认值
		return view;
	}

	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewStateRestored(savedInstanceState);
		
		
	}
	
	public void initPhotos() {
		if(photoList==null){
			return;
		}
		int size=photoList.size();
		if(size==0){
			return;
		}
		if(size==maxPhotoNum){//
			//隐藏 拍照按钮
			iv_take.setVisibility(View.GONE);
			Log.i("Test", photoType+"_隐藏了");
			switch (size) {
			case 1:
				reStorePhoto(photoList.get(0));
				break;
			case 2:
				 reStorePhoto(photoList.get(size-2));
				 reStorePhoto(photoList.get(size-1));
				 break;
			default:
				 reStorePhoto(photoList.get(size-3));
				 reStorePhoto(photoList.get(size-2));
				 reStorePhoto(photoList.get(size-1));
				break;
			}
			
		}else{
			if(size==1){
			   reStorePhoto(photoList.get(0));
		     }else{
				 reStorePhoto(photoList.get(size-2));
				 reStorePhoto(photoList.get(size-1));
			 }
		}
	}
   @Override
   public void onResume() {
	// TODO Auto-generated method stub
	  super.onResume();
   }
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
	}

	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
	}

   public void takePhoto(){
	//加到 倒数第二个位置
		BasePhotoActivity a=(BasePhotoActivity) getActivity();
		if(a==null){
			return;
		}
		if(TextUtils.isEmpty(path)){
//			showToast("照片路径不能为空");
			return ;
		}
		
//		if(loadedListenner!=null){
//			loadedListenner.openCarmer();
//		}
		// 获取加速度传感器
	    if(sm!=null){
	    	Sensor acceleromererSensor = sm.getDefaultSensor(Sensor.TYPE_ORIENTATION);
	    	sm.registerListener(this, acceleromererSensor, SensorManager.SENSOR_DELAY_GAME);
	    }
		
		PhotoParams params=new PhotoParams();
	   //fileName文件名称为必须参数
		params.fileName= System.currentTimeMillis()+".jpg";
		params.path=path;
		//开始拍照
		params.photoSize= PhotoSize.M;
		params.waterParams=wm;
		a.currentPhotoType=photoType;
		a.startTakePhoto(params);
		isCallTakePhotoResult=true;//表示会执行 ontakephotoResult
		isPhotoListFinnished=false;//锁定list
		isOnDestory=false;
		//定位
		location=null;
		setFilePath(path);
	
     }

     public void onTakePhotoResult(final PhotoResult photoResult) {
	   if(sm!=null){
		   sm.unregisterListener(this);
	   }
	   
	    if(photoResult==null||photoResult.getStatus()<0){
	    	isPhotoListFinnished=true;
//	    	if(loadedListenner!=null){
//				loadedListenner.loadedCallBack(photoType);
//			}
	    	//showToast("不符合要求啦");
	    	return;
	    }
		BasePhotoActivity a=(BasePhotoActivity) getActivity();
		if(a==null){
//			showToast("参数错误");
			isPhotoListFinnished=true;
//			if(loadedListenner!=null){
//				loadedListenner.loadedCallBack(photoType);
//			}
			return;
		}
	    final int index=lay_views.getChildCount();
	    final PhotoLayout lay=new PhotoLayout(a);
	    if(isNeedLocation==1){
	    	lay.getLay_status().setVisibility(View.VISIBLE);
	    }
	    ImageView iv=lay.getImageView();
	    final ImageView iv_location=lay.getIv_location1();
	    final TextView tv=lay.getTv_location1();
		iv.setId(imagViewId);
		iv.setOnClickListener(this);
		iv.setScaleType(ScaleType.CENTER);
	   // 实时获取位置信息
		if(location==null){ //再获取一次
//			location= MLocationManager.getInstance(a.getApplicationContext()).getRealTimeLocation(true);
		}
		if(location!=null&&location.getLatitude()>15){
//			UmengUtil.onEvent(a.getApplicationContext(), "0003");
		}else{
//			UmengUtil.onEvent(a.getApplicationContext(), "0004");
		}
		query.id(iv).image(new File(photoResult.getSmallFileName()), true, width, new BitmapAjaxCallback(){
			@Override
			protected void callback(String url, ImageView iv, Bitmap bm,
                                    AjaxStatus status) {
				if(photoType==-1){
					isPhotoListFinnished=true;
//					if(loadedListenner!=null){
//						loadedListenner.loadedCallBack(photoType);
//					}
					return;
				}
				Photo photo=new Photo();
				photo.path="";
				photo.fileName=url;//具有唯一标识
				photo.photoType=photoType;
				photo.angle=(int) currentAngle;
				photo.orientation=(int) currentDirection;
				if(location!=null&&location.getLatitude()>15){
					photo.accuracy=(int) location.getAccuracy();
					photo.x=location.getLongitude();
					photo.y=location.getLatitude();
					photo.path=path;
					photo.time=location.getTime();
					tv.setText("定位成功");
				}else{
					tv.setText("定位失败");
					tv.setTextColor(Color.RED);
					iv_location.setImageResource(R.mipmap.location_fail);
				}
				iv.setTag(photo);
				photoList.add(photo);
				//代表拍照 已更新到photoList中了 
				isPhotoListFinnished=true;
//				if(loadedListenner!=null){
//					loadedListenner.loadedCallBack(photoType);
//				}
				query.id(iv).image(FilesUtils.rat(bm, photoResult.getSmallFileName()));
				LinearLayout.LayoutParams layParams=new LinearLayout.LayoutParams(width, height);
				layParams.leftMargin=space;
				lay.setLayoutParams(layParams);
				lay_views.addView(lay, index-1);
				//如果没有达到最大值 并且超过两张 
				if(photoList.size()<maxPhotoNum){
					if(lay_views.getChildCount()-1>2){
						lay_views.removeViewAt(0);
					}
				}
				//隐藏 照相按钮`
				if(photoList.size()==maxPhotoNum){
					iv_take.setVisibility(View.GONE);
				}
			}
		},0);
    }

    public void reStorePhoto(final Photo photo) {
		BasePhotoActivity a=(BasePhotoActivity) getActivity();
		if(a==null|| TextUtils.isEmpty(photo.fileName)){
			return;
		}
		final int index=lay_views.getChildCount();
		PhotoLayout lay=new PhotoLayout(a);
		if(isNeedLocation==1){
			lay.getLay_status().setVisibility(View.VISIBLE);
		}
		ImageView iv=lay.getImageView();
		final TextView tv=lay.getTv_location1();
		final ImageView iv_location=lay.getIv_location1();
		if(photo.y>15){
			tv.setText("定位成功");
		}else{
			tv.setText("定位失败");
			tv.setTextColor(Color.RED);
			iv_location.setImageResource(R.mipmap.location_fail);
		}
		iv.setTag(photo);
		iv.setId(imagViewId);
		iv.setScaleType(ScaleType.CENTER);
		iv.setOnClickListener(this);
		LinearLayout.LayoutParams layParams=new LinearLayout.LayoutParams(width, height);
		layParams.leftMargin=space;
		lay.setLayoutParams(layParams);
		lay_views.addView(lay, index-1);
		query.id(iv).image(new File(photo.fileName), true, width, new BitmapAjaxCallback(){
			@Override
			protected void callback(String url, ImageView iv, Bitmap bm,
                                    AjaxStatus status) {
				if(photoType==-1){
					return;
				}
				query.id(iv).image(FilesUtils.rat(bm, url));

			}
		},0);

   }

   @Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.iv_take:
			if(isPhotoListFinnished){
				takePhoto();
			}
			break;
		case imagViewId:
//			if(!isPhotoListFinnished){
//				return;
//			}
//			if(TextUtils.isEmpty(path)){
//				return;
//			}
//			if(task==null||task.taskStatus>0){
//				showToast("保存任务后不可查看大图");
//				return;
//			}
//			if(!isAllowEidt){
//				return;
//			}
//			Photo p=(Photo) v.getTag();
//			if(p==null){
//				return ;
//			}
//			FragmentActivity a=	getActivity();
//			if(a==null){
//				return;
//			}
//			PhotoListDto dto=new PhotoListDto();
//			dto.list=photoList;
//			String values= GsonUtils.toJson(dto);
//			int index= photoList.indexOf(p);
//			Intent intent=new Intent(getActivity(),LookPhotoActivity.class);
//			intent.putExtra("photoList", values);
//			intent.putExtra("currentIndexs", index);
//			intent.putExtra("path", path);
//			intent.putExtra("soureActivity", a.getClass().getName());
//			startActivityForResult(intent, Code.REQ_LookPhotoActivity_CODE);
//			isCallForResult=true;
//			isPhotoListFinnished=false;
			break;

		default:

			break;
		}
	 }

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		Log.i("Test", photoType+"_f_onActivityResult--->");
		isCallForResult=false;//恢复默认值
		if(data==null){
			return;
		}
		String json = data.getStringExtra("photoList");
//		PhotoListDto dto = GsonUtils.fromJson(json, PhotoListDto.class);
//		if(dto != null){
//			//因为可能发生了变化  清除所有的布局
//			lay_views.removeAllViews();
//			//重新添加 拍照按钮 并设置为可见
//			lay_views.addView(iv_take);
//			photoList.clear();
//			List<Photo> list=dto.list;
//			if(list!=null&&list.size()>0){
//				photoList.addAll(list);
//				initPhotos();
//			}
//			iv_take.setVisibility(isAllowEidt&&photoList.size()<maxPhotoNum?View.VISIBLE:View.INVISIBLE);
//			isPhotoListFinnished=true;
//			if(loadedListenner!=null){
//				loadedListenner.loadedCallBack(photoType);
//			}
//		}
	}

	/**
	 * 只有 保存了以后 才调用
	 */
	public void clearPhotoViews(){
		lay_views.removeAllViews();
		lay_views.addView(iv_take);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putInt("photoType", photoType);
		outState.putInt("isNeedLocation", isNeedLocation);
		outState.putInt("maxPhotoNum", maxPhotoNum);
		outState.putBoolean("isCallForResult", isCallForResult);
		outState.putBoolean("isCallTakePhotoResult", isCallTakePhotoResult);
		outState.putBoolean("isPhotoListFinnished", isPhotoListFinnished);
		outState.putBoolean("isAllowEidt", isAllowEidt);
		outState.putString("path", path);
//		PhotoListDto dto=new PhotoListDto();
//		dto.list=photoList;
//		outState.putString("Dto", GsonUtils.toJson(dto));
	}
	public List<Photo> getPhotoList(){
		return photoList;
	}

	public boolean isAllowEidt() {
		return isAllowEidt;
	}

	public void setAllowEidt(boolean isAllowEidt) {
		this.isAllowEidt = isAllowEidt;
		iv_take.setVisibility(isAllowEidt&&photoList.size()<maxPhotoNum? View.VISIBLE: View.INVISIBLE);
	}

	public void setPhotoList(List<Photo> photoList) {
		this.photoList = photoList;
	}

	/** 设置要监听的文件夹路径 */
	public void setFilePath(String filePath) {
		if(observer==null){
			observer = new FileObserver(filePath) {
				@Override
				public void onEvent(int event, String path) {
					if (event == FileObserver.MODIFY && (!PublicUtil.isFastDoubleClick())) {
						FragmentActivity a=	getActivity();
						if(a==null){
							return;
						}
						// 实时获取位置信息
//						location= MLocationManager.getInstance(a.getApplicationContext()).getRealTimeLocation(true);
						currentAngle=angle;
						currentDirection=direction;
					}
				}
			};
			observer.startWatching();
		}

	}

//	public PhotoListLoadListener getLoadedListenner() {
//		return loadedListenner;
//	}

//	public void setLoadedListenner(PhotoListLoadListener loadedListenner) {
//		this.loadedListenner = loadedListenner;
//	}

	public void onCannelTakePhoto() {
		if(!isOnDestory){//如果没有被回收过
//			if(loadedListenner!=null){
//				loadedListenner.loadedCallBack(photoType);
//			}
		}
		isOnDestory=false;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent sensorEvent) {
		float x,y,z;
		if (sensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION) {
			x = sensorEvent.values[0];
			y = sensorEvent.values[1];
			z = sensorEvent.values[2];

			if(Math.abs(z)>45){
				//横屏
				if (z > 0) {
					//左侧边横屏
					direction = x + 90;
					if (direction > 360) {
						direction = direction - 360;
					}
					if (y < 0 && y > -90) {
						angle = -(90 - z);
					} else if (y < -90 && y > -180) {
						angle = 90 - z;
					}
				} else {
					direction = x - 90;
					if (direction < 0) {
						direction = direction + 360;
					}
					if (y > 0 && y < 90) {
						angle = -(90 + z);
					} else if (y > 90 && y < 180) {
						angle = 90 + z;
					}
				}
			} else {

				direction = x;
				if (y > -90 && y < 90) {
					if (y < 0) {
						angle = -90 - y;
					} else {
						angle = -90 + y;
					}
				} else if (y > -180 && y < -90) {
					angle = -(90 + y);
				} else if (y > 90 && y < 180) {
					angle = -(90 - y);
				}

			}
		}
	}
}
