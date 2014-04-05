package com.deezer.sample.gracenote.ui;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import android.app.ListFragment;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.deezer.sample.gracenote.R;
import com.deezer.sample.gracenote.event.GN2DZResultListener;
import com.deezer.sample.gracenote.util.AsyncFindMusicFilesTask;
import com.deezer.sample.gracenote.util.AsyncFindMusicFilesTask.Listener;
import com.deezer.sample.gracenote.util.FileToTrackMap;
import com.deezer.sample.gracenote.util.deezer.DeezerHelper;
import com.deezer.sample.gracenote.util.gracenote.GraceNoteHelper;
import com.deezer.sample.gracenote.util.gracenote.RecognizeFileHelper;
import com.deezer.sdk.model.Track;

public class MusicFilesFragment extends ListFragment implements Listener,
		OnItemClickListener, GN2DZResultListener {

	private AsyncFindMusicFilesTask mFindMusicFilesTask;

	private final List<File> mFiles = new LinkedList<File>();
	private MusicFileAdapter mAdapter;

	private RecognizeFileHelper mHelper;

	/*
	 * FRAGMENT LIFECYCLE
	 */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setHasOptionsMenu(true);

		mHelper = GraceNoteHelper.getInstance().getRecognizeFileHelper(
				getActivity(), this);
	}

	@Override
	public void onResume() {
		super.onResume();

		Log.d("MusicFilesFragment", "onResume");

		getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		getListView().setOnItemClickListener(this);

		mAdapter = new MusicFileAdapter(getActivity(), mFiles);
		setListAdapter(mAdapter);

		mFindMusicFilesTask = new AsyncFindMusicFilesTask(this);
		mFindMusicFilesTask.execute(Environment.getExternalStorageDirectory());

	}

	@Override
	public void onPause() {
		super.onPause();

		if (mFindMusicFilesTask != null) {
			mFindMusicFilesTask.cancel(true);
		}
	}

	/*
	 * FIND MUSIC FILES LISTENER
	 */

	@Override
	public void onFileFound(File file) {
		mFiles.add(file);
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onComplete() {
		mFindMusicFilesTask = null;
	}

	/*
	 * LISTVIEW LISTENER
	 */

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		mAdapter.setSelected(position);
		getActivity().invalidateOptionsMenu();
	}

	/*
	 * SDK LISTENER
	 */

	// @Override
	// public void onTrackMapped(File file, Track track) {
	// getActivity().runOnUiThread(new Runnable() {
	//
	// @Override
	// public void run() {
	// mAdapter.notifyDataSetChanged();
	// getActivity().invalidateOptionsMenu();
	// }
	// });
	//
	// }

	// @Override
	// public void onTrackNotFound(final File file) {
	//

	// }

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
	public void onResultFound(Track track, Object requestId) {
		final File file = (File) requestId;
		FileToTrackMap.setMappedTrack(file, track);

		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				mAdapter.notifyDataSetChanged();
				getActivity().invalidateOptionsMenu();
				Toast.makeText(getActivity(),
						"Deezer Track found for file " + file.getName(),
						Toast.LENGTH_LONG).show();
			}
		});
	}

	@Override
	public void onNoResultFound(Object requestId) {
		final File file = (File) requestId;

		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {

				Toast.makeText(getActivity(),
						"No Match was found for file " + file.getName(),
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	/*
	 * OPTIONS MENU
	 */

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.file_actions, menu);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		File selected = mAdapter.getSelectedFile();

		Track track;
		if (selected == null) {
			track = null;
		} else {
			track = FileToTrackMap.getMappedTrack(selected);
		}

		menu.findItem(R.id.action_listen_file).setVisible(selected != null);
		menu.findItem(R.id.action_listen_deezer).setVisible(track != null);
		menu.findItem(R.id.action_gracenote_convert).setVisible(
				(selected != null) && (track == null));

		menu.findItem(R.id.action_add_favorites).setVisible(
				(DeezerHelper.getInstance().isUserLogged()) && (track != null));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		File selected = mAdapter.getSelectedFile();
		if (selected == null) {
			return true;
		}

		Track track = FileToTrackMap.getMappedTrack(selected);

		boolean res = true;
		switch (item.getItemId()) {
		case R.id.action_gracenote_convert:
			mHelper.recognizeFile(selected);
			break;
		case R.id.action_listen_file:
			new FilePlayerFragment(selected).show(getActivity()
					.getFragmentManager(), "File");

			break;
		case R.id.action_listen_deezer:
			if (track != null) {
				new DeezerPlayerFragment(track).show(getActivity()
						.getFragmentManager(), "File");
			}
			break;
		case R.id.action_add_favorites:
			DeezerHelper.getInstance()
					.addTrackToFavorites(getActivity(), track);
		default:
			res = super.onOptionsItemSelected(item);
			break;
		}

		return res;
	}

}
