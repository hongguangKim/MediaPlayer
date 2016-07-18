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
import com.media.util.Video;

public class VideoItemAdapter extends BaseAdapter {

	private List<Video> list;
	private int local_postion = 0;
	private boolean imageChage = false;
	private LayoutInflater mLayoutInflater;

	public VideoItemAdapter(Context context, List<Video> list) {
		mLayoutInflater = LayoutInflater.from(context);
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mLayoutInflater.inflate(R.layout.video_item, null);
			holder.img = (ImageView) convertView.findViewById(R.id.video_img);
			holder.title = (TextView) convertView
					.findViewById(R.id.video_title);
			holder.time = (TextView) convertView.findViewById(R.id.video_time);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.title.setText(list.get(position).getTitle());
		String time = toTime(list.get(position).getDuration());
		holder.time.setText(time);
		holder.img.setImageBitmap(list.get(position).getImage());

		return convertView;
	}

	public String toTime(long time) {

		time /= 1000;
		long minute = time / 60;
		long hour = minute / 60;
		long second = time % 60;
		minute %= 60;
		return String.format("%02d:%02d:%02d", hour, minute, second);
	}

	public final class ViewHolder {
		public ImageView img;
		public TextView title;
		public TextView time;
	}
}
