package isymphonyz.ako.ako555.service;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Protocol;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import isymphonyz.ako.ako555.utils.AppPreference;
import isymphonyz.ako.ako555.utils.MyConfiguration;

@SuppressLint("NewApi")
public class AKO555PublicMediaService extends Service {

	Handler handler;

	//private String urlReadDeviceMedia = MyConfiguration.DOMAIN + MyConfiguration.VERSION + MyConfiguration.READ_DEVICE_MEDIA;
	private String urlReadDeviceMedia = MyConfiguration.DOMAIN + MyConfiguration.VERSION + MyConfiguration.READ_MEDIA_PUBLIC;
	String folderNamePublic = MyConfiguration.FOLDER_AKO555 + File.separator + MyConfiguration.FOLDER_PUBLIC_SPOT;
	String folderNamePrivate = MyConfiguration.FOLDER_AKO555 + File.separator + MyConfiguration.FOLDER_PRIVATE_SPOT;
	AppPreference appPreference;
	private ArrayList<String> mediaUrlList;
	private ArrayList<String> mediaNameList;
	private ArrayList<String> mediaImageList;
	private ArrayList<String> mediaSizeList;
	private ArrayList<String> mediaDurationList;
	private ArrayList<String> mediaPublicStatusList;

	String folderName = MyConfiguration.FOLDER_AKO555 + File.separator + MyConfiguration.FOLDER_PUBLIC_SPOT;
	String folderTempName = MyConfiguration.FOLDER_AKO555 + File.separator + MyConfiguration.FOLDER_PUBLIC_SPOT_TEMP;
	private DownloadManager downloadManager;
	private long downloadReference;
	ArrayList<String> files;
	ArrayList<String> videoOnlineNameList;
	ArrayList<String> presentationList;
	ArrayList<Boolean> downloadStatusList;
	ArrayList<Boolean> isPublicStatusList;

	private static File rootToPublic;
	private static File rootToPrivate;
	private File temp;
	
	private ArrayList<String> localMediaFileList;
	private ArrayList<String> localPrivateMediaFileList;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		Log.d("AKO555SERVICE", "PublicMediaService Start");
		//ToastMessage.show(this, "AKO555PublicMediaService is onCreate");
		rootToPublic = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + folderNamePublic + File.separator);
		rootToPublic.mkdirs();
		
		rootToPrivate = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + folderNamePrivate + File.separator);
		rootToPrivate.mkdirs();
		
		appPreference = AppPreference.getInstance(this);
		Log.d("AKO555SERVICE", "device-id: " + appPreference.getDeviceID());
		Log.d("AKO555SERVICE", "-app-version: " + appPreference.getAppVersion());
		Log.d("AKO555SERVICE", "-system-user-id: " + appPreference.getSystemUserMobileID());
		//fileTimeStamp = appPreference.getFileTimeStamp();

		localMediaFileList = new ArrayList<String>();
		localPrivateMediaFileList = new ArrayList<String>();
		mediaNameList = new ArrayList<String>();
		mediaImageList = new ArrayList<String>();
		mediaSizeList = new ArrayList<String>();
		mediaDurationList = new ArrayList<String>();
		mediaUrlList = new ArrayList<String>();
		mediaPublicStatusList = new ArrayList<String>();
		
		handler = new Handler(){

	        @Override
	        public void handleMessage(Message msg) {
	            // TODO Auto-generated method stub
	            super.handleMessage(msg);
	    		getListFileFromSDCard();
	            //Toast.makeText(AKO555PublicMediaService.this, "555", Toast.LENGTH_SHORT).show();
				OkHttpClient okHttpClient = new OkHttpClient();
				okHttpClient.setProtocols(Arrays.asList(Protocol.HTTP_1_1));

				com.squareup.okhttp.Request.Builder builder = new com.squareup.okhttp.Request.Builder();
				RequestBody formBody = new FormEncodingBuilder()
						.add("device-id", appPreference.getDeviceID())
						.add("-system-user-id", appPreference.getSystemUserMobileID())
						.add("-app-version", appPreference.getAppVersion())
						.build();
				com.squareup.okhttp.Request request = builder.url(urlReadDeviceMedia).post(formBody).build();
				//Request request = builder.url(url).get().build();
				okHttpClient.newCall(request).enqueue(new com.squareup.okhttp.Callback() {
					@Override
					public void onFailure(com.squareup.okhttp.Request request, IOException e) {
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
						try {
							setMedia(result);
						} catch(Exception e) {

						}
					}
				});
			}

		};

		new Thread(new Runnable() {

			public void run() {
	    		// TODO Auto-generated method stub
	    		while(true) {
	    			try {
	    				//Thread.sleep(300000);
	    				handler.sendEmptyMessage(0);
						Thread.sleep(300000);
	    			} catch (InterruptedException e) {
	    				// TODO Auto-generated catch block
	    				e.printStackTrace();
	    				//showNotification("Exception: " + e.toString());
	    			} 

	    		}
	    	}
	    }).start();
	}

	@Override
	public void onDestroy() {
		// Toast.makeText(this, "My Service Stopped", Toast.LENGTH_LONG).show();
		//ToastMessage.show(this, "AKO555PublicMediaService is onDestroy");
	}

	@Override
	public void onStart(Intent intent, int startid) {
		//ToastMessage.show(this, "AKO555PublicMediaService is onStart");
	}
	
	public void getListFileFromSDCard() {
		String[] arrayName = rootToPublic.list();
		String[] privateMediaName = rootToPrivate.list();
		//Toast.makeText(this, "ArrayName: " + arrayName.length, Toast.LENGTH_SHORT).show();
		//mobilefileList = (ArrayList<String>) Arrays.asList(arrayName);
		localMediaFileList = null;
		localMediaFileList = new ArrayList<String>(Arrays.asList(arrayName));
		localPrivateMediaFileList = null;
		localPrivateMediaFileList = new ArrayList<String>(Arrays.asList(privateMediaName));
		for(int x=0; x<localPrivateMediaFileList.size(); x++) {
			//localMediaFileList.add(localPrivateMediaFileList.get(x));
		}
		//Toast.makeText(this, "MobileFileName: " + mobilefileList.size(), Toast.LENGTH_SHORT).show();
	}

	public void setMedia(String result) {
		// TODO Auto-generated method stub
		Log.d("AKO555SERVICE", "setMedia: " + result);

		mediaNameList.clear();
		mediaImageList.clear();
		mediaSizeList.clear();
		mediaDurationList.clear();
		mediaUrlList.clear();
		mediaPublicStatusList.clear();
		
		try {
			JSONObject jObj = new JSONObject(result);
			JSONArray jArrayData = jObj.getJSONArray("data");
			for(int x=0; x<jArrayData.length(); x++) {
				mediaNameList.add(jArrayData.getJSONObject(x).getString("title-text"));
				mediaImageList.add(jArrayData.getJSONObject(x).getString("title-image"));
				mediaSizeList.add(jArrayData.getJSONObject(x).getString("size"));
				mediaDurationList.add(jArrayData.getJSONObject(x).getString("duration"));
				mediaUrlList.add(jArrayData.getJSONObject(x).getString("url"));
				mediaPublicStatusList.add(jArrayData.getJSONObject(x).getString("status-public"));
				Log.d("AKO555SERVICE", "x: " + x);
			}

			prepareMediaDownload();

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d("AKO555SERVICE", "JSONException: " + e.toString());
		}
	}
	
	public void prepareMediaDownload() {

		downloadManager = (DownloadManager) this.getSystemService(this.DOWNLOAD_SERVICE);
        this.getApplicationContext().registerReceiver(downloadReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        rootToPublic = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + folderNamePublic);
        rootToPublic.mkdirs();

        rootToPrivate = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + folderNamePrivate);
        rootToPrivate.mkdirs();
        
        boolean isDuplicate = false;
        String fileName = "";
		String[] videoOfflineNameArray = rootToPublic.list();
		String[] privateVideoOfflineNameArray = rootToPrivate.list();
		String[] videoTempNameArray = rootToPublic.list();
		
		int lastIndex = videoOfflineNameArray.length;
		for(int x=0; x<privateVideoOfflineNameArray.length; x++) {
			//videoOfflineNameArray[lastIndex] = privateVideoOfflineNameArray[x];
			lastIndex++;
		}
		
		getListFileFromSDCard();
		
        videoOnlineNameList = null;
        videoOnlineNameList = new ArrayList<String>();
        presentationList = null;
        presentationList = new ArrayList<String>();
        downloadStatusList = null;
        downloadStatusList = new ArrayList<Boolean>();
        isPublicStatusList = null;
        isPublicStatusList = new ArrayList<Boolean>();
        
        /*boolean isExist = false;
        for(int x=0; x<localMediaFileList.size(); x++) {
        	for(int y=0; y<mediaNameList.size(); y++) {
        		if(localMediaFileList.get(x).equals(mediaNameList.get(y))) {
					isExist = true;
				}
        	}
			if(!isExist) {
				Log.d("AKO555SERVICE", "Delete File: " + localMediaFileList.get(x));
				//delete file
				//Toast.makeText(this, "Delete MobileFileName: " + mobilefileList.get(x), Toast.LENGTH_SHORT).show();
				final ConnectivityManager conMgr =  (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
				final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
				if (activeNetwork != null && activeNetwork.isConnected()) {
				    //notify user you are online
					try {
						File rootPublic = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + folderNamePublic + File.separator);
						File filePublic = new File(rootPublic, localMediaFileList.get(x));
						filePublic.delete();
						
						File rootPrivate = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + folderNamePrivate + File.separator);
						File filePrivate = new File(rootPrivate, localMediaFileList.get(x));
						filePrivate.delete();
					} catch(Exception e) {
						
					}
					Log.d("AKO555SERVICE", "Network is connected");
				} else {
				    //notify user you are not online
					Log.d("AKO555SERVICE", "Network is not connected");
				}
			} else {
				Log.d("AKO555SERVICE", "Not Delete File: " + localMediaFileList.get(x));
				//Toast.makeText(this, "Found MobileFileName: " + mobilefileList.get(x), Toast.LENGTH_SHORT).show();
			}
			isExist = false;
        }*/
        
        for(int x=0; x<mediaUrlList.size(); x++) {
        	fileName = mediaUrlList.get(x).split("/")[mediaUrlList.get(x).split("/").length-1];
        	if(fileName.toLowerCase().endsWith(".mp4") || fileName.toLowerCase().endsWith(".avi") || fileName.toLowerCase().endsWith(".3gp") || fileName.toLowerCase().endsWith(".wmv") || fileName.toLowerCase().endsWith(".mp3") || fileName.toLowerCase().endsWith(".wav") || fileName.toLowerCase().endsWith(".mpeg")) {
    			videoOnlineNameList.add(mediaNameList.get(x));
    			downloadStatusList.add(true);
    			/*if(mediaPublicStatusList.get(x).equals("1")) {
        			isPublicStatusList.add(true);
    			} else {
        			isPublicStatusList.add(false);
    			}*/
    		}
        }
        
        /*for(int x=0; x<videoOfflineNameArray.length; x++) {
        	for(int y=0; y<videoOnlineNameList.size(); y++) {
        		if(videoOnlineNameList.get(y).equals(videoOfflineNameArray[x])) {
        			downloadStatusList.set(y, false);
        		}
        	}
        }*/

		for(int x=0; x<videoOnlineNameList.size(); x++) {
			for(int y=0; y<videoOfflineNameArray.length; y++) {
				if(videoOnlineNameList.get(x).equals(videoOfflineNameArray[y])) {
					downloadStatusList.set(x, false);
				}
			}
		}
        
        /*if(localMediaFileList.size() > 0) {
			for(int x=0; x<localMediaFileList.size(); x++) {
				for(int y=0; y<videoOnlineNameList.size(); y++) {
					if(videoOnlineNameList.get(y).equals(localMediaFileList.get(x))) {
						downloadStatusList.set(y, false);
					}
				}
			}
		} else {
			Log.d("AKO555SERVICE", "Size of mediaUrlList: " + mediaUrlList.size());
			Log.d("AKO555SERVICE", "Size of downloadStatusList: " + downloadStatusList.size());
			for(int x=0; x<mediaUrlList.size(); x++) {
				Log.d("AKO555SERVICE", "mediaURL: " + mediaUrlList.get(x));
				downloadStatusList.add(true);
			}
		}*/
        for(int x=0; x<downloadStatusList.size(); x++) {
			Log.d("AKO555SERVICE", "downloadStatus: " + downloadStatusList.get(x));
        	if(downloadStatusList.get(x)) {
				Log.d("AKO555SERVICE", "Position: " + x);
        		//String videoURL = "http://58.64.37.251/ako555content/content/media/" + Uri.encode(videoOnlineNameList.get(x));
        		String videoURL = mediaUrlList.get(x);
				Log.d("AKO555SERVICE", "mediaUrlList: " + mediaUrlList.get(x));
        		downloadManager = (DownloadManager) this.getSystemService(this.DOWNLOAD_SERVICE);
        		//Uri Download_Uri = Uri.parse(videoURL + presentationList.get(arg2));
        		Uri Download_Uri = Uri.parse(videoURL);
        		Request request = new Request(Download_Uri);
        		
        		//request.setMimeType(Mim)
        		
        		//Restrict the types of networks over which this download may proceed.
        		request.setAllowedNetworkTypes(Request.NETWORK_WIFI | Request.NETWORK_MOBILE);
        		//Set whether this download may proceed over a roaming connection.
        		request.setAllowedOverRoaming(false);
        		//Set the title of this download, to be displayed in notifications (if enabled).
        		//request.setTitle(videoOnlineNameList.get(x));
        		request.setTitle(mediaNameList.get(x));
        		//Set a description of this download, to be displayed in notifications (if enabled)
        		//request.setDescription("Android Data download using DownloadManager.");
        		//request.setDescription("Download: " + presentationList.get(arg2));
        		//request.setDescription("Download: " + videoOnlineNameList.get(x));
        		request.setDescription("Downloading: " + mediaNameList.get(x));
        		//Set the local destination for the downloaded file to a path within the application's external files directory
        		//request.setDestinationInExternalFilesDir(PCConsultPresentation.this, Environment.DIRECTORY_DOWNLOADS, presentationList.get(arg2));
        		//request.setDestinationInExternalPublicDir(File.separator + folderName, presentationList.get(arg2));
        		//request.setDestinationInExternalPublicDir(File.separator + folderName, mediaNameList.get(x));
        		/*if(isPublicStatusList.get(x)) {
        			Log.d("AKO555SERVICE", "Downloading: " + folderNamePublic + ": " + mediaNameList.get(x));
            		request.setDestinationInExternalPublicDir(File.separator + folderNamePublic, mediaNameList.get(x));
        		} else {
        			Log.d("AKO555SERVICE", "Downloading: " + folderNamePrivate + ": " + mediaNameList.get(x));
            		request.setDestinationInExternalPublicDir(File.separator + folderNamePrivate, mediaNameList.get(x));
        		}*/
				Log.d("AKO555SERVICE", "Downloading: " + folderNamePublic + ": " + mediaNameList.get(x));
				request.setDestinationInExternalPublicDir(File.separator + folderNamePublic, mediaNameList.get(x));
        		request.setNotificationVisibility(Request.VISIBILITY_VISIBLE);
        		request.setVisibleInDownloadsUi(true);
        		//Log.d("AKO555SERVICE", "Path: " + rootToPublic.getPath());
        		
        		//Enqueue a new download and same the referenceId
        		downloadReference = downloadManager.enqueue(request);
        	}
        }
	}
	
	private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
	        if (!DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
	        	//Log.d("PCConsult", "Download");
	            return;
	        }
	        //getActivity().getApplicationContext().unregisterReceiver(downloadReceiver);
	        //getActivity().unregisterReceiver(downloadReceiver);
	        Query query = new Query();
	        query.setFilterById(downloadReference);
	        Cursor c = downloadManager.query(query);
	        if (c.moveToFirst()) {
	            int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
	            if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
	                String uriString = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
	                //Log.d("AKO555SERVICE", "Downloaded file " + uriString);   
	                Intent videoIntent = new Intent();
                    videoIntent.setAction(Intent.ACTION_VIEW);
					videoIntent.setDataAndType(Uri.parse(uriString), "video/*");
					//Log.d("AKO555SERVICE", "File: " + Uri.parse(uriString));
					//startActivity(videoIntent);
	            } else {
	                Log.i("AKO555SERVICE", "download failed " + c.getInt(columnIndex));
	            }
	        }
		}
	};

}
