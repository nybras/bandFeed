/**
 * @author Brett Flitter
 * @version Prototype1 - 20/02/2013
 * @title Project bandFeed
 */

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

	private static final String UsersBandsURL = "http://bandfeed.co.uk/api/users_bands.php";
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
		JSONObject json = jsonParser.makeHttpRequest(UsersBandsURL, "GET",
				params);

		if (json != null) {
			// check log cat for response
			Log.d("Create Response", json.toString());
			try {
				if (json.getInt("success") == 1) {
					//Users bands returned from server
					JSONArray bandsObj = json.getJSONArray("bands"); 

					for (int i = 0; i < bandsObj.length(); i++) {
						JSONObject band = bandsObj.getJSONObject(i);
						bands.add(band.getString("band"));
					}
					//At least one band found, list of bands returned
					return bands;
				}
			} catch (JSONException e) {
				//JSON Exception, can't return any bands
				return null;
			}
		}
		//Returns null if no bands are found
		return null;
	}
}
