package com.deezer.sample.gracenote.util.deezer;

import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.deezer.sdk.model.Track;
import com.deezer.sdk.network.connect.DeezerConnect;
import com.deezer.sdk.network.connect.SessionStore;
import com.deezer.sdk.network.connect.event.DialogError;
import com.deezer.sdk.network.connect.event.DialogListener;
import com.deezer.sdk.network.request.DeezerRequest;
import com.deezer.sdk.network.request.DeezerRequestFactory;
import com.deezer.sdk.network.request.event.DeezerError;
import com.deezer.sdk.network.request.event.JsonRequestListener;
import com.deezer.sdk.network.request.event.OAuthException;
import com.deezer.sdk.network.request.event.RequestListener;
import com.deezer.sdk.player.TrackPlayer;
import com.deezer.sdk.player.exception.TooManyPlayersExceptions;
import com.deezer.sdk.player.networkcheck.WifiAndMobileNetworkStateChecker;

/**
 * An helper class, handling the Deezer SDK actions
 */
public class DeezerHelper {

	/** Replace this with your deezer app id */
	public static final String DEEZER_APP_ID = "xxxxxx";

	private static final String TAG = "DeezerHelper";

	private DeezerConnect mDeezerConnect;

	private static DeezerHelper sInstance;

	/**
	 * A listener for the authentication action
	 */
	public interface DeezerLoginListener {

		void onUserLogged();

		void onLogError(Throwable t);

		void onCancelled();
	}

	/**
	 * A listener for Track search requests
	 */
	public interface DeezerTrackInfoListener {

		void onTrackInfoRetrieved(Track track);

		void onTrackInfoError(Throwable t);
	}

	/**
	 * @return the singleton instance of this helper class
	 */
	public static DeezerHelper getInstance() {
		if (sInstance == null) {
			synchronized (DeezerHelper.class) {
				if (sInstance == null) {
					sInstance = new DeezerHelper();
				}
			}
		}

		return sInstance;
	}

	/**
	 * Initialises this helper
	 * 
	 * @param context
	 *            the current application context
	 */
	public void initialize(Context context) {
		// Initialise the Deezer SDK
		mDeezerConnect = new DeezerConnect(context, DEEZER_APP_ID);
		if (new SessionStore().restore(mDeezerConnect, context)) {
			Log.i(TAG, "Deezer User is logged in");
		}
	}

	/**
	 * @return if a user is logged
	 */
	public boolean isUserLogged() {
		return mDeezerConnect.isSessionValid();
	}

	/**
	 * Authenticates the user
	 * 
	 * @param activity
	 *            the current top activity
	 * @param listener
	 *            the listener
	 */
	public void deezerLogin(final Activity activity,
			final DeezerLoginListener listener) {
		mDeezerConnect.authorize(activity, new String[] { "basic_access",
				"offline_access", "manage_library" }, new DialogListener() {

			@Override
			public void onComplete(Bundle values) {
				new SessionStore().save(mDeezerConnect, activity);
				listener.onUserLogged();
			}

			@Override
			public void onCancel() {
				listener.onCancelled();
			}

			@Override
			public void onOAuthException(OAuthException oAuthException) {
				listener.onLogError(oAuthException);
			}

			@Override
			public void onError(DialogError e) {
				listener.onLogError(e);
			}

			@Override
			public void onDeezerError(DeezerError e) {
				listener.onLogError(e);
			}

		});
	}

	/**
	 * Logs the user out
	 * 
	 * @param activity
	 *            the current top activity
	 */
	public void deezerLogout(Activity activity) {
		new SessionStore().clear(activity);
		mDeezerConnect.logout(activity);

		activity.invalidateOptionsMenu();
	}

	/**
	 * Launches a request to add a track to the current user's favorites
	 * 
	 * @param activity
	 *            the current top activity
	 * @param track
	 *            the track to add to the favorites
	 */
	public void addTrackToFavorites(final Activity activity, final Track track) {
		Bundle params = new Bundle(1);
		params.putString("track_id", Long.toString(track.getId()));
		DeezerRequest request = new DeezerRequest("user/me/tracks", params,
				"POST");

		mDeezerConnect.requestAsync(request, new RequestListener() {

			@Override
			public void onComplete(String response, Object requestId) {
				activity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(
								activity,
								track.getTitle()
										+ " has been added to your favorites",
								Toast.LENGTH_LONG).show();
					}
				});
			}

			@Override
			public void onOAuthException(OAuthException exception,
					Object requestId) {
				Log.e("SDK", "addTrackToFavorites", exception);
				onError();
			}

			@Override
			public void onMalformedURLException(
					MalformedURLException exception, Object requestId) {
				Log.e("SDK", "addTrackToFavorites", exception);
				onError();
			}

			@Override
			public void onIOException(IOException exception, Object requestId) {
				Log.e("SDK", "addTrackToFavorites", exception);
				onError();
			}

			@Override
			public void onDeezerError(DeezerError exception, Object requestId) {
				Log.e("SDK", "addTrackToFavorites", exception);
				onError();
			}

			private void onError() {
				activity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(activity,
								"Unable to add the track to the favorites",
								Toast.LENGTH_LONG).show();
					}
				});
			}
		});
	}

	/**
	 * Generates a Track Player object to play a Deezer Track
	 * 
	 * @param application
	 *            the current application
	 * @return the track player instance
	 * @throws OAuthException
	 *             if the deezerConnect's token is not valid
	 * @throws TooManyPlayersExceptions
	 *             if too many players are already created
	 * @throws DeezerError
	 *             if the SDK has not been initialised
	 */
	public TrackPlayer getTrackPlayer(Application application)
			throws OAuthException, TooManyPlayersExceptions, DeezerError {
		return new TrackPlayer(application, mDeezerConnect,
				new WifiAndMobileNetworkStateChecker());
	}

	/**
	 * Launches a request to get a Track full informations
	 * 
	 * @param id
	 *            the track id
	 * @param listener
	 *            the request listener
	 */
	public void getTrackInfo(String id, final DeezerTrackInfoListener listener) {
		long longId = 0;
		try {
			longId = Long.valueOf(id);
		} catch (NumberFormatException e) {
			listener.onTrackInfoError(e);
			return;
		}

		DeezerRequest request = DeezerRequestFactory.requestTrack(longId);

		mDeezerConnect.requestAsync(request, new JsonRequestListener() {

			@Override
			public void onResult(Object result, Object requestId) {
				Track track = (Track) result;
				listener.onTrackInfoRetrieved(track);
			}

			@Override
			public void onOAuthException(OAuthException exception,
					Object requestId) {
				listener.onTrackInfoError(exception);
			}

			@Override
			public void onMalformedURLException(
					MalformedURLException exception, Object requestId) {
				listener.onTrackInfoError(exception);
			}

			@Override
			public void onIOException(IOException exception, Object requestId) {
				listener.onTrackInfoError(exception);
			}

			@Override
			public void onDeezerError(DeezerError exception, Object requestId) {
				listener.onTrackInfoError(exception);
			}

			@Override
			public void onJSONParseException(JSONException exception,
					Object requestId) {
				listener.onTrackInfoError(exception);
			}
		});
	}
}
