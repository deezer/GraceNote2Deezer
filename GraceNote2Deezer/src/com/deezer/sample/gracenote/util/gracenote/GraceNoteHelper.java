package com.deezer.sample.gracenote.util.gracenote;

import android.content.Context;

import com.deezer.sample.gracenote.event.GN2DZResultListener;
import com.gracenote.mmid.MobileSDK.GNConfig;

/**
 * An helper class, handling the GraceNote SDK actions
 */
public class GraceNoteHelper {

	/** Replace this with your app id in the form "[ClientID]-[ClientID Tag]" */
	public static final String GRACENOTE_APP_ID = "xxxxxx-xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";

	private GNConfig mGraceNoteConfig;

	private static GraceNoteHelper sInstance;

	/**
	 * @return the singleton instance of this helper class
	 */
	public static GraceNoteHelper getInstance() {
		if (sInstance == null) {
			synchronized (GraceNoteHelper.class) {
				if (sInstance == null) {
					sInstance = new GraceNoteHelper();
				}
			}
		}

		return sInstance;
	}

	/**
	 * 
	 * Initialises this helper
	 * 
	 * @param context
	 *            the current application context
	 */
	public void initialize(Context context) {
		// Initialise the GraceNote SDK
		mGraceNoteConfig = GNConfig.init(GRACENOTE_APP_ID,
				context.getApplicationContext());
	}

	public RecognizeStreamHelper getRecognizeStreamHelper(Context context,
			GN2DZResultListener listener) {
		return new RecognizeStreamHelper(mGraceNoteConfig, context, listener);
	}

	public RecognizeFileHelper getRecognizeFileHelper(Context context,
			GN2DZResultListener listener) {
		return new RecognizeFileHelper(mGraceNoteConfig, context, listener);
	}
}
