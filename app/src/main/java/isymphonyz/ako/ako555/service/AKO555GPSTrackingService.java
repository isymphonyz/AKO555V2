package isymphonyz.ako.ako555.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Protocol;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.Arrays;

import isymphonyz.ako.ako555.utils.AppPreference;
import isymphonyz.ako.ako555.utils.MyConfiguration;

@SuppressLint("LongLogTag")
public class AKO555GPSTrackingService extends Service implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

	// LogCat tag
	private static final String TAG = "AKO555GPSTrackingService";

	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

	//private Location mFirstLocation;
	private Location mLastLocation;

	// Google client to interact with Google API
	private GoogleApiClient mGoogleApiClient;

	// boolean flag to toggle periodic location updates
	private boolean mRequestingLocationUpdates = false;

	private LocationRequest mLocationRequest;

	// Location updates intervals in sec
	private static int UPDATE_INTERVAL = 5000; // 5 sec
	private static int FATEST_INTERVAL = 5000; // 5 sec
	private static int DISPLACEMENT = 10; // 10 meters

	private AppPreference appPreference;
	String memberID = "";
	String deviceID = "";
	String systemUserMobileID = "";
	String mediaID = "";
	String mediaPosition = "";
	String projectID = "";
	String urlTracking = MyConfiguration.DOMAIN + MyConfiguration.VERSION + MyConfiguration.TRACKING_URL;
	boolean allowUpdateToServer = true;

	@Override
	public void onCreate() {

		appPreference = new AppPreference(this);

		// First we need to check availability of play services
		if (checkPlayServices()) {
			Log.d("AKO555", "checkPlayServices == true");

			// Building the GoogleApi client
			buildGoogleApiClient();

			createLocationRequest();
		} else {
			Log.d("AKO555", "checkPlayServices == false");
		}

		if (mGoogleApiClient != null) {
			mGoogleApiClient.connect();
			Log.d("AKO555", "mGoogleApiClient.connect()");
		}

		//mGoogleApiClient = AKO555Home.mGoogleApiClient;
		//mLocationRequest = AKO555Home.mLocationRequest;
	}

	@Override
	public void onStart(Intent intent, int startid) {
		//Toast.makeText(this, "My Service Started", Toast.LENGTH_LONG).show();
		Log.d(TAG, "V2 onStart");

		mRequestingLocationUpdates = true;

		Log.d(TAG, "Periodic location updates started!");

		//checkPlayServices();

		if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
			startLocationUpdates();
		}
	}

	@Override
	public void onDestroy() {
		//Toast.makeText(this, "My Service Stopped", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onDestroy");
		stopLocationUpdates();
		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
	}

	/**
	 * Method to display the location on UI
	 * */
	private void displayLocation() {
		Log.d("AKO555", "displayLocation");
		//mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

		if (mLastLocation != null) {
			Log.d("AKO555", "mLastLocation != null");
			double latitude = mLastLocation.getLatitude();
			double longitude = mLastLocation.getLongitude();

			//lblLocation.setText(latitude + ", " + longitude);
			if(allowUpdateToServer) {
				updateLocationToServer(mLastLocation);
			}
			//Toast.makeText(getApplicationContext(), latitude + ", " + longitude, Toast.LENGTH_LONG).show();

		} else {
			Log.d("AKO555", "mLastLocation == null");
			//lblLocation.setText("(Couldn't get the location. Make sure location is enabled on the device)");

		}
	}

	/**
	 * Method to toggle periodic location updates
	 * */
	private void togglePeriodicLocationUpdates() {
		if (!mRequestingLocationUpdates) {
			// Changing the button text
			//btnStartLocationUpdates.setText(getString(R.string.btn_stop_location_updates));

			mRequestingLocationUpdates = true;

			// Starting the location updates
			startLocationUpdates();

			Log.d(TAG, "Periodic location updates started!");

		} else {
			// Changing the button text
			//btnStartLocationUpdates.setText(getString(R.string.btn_start_location_updates));

			mRequestingLocationUpdates = false;

			// Stopping the location updates
			stopLocationUpdates();

			Log.d(TAG, "Periodic location updates stopped!");
		}
	}

	/**
	 * Creating google api client object
	 * */
	protected synchronized void buildGoogleApiClient() {
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addApi(LocationServices.API)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.build();
		Log.d("AKO555", "buildGoogleApiClient == success");
	}

	/**
	 * Creating location request object
	 * */
	protected void createLocationRequest() {
		mLocationRequest = new LocationRequest();
		mLocationRequest.setInterval(UPDATE_INTERVAL);
		mLocationRequest.setFastestInterval(FATEST_INTERVAL);
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
		Log.d("AKO555", "createLocationRequest == success");
	}

	/**
	 * Method to verify google play services on the device
	 * */
	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				//GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Toast.makeText(getApplicationContext(), "This device is not supported.", Toast.LENGTH_LONG).show();
			}
			return false;
		}
		return true;
	}

	/**
	 * Starting the location updates
	 * */
	protected void startLocationUpdates() {
		LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
	}

	/**
	 * Stopping location updates
	 */
	protected void stopLocationUpdates() {
		LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
	}

	/**
	 * Google api callback methods
	 */
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
	}

	@Override
	public void onConnected(Bundle arg0) {
		Log.d("AKO555", "onConnected");

		mLastLocation = LocationServices.FusedLocationApi
				.getLastLocation(mGoogleApiClient);

		// Resuming the periodic location updates
		if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
			startLocationUpdates();
		} else {
			Log.d("AKO555", "mGoogleApiClient.isConnected(): " + mGoogleApiClient.isConnected());
			Log.d("AKO555", "mRequestingLocationUpdates: " + mRequestingLocationUpdates);
		}

		// Once connected with google api, get the location
		displayLocation();

		if (mRequestingLocationUpdates) {
			startLocationUpdates();
		}
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		Log.d("AKO555", "onConnectionSuspended");
		mGoogleApiClient.connect();
	}

	@Override
	public void onLocationChanged(Location location) {
		// Assign the new location
		mLastLocation = location;

		//Toast.makeText(getApplicationContext(), "Location changed!", Toast.LENGTH_SHORT).show();

		// Displaying the new location on UI
		displayLocation();
	}

	public void updateLocationToServer(Location mLastLocation) {

		memberID = appPreference.getMemberID();

		if(memberID.length() > 0) {
			try {
				allowUpdateToServer = false;
				double latitude = mLastLocation.getLatitude();
				double longitude = mLastLocation.getLongitude();
				double speed = mLastLocation.getSpeed();
				double bearing = mLastLocation.getBearing();
				double accuracy = mLastLocation.getAccuracy();
				String provider = mLastLocation.getProvider();
				appPreference.setLatitude((float)latitude);
				appPreference.setLongitude((float)longitude);

				deviceID = appPreference.getDeviceID();
				systemUserMobileID = appPreference.getSystemUserMobileID();
				mediaID = appPreference.getMediaID();
				mediaPosition = appPreference.getMediaPosition();
				projectID = appPreference.getProjectID();

				Log.d(TAG, "##################");
				//Log.d(TAG, "Start: " + mFirstLocation.getLatitude() + ", " + mFirstLocation.getLongitude());
				Log.d(TAG, "End: " + mLastLocation.getLatitude() + ", " + mLastLocation.getLongitude());
				Log.d(TAG, "==================");

				//double distance = mFirstLocation.distanceTo(mLastLocation);
				double distance = mLastLocation.distanceTo(mLastLocation);

				projectID = appPreference.getProjectID();
				Log.d(TAG, "device-id: " + deviceID);
				Log.d(TAG, "project-id: " + projectID);
				Log.d(TAG, "provider: " + provider);
				Log.d(TAG, "lattitude: " + latitude);
				Log.d(TAG, "longitude: " + longitude);
				Log.d(TAG, "distance: " + distance);
				Log.d(TAG, "speed: " + speed);
				Log.d(TAG, "bearing: " + bearing);
				Log.d(TAG, "accuracy: " + accuracy);

				//gpsTracker = null;

				//mobileTrackingIntervalLocation = Integer.parseInt(appPreference.getMobileTrackingIntervalLocation()) / 1000;

				String message = "Lat: " + latitude + ", Lng: " + longitude;
				Log.d(TAG, message);

				OkHttpClient okHttpClient = new OkHttpClient();
				okHttpClient.setProtocols(Arrays.asList(Protocol.HTTP_1_1));

				Request.Builder builder = new Request.Builder();
				RequestBody formBody = new FormEncodingBuilder()
						.add("device-id", deviceID)
						.add("-system-user-id", systemUserMobileID)
						.add("-app-version", appPreference.getAppVersion())
						.add("lattitude", "" + latitude)
						.add("longitude", "" + longitude)
						.add("media-id", mediaID)
						.add("media-position", mediaPosition)
						.add("member-id", memberID)
						.add("project-id", projectID)
						.add("speed-device", "" + speed)
						.add("bearing", "" + bearing)
						.add("accuracy", "" + accuracy)
						.build();
				Request request = builder.url(urlTracking).post(formBody).build();
				//Request request = builder.url(url).get().build();
				okHttpClient.newCall(request).enqueue(new Callback() {
					@Override
					public void onFailure(Request request, IOException e) {
						connectionCallBack("Error - " + e.getMessage());
					}

					@Override
					public void onResponse(Response response) {
						if (response.isSuccessful()) {
							try {
								connectionCallBack(response.body().string());
							} catch (IOException e) {
								e.printStackTrace();
								connectionCallBack("Error - " + e.getMessage());
							}
						} else {
							connectionCallBack("Not Success - code : " + response.code());
						}
					}

					public void connectionCallBack(final String result) {
						try{
							setTracking(result);
						} catch(Exception e) {

						}
					}
				});
			} catch(Exception e) {
				Log.d(TAG, "Exception: " + e.toString());
				allowUpdateToServer = true;
			}
		} else {
			Log.d(TAG, "Member == 0");
		}
	}

	public void setTracking(String result) {
		// TODO Auto-generated method stub
		Log.d(TAG, "Result: " + result);
		allowUpdateToServer = true;
		//mFirstLocation = mLastLocation;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	public static boolean isLocationEnabled(Context context) {
		int locationMode = 0;
		String locationProviders;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
			try {
				locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

			} catch (Settings.SettingNotFoundException e) {
				e.printStackTrace();
			}

			return locationMode != Settings.Secure.LOCATION_MODE_OFF;

		}else{
			locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
			return !TextUtils.isEmpty(locationProviders);
		}
	}

}
