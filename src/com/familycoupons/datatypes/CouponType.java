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
	private static final String COLUMN_ACTIVE_DEF = COLUMN_ACTIVE + " integer default 0";
	
	private static final String TABLE_UPGRADE_SQL_2 = "alter table " + TABLE_NAME + " add column " + COLUMN_ACTIVE_DEF + ";";
	
	private static final String[][] defaultValues = {
		{"Star", "Stay up 15 minutes longer past your bedtime", "star"},
		{"Dessert","Dessert of your choosing for the whole family","dessert"},
		{"Hair Drying", "Adult will dry your hair after a washing","hairdryer"},
		{"Bronze Star", "","award_star_bronze"},
		{"Silver Star", "","award_star_silver"},
		{"Gold Star", "","award_star_gold"},
		{"Game", "","board_game"},
		{"Brain", "","brain"},
		{"Construction Bricks", "","bricks"},
		{"Day Off", "","calandar"},
		{"Check", "","dialog_ok"},
		{"Drill", "","drill"},
		{"Zoo", "","elephant"},
		{"Dinner", "","fancy_steak_dinner"},
		{"Gear", "","gear_in"},
		{"Ice Cream", "","icecream"},
		{"Computer", "","laptop"},
		{"Theater", "","masks"},
		{"Movie", "","movielogo"},
		{"Race", "","musclecar"},
		{"Museum", "","museum"},
		{"Peacock", "","peacock"},
		{"Pencil", "","pencil"},
		{"Surprise", "","question_mark"},
		{"Shopping", "","shopping"},
		{"Siren", "","siren"},
		{"Trophy", "","trophy"},
		{"TV", "","tv"},
		{"Wizard", "","wizard"}
	};
	
	public static final String TABLE_CREATE_SQL = "create table "
		+ TABLE_NAME
		+ "(" + COLUMN_ID_DEF + SEP
		+ COLUMN_NAME_DEF + SEP
		+ COLUMN_DESC_DEF + SEP
		+ COLUMN_IMAGE_DEF + SEP
		+ COLUMN_ACTIVE_DEF
		+ ");";
	
	
	private static final String DEFAULT_INSERT_SQL = "insert into "
		+ TABLE_NAME
		+ " (" + COLUMN_NAME + ", " + COLUMN_DESC + ", " + COLUMN_IMAGE + ") values "
		+ "(?, ?, ?);";
	
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
		for (int i = 0; i < defaultValues.length; i++) {
			database.execSQL(DEFAULT_INSERT_SQL, defaultValues[i]);
		}
	}
}
