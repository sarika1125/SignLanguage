package com.sign.language;

import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

public class SpeechResponse extends Activity implements
		TextToSpeech.OnInitListener {

	private ImageButton btnVideo;
	private EditText txtInput;
	private TextToSpeech tts;
	private String textToSpeech;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.speech_response);

		textToSpeech = getIntent().getExtras().getString("textToSpeech"); 
		
		tts = new TextToSpeech(this, this);

		btnVideo = (ImageButton) findViewById(R.id.btnVideo);
		txtInput = (EditText) findViewById(R.id.editTextInput);

		btnVideo.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				speakOut();
			}
		});

	}

	@Override
	public void onDestroy() {
		// Don't forget to shutdown tts!
		if (tts != null) {
			tts.stop();
			tts.shutdown();
		}
		super.onDestroy();
	}

	@Override
	public void onInit(int status) {

		if (status == TextToSpeech.SUCCESS) {

			int result = tts.setLanguage(Locale.US);

			if (result == TextToSpeech.LANG_MISSING_DATA
					|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
				Log.e("TTS", "This Language is not supported");
			} else {
				btnVideo.setEnabled(true);
				speakOut();
			}

		} else {
			Log.e("TTS", "Initilization Failed!");
		}

	}

	private void speakOut() {

		tts.speak(textToSpeech, TextToSpeech.QUEUE_FLUSH, null);
	}

}
