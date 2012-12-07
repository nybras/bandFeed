package com.fyp.bandfeed;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;

import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import android.graphics.Bitmap;


public class UploadImage {
	InputStream inputStream;
	ArrayList<NameValuePair> nameValuePairs;
	
	public UploadImage() {
		nameValuePairs = new ArrayList<NameValuePair>();
	}

	public void getPicture(Bitmap bitmap, String bandName) {

		//Bitmap bitmap = BitmapFactory.decodeFile(“/sdcard/android.jpg”);
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream); // compress to
																// which format
																// you want.
		byte[] byte_arr = stream.toByteArray();
		String image_str = Base64.encodeBytes(byte_arr);
		
		nameValuePairs.add(new BasicNameValuePair("image", image_str));
		nameValuePairs.add(new BasicNameValuePair("bandName", bandName));
		
	}

	public String makeConnection() {
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(
					"http://bandfeed.co.uk/images/upload_image.php");
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			
			String the_string_response = convertResponseToString(response);
			return the_string_response;

		} catch (Exception e) {
			// Connection Error
			System.out.println("Error in http connection " + e.toString());
		}
		return null;
	}

	public String convertResponseToString(HttpResponse response)
			throws IllegalStateException, IOException {

		String res = "";
		StringBuffer buffer = new StringBuffer();
		inputStream = response.getEntity().getContent();
		int contentLength = (int) response.getEntity().getContentLength(); // getting
																			// content
																			// length…..

		if (contentLength < 0) {
			// Picture will not send
		} else {
			byte[] data = new byte[512];
			int len = 0;
			try {
				while (-1 != (len = inputStream.read(data))) {
					buffer.append(new String(data, 0, len)); // converting to
																// string and
																// appending to
																// stringbuffer…..
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				inputStream.close(); // closing the stream…..
			} catch (IOException e) {
				e.printStackTrace();
			}
			res = buffer.toString(); // converting stringbuffer to string…..

//			Toast.makeText(UploadImage.this, "Result : " + res,
//					Toast.LENGTH_LONG).show();
			// System.out.println("Response => " +
			// EntityUtils.toString(response.getEntity()));
		}
		return res;
	}
	

}