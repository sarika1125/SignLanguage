package com.sign.language;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SmsInbox extends Activity {
	/** Called when the activity is first created. */
	@SuppressWarnings("rawtypes")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sms_inbox);

		ListView lViewSMS = (ListView) findViewById(R.id.listViewSMS);

		if (fetchInbox() != null) {
			@SuppressWarnings("unchecked")
			ArrayAdapter adapter = new ArrayAdapter(this,
					android.R.layout.simple_list_item_1, fetchInbox());
			lViewSMS.setAdapter(adapter);
		}
		
		lViewSMS.setOnItemClickListener(new OnItemClickListener() {

		    @Override
		    public void onItemClick(AdapterView<?> parent, View view,
		            int position, long id) {
		    	String body = ((TextView) view).getText().toString();
		    	Intent i = new Intent(SmsInbox.this, SignVideoStream.class);
				i.putExtra("speechText", body);
				startActivity(i);
		    }

		});
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ArrayList fetchInbox() {
		ArrayList sms = new ArrayList();

		Uri uriSms = Uri.parse("content://sms/inbox");
		Cursor cursor = getContentResolver().query(uriSms,
				new String[] { "_id", "address", "date", "body" }, null, null,
				null);

		cursor.moveToFirst();
		while (cursor.moveToNext()) {
			String address = cursor.getString(1);
			String body = cursor.getString(3);
			sms.add(body);
		}
		return sms;

	}
}
