package com.satellite.takephoto.model;
/**
 * 
 * @author chuangbo.cheng
 *
 */
public class ErrorLog {
String id;
String type="1";
String version;
String userName;
String appVersion;
String phoneVersion;
String phoneBrand;
String phoneModel;
String phoneIMEI;
String eventTime;
String message;//
String exception;
public String getType() {
	return type;
}
public void setType(String type) {
	this.type = type;
}
public String getVersion() {
	return version;
}
public void setVersion(String version) {
	this.version = version;
}
public String getUserName() {
	return userName;
}
public void setUserName(String userName) {
	this.userName = userName;
}
public String getAppVersion() {
	return appVersion;
}
public void setAppVersion(String appVersion) {
	this.appVersion = appVersion;
}
public String getPhoneVersion() {
	return phoneVersion;
}
public void setPhoneVersion(String phoneVersion) {
	this.phoneVersion = phoneVersion;
}
public String getPhoneBrand() {
	return phoneBrand;
}
public void setPhoneBrand(String phoneBrand) {
	this.phoneBrand = phoneBrand;
}
public String getPhoneModel() {
	return phoneModel;
}
public void setPhoneModel(String phoneModel) {
	this.phoneModel = phoneModel;
}
public String getPhoneIMEI() {
	return phoneIMEI;
}
public void setPhoneIMEI(String phoneIMEI) {
	this.phoneIMEI = phoneIMEI;
}
public String getEventTime() {
	return eventTime;
}
public void setEventTime(String eventTime) {
	this.eventTime = eventTime;
}
public String getMessage() {
	return message;
}
public void setMessage(String message) {
	this.message = message;
}
public String getException() {
	return exception;
}
public void setException(String exception) {
	this.exception = exception;
}
public String getId() {
	return id;
}
public void setId(String id) {
	this.id = id;
}



}

/**
 * type	String	类型
version	String	产品版本
userName	String	用户名
appVersion	String	App版本号
phoneVersion	String	手机系统版本
phoneBrand	String	手机品牌
phoneModel	String	手机型号
phoneIMEI	String	手机标识码
eventTime	long	错误发生时间戳
message	String	异常信息描述
exception	String	异常堆栈信息

 */


