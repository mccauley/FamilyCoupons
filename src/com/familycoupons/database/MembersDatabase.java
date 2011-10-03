package com.familycoupons.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.familycoupons.datatypes.CouponType;
import com.familycoupons.datatypes.Coupons;
import com.familycoupons.datatypes.FamilyMembers;

/**
 * Contains logic to return specific words from the dictionary, and load the
 * dictionary table when it needs to be created.
 */
public class MembersDatabase extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "members.db";

	private static final int DATABASE_VERSION = 1;

	public MembersDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Method is called during creation of the database
	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(FamilyMembers.TABLE_CREATE_SQL);
		database.execSQL(Coupons.TABLE_CREATE_SQL);
		database.execSQL(CouponType.TABLE_CREATE_SQL);
		database.execSQL(CouponType.DEFAULT_TYPE_STAR);
		database.execSQL(CouponType.DEFAULT_TYPE_DESSERT);
		database.execSQL(CouponType.DEFAULT_TYPE_HAIR);
	}

	// Method is called during an upgrade of the database, e.g. if you increase
	// the database version
	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		Log.w(MembersDatabase.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + FamilyMembers.TABLE_NAME);
		database.execSQL("DROP TABLE IF EXISTS " + Coupons.TABLE_NAME);
		database.execSQL("DROP TABLE IF EXISTS " + CouponType.TABLE_NAME);
		onCreate(database);
	}
}
