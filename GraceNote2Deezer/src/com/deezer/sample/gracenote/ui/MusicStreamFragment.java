package com.deezer.sample.gracenote.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.deezer.sample.gracenote.R;
import com.deezer.sample.gracenote.event.GN2DZResultListener;
import com.deezer.sample.gracenote.util.deezer.DeezerHelper;
import com.deezer.sample.gracenote.util.gracenote.GraceNoteHelper;
import com.deezer.sample.gracenote.util.gracenote.RecognizeStreamHelper;
import com.deezer.sdk.model.Track;

public class MusicStreamFragment extends Fragment implements OnClickListener,
		GN2DZResultListener {

	private Track mTrack = null;

	private ImageButton mButton;
	private View mResult;

	private TextView mTitle, mArtist, mAlbum;
	private ImageView mCover;

	private boolean mRecording;

	private RecognizeStreamHelper mHelper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setHasOptionsMenu(true);

		mHelper = GraceNoteHelper.getInstance().getRecognizeStreamHelper(
				getActivity(), this);
		mRecording = false;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_mic, container, false);

		mButton = (ImageButton) root.findViewById(R.id.toggle);
		mButton.setOnClickListener(this);

		mResult = root.findViewById(R.id.layout_result);

		mTitle = (TextView) root.findViewById(R.id.text_title);
		mArtist = (TextView) root.findViewById(R.id.text_artist);
		mAlbum = (TextView) root.findViewById(R.id.text_album);
		mCover = (ImageView) root.findViewById(R.id.image_cover);

		clearResults();

		return root;
	}

	@Override
	public void onResume() {
		super.onResume();
		mHelper.startOperation();
	}

	@Override
	public void onPause() {
		super.onPause();
		mHelper.stopOperation();
	}

	@Override
	public void onClick(View v) {
		if (mRecording) {
			// stop recording
			mButton.setImageResource(R.drawable.ic_action_mic_off);
			mHelper.cancelIdNow();
		} else {
			// start recording
			mButton.setImageResource(R.drawable.ic_action_mic_on);
			clearResults();
			mHelper.idNow();
		}

	}

	private void clearResults() {
		mResult.setVisibility(View.GONE);

	}

	public void displayTrack(Track track) {
		mCover.setImageResource(R.drawable.ic_track_valid);
		mTitle.setText(track.getTitle());
		mResult.setVisibility(View.VISIBLE);

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

	@Override
	public void onResultFound(final Track track, Object requestId) {
		mTrack = track;

		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(getActivity(), "We found something",
						Toast.LENGTH_LONG).show();

				mButton.setImageResource(R.drawable.ic_action_mic_off);
				mRecording = false;

				displayTrack(track);

				getActivity().invalidateOptionsMenu();
			}
		});
	}

	@Override
	public void onStatusUpdate(final String status, Object requestId) {
		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(getActivity(), status, Toast.LENGTH_SHORT)
						.show();
			}
		});

	}

	@Override
	public void onNoResultFound(Object requestId) {
		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {

				mButton.setImageResource(R.drawable.ic_action_mic_off);
				mRecording = false;
				Toast.makeText(getActivity(), "No Match was found",
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	/*
	 * OPTIONS MENU
	 */

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.mic_actions, menu);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {

		menu.findItem(R.id.action_listen_deezer).setVisible(mTrack != null);
		menu.findItem(R.id.action_add_favorites)
				.setVisible(
						(DeezerHelper.getInstance().isUserLogged())
								&& (mTrack != null));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		boolean res = true;
		switch (item.getItemId()) {
		case R.id.action_listen_deezer:
			if (mTrack != null) {
				new DeezerPlayerFragment(mTrack).show(getActivity()
						.getFragmentManager(), "File");
			}
			break;
		case R.id.action_add_favorites:
			DeezerHelper.getInstance().addTrackToFavorites(getActivity(),
					mTrack);
		default:
			res = super.onOptionsItemSelected(item);
			break;
		}

		return res;
	}
}
