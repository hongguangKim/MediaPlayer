package com.media.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mediaplay.R;
import com.media.util.Music;

public class MusicItemAdapter extends BaseAdapter {
	List lists;
	private LayoutInflater mLayoutInflater;

	public MusicItemAdapter(Context context, List list) {
		mLayoutInflater = LayoutInflater.from(context);
		this.lists = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return lists.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return lists.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.music_item, null);
		}
		Music m = (Music) lists.get(position);
		TextView textName = (TextView) convertView
				.findViewById(R.id.music_list_singer);
		textName.setText(m.getSinger());
		TextView music_singer = (TextView) convertView
				.findViewById(R.id.music_list_name);
		music_singer.setText(m.getTitle());
		TextView music_time = (TextView) convertView
				.findViewById(R.id.music_time);
		music_time.setText(toTime((int) m.getTime()));

		ImageView img = (ImageView) convertView.findViewById(R.id.item_image);
		img.setBackgroundResource(R.drawable.item);

		return convertView;
	}

	/**
	 * @param time
	 * @return
	 */
	public String toTime(int time) {
		time /= 1000;
		int minute = time / 60;
		int second = time % 60;
		minute %= 60;
		return String.format("%02d:%02d", minute, second);
	}
}