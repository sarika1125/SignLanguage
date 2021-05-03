package com.service.handler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.sign.language.SignVideoStream;
import com.sign.language.SpeechResponse;

public class StartStopReceiver extends BroadcastReceiver {

	String body = null;
	private boolean deaf = true;

	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Bundle b = intent.getExtras();
		if(b == null) {
			return;
		}
		Object[] pdus = (Object[]) b.get("pdus");
		if (pdus != null) {
			for (int i = 0; i < pdus.length; i++) {
				SmsMessage SMessage = SmsMessage
						.createFromPdu((byte[]) pdus[i]);

				body = SMessage.getMessageBody().toString();

			}

			Log.i("Lock", "working" + body);
			if (deaf) {
				Intent i = new Intent(context, SignVideoStream.class);
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.putExtra("speechText", body);
				context.startActivity(i);
			//	abortBroadcast();
			} else {
				Intent i = new Intent(context, SpeechResponse.class);
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.putExtra("textToSpeech", body);
				context.startActivity(i);
				//abortBroadcast();
			}

		}
	}

}
