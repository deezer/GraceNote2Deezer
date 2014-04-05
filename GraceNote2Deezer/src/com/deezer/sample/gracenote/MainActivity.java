package com.deezer.sample.gracenote;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.deezer.sample.gracenote.ui.MainFragment;
import com.deezer.sample.gracenote.ui.MusicStreamFragment;
import com.deezer.sample.gracenote.ui.MusicFilesFragment;
import com.deezer.sample.gracenote.util.deezer.DeezerHelper;
import com.deezer.sample.gracenote.util.deezer.DeezerHelper.DeezerLoginListener;
import com.deezer.sample.gracenote.util.gracenote.GraceNoteHelper;

/**
 * The main activity
 */
public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new MainFragment()).commit();
		}

		DeezerHelper.getInstance().initialize(this);
		GraceNoteHelper.getInstance().initialize(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.deezer_actions, menu);

		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		boolean logged = DeezerHelper.getInstance().isUserLogged();

		menu.findItem(R.id.action_login).setVisible(!logged);
		menu.findItem(R.id.action_logout).setVisible(logged);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean res = true;

		switch (item.getItemId()) {
		case R.id.action_login:
			DeezerHelper.getInstance().deezerLogin(this, mDeezerLoginListener);
			break;
		case R.id.action_logout:
			DeezerHelper.getInstance().deezerLogout(this);
			break;
		default:
			res = super.onOptionsItemSelected(item);
			break;
		}

		return res;
	}

	//
	// FRAGMENTS
	//

	/**
	 * Launches the {@link MusicFilesFragment}
	 * 
	 * @param view
	 *            the button view
	 */
	public void onRecognizeFiles(View view) {
		getFragmentManager().beginTransaction()
				.replace(R.id.container, new MusicFilesFragment())
				.addToBackStack("Files").commit();
	}

	/**
	 * Launches the {@link MusicStreamFragment}
	 * 
	 * @param view
	 *            the button view
	 */
	public void onRecognizeMicro(View view) {
		getFragmentManager().beginTransaction()
				.replace(R.id.container, new MusicStreamFragment())
				.addToBackStack("Micro").commit();
	}

	//
	// DEEZER LOGIN
	//

	private final DeezerLoginListener mDeezerLoginListener = new DeezerLoginListener() {

		@Override
		public void onUserLogged() {
			invalidateOptionsMenu();
			Toast.makeText(MainActivity.this, "You are now logged in Deezer",
					Toast.LENGTH_LONG).show();
		}

		@Override
		public void onLogError(Throwable t) {
			Toast.makeText(MainActivity.this, "Unable to log in to Deezer",
					Toast.LENGTH_LONG).show();
		}

		@Override
		public void onCancelled() {
		}
	};

}
