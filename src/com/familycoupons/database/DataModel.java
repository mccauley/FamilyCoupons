package com.familycoupons.database;

import android.database.sqlite.SQLiteDatabase;

public interface DataModel {
	static final String SEP = ", ";
	public String getTableName();
	public String getCreateSQL();
	public void upgradeTo(SQLiteDatabase database, int newVersion);
	public void onCreate(SQLiteDatabase database);

}
