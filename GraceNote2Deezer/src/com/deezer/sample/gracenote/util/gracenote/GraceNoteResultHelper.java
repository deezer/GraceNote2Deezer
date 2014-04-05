package com.deezer.sample.gracenote.util.gracenote;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.util.Log;

import com.deezer.sample.gracenote.event.GN2DZResultListener;
import com.deezer.sample.gracenote.util.deezer.DeezerHelper;
import com.deezer.sample.gracenote.util.deezer.DeezerHelper.DeezerTrackInfoListener;
import com.deezer.sample.gracenote.util.deezer.TrackComparator;
import com.deezer.sdk.model.Track;
import com.gracenote.mmid.MobileSDK.GNLinkData;
import com.gracenote.mmid.MobileSDK.GNSearchResponse;
import com.gracenote.mmid.MobileSDK.GNSearchResult;
import com.gracenote.mmid.MobileSDK.GNSearchResultReady;

/**
 * Parses the results of a GraceNote request and launches Deezer request to get
 * the corresponding tracks informations
 */
public class GraceNoteResultHelper implements GNSearchResultReady,
		DeezerTrackInfoListener {

	private static final String DZ_TRACk_ID = "deezer-track-id";
	private static final String TAG = "GraceNoteResultHelper";

	private final GN2DZResultListener mListener;

	private final List<Track> mMatchingTracks = new ArrayList<>();

	private int mExpectedResults;
	private boolean mAllRequestsSent;
	private final Object mRequestId;

	/**
	 * 
	 * @param listener
	 *            the listener for results
	 * @param requestId
	 *            the id representing the request for a track
	 */
	public GraceNoteResultHelper(GN2DZResultListener listener, Object requestId) {
		mListener = listener;
		mExpectedResults = 0;
		mAllRequestsSent = false;
		mRequestId = requestId;
	}

	@Override
	public void GNResultReady(GNSearchResult result) {

		// No Match Found, lets get out of here
		if (result.isFingerprintSearchNoMatchStatus()) {
			Log.i("Response", "No match found");
			mListener.onNoResultFound(mRequestId);
			return;
		}

		GNSearchResponse[] responses = result.getResponses();

		if (responses.length == 0) {
			mListener.onNoResultFound(mRequestId);
		} else {
			mListener.onStatusUpdate("GraceNote found " + responses.length
					+ " match(es)", mRequestId);
			for (GNSearchResponse response : responses) {
				handleResponse(response);
			}
		}

		mAllRequestsSent = true;
	}

	private void handleResponse(GNSearchResponse response) {
		// safe check
		if (response == null) {
			return;
		}

		Log.d(TAG, "handleResponse");

		GNLinkData[] links = response.getTrackLinkData();
		if (links.length == 0) {
			mListener.onNoResultFound(mRequestId);
		} else {

			for (GNLinkData link : links) {
				if (DZ_TRACk_ID.equals(link.getSource())) {
					handleLinkData(link);
				} else {
					Log.w(TAG, "Unknown source " + link.getSource());
				}
			}
		}
	}

	private void handleLinkData(GNLinkData link) {
		Log.d(TAG, "handleLinkData (" + link.getSource() + " #" + link.getId()
				+ ")");

		mExpectedResults++;
		DeezerHelper.getInstance().getTrackInfo(link.getId(), this);
	}

	@Override
	public void onTrackInfoRetrieved(Track track) {
		mExpectedResults--;
		mMatchingTracks.add(track);
		checkAllResultsRetrieved();
	}

	@Override
	public void onTrackInfoError(Throwable t) {
		mExpectedResults--;
		checkAllResultsRetrieved();
	}

	private synchronized void checkAllResultsRetrieved() {
		Log.d(TAG, "checkAllResultsRetrieved");
		if (!mAllRequestsSent) {
			Log.d(TAG, "mAllRequestsSent is false");
			return;
		}

		if (mExpectedResults > 0) {
			Log.d(TAG, "mExpectedResults > 0");
			return;
		}

		if (mMatchingTracks.size() > 0) {

			Log.d(TAG, "Sorting tracks :) ");
			Collections.sort(mMatchingTracks, new TrackComparator());

			// get best result
			Track best = mMatchingTracks.get(0);
			mListener.onResultFound(best, mRequestId);
		} else {
			mListener.onNoResultFound(mRequestId);
		}
	}

}
