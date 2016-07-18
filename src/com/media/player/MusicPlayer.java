package com.media.player;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mediaplay.R;
import com.media.provider.FilesProvider;
import com.media.util.Music;

public class MusicPlayer extends Activity implements OnSeekBarChangeListener {

	public static MediaPlayer player = null;
	private TextView textName;
	private TextView textSinger;
	private TextView textStartTime;
	private TextView textEndTime;
	private SeekBar musicSeekBar;
	private ImageView icon;
	private ImageButton imageBtnLast;
	private ImageButton imageBtnRewind;
	private ImageButton imageBtnPlay;
	private ImageButton imageBtnForward;
	private ImageButton imageBtnNext;
	private ImageButton imageBtnLoop;
	private ImageButton imageBtnRandom;
	private boolean isOneLoop = false;
	private boolean isRandom = false;
	public static int music_id = -1;
	private List<Music> musics;
	private AudioManager audioManager;// Volume Manager
	private int maxVolume;
	private int currentVolume;
	private SeekBar seekBarVolume;
	private Thread thread;
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub

			int position = player.getCurrentPosition();
			int total = player.getDuration();
			int progress = position * 100 / total;
			musicSeekBar.setProgress(progress);
			textStartTime.setText(toTime(position));
			super.handleMessage(msg);
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.music_player);

		textName = (TextView) this.findViewById(R.id.music_name);
		textSinger = (TextView) this.findViewById(R.id.music_singer);
		textStartTime = (TextView) this.findViewById(R.id.music_start_time);
		textEndTime = (TextView) this.findViewById(R.id.music_end_time);
		musicSeekBar = (SeekBar) this.findViewById(R.id.music_seekBar);
		icon = (ImageView) this.findViewById(R.id.image_icon);
		imageBtnLast = (ImageButton) this.findViewById(R.id.music_lasted);
		imageBtnRewind = (ImageButton) this.findViewById(R.id.music_rewind);
		imageBtnPlay = (ImageButton) this.findViewById(R.id.music_play);
		imageBtnForward = (ImageButton) this.findViewById(R.id.music_foward);
		imageBtnNext = (ImageButton) this.findViewById(R.id.music_next);
		imageBtnLoop = (ImageButton) this.findViewById(R.id.music_loop);
		seekBarVolume = (SeekBar) this.findViewById(R.id.music_volume);
		imageBtnRandom = (ImageButton) this.findViewById(R.id.music_random);

		// Get musics list
		FilesProvider provider = new FilesProvider(this);
		musics = provider.getMusicList();

		imageBtnLast.setOnClickListener(new MyListener());
		imageBtnRewind.setOnClickListener(new MyListener());
		imageBtnPlay.setOnClickListener(new MyListener());
		imageBtnForward.setOnClickListener(new MyListener());
		imageBtnNext.setOnClickListener(new MyListener());
		imageBtnLoop.setOnClickListener(new MyListener());
		imageBtnRandom.setOnClickListener(new MyListener());
		musicSeekBar.setOnSeekBarChangeListener(this);

		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		seekBarVolume.setMax(maxVolume);
		seekBarVolume.setProgress(currentVolume);
		seekBarVolume.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
						progress, AudioManager.FLAG_ALLOW_RINGER_MODES);
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		int progress = seekBarVolume.getProgress();
		switch (keyCode) {
		// Volume reduce key
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			if (progress > 0) {
				progress -= 1;
			} else
				progress = 0;
			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress,
					AudioManager.FLAG_ALLOW_RINGER_MODES);
			seekBarVolume.setProgress(progress);
			return true;
			// Volume increment key
		case KeyEvent.KEYCODE_VOLUME_UP:
			if (progress < maxVolume) {
				progress += 1;
			} else
				progress = maxVolume;
			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress,
					AudioManager.FLAG_ALLOW_RINGER_MODES);
			seekBarVolume.setProgress(progress);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		if (player == null) {
			int id = getIntent().getIntExtra("id", 1);
			Music m = musics.get(id);
			textName.setText(m.getTitle());
			textSinger.setText(m.getSinger());
			textEndTime.setText(toTime((int) m.getTime()));
			imageBtnPlay.setImageResource(R.drawable.pause1);
			icon.setImageBitmap(m.getBmpDraw());
			music_id = id;
			String url = m.getUrl();
			Uri musicUri = Uri.parse(url);

			player = new MediaPlayer();
			player.setAudioStreamType(AudioManager.STREAM_MUSIC);
			player.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK);
			try {
				player.setDataSource(getApplicationContext(), musicUri);
				player.prepare();
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
			}
			player.start();

		}
		player.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				player.reset();
				forward();
			}
		});
		thread = new Thread(updateThread);
		thread.start();
		super.onStart();
	}

	private class MyListener implements OnClickListener {

		@Override
		public synchronized void onClick(View v) {
			// TODO Auto-generated method stub
			if (v == imageBtnLast) {
				// first
				player.release();
				player = null;
				first();

			} else if (v == imageBtnRewind) {
				// previous
				player.release();
				player = null;
				rewind();
			} else if (v == imageBtnPlay) {
				if (player != null && player.isPlaying()) {// pause
					player.pause();
					imageBtnPlay.setImageResource(R.drawable.play1);
					imageBtnPlay.setTag(R.drawable.play1);
				} else { // start
					player.start();
					imageBtnPlay.setImageResource(R.drawable.pause1);
					imageBtnPlay.setTag(R.drawable.pause1);
				}
			} else if (v == imageBtnForward) {
				// Next
				player.release();
				player = null;
				forward();
			} else if (v == imageBtnNext) {
				// last
				player.release();
				player = null;
				last();
			} else if (v == imageBtnLoop) {
				isRandom = false;
				imageBtnRandom.setBackgroundResource(R.drawable.play_random);

				if (isOneLoop == false) {
					// one loop
					imageBtnLoop
							.setBackgroundResource(R.drawable.play_loop_spec);
					isOneLoop = true;
				} else {
					// all loop
					imageBtnLoop
							.setBackgroundResource(R.drawable.play_loop_sel);
					isOneLoop = false;
				}
			} else if (v == imageBtnRandom) {
				if (isRandom == true) {

					imageBtnRandom
							.setBackgroundResource(R.drawable.play_random);
					isRandom = false;

					if (isOneLoop == false) {
						imageBtnLoop
								.setBackgroundResource(R.drawable.play_loop_sel);
					} else {
						imageBtnLoop
								.setBackgroundResource(R.drawable.play_loop_spec);
					}

				} else {
					// Random
					imageBtnRandom
							.setBackgroundResource(R.drawable.play_random_sel);
					imageBtnLoop.setBackgroundResource(R.drawable.play_loop);
					Log.i("loop state", "[ Random ]");
					isRandom = true;
				}
			}
			player.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					// TODO Auto-generated method stub
					player.release();
					player = null;
					if (isOneLoop) {
						if (music_id <= 0) {
							music_id = musics.size() - 1;
						} else {
							music_id = music_id - 1;
						}
					}
					forward();
				}
			});

		}
	}

	private void first() {
		music_id = 0;
		Music m = musics.get(0);
		textName.setText(m.getTitle());
		textSinger.setText(m.getSinger());
		textEndTime.setText(toTime((int) m.getTime()));
		icon.setImageBitmap(m.getBmpDraw());

		// log first music item
		Log.i("first music item: ", m.getTitle());

		imageBtnPlay.setImageResource(R.drawable.pause1);

		String url = m.getUrl();
		Uri myUri = Uri.parse(url);
		player = new MediaPlayer();
		player.setAudioStreamType(AudioManager.STREAM_MUSIC);
		try {
			player.setDataSource(getApplicationContext(), myUri);
			player.prepare();
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
		}

		player.start();
		player.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				player.reset();
				if (isOneLoop) {
					if (music_id <= 0) {
						music_id = musics.size() - 1;
					} else {
						music_id = music_id - 1;
					}
				}
				forward();
			}
		});

	}

	private void rewind() {
		if (isRandom == true) {
			int key = Math
					.abs((int) System.currentTimeMillis() % musics.size());
			music_id = key;
		} else {
			if (music_id <= 0) {
				music_id = musics.size() - 1;
			} else {
				music_id = music_id - 1;
			}
		}
		Music m = musics.get(music_id);
		textName.setText(m.getTitle());
		textSinger.setText(m.getSinger());
		textEndTime.setText(toTime((int) m.getTime()));
		icon.setImageBitmap(m.getBmpDraw());

		// log rewind current music item
		Log.i("--->rewind(current)  play id: ", m.getTitle());

		imageBtnPlay.setImageResource(R.drawable.pause1);

		String url = m.getUrl();
		Uri myUri = Uri.parse(url);
		player = new MediaPlayer();
		player.setAudioStreamType(AudioManager.STREAM_MUSIC);
		try {
			player.setDataSource(getApplicationContext(), myUri);
			player.prepare();
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
		}

		player.start();
		player.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				player.reset();
				if (isOneLoop) {
					if (music_id <= 0) {
						music_id = musics.size() - 1;
					} else {
						music_id = music_id - 1;
					}
				}
				forward();
			}
		});

	}

	private void forward() {
		if (isRandom == true) {
			int key = Math
					.abs((int) System.currentTimeMillis() % musics.size());
			// log forward Ramdom music item id
			music_id = key;
		} else {
			if (music_id >= musics.size() - 1) {
				music_id = 0;
			} else {
				music_id = music_id + 1;
			}
		}
		Music m = musics.get(music_id);
		textName.setText(m.getTitle());
		textSinger.setText(m.getSinger());
		textEndTime.setText(toTime((int) m.getTime()));
		imageBtnPlay.setImageResource(R.drawable.pause1);
		icon.setImageBitmap(m.getBmpDraw());

		// log forward current music item
		Log.i("--->forward(current)  play id: ", m.getTitle());

		String url = m.getUrl();
		Uri musicUri = Uri.parse(url);
		player = new MediaPlayer();
		player.setAudioStreamType(AudioManager.STREAM_MUSIC);
		try {
			player.setDataSource(getApplicationContext(), musicUri);
			player.prepare();
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
		}

		player.start();
		player.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				player.reset();
				if (isOneLoop) {
					if (music_id <= 0) {
						music_id = musics.size() - 1;
					} else {
						music_id = music_id - 1;
					}
				}
				forward();
			}
		});

	}

	private void last() {
		music_id = musics.size() - 1;
		Music m = musics.get(musics.size() - 1);
		textName.setText(m.getTitle());
		System.out.println("getlast: " + m.getTitle());
		textSinger.setText(m.getSinger());
		textEndTime.setText(toTime((int) m.getTime()));
		icon.setImageBitmap(m.getBmpDraw());

		// log last music item
		Log.i("last music item: ", m.getTitle());

		imageBtnPlay.setImageResource(R.drawable.pause1);

		String url = m.getUrl();
		Uri myUri = Uri.parse(url);
		player = new MediaPlayer();
		player.setAudioStreamType(AudioManager.STREAM_MUSIC);
		try {
			player.setDataSource(getApplicationContext(), myUri);
			player.prepare();
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
		}

		player.start();
		player.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				player.reset();
				if (isOneLoop) {
					if (music_id <= 0) {
						music_id = musics.size() - 1;
					} else {
						music_id = music_id - 1;
					}
				}
				forward();
			}
		});

	}

	/**
	 * 
	 * @param time
	 * @return
	 */
	public String toTime(int time) {

		time /= 1000;
		int minute = (time / 60) % 60;
		int second = time % 60;
		return String.format("%02d:%02d", minute, second);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub
		if (fromUser) {
			player.seekTo(seekBar.getProgress() * player.getDuration() / 100);
			handler.sendEmptyMessage(0);
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		player.pause();
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		if (imageBtnPlay.getTag() != null
				&& imageBtnPlay.getTag().equals((R.drawable.play1))) {
			imageBtnPlay.setImageResource(R.drawable.pause1);
			imageBtnPlay.setTag(R.drawable.pause1);
		}
		player.start();
	}

	Runnable updateThread = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (player != null) {
				handler.sendEmptyMessage(0);
				handler.postDelayed(updateThread, 500);
			}
		}

	};

	@Override
	protected void onDestroy() {
		if (player != null) {
			player.stop();
			player.release();
			player = null;
		}
		super.onDestroy();
	}
}
