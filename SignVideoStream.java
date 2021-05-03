package com.sign.language;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.service.handler.ServiceHandler;

public class SignVideoStream extends Activity {

	// Declare variables
	private ProgressDialog pDialog;
	private VideoView videoview;

	// URL to get contacts JSON
	private static String jsonUrl = "http://beta.json-generator.com/api/json/get/AFa876c";

	// JSON Node names
	private static final String TAG_SIGN_VIDEO = "signVideo";
	private static final String TAG_TITLE = "title";
	private static final String TAG_URL = "url";
	private String title, url;
	private String speechText;
	private Boolean isAvailable= false;
	Map<String, String> testMap;

	// contacts JSONArray
	JSONArray contacts = null;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sign_video_stream);

		speechText = getIntent().getExtras().getString("speechText");
		testMap = new HashMap<String, String>();
		// Find your VideoView in your video_main.xml layout
		videoview = (VideoView) findViewById(R.id.VideoView);
		// Execute StreamVideo AsyncTask

		// Calling async task to get json
		new GetContacts().execute();
	}

	
	private class GetContacts extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			// Create a progressbar
			pDialog = new ProgressDialog(SignVideoStream.this);
			// Set progressbar title
			pDialog.setTitle("Sing Language Video");
			// Set progressbar message
			pDialog.setMessage("Buffering...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			// Show progressbar
			pDialog.show();
			
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// Creating service handler class instance
			ServiceHandler sh = new ServiceHandler();

			// Making a request to url and getting response
			String jsonStr = sh.makeServiceCall(jsonUrl, ServiceHandler.GET);

			Log.d("Response: ", "> " + jsonStr);

			if (jsonStr != null && speechText != null) {
				try {
					JSONObject jsonObj = new JSONObject(jsonStr);

					// Getting JSON Array node
					contacts = jsonObj.getJSONArray(TAG_SIGN_VIDEO);

					// looping through All Contacts
					for (int i = 0; i < contacts.length(); i++) {
						JSONObject c = contacts.getJSONObject(i);
						
						title = c.getString(TAG_TITLE);
						url = c.getString(TAG_URL);
						testMap.put(title, url);
					}
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				Log.e("ServiceHandler", "Couldn't get any data from the url");
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			for (Entry<String, String> entry : testMap.entrySet()) {
	            if (entry.getKey().equals(speechText)) {
	                showSignVideo(entry.getValue());
	                isAvailable = true;
	            } else {
	            	System.out.println(entry.getKey()+" = "+speechText);
	            }
	        }
			
			if(!isAvailable) {
				Toast.makeText(SignVideoStream.this, "Sign video not availble..", Toast.LENGTH_LONG).show();
				finish();
			}
		}

	}
	
	private void showSignVideo(String url) {
		try {
			// Start the MediaController
			MediaController mediacontroller = new MediaController(
					SignVideoStream.this);
			mediacontroller.setAnchorView(videoview);
			// Get the URL from String VideoURL
			Uri video = Uri.parse(url);
			videoview.setMediaController(mediacontroller);
			videoview.setVideoURI(video);

		} catch (Exception e) {
			Log.e("Error", e.getMessage());
			e.printStackTrace();
		}

		videoview.requestFocus();
		videoview.setOnPreparedListener(new OnPreparedListener() {
			// Close the progress bar and play the video
			public void onPrepared(MediaPlayer mp) {
				videoview.start();
				pDialog.dismiss();
			}
		});
	}

}
