package com.familycoupons.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.familycoupons.datatypes.CouponType;
import com.familycoupons.datatypes.Coupons;
import com.familycoupons.datatypes.FamilyMembers;
import com.familycoupons.datatypes.IconType;

/**
 * Contains logic to return specific words from the dictionary, and load the
 * dictionary table when it needs to be created.
 */
public class MembersDatabase extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "members.db";

	private static final int DATABASE_VERSION = 3;
	
	private static final DataModel[] DATA_MODELS = new DataModel[] {new CouponType(), new FamilyMembers(), new Coupons(), new IconType() };

	public MembersDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Method is called during creation of the database
	@Override
	public void onCreate(SQLiteDatabase database) {
		for(DataModel model : DATA_MODELS) {
			model.onCreate(database);
		}
	}

	// Method is called during an upgrade of the database, e.g. if you increase
	// the database version
	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		Log.w(MembersDatabase.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion);
		for (int i = oldVersion; i < newVersion; i++) {
			for(DataModel model : DATA_MODELS) {
				Log.w(MembersDatabase.class.getName(), "Upgrading table " + model.getTableName());
				model.upgradeTo(database, i+1);
			}
		}
	}
}
