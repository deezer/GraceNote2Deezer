package com.deezer.sample.gracenote.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.deezer.sample.gracenote.util.deezer.DeezerHelper;
import com.deezer.sdk.model.Track;
import com.deezer.sdk.player.TrackPlayer;

public class DeezerPlayerFragment extends AMusicPlayerFragment {

	private TrackPlayer mPlayer;
	private Track mTrack;

	public DeezerPlayerFragment(Track track) {
		mTrack = track;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			mPlayer = DeezerHelper.getInstance().getTrackPlayer(
					getActivity().getApplication());
		} catch (Exception e) {
			Log.e("DeezerPlayerFragment", "Unable to create player", e);

			Toast.makeText(getActivity(),
					"Unable to create player, you wont be able to play music",
					Toast.LENGTH_LONG).show();

			dismiss();
		}
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mTitle.setText(mTrack.getTitle());
	}

	@Override
	protected void onPlayMusic() {

		switch (mPlayer.getPlayerState()) {
		case STARTED:
			mPlayer.playTrack(mTrack.getId());
			break;
		case READY:
		case PAUSED:
		case PLAYBACK_COMPLETED:
			mPlayer.play();
		default:
			break;
		}

	}

	@Override
	protected void onPauseMusic() {
		mPlayer.pause();
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
		if (mPlayer != null) {
			mPlayer.stop();
			mPlayer.release();
		}
	}

}
