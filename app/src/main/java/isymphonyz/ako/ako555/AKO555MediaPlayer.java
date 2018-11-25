package isymphonyz.ako.ako555;

import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.concurrent.TimeUnit;

import isymphonyz.ako.ako555.utils.MyConfiguration;

//@SuppressLint("NewApi")
public class AKO555MediaPlayer extends AppCompatActivity {

    public static MediaPlayer mediaPlayer;
    public TextView songName, duration;
    private double timeElapsed = 0, finalTime = 0;
    private int forwardTime = 2000, backwardTime = 2000;
    private Handler durationHandler = new Handler();
    private SeekBar seekbar;
    private ImageView media_previous;
    private ImageView media_rew;
    private ImageView media_pause;
    private ImageView media_play;
    private ImageView media_ff;
    private ImageView media_next;

    String folderName = MyConfiguration.FOLDER_AKO555 + File.separator + MyConfiguration.FOLDER_PUBLIC_SPOT;
    Bundle extras;
    String prefixPath = "";
    String filePath = "";
    String spotName = "";
    String[] videoOfflineNameArray;
    int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set the layout of the Activity
        setContentView(R.layout.media_player);

        //prefixPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + folderName + File.separator;
        extras = getIntent().getExtras();
        //spotName = extras.getString("media");
        //videoOfflineNameArray = extras.getStringArray("videoOfflineNameArray");
        //index = extras.getInt("index");
        prefixPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + extras.getString("prefixPath") + File.separator;
        spotName = extras.getString("spotName");

        //spotName = videoOfflineNameArray[index];
        filePath = prefixPath + spotName;

        // initialize views
        initializeViews();
    }

    @Override
    public void onPause() {
        super.onPause();
        //mediaPlayer.stop();
        //mediaPlayer.reset();
        //mediaPlayer = null;
    }

    public void initializeViews() {
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/rsu-light.ttf");

        songName = (TextView) findViewById(R.id.songName);
        songName.setTypeface(tf);

        media_previous = (ImageView) findViewById(R.id.media_previous);
        media_rew = (ImageView) findViewById(R.id.media_rew);
        media_pause = (ImageView) findViewById(R.id.media_pause);
        media_play = (ImageView) findViewById(R.id.media_play);
        media_ff = (ImageView) findViewById(R.id.media_ff);
        media_next = (ImageView) findViewById(R.id.media_next);

        setMediaPlayer(filePath);

        finalTime = mediaPlayer.getDuration();
        duration = (TextView) findViewById(R.id.songDuration);
        duration.setTypeface(tf);

        seekbar = (SeekBar) findViewById(R.id.seekBar);

        songName.setText(spotName);

        seekbar.setMax((int) finalTime);
        seekbar.setClickable(false);
    }

    public void setMediaPlayer(String filePath) {
        Log.d("AKO555", "FilePath: " + filePath);
        //mediaPlayer = null;
        //mediaPlayer = AKO555Home.mediaPlayer;
		/*if(mediaPlayer == null) {
			mediaPlayer = new MediaPlayer();
			Log.d("AKO555", "new MediaPlayer()");
		}*/
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.reset();
        }
        mediaPlayer = MediaPlayer.create(this, Uri.parse(filePath));
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        media_play.setBackgroundResource(R.drawable.custom_blue_button);
        media_play.invalidate();

        media_pause.setBackgroundResource(R.drawable.custom_green_button);
        media_pause.invalidate();
        //mediaPlayer = MediaPlayer.create(this, R.raw.sample_song);
		/*try {
			Log.d("AKO555", "File: " + filePath);
			//mediaPlayer.setDataSource(filePath);
			mediaPlayer.prepare();
			mediaPlayer.setOnPreparedListener(new OnPreparedListener() {

				@Override
				public void onPrepared(MediaPlayer mp) {
					// TODO Auto-generated method stub
					mp.start();
					songName.setText(videoOfflineNameArray[index]);
					finalTime = mediaPlayer.getDuration();
					seekbar.setMax((int) finalTime);
					timeElapsed = mediaPlayer.getCurrentPosition();
					seekbar.setProgress((int) timeElapsed);
					durationHandler.postDelayed(updateSeekBarTime, 100);

					media_play.setBackgroundResource(R.drawable.custom_blue_button);
					media_play.invalidate();

					media_pause.setBackgroundResource(R.drawable.custom_green_button);
					media_pause.invalidate();
				}
			});
			mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					// TODO Auto-generated method stub
					media_next.performClick();
				}
			});
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
    }

    // play mp3 song
    public void play(View view) {
        mediaPlayer.start();
        timeElapsed = mediaPlayer.getCurrentPosition();
        seekbar.setProgress((int) timeElapsed);
        //durationHandler.postDelayed(updateSeekBarTime, 100);

        media_play.setBackgroundResource(R.drawable.custom_blue_button);
        media_play.invalidate();

        media_pause.setBackgroundResource(R.drawable.custom_green_button);
        media_pause.invalidate();
    }

    // handler to change seekBarTime
    private Runnable updateSeekBarTime = new Runnable() {
        public void run() {
            try {
                // get current position
                timeElapsed = mediaPlayer.getCurrentPosition();
                // set seekbar progress
                seekbar.setProgress((int) timeElapsed);
                // set time remaing
                double timeRemaining = finalTime - timeElapsed;
                duration.setText(String.format(
                        "%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining),
                        TimeUnit.MILLISECONDS.toSeconds((long) timeRemaining)
                                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
                                .toMinutes((long) timeRemaining))));

                // repeat yourself that again in 100 miliseconds
                durationHandler.postDelayed(this, 100);
            } catch(Exception e) {

            }
        }
    };

    // pause mp3 song
    public void pause(View view) {
        mediaPlayer.pause();
        media_play.setBackgroundResource(R.drawable.custom_green_button);
        media_play.invalidate();

        media_pause.setBackgroundResource(R.drawable.custom_blue_button);
        media_pause.invalidate();
    }

    // go forward at forwardTime seconds
    public void forward(View view) {
        //check if we can go forward at forwardTime seconds before song endes
		/*if ((timeElapsed + forwardTime) > 0) {
			timeElapsed = timeElapsed - backwardTime;

			//seek to the exact second of the track
			mediaPlayer.seekTo((int) timeElapsed);
		}*/
        if ((timeElapsed + forwardTime) <= finalTime) {
            timeElapsed = timeElapsed + forwardTime;

            //seek to the exact second of the track
            mediaPlayer.seekTo((int) timeElapsed);
        }
    }

    public void next(View view) {
        if(index == videoOfflineNameArray.length-1) {
            index = 0;
        } else {
            index++;
        }
        //mediaPlayer.stop();
        mediaPlayer.reset();
        spotName = videoOfflineNameArray[index];
        filePath = prefixPath + spotName;
        setMediaPlayer(filePath);
    }

    public void previous(View view) {
        if(index == 0) {
            index = videoOfflineNameArray.length - 1;
        } else {
            index--;
        }
        //mediaPlayer.stop();
        mediaPlayer.reset();
        spotName = videoOfflineNameArray[index];
        filePath = prefixPath + spotName;
        setMediaPlayer(filePath);
    }
}
