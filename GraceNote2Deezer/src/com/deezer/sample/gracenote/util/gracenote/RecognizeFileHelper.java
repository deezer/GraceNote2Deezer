package com.deezer.sample.gracenote.util.gracenote;

import java.io.File;

import android.content.Context;

import com.deezer.sample.gracenote.event.GN2DZResultListener;
import com.gracenote.mmid.MobileSDK.GNConfig;
import com.gracenote.mmid.MobileSDK.GNOperations;

/**
 * A helper
 */
public class RecognizeFileHelper {

	private final GN2DZResultListener mListener;
	private final GNConfig mGraceNoteConfig;

	/**
	 * @param config
	 *            the {@link GNConfig} instance
	 * @param context
	 *            the current application context
	 * @param listener
	 *            the listener for GraceNote results
	 */
	public RecognizeFileHelper(GNConfig config, Context context,
			GN2DZResultListener listener) {

		mGraceNoteConfig = config;
		mListener = listener;
	}

	/**
	 * Starts a recognition process on a music file
	 * 
	 * @param file
	 *            the file to recognize
	 */
	public void recognizeFile(File file) {
		GNOperations.recognizeMIDFileFromFile(new GraceNoteResultHelper(
				mListener, file), mGraceNoteConfig, file.getAbsolutePath());
	}
}
