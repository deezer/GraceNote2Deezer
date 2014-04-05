package com.deezer.sample.gracenote.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.deezer.sdk.model.Track;

/**
 * A simple map between user files and Deezer tracks
 */
public class FileToTrackMap {

	private static final Map<String, Track> MAP = new HashMap<String, Track>();

	/**
	 * 
	 * @param file
	 *            a user local file
	 * @return the Deezer track mapped to this file or null
	 */
	public static Track getMappedTrack(File file) {
		if (file == null) {
			return null;
		}
		return MAP.get(file.getPath());
	}

	/**
	 * Adds a mapping between a file and a track
	 * 
	 * @param file
	 *            the user local file
	 * @param track
	 *            the corresponding Deezer Track
	 */
	public static void setMappedTrack(File file, Track track) {
		if (file == null) {
			return;
		}

		if (track == null) {
			return;
		}

		MAP.put(file.getPath(), track);
	}

}
