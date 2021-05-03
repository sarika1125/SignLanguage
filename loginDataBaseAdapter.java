package com.sign.language;

import java.util.HashMap;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;


public class loginDataBaseAdapter {

	static final String DATABASE_NAME = "login3.db";
	static final int DATABASE_VERSION = 1;
	public static final int NAME_COLUMN = 3;

	static final String DATABASE_CREATE = "create table "+"LOGIN"+
			"( " +"ID integer primary key autoincrement,"+"USERNAME  text,"+ "PASSWORD  text,"+"REPASSWORD text,"+"MOBILE text)";

	public  SQLiteDatabase db;
	private final Context context;
	private DataBaseHelper dbHelper;

	public  loginDataBaseAdapter(Context _context) 
	{
		context = _context;
		dbHelper = new DataBaseHelper(context, DATABASE_NAME, null, DATABASE_VERSION);

	}
	public  loginDataBaseAdapter open() throws SQLException 
	{
		db = dbHelper.getWritableDatabase();
		return this;
	}
	public void close() 
	{
		db.close();
	}

	public  SQLiteDatabase getDatabaseInstance()
	{
		return db;
	}

	public void insertEntry(String username,String password,String repassword,String mobile)
	{
		ContentValues newValues = new ContentValues();
		newValues.put("USERNAME", username);
		newValues.put("PASSWORD", password);
		newValues.put("REPASSWORD",repassword);
		newValues.put("MOBILE",mobile);
//		newValues.put("SECURITYHINT",securityhint);
//		newValues.put("SECURITYHINT1",securityhint1);
//		newValues.put("SECURITYHINT2",securityhint2);

		db.insert("LOGIN", null, newValues);
	}

	public int deleteEntry(String password)
	{
		String where="PASSWORD=?";
		int numberOFEntriesDeleted= db.delete("LOGIN", where, new String[]{password}) ;
		return numberOFEntriesDeleted;
	}	

	public String getSinlgeEntry(String username,String password)
	{
		Cursor cursor=db.query("LOGIN", null, " USERNAME=?", new String[]{username}, null, null, null);
		if(cursor.getCount()<1)
		{
			cursor.close();
			return "NOT EXIST";
		}
		cursor.moveToFirst();
		String repassword= cursor.getString(cursor.getColumnIndex("REPASSWORD"));
		cursor.close();
		return repassword;				
	}

	public String getAllTags(String a) 
	{


		Cursor c = db.rawQuery("SELECT * FROM " + "LOGIN" + " where SECURITYHINT = '" +a + "'" , null);
		String str = null;
		if (c.moveToFirst()) {
			do {
				str = c.getString(c.getColumnIndex("PASSWORD"));
			} while (c.moveToNext());
		}
		return str;
	}
	public String getAllTags1(String a) {


		Cursor c = db.rawQuery("SELECT * FROM " + "LOGIN" + " where SECURITYHINT1 = '" +a + "'" , null);
		String str = null;
		if (c.moveToFirst()) {
			do {
				str = c.getString(c.getColumnIndex("PASSWORD"));
			} while (c.moveToNext());
		}
		return str;
	}


	public void  updateEntry(String username,String password,String repassword)
	{
		ContentValues updatedValues = new ContentValues();
		
		updatedValues.put("PASSWORD", password);
		updatedValues.put("REPASSWORD",repassword);
	//updatedValues.put("SECURITYHINT",repassword);

		String where="USERNAME = ?";
		db.update("LOGIN",updatedValues, where, new String[]{password});			   
	}	



	public HashMap<String, String> getAnimalInfo(String id) {
		HashMap<String, String> wordList = new HashMap<String, String>();
		String selectQuery = "SELECT * FROM LOGIN where MOBILE='"+id+"'";
		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				wordList.put("PASSWORD", cursor.getString(1));
			} while (cursor.moveToNext());
		}				    
		return wordList;
	}	
}
