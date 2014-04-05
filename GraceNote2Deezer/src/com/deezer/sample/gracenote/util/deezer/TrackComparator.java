package com.deezer.sample.gracenote.util.deezer;

import java.util.Comparator;

import android.text.TextUtils;

import com.deezer.sdk.model.Track;

/**
 * A comparator to sort track results to find the most relevant one for the
 * current user
 */
public class TrackComparator implements Comparator<Track> {

	@Override
	public int compare(Track lhs, Track rhs) {

		if (lhs == rhs) {
			return 0;
		}

		boolean lStream = "false".equals(lhs.getStream())
				|| TextUtils.isEmpty(lhs.getStream());
		boolean rStream = "false".equals(lhs.getStream())
				|| TextUtils.isEmpty(lhs.getStream());

		// Readability differences
		if (lhs.isReadable() != rhs.isReadable()) {
			if (lhs.isReadable()) {
				return -1;
			} else {
				return 1;
			}
		} else if (rStream != lStream) {
			if (lStream) {
				return -1;
			} else {
				return 1;
			}
		} else {
			return rhs.getRank() - lhs.getRank();
		}

	}
}
