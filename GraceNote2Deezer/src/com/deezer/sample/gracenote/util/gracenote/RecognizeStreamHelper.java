package com.deezer.sample.gracenote.util.gracenote;

import android.content.Context;
import android.util.Log;

import com.deezer.sample.gracenote.event.GN2DZResultListener;
import com.gracenote.mmid.MobileSDK.GNAudioConfig;
import com.gracenote.mmid.MobileSDK.GNAudioSourceDelegate;
import com.gracenote.mmid.MobileSDK.GNAudioSourceMic;
import com.gracenote.mmid.MobileSDK.GNConfig;
import com.gracenote.mmid.MobileSDK.GNOperationStatusChanged;
import com.gracenote.mmid.MobileSDK.GNRecognizeStream;
import com.gracenote.mmid.MobileSDK.GNStatus;

/**
 * A helper class used to recognize a live stream using GraceNote's SDK. This
 * handles the whole process of listening to the microphone and triggering a
 * recognition request
 */
public class RecognizeStreamHelper implements GNOperationStatusChanged,
		GNAudioSourceDelegate {

	private final GN2DZResultListener mListener;

	private final GNConfig mGNConfig;

	private final GNRecognizeStream mGNRecognizeStream;
	private final GNAudioSourceMic mGNAudioSourceMic;
	private final GNAudioConfig mGNAudioConfig;

	/**
	 * @param config
	 *            the {@link GNConfig} instance
	 * @param context
	 *            the current application context
	 * @param listener
	 *            the listener for GraceNote results
	 */
	public RecognizeStreamHelper(GNConfig config, Context context,
			GN2DZResultListener listener) {
		mGNConfig = config;

		int sampleRate = 44100;
		int bytesPerSample = 2;
		int numChannels = 1;
		mGNAudioConfig = new GNAudioConfig(sampleRate, bytesPerSample,
				numChannels);
		mGNRecognizeStream = new GNRecognizeStream(mGNConfig);
		mGNAudioSourceMic = new GNAudioSourceMic(mGNAudioConfig, this);

		mListener = listener;
	}

	/**
	 * Starts listening to the audio input
	 */
	public void startOperation() {
		Log.i("RecognizeStreamHelper", "startOperation");
		mGNRecognizeStream.startSession(new GraceNoteResultHelper(mListener,
				null), mGNAudioConfig);
		try {
			mGNAudioSourceMic.startRecording();
		} catch (Exception e) {
			// audio-recording initialization failed
			e.printStackTrace();
			stopOperation();

			// TODO notify listener
			// updateStatusOnMainThread("Error: Unable to get input from microphone. ID-Now will fail.");
			// buttonMic.setError("Invalid operation");
		}
	}

	/**
	 * Starts a recognition process. The {@link #startOperation()} method should
	 * be called before that
	 */
	public void idNow() {
		Log.i("RecognizeStreamHelper", "idNow");
		mGNRecognizeStream.idNow();
	}

	/**
	 * Cancel a recognition process
	 */
	public void cancelIdNow() {
		Log.i("RecognizeStreamHelper", "cancelIdNow");
		mGNRecognizeStream.cancelIdNow();
	}

	/**
	 * Stops all process (recognition and audio input listening)
	 */
	public void stopOperation() {
		Log.i("RecognizeStreamHelper", "stopOperation");
		mGNAudioSourceMic.stopRecording();

		mGNRecognizeStream.stopSession();
		// clearResults();
	}

	@Override
	public void audioBufferReady(byte[] samples, boolean audioInputError) {
		Log.v("RecognizeStreamHelper", "audioBufferReady ( [" + samples.length
				+ "], " + audioInputError + ")");

		if (audioInputError) {
			mListener.onStatusUpdate(
					"Unable to get input from microphone. ID-Now will fail.",
					null);
		} else {
			try {
				mGNRecognizeStream.writeBytes(samples);
			} catch (Exception e) {
				Log.e("RecognizeStreamHelper", "writeBytes", e);
			}
		}
	}

	@Override
	public void GNStatusChanged(GNStatus status) {
		Log.d("RecognizeStreamHelper",
				"GNStatusChanged (" + status.getMessage() + ")");
		mListener.onStatusUpdate(status.getMessage(), null);
	}

}
