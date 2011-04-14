package com.softwareprojects.androidtasks.domain;

public interface ILog {
	
	abstract void e(String tag, String message);

	abstract void v(String tag, String message);

	abstract void d(String tag, String message);

	abstract void w(String tag, String message);

	abstract void i(String tag, String message);

}
