package com.satellite.takephoto.model;

import com.google.gson.annotations.SerializedName;

/**
 * 照片实体
 * @author chuangbo.cheng
 */
public class Photo {
    public int id = -1;

    /** 站任务主键本地ID **/
    @SerializedName("stopTaskId")
    public int stopTaskId = 1;

    /** stopBoardId **/
    @SerializedName("stopBoardId")
    public int stopBoardId = -1;

    /** 照片类型 0站台   1站桩  2站牌   3任务不存在顺流照片  4任务不存在逆流照片   **/
    @SerializedName("photoType")
    public int photoType = -1;

    /** 照片名称 **/
    @SerializedName("fileName")
    public String fileName;

    /** 照片路径 **/
    @SerializedName("path")
    public String path;

    /** 定位的经度lon **/
    public double x = -1;

    /** 定位的纬度lat **/
    public double y = -1;

    /** 定位的精度 **/
    public int accuracy = -1;

    /** 拍照的方向 **/
    public int orientation = -1;

    /** 拍照的角度 **/
    public int angle = -1;

    /** 拍照的时间 */
    public long time = -1;

}
