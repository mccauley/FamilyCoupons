package com.familycoupons.datatypes;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.familycoupons.database.DataModel;

public class IconType implements DataModel {
	public static final String TABLE_NAME = "iconTypes";
	
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_IMAGE = "imageName";
	
	private static final String COLUMN_ID_DEF = COLUMN_ID + " integer primary key autoincrement";
	private static final String COLUMN_IMAGE_DEF = COLUMN_IMAGE + " text not null";
	
	public static final String TABLE_CREATE_SQL = "create table "
		+ TABLE_NAME
		+ "(" + COLUMN_ID_DEF + SEP
		+ COLUMN_IMAGE_DEF 
		+ ");";
	
	private static final String DEFAULT_TYPE_DESSERT =
		"insert into "
		+ TABLE_NAME
		+ " (" + COLUMN_IMAGE + ") values "
		+ "(\"dessert\");";
	
	private static final String DEFAULT_TYPE_STAR =
		"insert into "
		+ TABLE_NAME
		+ " (" + COLUMN_IMAGE + ") values "
		+ "(\"star\");";
	
	private static final String DEFAULT_TYPE_HAIRDRYER =
		"insert into "
		+ TABLE_NAME
		+ " (" + COLUMN_IMAGE + ") values "
		+ "(\"hairdryer\");";

	@Override
	public String getCreateSQL() {
		return TABLE_CREATE_SQL;
	}

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		Log.w(IconType.class.getName(), "Creating: " + TABLE_NAME);
		database.execSQL(TABLE_CREATE_SQL);
		database.execSQL(DEFAULT_TYPE_DESSERT);
		database.execSQL(DEFAULT_TYPE_STAR);
		database.execSQL(DEFAULT_TYPE_HAIRDRYER);
	}

	@Override
	public void upgradeTo(SQLiteDatabase database, int newVersion) {
		if (newVersion == 3) {
			onCreate(database);
		} else {
			Log.w(IconType.class.getName(), "Nothing to upgrade");
		}
	}

}
