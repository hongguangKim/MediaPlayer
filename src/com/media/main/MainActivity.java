package com.media.main;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;

import com.example.mediaplay.R;
import com.media.player.MusicItemActivity;
import com.media.player.VideoItemActivity;

public class MainActivity extends TabActivity {

	TabHost tabHost;
	TabHost.TabSpec spec;

	private TabHost mTabhost;
	private TabWidget mTabWidget;
	private LayoutInflater mInflater;
	private TextView mtext;
	private TabSpec mTabSpec;
	private LinearLayout linearLayout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mInflater = LayoutInflater.from(this);
		mTabhost = (TabHost) findViewById(android.R.id.tabhost);
		mTabWidget = (TabWidget) findViewById(android.R.id.tabs);
		creatTab();

	}

	public void creatTab() {

		mTabSpec = mTabhost.newTabSpec("music");
		linearLayout = (LinearLayout) mInflater.inflate(R.layout.tabwidget,
				null);
		mtext = (TextView) linearLayout.findViewById(R.id.tab_name);
		mtext.setText("Music");
		mTabSpec.setIndicator(linearLayout);
		mTabSpec.setContent(new Intent(this, MusicItemActivity.class));
		mTabhost.addTab(mTabSpec);

		mTabSpec = mTabhost.newTabSpec("video");
		linearLayout = (LinearLayout) mInflater.inflate(R.layout.tabwidget,
				null);
		mtext = (TextView) linearLayout.findViewById(R.id.tab_name);
		mtext.setText("Video");
		mTabSpec.setIndicator(linearLayout);
		mTabSpec.setContent(new Intent(this, VideoItemActivity.class));
		mTabhost.addTab(mTabSpec);
	}
}