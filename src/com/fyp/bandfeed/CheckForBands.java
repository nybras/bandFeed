package com.fyp.bandfeed;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class CheckForBands {

	private static String UsersBandsURL = "http://bandfeed.co.uk/api/users_bands.php";
	private String username;
	private ArrayList<String> bands;

	public CheckForBands(String username) {

		this.username = username;
		bands = new ArrayList<String>();
	}


	public ArrayList<String> check() {

		JSONParser jsonParser = new JSONParser();

		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("user_name", username));

		// getting JSON Object
		// Note that create profile url accepts POST method
		JSONObject json = jsonParser.makeHttpRequest(UsersBandsURL, "GET",
				params);

		// check log cat for response
		Log.d("Create Response", json.toString());
		try {
			int success = json.getInt("success");

			if (success == 1) {
				// successfully logged in

				JSONArray bandsObj = json.getJSONArray("bands"); // JSON
				
				

				for (int i = 0; i < bandsObj.length(); i++) {
					JSONObject band = bandsObj.getJSONObject(i);
					bands.add(band.getString("band"));
				}
				
				return bands;
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;

	}
}
