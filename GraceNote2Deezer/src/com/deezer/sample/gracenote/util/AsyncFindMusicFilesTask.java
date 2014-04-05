package com.deezer.sample.gracenote.util;

import java.io.File;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

/**
 * An AsyncTask searching for music files in given directories
 */
public class AsyncFindMusicFilesTask extends AsyncTask<File, File, Void> {

	/**
	 * A listener for events from this task
	 */
	public interface Listener {

		/**
		 * Called when a music file was found
		 * 
		 * @param file
		 *            the file
		 */
		void onFileFound(File file);

		/**
		 * Called when all the directories where searched
		 */
		void onComplete();
	}

	private Listener mListener;

	/**
	 * 
	 * @param listener
	 *            the listener
	 */
	public AsyncFindMusicFilesTask(Listener listener) {
		mListener = listener;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		Log.d("Task", "onPreExecute");

	}

	@Override
	protected Void doInBackground(File... params) {

		for (File file : params) {
			// only parse files we can read
			if (!file.canRead()) {
				Log.w("Task", "Unreadable : " + file.getPath());
				continue;
			}

			// only handle directory params
			if (file.isDirectory()) {
				findMusicFiles(file);
			}
		}

		return null;
	}

	@Override
	protected void onProgressUpdate(File... values) {
		super.onProgressUpdate(values);

		for (File file : values) {
			// Log.d("Task", "onProgressUpdate " + file.getName());
			mListener.onFileFound(file);
		}
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);

		Log.d("Task", "onPostExecute");

	}

	private void findMusicFiles(File directory) {

		// Log.v("Task", "Checking dir " + directory.getPath());

		File[] children = directory.listFiles();

		// safe check
		if (children == null) {
			return;
		}

		// speed things up by ignoring the sdcard/android folder
		String ignoredPath = new File(
				Environment.getExternalStorageDirectory(), "Android")
				.getAbsolutePath();
		if (ignoredPath.equals(directory.getAbsolutePath())) {
			Log.d("Task", "Ignored Android data folder");
			return;
		}

		for (File child : children) {

			// only parse files we can read
			if (!child.canRead()) {
				Log.w("Task", "Unreadable : " + child.getPath());
				continue;
			}

			if (child.isDirectory()) {
				findMusicFiles(child);
			} else if (isSupportedFormat(child.getName())) {
				publishProgress(child);
			}
		}

	}

	private boolean isSupportedFormat(String filename) {

		String lowerCaseFilePath = filename.toLowerCase();
		return (lowerCaseFilePath.endsWith(".wav") || lowerCaseFilePath
				.endsWith(".mp3"));
	}
}
