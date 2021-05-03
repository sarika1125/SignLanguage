package com.sign.language;

import java.util.Locale;

import android.os.Bundle;
import android.app.Activity;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Get_voice extends Activity {
EditText edit;
Button get;
TextToSpeech textToSpeech;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_get_voice);
		edit=(EditText)findViewById(R.id.voicetext);
		get=(Button)findViewById(R.id.voice);
		textToSpeech =new TextToSpeech(Get_voice.this, new OnInitListener() {
			
			@Override
			public void onInit(int status) {
				// TODO Auto-generated method stub
				if (status!=TextToSpeech.ERROR) {
					textToSpeech.setLanguage(Locale.UK);
				}
			}
		});
get.setOnClickListener(new OnClickListener() {
			
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String data=edit.getText().toString();
				textToSpeech.speak(data, TextToSpeech.QUEUE_FLUSH, null);
			}
		});
		
	}

	public void onpause(){
		if (textToSpeech!=null) {
			textToSpeech.stop();
			textToSpeech.shutdown();
		}
		super.onPause();
	}
		
	


}
