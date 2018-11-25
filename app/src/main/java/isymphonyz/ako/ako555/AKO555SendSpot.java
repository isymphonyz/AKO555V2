package isymphonyz.ako.ako555;

import android.app.AlertDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Protocol;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import isymphonyz.ako.ako555.utils.AppPreference;
import isymphonyz.ako.ako555.utils.MyConfiguration;
import isymphonyz.ako.ako555.utils.ToastMessage;

public class AKO555SendSpot extends AppCompatActivity {

    /*@Bind(R.id.progressBar) ProgressBar progressBar;
    @Bind(R.id.inputSpotTitle) EditText inputSpotTitle;
    @Bind(R.id.inputSpotDescription) EditText inputSpotDescription;
    @Bind(R.id.btnSend) Button btnSend;*/

    private ProgressBar progressBar;
    private EditText inputSpotTitle;
    private EditText inputSpotDescription;
    private Button btnAddImage;
    private LinearLayout layoutImage;
    private LinearLayout.LayoutParams params;
    private Button btnSend;
    private Typeface tf;

    private AppPreference appPreference;

    private String urlInsertMediaRequest = MyConfiguration.DOMAIN + MyConfiguration.VERSION + MyConfiguration.INSERT_MEDIA_REQUEST;

    int REQUEST_CAMERA = 0, SELECT_FILE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_spot);
        //ButterKnife.bind(this);

        tf = Typeface.createFromAsset(getAssets(), "fonts/rsu-light.ttf");

        appPreference = new AppPreference(this);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        inputSpotTitle = (EditText) findViewById(R.id.inputSpotTitle);
        inputSpotDescription = (EditText) findViewById(R.id.inputSpotDescription);
        layoutImage = (LinearLayout) findViewById(R.id.layoutImage);
        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        inputSpotTitle.setTypeface(tf);
        inputSpotDescription.setTypeface(tf);

        btnAddImage = (Button) findViewById(R.id.btnAddImage);
        btnAddImage.setTypeface(tf);
        btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        btnSend = (Button) findViewById(R.id.btnSend);
        btnSend.setTypeface(tf);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                OkHttpClient okHttpClient = new OkHttpClient();
                okHttpClient.setProtocols(Arrays.asList(Protocol.HTTP_1_1));

                Request.Builder builder = new Request.Builder();
                RequestBody formBody = new FormEncodingBuilder()
                        .add("device-id", appPreference.getDeviceID())
                        .add("-system-user-id", appPreference.getSystemUserMobileID())
                        .add("-app-version", appPreference.getAppVersion())
                        .build();

                Request request = builder.url(urlInsertMediaRequest).post(getRequestBody()).build();
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
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setInsertMediaRequest(result);
                            }
                        });
                    }
                });
            }
        });
    }

    public void setInsertMediaRequest(String result) {
        Log.d("AKO555", result);
        progressBar.setVisibility(View.GONE);
        ToastMessage.show(getApplicationContext(), getText(R.string.send_spot_txt_complete).toString());
        finish();
    }

    public RequestBody getRequestBody() {
        /*RequestBody requestBody = new MultipartBuilder().type(MultipartBuilder.FORM)
                .addFormDataPart("project-id", "" + 0)
                .addFormDataPart("title", inputSpotTitle.getText().toString())
                .addFormDataPart("description", inputSpotDescription.getText().toString())
                .addFormDataPart("-system-user-id", appPreference.getSystemUserMobileID())
                .addFormDataPart("media-request-image[]", "file.png", RequestBody.create(MediaType.parse("image/jpg"), getFile()))
                .build();*/
        MultipartBuilder multipartBuilder = new MultipartBuilder().type(MultipartBuilder.FORM);
        multipartBuilder.addFormDataPart("project-id", "0")
                .addFormDataPart("title", inputSpotTitle.getText().toString())
                .addFormDataPart("description", inputSpotDescription.getText().toString())
                .addFormDataPart("member-id", appPreference.getMemberID())
                .addFormDataPart("device-id", appPreference.getDeviceID())
                .addFormDataPart("status", "1")
                .addFormDataPart("-app-version", appPreference.getAppVersion())
                .addFormDataPart("-system-user-id", appPreference.getSystemUserMobileID());
        for(int x=0; x<bitmapList.size(); x++) {
            multipartBuilder.addFormDataPart("media-request-image[]", x + ".png", RequestBody.create(MediaType.parse("image/*"), getFileFromBitmap(bitmapList.get(x), x + ".png")));
        }
        RequestBody requestBody = multipartBuilder.build();
        return requestBody;
    }

    public File getFile() {
        return new File("/storage/emulated/0/Download/image.jpg");
    }

    public File getFileFromBitmap(Bitmap bitmap, String name) {
        //ToastMessage.show(getApplicationContext(), getFilesDir().getPath());
        File filesDir = getFilesDir();
        File imageFile = new File(filesDir, name);

        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            os.flush();
            os.close();
            return imageFile;
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
        }
        return imageFile;
    }

    private void selectImage() {
        final String addPhoto = getText(R.string.send_spot_dialog_image_txt_add_photo).toString();
        final String takePhoto = getText(R.string.send_spot_dialog_image_txt_take_camera).toString();
        final String chooseFile = getText(R.string.send_spot_dialog_image_txt_choose_file).toString();
        final String cancel = getText(R.string.send_spot_dialog_image_txt_cancel).toString();
        final CharSequence[] items = {takePhoto, chooseFile, cancel};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(addPhoto);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(takePhoto)) {
                    fileUri = getOutputMediaFileUri();
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra( MediaStore.EXTRA_OUTPUT, fileUri );
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals(chooseFile)) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, chooseFile),
                            SELECT_FILE);
                } else if (items[item].equals(cancel)) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private Uri fileUri;
    private ArrayList<ImageView> imageViewList = new ArrayList<ImageView>();
    private ArrayList<Bitmap> bitmapList = new ArrayList<Bitmap>();
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Log.d("AKO555", "fileUri: " + fileUri.toString());
            Bitmap bitmap;
            int padding = (int) convertDpToPixel(8, this);
            final ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(params);
            imageView.setAdjustViewBounds(true);
            imageView.setPadding(padding, padding, padding, padding);
            if (requestCode == REQUEST_CAMERA) {
                //Bitmap thumbnail = (Bitmap) data.getExtras().get("data");

                File imgFile = new  File(fileUri.getPath());
                Bitmap thumbnail = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                if(imgFile.exists()){
                    Log.d("AKO555", "imgFile exist");
                } else {
                    Log.d("AKO555", "imgFile not exist");
                }
                bitmapList.add(thumbnail);


                /*ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

                File destination = new File(Environment.getExternalStorageDirectory(),
                        System.currentTimeMillis() + ".jpg");

                FileOutputStream fo;
                try {
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/

                imageView.setImageBitmap(thumbnail);
                imageView.buildDrawingCache();

            } else if (requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();
                String[] projection = {MediaStore.MediaColumns.DATA};
                CursorLoader cursorLoader = new CursorLoader(this, selectedImageUri, projection, null, null, null);
                Cursor cursor = cursorLoader.loadInBackground();
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();

                String selectedImagePath = cursor.getString(column_index);

                Bitmap bm;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(selectedImagePath, options);
                final int REQUIRED_SIZE = 1000;
                int scale = 1;
                while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                        && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                    scale *= 2;
                options.inSampleSize = scale;
                options.inJustDecodeBounds = false;
                bm = BitmapFactory.decodeFile(selectedImagePath, options);

                imageView.setImageBitmap(bm);
                imageView.buildDrawingCache();
                bitmapList.add(bm);
            }
            imageView.setTag(imageViewList.size());
            layoutImage.addView(imageView);
            imageViewList.add(imageView);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //ToastMessage.show(getApplicationContext(), "Tag: " + v.getTag().toString());
                    showImageDialog(v.getTag().toString());
                }
            });
        }
    }

    public void showImageDialog(final String tag) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton(getText(R.string.send_spot_dialog_image_txt_delete), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                layoutImage.removeViewAt(Integer.parseInt(tag));
                imageViewList.remove(Integer.parseInt(tag));
                bitmapList.remove(Integer.parseInt(tag));
            }
        }).setNegativeButton(getText(R.string.send_spot_dialog_image_txt_close), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        final AlertDialog dialog = builder.create();
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.dialog_image, null);
        dialog.setView(dialogLayout);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        LinearLayout layoutDialogImage = (LinearLayout) dialogLayout.findViewById(R.id.layoutDialogImage);
        ImageView image = (ImageView) dialogLayout.findViewById(R.id.goProDialogImage);
        //Bitmap icon = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.whygoprodialogimage);
        //BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        //Bitmap icon = drawable.getBitmap();
        /*Bitmap icon = bitmapList.get(Integer.parseInt(tag));
        float imageWidthInPX = (float)image.getWidth();

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(Math.round(imageWidthInPX),
                Math.round(imageWidthInPX * (float)icon.getHeight() / (float)icon.getWidth()));
        image.setLayoutParams(layoutParams);
        image.setImageBitmap(icon);*/
        //image.setImageBitmap(bitmapList.get(Integer.parseInt(tag)));

        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(params);
        imageView.setAdjustViewBounds(true);
        imageView.setImageBitmap(bitmapList.get(Integer.parseInt(tag)));
        layoutDialogImage.addView(imageView);

        dialog.show();
    }

    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }
    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(){
        return Uri.fromFile(getOutputMediaFile());
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + ".jpg");

        return mediaFile;
    }

}
