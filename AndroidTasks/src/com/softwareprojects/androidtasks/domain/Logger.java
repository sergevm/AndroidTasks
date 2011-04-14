package com.softwareprojects.androidtasks.domain;

import android.util.Log;

public class Logger implements ILog {

	@Override
	public void i(String tag, String message) {
		Log.i(tag, message);
	}

	@Override
	public void w(String tag, String message) {
		Log.w(tag, message);
	}

	@Override
	public void d(String tag, String message) {
		Log.d(tag, message);
	}

	@Override
	public void v(String tag, String message) {
		Log.v(tag, message);
	}

	@Override
	public void e(String tag, String message) {
		Log.e(tag, message);
	}
}
