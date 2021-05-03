package com.sign.language;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.service.handler.StartStopReceiver;

public class CustomKeyboard extends Activity implements OnTouchListener,
		OnClickListener, OnFocusChangeListener {
	protected static final int PICK_CONTACT = 100;
	private EditText mEt, ph; // Edit Text boxes
	private Button mBSpace, mBdone, mBack, mBChange, mNum,getvoice;
	private RelativeLayout mLayout, mKLayout;
	private boolean isEdit = false, isEdit1 = false;
	private String mUpper = "upper", mLower = "lower";
	private int w, mWindowWidth;
	private String sL[] = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
			"k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w",
			"x", "y", "z", "ç", "à", "é", "è", "û", "î" };
	private String cL[] = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
			"K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W",
			"X", "Y", "Z", "ç", "à", "é", "è", "û", "î" };
	private String nS[] = { "!", ")", "'", "#", "3", "$", "%", "&", "8", "*",
			"?", "/", "+", "-", "9", "0", "1", "4", "@", "5", "7", "(", "2",
			"\"", "6", "_", "=", "]", "[", "<", ">", "|" };
	private Button mB[] = new Button[32];
	private ImageButton btnSpeak;
	private Button btnVideo;
	private final int REQ_CODE_SPEECH_INPUT = 1;
	private ArrayList<String> result;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			setContentView(R.layout.main);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

			btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);
			btnVideo = (Button) findViewById(R.id.sendMessage);

			setAlarmAndFinishThis();

			// adjusting key regarding window sizes
			setKeys();
			setFrow();
			setSrow();
			setTrow();
			setForow();

			mEt = (EditText) findViewById(R.id.xEt);
			ph = (EditText) findViewById(R.id.phNo);
			mLayout = (RelativeLayout) findViewById(R.id.xK1);
			mKLayout = (RelativeLayout) findViewById(R.id.xKeyBoard);
			getvoice=(Button)findViewById(R.id.getvoice);

			mEt.setOnTouchListener(this);
			mEt.setOnFocusChangeListener(this);

			mEt.setOnClickListener(this);
			ph.setOnClickListener(this);
getvoice.setOnClickListener(new OnClickListener() {
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		Intent intent=new Intent(CustomKeyboard.this,Get_voice.class);
		startActivity(intent);
	}
});
			ph.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					mLayout.setVisibility(View.GONE);
					mKLayout.setVisibility(View.GONE);
					Intent intent = new Intent(Intent.ACTION_PICK,
							ContactsContract.Contacts.CONTENT_URI);
					startActivityForResult(intent, PICK_CONTACT);

				}
			});

			mEt.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					hideDefaultKeyboard();
				}
			});

			btnSpeak.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					promptSpeechInput();
				}
			});

			btnVideo.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					sendSMSMessage();
				}
			});

		} catch (Exception e) {
			Log.w(getClass().getName(), e.toString());
		}

	}

	private void setAlarmAndFinishThis() {
		Intent alarmReceiverIntent = new Intent(CustomKeyboard.this,
				StartStopReceiver.class);

		PendingIntent pendingIntent = PendingIntent.getBroadcast(
				CustomKeyboard.this, 0, alarmReceiverIntent, 0);

		AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);

		alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 0, 0,
				pendingIntent);

	}

	protected void sendSMSMessage() {
		Log.i("Send SMS", "");

		String phoneNo = ph.getText().toString();
		String message = mEt.getText().toString();

		try {
			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage(phoneNo, null, message, null, null);
			Toast.makeText(getApplicationContext(), "SMS sent.",
					Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(),
					"SMS faild, please try again.", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}

	/**
	 * Showing google speech input dialog
	 * */
	private void promptSpeechInput() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-IN");
		try {
			startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
		} catch (ActivityNotFoundException a) {
			Toast.makeText(getApplicationContext(),
					getString(R.string.speech_not_supported),
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Receiving speech input
	 * */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case REQ_CODE_SPEECH_INPUT: {
			if (resultCode == RESULT_OK && null != data) {
				result = data
						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				mEt.setText(result.get(0));
			}
			break;
		}

		case (PICK_CONTACT):

			if (resultCode == Activity.RESULT_OK) {

				Uri contactData = data.getData();
				Cursor contactCursor = getContentResolver().query(contactData,
						new String[] { ContactsContract.Contacts._ID }, null,
						null, null);
				String id = null;
				if (contactCursor.moveToFirst()) {
					id = contactCursor.getString(contactCursor
							.getColumnIndex(ContactsContract.Contacts._ID));
				}
				contactCursor.close();
				String phoneNumber = null;
				Cursor phoneCursor = getContentResolver()
						.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
								new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER },
								ContactsContract.CommonDataKinds.Phone.CONTACT_ID
										+ "= ? ", new String[] { id }, null);
				if (phoneCursor.moveToFirst()) {
					phoneNumber = phoneCursor
							.getString(phoneCursor
									.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				}
				phoneCursor.close();
				Toast.makeText(getApplicationContext(), phoneNumber,
						Toast.LENGTH_LONG).show();
				ph.setText(phoneNumber);
				break;
			}

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.signLearn:
			startActivity(new Intent(this, SignImage.class));
			break;
		case R.id.sms_inbox:
			startActivity(new Intent(this, SmsInbox.class));
			break;
		}
		return true;

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (v == mEt) {
			hideDefaultKeyboard();
			enableKeyboard();

		}

		return true;
	}

	@Override
	public void onClick(View v) {

		if (v == mBChange) {

			if (mBChange.getTag().equals(mUpper)) {
				changeSmallLetters();
				changeSmallTags();
			} else if (mBChange.getTag().equals(mLower)) {
				changeCapitalLetters();
				changeCapitalTags();
			}

		} else if (v != mBdone && v != mBack && v != mBChange && v != mNum) {
			addText(v);

		} else if (v == mBdone) {

			disableKeyboard();

		} else if (v == mBack) {
			isBack(v);
		} else if (v == mNum) {
			String nTag = (String) mNum.getTag();
			if (nTag.equals("num")) {
				changeSyNuLetters();
				changeSyNuTags();
				mBChange.setVisibility(Button.INVISIBLE);

			}
			if (nTag.equals("ABC")) {
				changeCapitalLetters();
				changeCapitalTags();
			}

		}

	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (v == mEt && hasFocus == true) {
			isEdit = true;
			isEdit1 = false;
			mEt.requestFocus();
		} else if (v == ph && hasFocus == true) {
			ph.requestFocus();
		}

	}

	private void addText(View v) {
		if (isEdit == true) {
			String b = "";
			b = (String) v.getTag();
			if (b != null) {
				// adding text in Edittext
				mEt.append(b);

			}
		}

		if (isEdit1 == true) {
			String b = "";
			b = (String) v.getTag();
			if (b != null) {
				// adding text in Edittext
				ph.append(b);

			}

		}

	}

	private void isBack(View v) {
		if (isEdit == true) {
			CharSequence cc = mEt.getText();
			if (cc != null && cc.length() > 0) {
				{
					mEt.setText("");
					mEt.append(cc.subSequence(0, cc.length() - 1));
				}

			}
		}

		if (isEdit1 == true) {
			CharSequence cc = ph.getText();
			if (cc != null && cc.length() > 0) {
				{
					ph.setText("");
					ph.append(cc.subSequence(0, cc.length() - 1));
				}
			}
		}

	}

	private void changeSmallLetters() {
		mBChange.setVisibility(Button.VISIBLE);
		for (int i = 0; i < sL.length; i++)
			mB[i].setText(sL[i]);
		mNum.setTag("12#");
	}

	private void changeSmallTags() {
		for (int i = 0; i < sL.length; i++)
			mB[i].setTag(sL[i]);
		mBChange.setTag("lower");
		mNum.setTag("num");
	}

	private void changeCapitalLetters() {
		mBChange.setVisibility(Button.VISIBLE);
		for (int i = 0; i < cL.length; i++)
			mB[i].setText(cL[i]);
		mBChange.setTag("upper");
		mNum.setText("12#");

	}

	private void changeCapitalTags() {
		for (int i = 0; i < cL.length; i++)
			mB[i].setTag(cL[i]);
		mNum.setTag("num");

	}

	private void changeSyNuLetters() {

		for (int i = 0; i < nS.length; i++)
			mB[i].setText(nS[i]);
		mNum.setText("ABC");
	}

	private void changeSyNuTags() {
		for (int i = 0; i < nS.length; i++)
			mB[i].setTag(nS[i]);
		mNum.setTag("ABC");
	}

	// enabling customized keyboard
	private void enableKeyboard() {

		mLayout.setVisibility(RelativeLayout.VISIBLE);
		mKLayout.setVisibility(RelativeLayout.VISIBLE);

	}

	// Disable customized keyboard
	private void disableKeyboard() {
		mLayout.setVisibility(RelativeLayout.INVISIBLE);
		mKLayout.setVisibility(RelativeLayout.INVISIBLE);

	}

	private void hideDefaultKeyboard() {
		InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (getCurrentFocus() != null) {
			inputManager.hideSoftInputFromWindow(getCurrentFocus()
					.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}

	}

	private void setFrow() {
		w = (mWindowWidth / 13);
		w = w - 15;
		mB[16].setWidth(w);
		mB[22].setWidth(w + 3);
		mB[4].setWidth(w);
		mB[17].setWidth(w);
		mB[19].setWidth(w);
		mB[24].setWidth(w);
		mB[20].setWidth(w);
		mB[8].setWidth(w);
		mB[14].setWidth(w);
		mB[15].setWidth(w);
		mB[16].setHeight(50);
		mB[22].setHeight(50);
		mB[4].setHeight(50);
		mB[17].setHeight(50);
		mB[19].setHeight(50);
		mB[24].setHeight(50);
		mB[20].setHeight(50);
		mB[8].setHeight(50);
		mB[14].setHeight(50);
		mB[15].setHeight(50);

	}

	private void setSrow() {
		w = (mWindowWidth / 10);
		mB[0].setWidth(w);
		mB[18].setWidth(w);
		mB[3].setWidth(w);
		mB[5].setWidth(w);
		mB[6].setWidth(w);
		mB[7].setWidth(w);
		mB[26].setWidth(w);
		mB[9].setWidth(w);
		mB[10].setWidth(w);
		mB[11].setWidth(w);
		mB[26].setWidth(w);

		mB[0].setHeight(50);
		mB[18].setHeight(50);
		mB[3].setHeight(50);
		mB[5].setHeight(50);
		mB[6].setHeight(50);
		mB[7].setHeight(50);
		mB[9].setHeight(50);
		mB[10].setHeight(50);
		mB[11].setHeight(50);
		mB[26].setHeight(50);
	}

	private void setTrow() {
		w = (mWindowWidth / 12);
		mB[25].setWidth(w);
		mB[23].setWidth(w);
		mB[2].setWidth(w);
		mB[21].setWidth(w);
		mB[1].setWidth(w);
		mB[13].setWidth(w);
		mB[12].setWidth(w);
		mB[27].setWidth(w);
		mB[28].setWidth(w);
		mBack.setWidth(w);

		mB[25].setHeight(50);
		mB[23].setHeight(50);
		mB[2].setHeight(50);
		mB[21].setHeight(50);
		mB[1].setHeight(50);
		mB[13].setHeight(50);
		mB[12].setHeight(50);
		mB[27].setHeight(50);
		mB[28].setHeight(50);
		mBack.setHeight(50);

	}

	private void setForow() {
		w = (mWindowWidth / 10);
		mBSpace.setWidth(w * 4);
		mBSpace.setHeight(50);
		mB[29].setWidth(w);
		mB[29].setHeight(50);

		mB[30].setWidth(w);
		mB[30].setHeight(50);

		mB[31].setHeight(50);
		mB[31].setWidth(w);
		mBdone.setWidth(w + (w / 1));
		mBdone.setHeight(50);

	}

	private void setKeys() {
		mWindowWidth = getWindowManager().getDefaultDisplay().getWidth(); // getting
		// window
		// height
		// getting ids from xml files
		mB[0] = (Button) findViewById(R.id.xA);
		mB[1] = (Button) findViewById(R.id.xB);
		mB[2] = (Button) findViewById(R.id.xC);
		mB[3] = (Button) findViewById(R.id.xD);
		mB[4] = (Button) findViewById(R.id.xE);
		mB[5] = (Button) findViewById(R.id.xF);
		mB[6] = (Button) findViewById(R.id.xG);
		mB[7] = (Button) findViewById(R.id.xH);
		mB[8] = (Button) findViewById(R.id.xI);
		mB[9] = (Button) findViewById(R.id.xJ);
		mB[10] = (Button) findViewById(R.id.xK);
		mB[11] = (Button) findViewById(R.id.xL);
		mB[12] = (Button) findViewById(R.id.xM);
		mB[13] = (Button) findViewById(R.id.xN);
		mB[14] = (Button) findViewById(R.id.xO);
		mB[15] = (Button) findViewById(R.id.xP);
		mB[16] = (Button) findViewById(R.id.xQ);
		mB[17] = (Button) findViewById(R.id.xR);
		mB[18] = (Button) findViewById(R.id.xS);
		mB[19] = (Button) findViewById(R.id.xT);
		mB[20] = (Button) findViewById(R.id.xU);
		mB[21] = (Button) findViewById(R.id.xV);
		mB[22] = (Button) findViewById(R.id.xW);
		mB[23] = (Button) findViewById(R.id.xX);
		mB[24] = (Button) findViewById(R.id.xY);
		mB[25] = (Button) findViewById(R.id.xZ);
		mB[26] = (Button) findViewById(R.id.xS1);
		mB[27] = (Button) findViewById(R.id.xS2);
		mB[28] = (Button) findViewById(R.id.xS3);
		mB[29] = (Button) findViewById(R.id.xS4);
		mB[30] = (Button) findViewById(R.id.xS5);
		mB[31] = (Button) findViewById(R.id.xS6);
		mBSpace = (Button) findViewById(R.id.xSpace);
		mBdone = (Button) findViewById(R.id.xDone);
		mBChange = (Button) findViewById(R.id.xChange);
		mBack = (Button) findViewById(R.id.xBack);
		mNum = (Button) findViewById(R.id.xNum);
		for (int i = 0; i < mB.length; i++)
			mB[i].setOnClickListener(this);
		mBSpace.setOnClickListener(this);
		mBdone.setOnClickListener(this);
		mBack.setOnClickListener(this);
		mBChange.setOnClickListener(this);
		mNum.setOnClickListener(this);

	}

	@Override
	public void onBackPressed() {
		if (mLayout.getVisibility() == View.VISIBLE) {
			mLayout.setVisibility(View.GONE);
		} else if (mLayout.getVisibility() == View.GONE) {
			super.onBackPressed();
		} else if (mLayout == null) {
			super.onBackPressed();
		}
	}

}