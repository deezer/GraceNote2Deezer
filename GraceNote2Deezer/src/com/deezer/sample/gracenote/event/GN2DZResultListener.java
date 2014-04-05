package com.deezer.sample.gracenote.event;

import com.deezer.sdk.model.Track;

/**
 * A listener for GraceNote recognition and Deezer mapping
 */
public interface GN2DZResultListener {

	/**
	 * Called to provide a message to the user
	 * 
	 * @param message
	 *            the message to display
	 * @param requestId
	 *            the id of the request which triggered this call
	 */
	void onStatusUpdate(String message, Object requestId);

	/**
	 * Called when no result could be found
	 * 
	 * @param requestId
	 *            the id of the request which triggered this call
	 */
	void onNoResultFound(Object requestId);

	/**
	 * Called when a Deezer track matched the search GraceNote recognition
	 * request
	 * 
	 * @param track
	 *            the track instance
	 * @param requestId
	 *            the id of the request which triggered this call
	 */
	void onResultFound(Track track, Object requestId);
}
