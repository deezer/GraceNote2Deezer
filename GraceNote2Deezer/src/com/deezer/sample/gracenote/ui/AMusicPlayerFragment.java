package com.deezer.sample.gracenote.ui;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import com.deezer.sample.gracenote.R;

public abstract class AMusicPlayerFragment extends DialogFragment implements
		OnClickListener {

	protected TextView mTitle;
	protected ImageButton mPlayPauseButton;

	private boolean mIsPlaying;
	private boolean mPlayOnResume = true;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);

		// request a window without the title
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		return dialog;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View root = inflater
				.inflate(R.layout.fragment_player, container, false);

		mTitle = (TextView) root.findViewById(R.id.text_title);
		mPlayPauseButton = (ImageButton) root.findViewById(R.id.image_state);
		mPlayPauseButton.setOnClickListener(this);

		return root;
	}

	@Override
	public void onClick(View v) {
		if (mIsPlaying) {
			onPauseMusic();
			mIsPlaying = false;
			mPlayPauseButton.setImageResource(R.drawable.ic_action_play);
		} else {
			onPlayMusic();
			mIsPlaying = true;
			mPlayPauseButton.setImageResource(R.drawable.ic_action_pause);
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		if (mPlayOnResume) {
			mPlayOnResume = false;
			onClick(null);
		}
	}

	protected abstract void onPauseMusic();

	protected abstract void onPlayMusic();
}
