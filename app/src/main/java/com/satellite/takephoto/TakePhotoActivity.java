package com.satellite.takephoto;

import android.os.Bundle;

import com.autonavi.photosdk.entity.PhotoResult;
import com.satellite.takephoto.model.Photo;
import com.satellite.takephoto.util.PublicUtil;

import java.util.ArrayList;
import java.util.List;



/**
 * Created by leador_yang on 2017/8/15.
 */

public class TakePhotoActivity extends BasePhotoActivity{

    TakePhotoFragment f;
    List<Photo> photoList=new ArrayList<Photo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do_task);

        f=(TakePhotoFragment) getSupportFragmentManager().findFragmentById(R.id.fm_photo);
        String picPath= PublicUtil.getPicsPath();
        f.path=picPath;
        f.photoType=2;
        f.isNeedLocation=1;
        f.setAllowEidt(true);//未完成  允许编辑
    }

    @Override
    public void onTakePhotoResult(PhotoResult result) {
        if(f!=null){
            f.onTakePhotoResult(result);
        }
    }

    @Override
    public void onCannelTakePhoto() {
        if(f!=null){
            f.isPhotoListFinnished=true;
        }
    }
}
