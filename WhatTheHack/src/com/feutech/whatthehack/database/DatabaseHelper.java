package com.feutech.whatthehack.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper{
	
	protected final static int DB_VERSION = 3;
	protected final static String DB_NAME = "turistagram.db";

	private String createTable, dropTable;
	
	public DatabaseHelper(Context context, String createTable, String dropTable) {
		super(context, DB_NAME, null , DB_VERSION);
		this.createTable = createTable;
		this.dropTable = dropTable;
	}
 
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(createTable);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(dropTable);
	}
}
