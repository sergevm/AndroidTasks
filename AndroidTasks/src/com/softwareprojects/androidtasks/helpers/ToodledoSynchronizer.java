package com.softwareprojects.androidtasks.helpers;

import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.softwareprojects.androidtasks.helpers.RestClient.RequestMethod;

public class ToodledoSynchronizer {

	private static final String TAG = ToodledoSynchronizer.class.getSimpleName();
	private static final double A_LITTLE_LESS_THAN_4_HOURS = 1000 * 60 * 60 * 3.99;

	private static final String TOODLEDO_APPTOKEN = "api4d360500c4156";
	private static final String TOODLEDO_APPID = "AndroidTasks";

	private ToodledoAccountInfo accountInfo;

	private static Calendar sessionTokenAcquisitionDate;
	private static String sessionToken;
	private static String signature;
	private static String key;

	public void init(final String userId, final String password) {

		if (signature == null)
			signature = getSignature(userId);

		if (sessionTokenExpired()) {
			Log.v(TAG, "Session token expired. Retrieving a new token ...");

			sessionTokenAcquisitionDate = Calendar.getInstance();
			sessionToken = getSessionToken(signature, userId);
			key = getInteractionKey(password, sessionToken);

			Log.v(TAG, "Session token requested and acquired.");
		}

		accountInfo = new ToodledoAccountInfo(userId);
	}

	public void sync() {
		updateAccountInfo();
	}

	private void updateAccountInfo() {
		String url = "http://api.toodledo.com/2/account/get.php?key=" + key;

		RestClient client = new RestClient(url);
		try {
			client.Execute(RequestMethod.GET);
			String response = client.getResponse();

			if (response == null || response == "") {
				Log.d(TAG, "Get request for account info returned empty response");
				return;
			}

			JSONObject json = new JSONObject(response);
			accountInfo.setLastEditTask(json.getLong("lastedit_task"));
			accountInfo.setLastDeleteTask(json.getLong("lastdelete_task"));
			switch (json.getInt("dateformat")) {
			case 0:
				accountInfo.setDateFormat("MM yyyy");
				break;
			case 1:
				accountInfo.setDateFormat("MM/dd/yyyy");
				break;
			case 2:
				accountInfo.setDateFormat("dd/MM/yyyy");
				break;
			case 3:
				accountInfo.setDateFormat("yyyy-MM-dd");
				break;
			}

		} catch (JSONException ex) {
			Log.e(TAG, "updateAccountInfo: " + ex.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean sessionTokenExpired() {
		if (sessionTokenAcquisitionDate == null)
			return true;
	
		Calendar now = Calendar.getInstance();
		long sessionTokenAcquisitionAge = now.getTimeInMillis() - sessionTokenAcquisitionDate.getTimeInMillis();
		return (sessionTokenAcquisitionAge > A_LITTLE_LESS_THAN_4_HOURS);
	}

	private String getSignature(String userId) {
		try {
			String toDigest = userId + TOODLEDO_APPTOKEN;
			return MD5Helper.calculate(toDigest);
		} catch (NoSuchAlgorithmException e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		}

		return null;
	}

	private String getSessionToken(final String signature, final String userId) {
	
		String url = "http://api.toodledo.com/2/account/token.php?userid=" + userId + ";appid=" + TOODLEDO_APPID
				+ ";sig=" + signature;
	
		RestClient client = new RestClient(url);
		try {
			client.Execute(RequestMethod.GET);
			String response = client.getResponse();
	
			if (response == null || response == "") {
				Log.d(TAG, "getSessionToken returned empty response");
				return null;
			}
	
			JSONObject json = new JSONObject(response);
			String token = json.getString("token");
			return token;
	
		} catch (JSONException ex) {
			Log.e(TAG, "getSessionToken: " + ex.getMessage());
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private String getInteractionKey(final String password, final String sessionToken) {
		try {
			return MD5Helper.calculate(MD5Helper.calculate(password) + TOODLEDO_APPTOKEN + sessionToken);
		} catch (NoSuchAlgorithmException e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

}