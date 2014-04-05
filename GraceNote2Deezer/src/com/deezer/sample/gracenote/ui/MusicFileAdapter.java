package com.deezer.sample.gracenote.ui;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.deezer.sample.gracenote.R;
import com.deezer.sample.gracenote.util.FileToTrackMap;
import com.deezer.sdk.model.Track;

public class MusicFileAdapter extends ArrayAdapter<File> {

	private File mSelectedFile;

	public MusicFileAdapter(Context context, List<File> files) {
		super(context, R.layout.item_file, files);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View view = convertView;
		MusicFileViewHolder holder;

		// view recycling
		if (view == null) {
			view = LayoutInflater.from(getContext()).inflate(
					R.layout.item_file, parent, false);
			holder = new MusicFileViewHolder(view);
			view.setTag(holder);
		} else {
			holder = (MusicFileViewHolder) view.getTag();
		}

		// adapter
		File file = getItem(position);
		Track track = FileToTrackMap.getMappedTrack(file);

		if (track == null) {
			Log.i("Adapter", "Track is null for " + file.getName());
			holder.displayFile(file);
		} else {
			holder.displayTrack(track);
		}

		holder.displaySelected(file == mSelectedFile);

		return view;
	}

	private class MusicFileViewHolder {
		private TextView mTitle, mArtist, mAlbum;
		private ImageView mCover;

		public MusicFileViewHolder(View root) {
			mTitle = (TextView) root.findViewById(R.id.text_title);
			mArtist = (TextView) root.findViewById(R.id.text_artist);
			mAlbum = (TextView) root.findViewById(R.id.text_album);
			mCover = (ImageView) root.findViewById(R.id.image_cover);
		}

		public void displaySelected(boolean selected) {
			if (selected) {
				mTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0,
						R.drawable.ic_checked, 0);
			} else {
				mTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			}
		}

		public void displayTrack(Track track) {
			mCover.setImageResource(R.drawable.ic_track_valid);
			mTitle.setText(track.getTitle());

			if (track.getAlbum() == null) {
				mAlbum.setText("");
			} else {
				mAlbum.setText(track.getAlbum().getTitle());
			}

			if (track.getArtist() == null) {
				mArtist.setText("");
			} else {
				mArtist.setText(track.getArtist().getName());
			}

		}

		public void displayFile(File file) {
			mCover.setImageResource(R.drawable.ic_track);
			mTitle.setText(file.getName());
			mAlbum.setText("");
			mArtist.setText("");
		}
	}

	public void setSelected(int position) {
		if (position < 0) {
			mSelectedFile = null;
		} else {
			mSelectedFile = getItem(position);
		}
		notifyDataSetChanged();
	}

	public File getSelectedFile() {
		return mSelectedFile;
	}
}
