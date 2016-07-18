package com.media.provider;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AudioColumns;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.MediaColumns;
import android.provider.MediaStore.Video.VideoColumns;

import com.media.util.Music;
import com.media.util.Video;

public class FilesProvider {
	private Context context;

	public FilesProvider(Context context) {
		this.context = context;
	}

	public List<Music> getMusicList() {
		List<Music> lists = new ArrayList<Music>();

		// search external data
		Cursor cursor = context.getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
				MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
		if (null == cursor) {
			return null;
		}
		if (cursor.moveToFirst()) {
			do {
				Music m = new Music();
				String title = cursor.getString(cursor
						.getColumnIndex(MediaColumns.TITLE));
				String singer = cursor.getString(cursor
						.getColumnIndex(AudioColumns.ARTIST));
				String album = cursor.getString(cursor
						.getColumnIndex(AudioColumns.ALBUM));
				long size = cursor.getLong(cursor
						.getColumnIndex(MediaColumns.SIZE));
				long time = cursor.getLong(cursor
						.getColumnIndex(AudioColumns.DURATION));
				String url = cursor.getString(cursor
						.getColumnIndex(MediaColumns.DATA));
				Bitmap bm = getMusicPhoto(cursor);

				m.setTitle(title);
				m.setSinger(singer);
				m.setAlbum(album);
				m.setSize(size);
				m.setTime(time);
				m.setUrl(url);
				m.setBmpDraw(bm);
				lists.add(m);
			} while (cursor.moveToNext());
		}

		return lists;
	}

	private Bitmap getMusicPhoto(Cursor cursor) {
		int musicColumnIndex = cursor.getColumnIndex(AudioColumns.ALBUM_KEY);
		String musicAlbumKey = cursor.getString(musicColumnIndex);
		String[] argArr = { musicAlbumKey };
		ContentResolver albumResolver = context.getContentResolver();
		Cursor albumCursor = albumResolver.query(
				MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, null,
				MediaStore.Audio.AudioColumns.ALBUM_KEY + " = ?", argArr, null);
		if (null != albumCursor && albumCursor.getCount() > 0) {
			albumCursor.moveToFirst();
			int albumArtIndex = albumCursor
					.getColumnIndex(MediaStore.Audio.AlbumColumns.ALBUM_ART);
			String musicAlbumArtPath = albumCursor.getString(albumArtIndex);
			if (null != musicAlbumArtPath && !"".equals(musicAlbumArtPath)) {
				return BitmapFactory.decodeFile(musicAlbumArtPath);
			}
		}
		return null;
	}

	public List<Video> getVideoList() {
		List<Video> list = null;
		if (context != null) {
			Cursor cursor = context.getContentResolver().query(
					MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null,
					null, MediaStore.Video.DEFAULT_SORT_ORDER);
			if (null == cursor) {
				return null;
			}
			list = new ArrayList<Video>();
			while (cursor.moveToNext()) {
				int id = cursor.getInt(cursor
						.getColumnIndexOrThrow(BaseColumns._ID));
				String title = cursor.getString(cursor
						.getColumnIndexOrThrow(MediaColumns.TITLE));
				String album = cursor.getString(cursor
						.getColumnIndexOrThrow(VideoColumns.ALBUM));
				String artist = cursor.getString(cursor
						.getColumnIndexOrThrow(VideoColumns.ARTIST));
				String displayName = cursor.getString(cursor
						.getColumnIndexOrThrow(MediaColumns.DISPLAY_NAME));
				String mimeType = cursor.getString(cursor
						.getColumnIndexOrThrow(MediaColumns.MIME_TYPE));
				String path = cursor.getString(cursor
						.getColumnIndexOrThrow(MediaColumns.DATA));
				Bitmap bitmap = getVideoThumbnail(id);
				long duration = cursor.getInt(cursor
						.getColumnIndexOrThrow(VideoColumns.DURATION));
				long size = cursor.getLong(cursor
						.getColumnIndexOrThrow(MediaColumns.SIZE));
				Video video = new Video(id, title, album, artist, displayName,
						mimeType, path, size, duration, bitmap);
				list.add(video);
			}
			cursor.close();
		}
		return list;
	}

	private Bitmap getVideoThumbnail(int id) {
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		bitmap = MediaStore.Video.Thumbnails.getThumbnail(
				context.getContentResolver(), id, Images.Thumbnails.MICRO_KIND,
				options);
		return bitmap;
	}
}