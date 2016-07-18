package com.media.player;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.example.mediaplay.R;
import com.media.adapter.MusicItemAdapter;
import com.media.provider.FilesProvider;
import com.media.util.Music;

public class MusicItemActivity extends Activity {
	private ListView listview;
	private List<Music> musics;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.music_list);
		listview = (ListView) this.findViewById(R.id.music_item);

		FilesProvider provider = new FilesProvider(this);
		musics = provider.getMusicList();

		MusicItemAdapter adapter = new MusicItemAdapter(this, musics);
		listview.setAdapter(adapter);

		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long id) {
				// TODO Auto-generated method stub

				Intent intent = new Intent(MusicItemActivity.this,
						MusicPlayer.class);
				intent.putExtra("id", position);
				startActivity(intent);
			}
		});
	}
}
