package com.familycoupons.datatypes;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.familycoupons.database.DataModel;

public class CouponType implements DataModel {
	public static final String TABLE_NAME = "couponTypes";
	
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_DESC = "description";
	public static final String COLUMN_IMAGE = "imageName";
	public static final String COLUMN_ACTIVE = "isActive";
	
	private static final String COLUMN_ID_DEF = COLUMN_ID + " integer primary key autoincrement";
	private static final String COLUMN_NAME_DEF = COLUMN_NAME + " text not null";
	private static final String COLUMN_DESC_DEF = COLUMN_DESC + " text";
	private static final String COLUMN_IMAGE_DEF = COLUMN_IMAGE + " text not null";
	private static final String COLUMN_ACTIVE_DEF = COLUMN_ACTIVE + " integer default 1";
	
	private static final String TABLE_UPGRADE_SQL_2 = "alter table " + TABLE_NAME + " add column " + COLUMN_ACTIVE_DEF + ";";
	
	private static final String SEP = ", ";
	
	public static final String TABLE_CREATE_SQL = "create table "
		+ TABLE_NAME
		+ "(" + COLUMN_ID_DEF + SEP
		+ COLUMN_NAME_DEF + SEP
		+ COLUMN_DESC_DEF + SEP
		+ COLUMN_IMAGE_DEF + SEP
		+ COLUMN_ACTIVE_DEF
		+ ");";
	
	
	private static final String DEFAULT_TYPE_STAR = "insert into "
		+ TABLE_NAME
		+ " (" + COLUMN_NAME + ", " + COLUMN_DESC + ", " + COLUMN_IMAGE + ") values "
		+ "(\"Star\", \"Stay up 15 minutes longer past your bedtime\", \"star\");";
	
	private static final String DEFAULT_TYPE_DESSERT =
		"insert into "
		+ TABLE_NAME
		+ " (" + COLUMN_NAME + ", " + COLUMN_DESC + ", " + COLUMN_IMAGE + ") values "
		+ "(\"Dessert\", \"Dessert of your choosing for the whole family\", \"dessert\");";
		
	private static final String DEFAULT_TYPE_HAIR =
		"insert into "
		+ TABLE_NAME
		+ " (" + COLUMN_NAME + ", " + COLUMN_DESC + ", " + COLUMN_IMAGE + ") values "
		+ "(\"Hair Drying\", \"Adult will dry your hair after a washing\", \"hairdryer\");";
	
	@Override
	public String getCreateSQL() {
		return TABLE_CREATE_SQL;
	}

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}

	@Override
	public void upgradeTo(SQLiteDatabase database, int newVersion) {
		if (newVersion == 2) {
			Log.w(CouponType.class.getName(), "Executing: " + TABLE_UPGRADE_SQL_2);
			database.execSQL(TABLE_UPGRADE_SQL_2);
		} else {
			Log.w(CouponType.class.getName(), "Nothing to upgrade");
		}
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(TABLE_CREATE_SQL);
		database.execSQL(DEFAULT_TYPE_DESSERT);
		database.execSQL(DEFAULT_TYPE_HAIR);
		database.execSQL(DEFAULT_TYPE_STAR);
	}
}
