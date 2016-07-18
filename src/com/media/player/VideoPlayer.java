package com.media.player;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.example.mediaplay.R;
import com.media.provider.FilesProvider;
import com.media.util.Video;

public class VideoPlayer extends Activity implements OnCompletionListener {
	private String videoPath;
	private VideoView mVideoView;
	private View mVolumeBrightnessLayout;
	private ImageView mOperationBg;
	private ImageView mOperationPercent;
	private AudioManager mAudioManager;

	private List<Video> videos;
	/** current video id */
	private int video_id = -1;

	private int mMaxVolume;
	/** current volume */
	private int mVolume = -1;
	/** current Brightness */
	private float mBrightness = -1f;

	private GestureDetector mGestureDetector;
	private MediaController mMediaController;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_view);

		mVideoView = (VideoView) findViewById(R.id.surface_view);
		mVolumeBrightnessLayout = findViewById(R.id.operation_volume_brightness);
		mOperationBg = (ImageView) findViewById(R.id.operation_bg);
		mOperationPercent = (ImageView) findViewById(R.id.operation_percent);

		// ~~~ Get video list
		FilesProvider provider = new FilesProvider(this);
		videos = provider.getVideoList();

		// ~~~ Get video item
		int id = getIntent().getIntExtra("id", 1);
		Video video = videos.get(id);
		video_id = id;
		videoPath = video.getPath();

		// ~~~ Bind video
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		mMaxVolume = mAudioManager
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		mVideoView.setVideoPath(videoPath);

		// ~~~ Bind Listeners
		mMediaController = new MediaController(this);
		mMediaController.setPrevNextListeners(new OnClickListener() {
			@Override
			public synchronized void onClick(View v) {
				// TODO Auto-generated method stub
				previous();
			}
		}, new OnClickListener() {
			@Override
			public synchronized void onClick(View v) {
				// TODO Auto-generated method stub
				next();
			}
		});

		mVideoView.setMediaController(mMediaController);
		mVideoView.setOnCompletionListener(this);
		mVideoView.requestFocus();

		// Gesture(Volume & Brightness Control)
		mGestureDetector = new GestureDetector(this, new MyGestureListener());
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}

	private void previous() {
		if (video_id <= 0) {
			video_id = videos.size() - 1;
		} else {
			video_id = video_id - 1;
		}
		Video video = videos.get(video_id);
		videoPath = video.getPath();
		mVideoView.setVideoPath(videoPath);
		mVideoView.start();
		mVideoView.setOnCompletionListener(this);
	}

	private void next() {
		if (video_id >= videos.size() - 1) {
			video_id = 0;
		} else {
			video_id = video_id + 1;
		}
		Video video = videos.get(video_id);
		videoPath = video.getPath();
		mVideoView.setVideoPath(videoPath);
		mVideoView.start();
		mVideoView.setOnCompletionListener(this);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		if (!isPlaying()) {
			startPlayer();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mVideoView != null)
			mVideoView.pause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mVideoView != null) {
			mVideoView.resume();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mVideoView != null)
			mVideoView.stopPlayback();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mGestureDetector.onTouchEvent(event))
			return false;

		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_UP:
			endGesture();
			break;
		}
		return super.onTouchEvent(event);
	}

	/** Gesture end*/
	private void endGesture() {
		mVolume = -1;
		mBrightness = -1f;
		mDismissHandler.sendEmptyMessageDelayed(0, 500);
	}

	private class MyGestureListener extends SimpleOnGestureListener {

		/** Slide */
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			float mOldX = e1.getX(), mOldY = e1.getY();
			int y = (int) e2.getRawY();
			Display disp = getWindowManager().getDefaultDisplay();
			int windowWidth = disp.getWidth();
			int windowHeight = disp.getHeight();

			if (mOldX > windowWidth * 4.0 / 5)// right slide
				onVolumeSlide((mOldY - y) / windowHeight);
			else if (mOldX < windowWidth / 5)// left slide
				onBrightnessSlide((mOldY - y) / windowHeight);

			return super.onScroll(e1, e2, distanceX, distanceY);
		}
	}

	/** Timing to hide */
	private Handler mDismissHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mVolumeBrightnessLayout.setVisibility(View.GONE);
		}
	};

	/**
	 * Sliding volume change
	 * 
	 * @param percent
	 */
	private void onVolumeSlide(float percent) {
		if (mVolume == -1) {
			mVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			if (mVolume < 0)
				mVolume = 0;

			// Display image
			mOperationBg.setImageResource(R.drawable.video_volumn_bg);
			mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
		}

		int index = (int) (percent * mMaxVolume) + mVolume;
		if (index > mMaxVolume)
			index = mMaxVolume;
		else if (index < 0)
			index = 0;

		// Volume
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);

		// Progress
		ViewGroup.LayoutParams lp = mOperationPercent.getLayoutParams();
		lp.width = findViewById(R.id.operation_full).getLayoutParams().width
				* index / mMaxVolume;
		mOperationPercent.setLayoutParams(lp);
	}

	/**
	 * Sliding Brightness change
	 * 
	 * @param percent
	 */
	private void onBrightnessSlide(float percent) {
		if (mBrightness < 0) {
			mBrightness = getWindow().getAttributes().screenBrightness;
			if (mBrightness < 0.01f)
				mBrightness = 0.01f;

			// Display image
			mOperationBg.setImageResource(R.drawable.video_brightness_bg);
			mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
		}
		WindowManager.LayoutParams lpa = getWindow().getAttributes();
		lpa.screenBrightness = mBrightness + percent;
		if (lpa.screenBrightness > 1.0f)
			lpa.screenBrightness = 1.0f;
		else if (lpa.screenBrightness < 0.01f)
			lpa.screenBrightness = 0.01f;
		getWindow().setAttributes(lpa);

		ViewGroup.LayoutParams lp = mOperationPercent.getLayoutParams();
		lp.width = (int) (findViewById(R.id.operation_full).getLayoutParams().width * lpa.screenBrightness);
		mOperationPercent.setLayoutParams(lp);
	}

	private void startPlayer() {
		if (mVideoView != null)
			mVideoView.start();
	}

	private boolean isPlaying() {
		return mVideoView != null && mVideoView.isPlaying();
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub
		next();
	}
}