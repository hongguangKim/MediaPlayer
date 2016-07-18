package com.media.player;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TabHost;

import com.example.mediaplay.R;
import com.media.adapter.VideoItemAdapter;
import com.media.provider.FilesProvider;
import com.media.util.Video;

public class VideoItemActivity extends Activity {

	public TabHost tabhost;

	ListView videoListView;
	VideoItemAdapter videoItemAdapter;
	List<Video> videos;
	int videoSize;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_list);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		FilesProvider provider = new FilesProvider(this);
		videos = provider.getVideoList();

		videoSize = videos.size();
		videoItemAdapter = new VideoItemAdapter(this, videos);

		videoListView = (ListView) findViewById(R.id.videolistfile);
		videoListView.setAdapter(videoItemAdapter);
		videoListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Intent intent = new Intent();
				intent.setClass(VideoItemActivity.this, VideoPlayer.class);
				intent.putExtra("id", position);
				startActivity(intent);
			}
		});

		super.onStart();
	}
}
