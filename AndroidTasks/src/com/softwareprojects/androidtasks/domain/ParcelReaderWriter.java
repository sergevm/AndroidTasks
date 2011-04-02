package com.softwareprojects.androidtasks.domain;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Parcel;

import com.softwareprojects.androidtasks.Constants;

class ParcelReaderWriter {
	static void writeDateToParcel(Parcel parcel, Date date) {
		if(date != null) {
			parcel.writeString(new SimpleDateFormat(
					Constants.DATETIME_FORMAT_STRING).format(date));
		}
	}
	
	static Date readDateFromParcel(Parcel parcel) throws ParseException {
		String dateAsString = parcel.readString();
		if (dateAsString != null) {
			return new SimpleDateFormat(
					Constants.DATETIME_FORMAT_STRING).parse(dateAsString);
		}
		
		return null;
	}

	static void writeDateOrNullToParcel(Parcel parcel, Date date) {
		if(date != null) {
			parcel.writeString(new SimpleDateFormat(
					Constants.DATETIME_FORMAT_STRING).format(date));
		}
		else {
			parcel.writeString(null);
		}
	}
}
