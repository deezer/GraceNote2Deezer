package com.deezer.sample.gracenote.ui;

import java.io.File;

import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class FilePlayerFragment extends AMusicPlayerFragment {

	private File mFile;
	private MediaPlayer mPlayer;

	public FilePlayerFragment(File file) {
		mFile = file;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mPlayer = new MediaPlayer();
		try {
			mPlayer.setDataSource(mFile.getAbsolutePath());
			mPlayer.prepare();
		} catch (Exception e) {
			Log.e("FilePlayerFragment", "Unable to create player", e);

			Toast.makeText(getActivity(),
					"Unable to create player, you wont be able to play music",
					Toast.LENGTH_LONG).show();

			dismiss();
		}
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mTitle.setText(mFile.getName());
	}

	@Override
	protected void onPlayMusic() {
		if (mPlayer != null) {
			mPlayer.start();
		}
	}

	@Override
	protected void onPauseMusic() {
		if (mPlayer != null) {
			mPlayer.pause();
		}
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
		mPlayer.release();
	}

	/*
	 * MediaPlayer mp = new MediaPlayer();
	 * mp.setDataSource("/mnt/sdcard/yourdirectory/youraudiofile.wav");
	 * mp.prepare(); mp.start();
	 */
}
