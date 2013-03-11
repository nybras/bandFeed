/**
 * @author Brett Flitter
 * @version Prototype1 - 20/02/2013
 * @title Project bandFeed
 */

package com.fyp.bandfeed;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.util.Log;

public class ConnectToHttpAPI extends Activity {

	// This class is used to connect to RabbitMQ's HTTP API
	
	public String connect(String band_name, String req) {
		
		InputStream is = null;
		
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpHost targetHost = new HttpHost("81.169.135.67", 55672, "http");

		HttpGet request = new HttpGet(req);

		//Set credentials for RabbitMQ
		httpClient.getCredentialsProvider().setCredentials(
				new AuthScope(targetHost.getHostName(), targetHost.getPort()),
				new UsernamePasswordCredentials("admin", "prrpm5uBbf"));

		request.setHeader("Accept", "application/json");
		request.setHeader("Content-Type", "application/json");

		try {
			HttpResponse response = httpClient.execute(targetHost, request);
			HttpEntity httpEntity = response.getEntity();
			is = httpEntity.getContent();

		} catch (ClientProtocolException e) {
			Log.e("ClientProtocol", "Error with credentials " + e.toString());
			return null;
		} catch (IOException e) {
			Log.e("IOException Error",
					"Error making http response " + e.toString());
			return null;
		}

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			return sb.toString();
			
		} catch (Exception e) {
			Log.e("Buffer Error", "Error converting result " + e.toString());
			return null;
		}
	}
}
