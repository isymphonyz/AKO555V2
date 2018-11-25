package isymphonyz.ako.ako555.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPreference {
	private final String preferenceName = "ako555";
	private final String APP_VERSION = "appVersion";
	private final String USERNAME = "username";
	private final String PASSWORD = "password";
	private final String MOBILE_DEVICE_NO = "mobile_device_no";
	private final String MOBILE_DEVICE_ID = "mobile_device_id";
	private final String MEMBER_ID = "member_id";
	private final String DEVICE_ID = "device_id";
	private final String MEDIA_ID = "media_id";
	private final String MEDIA_POSITION = "media_position";
	private final String PROJECT_ID = "project_id";
	private final String SYSTEM_USERMOBILE_ID = "system_usermobile_id";
	private final String MOBILE_TRACKING_INTERVAL_LOCATION = "mobile_tracking_interval_location";
	private final String LATITUDE = "latitude";
	private final String LONGITUDE = "longitude";
	private final String FOREGROUND_STATUS = "foreground_status";
	
	private SharedPreferences preference = null;
	private Context context = null;
	private static AppPreference appPreference = null;
	
	public AppPreference(Context context) {
		this.context = context;
		int mode = Context.MODE_PRIVATE;
		this.preference = this.context.getSharedPreferences(this.preferenceName, mode);
	}
	
	public static AppPreference getInstance(Context context) {
		if(appPreference == null) {
			appPreference = new AppPreference(context);
		}
		return appPreference;
	}
	
	public void setAppVersion(String appVersino) {
		SharedPreferences.Editor editor = this.preference.edit();
		editor.putString(this.APP_VERSION, appVersino);
		editor.commit();
	}
	
	public String getAppVersion() {
		String appVersino = this.preference.getString(this.APP_VERSION, "");
		return appVersino;
	}

	public void setUsername(String username) {
		SharedPreferences.Editor editor = this.preference.edit();
		editor.putString(this.USERNAME, username);
		editor.commit();
	}

	public String getUsername() {
		String username = this.preference.getString(this.USERNAME, "");
		return username;
	}
	
	public void setPassword(String password) {
		SharedPreferences.Editor editor = this.preference.edit();
		editor.putString(this.PASSWORD, password);
		editor.commit();
	}
	
	public String getPassword() {
		String password = this.preference.getString(this.PASSWORD, "");
		return password;
	}
	
	public void setMobileDeviceNo(String mobileDeviceNo) {
		SharedPreferences.Editor editor = this.preference.edit();
		editor.putString(this.MOBILE_DEVICE_NO, mobileDeviceNo);
		editor.commit();
	}
	
	public String getMobileDeviceNo() {
		String mobileDeviceNo = this.preference.getString(this.MOBILE_DEVICE_NO, "");
		return mobileDeviceNo;
	}
	
	public void setMobileDeviceID(String mobileDeviceID) {
		SharedPreferences.Editor editor = this.preference.edit();
		editor.putString(this.MOBILE_DEVICE_ID, mobileDeviceID);
		editor.commit();
	}
	
	public String getMobileDeviceID() {
		String mobileDeviceID = this.preference.getString(this.MOBILE_DEVICE_ID, "");
		return mobileDeviceID;
	}
	
	public void setMemberID(String memberID) {
		SharedPreferences.Editor editor = this.preference.edit();
		editor.putString(this.MEMBER_ID, memberID);
		editor.commit();
	}
	
	public String getMemberID() {
		String memberID = this.preference.getString(this.MEMBER_ID, "");
		return memberID;
	}
	
	public void setDeviceID(String deviceID) {
		SharedPreferences.Editor editor = this.preference.edit();
		editor.putString(this.DEVICE_ID, deviceID);
		editor.commit();
	}
	
	public String getDeviceID() {
		String deviceID = this.preference.getString(this.DEVICE_ID, "");
		return deviceID;
	}
	
	public void setMediaID(String mediaID) {
		SharedPreferences.Editor editor = this.preference.edit();
		editor.putString(this.MEDIA_ID, mediaID);
		editor.commit();
	}
	
	public String getMediaID() {
		String mediaID = this.preference.getString(this.MEDIA_ID, "");
		return mediaID;
	}
	
	public void setMediaPosition(String mediaPosition) {
		SharedPreferences.Editor editor = this.preference.edit();
		editor.putString(this.MEDIA_POSITION, mediaPosition);
		editor.commit();
	}
	
	public String getMediaPosition() {
		String mediaPosition = this.preference.getString(this.MEDIA_POSITION, "0");
		return mediaPosition;
	}
	
	public void setProjectID(String projectID) {
		SharedPreferences.Editor editor = this.preference.edit();
		editor.putString(this.PROJECT_ID, projectID);
		editor.commit();
	}
	
	public String getProjectID() {
		String projectID = this.preference.getString(this.PROJECT_ID, "");
		return projectID;
	}
	
	public void setSystemUserMobileID(String systemUserMobileID) {
		SharedPreferences.Editor editor = this.preference.edit();
		editor.putString(this.SYSTEM_USERMOBILE_ID, systemUserMobileID);
		editor.commit();
	}
	
	public String getSystemUserMobileID() {
		String systemUserMobileID = this.preference.getString(this.SYSTEM_USERMOBILE_ID, "");
		return systemUserMobileID;
	}
	
	public void setMobileTrackingIntervalLocation(String mobileTrackingIntervalTracking) {
		SharedPreferences.Editor editor = this.preference.edit();
		editor.putString(this.MOBILE_TRACKING_INTERVAL_LOCATION, mobileTrackingIntervalTracking);
		editor.commit();
	}
	
	public String getMobileTrackingIntervalLocation() {
		String mobileTrackingIntervalTracking = this.preference.getString(this.MOBILE_TRACKING_INTERVAL_LOCATION, "0");
		return mobileTrackingIntervalTracking;
	}

	public void setLatitude(float latitude) {
		SharedPreferences.Editor editor = this.preference.edit();
		editor.putFloat(this.LATITUDE, latitude);
		editor.commit();
	}

	public float getLatitude() {
		float latitude = this.preference.getFloat(this.LATITUDE, 0);
		return latitude;
	}

	public void setLongitude(float longitude) {
		SharedPreferences.Editor editor = this.preference.edit();
		editor.putFloat(this.LONGITUDE, longitude);
		editor.commit();
	}

	public float getLongitude() {
		float longitude = this.preference.getFloat(this.LONGITUDE, 0);
		return longitude;
	}

	public void setForegroundStatus(boolean foregroundStatus) {
		SharedPreferences.Editor editor = this.preference.edit();
		editor.putBoolean(this.FOREGROUND_STATUS, foregroundStatus);
		editor.commit();
	}

	public boolean getForegroundStatus() {
		boolean foregroundStatus = this.preference.getBoolean(this.FOREGROUND_STATUS, false);
		return foregroundStatus;
	}
}
