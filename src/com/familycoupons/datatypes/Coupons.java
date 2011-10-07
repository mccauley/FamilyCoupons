package com.familycoupons.datatypes;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.familycoupons.database.DataModel;

public class Coupons implements DataModel {
	public static final String TABLE_NAME = "coupons";
	
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_MEMBER_ID = "memberId";
	public static final String COLUMN_COUPON_TYPE_ID = "couponTypeId";
	public static final String COLUMN_COUPON_QTY = "quantity";
	
	public static final String TABLE_CREATE_SQL = "create table "
		+ TABLE_NAME
		+ "(" + COLUMN_ID + " integer primary key autoincrement, "
		+ COLUMN_MEMBER_ID + " integer not null, "
		+ COLUMN_COUPON_TYPE_ID + " integer not null, "
		+ COLUMN_COUPON_QTY + " integer not null"
		+ ");";

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
		Log.w(Coupons.class.getName(), "Nothing to upgrade");
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(TABLE_CREATE_SQL);
	}
}
