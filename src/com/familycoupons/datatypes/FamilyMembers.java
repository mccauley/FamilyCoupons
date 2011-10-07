package com.familycoupons.datatypes;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.familycoupons.database.DataModel;

public class FamilyMembers implements DataModel {
	public static final String TABLE_NAME = "members";
	
	/**
	 * Column name for the member's name
	 * <P>
	 * Type: INTEGER
	 * </P>
	 */
	public static final String COLUMN_ID = "_id";

	/**
	 * Column name for the member's name
	 * <P>
	 * Type: TEXT
	 * </P>
	 */
	public static final String COLUMN_MEMBER_NAME = "member_name";

	/**
	 * Column name for the creation timestamp
	 * <P>
	 * Type: INTEGER (long from System.curentTimeMillis())
	 * </P>
	 */
	public static final String COLUMN_CREATE_DATE = "created";

	/**
	 * Column name for the modification timestamp
	 * <P>
	 * Type: INTEGER (long from System.curentTimeMillis())
	 * </P>
	 */
	public static final String COLUMN_MODIFICATION_DATE = "modified";

	public static final String TABLE_CREATE_SQL = "create table "
			+ TABLE_NAME
			+ "(" + COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_MEMBER_NAME + " text not null, "
			+ COLUMN_CREATE_DATE + " long not null, "
			+ COLUMN_MODIFICATION_DATE + " long not null"
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
		Log.w(FamilyMembers.class.getName(), "Nothing to upgrade");
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(TABLE_CREATE_SQL);
	}

}
