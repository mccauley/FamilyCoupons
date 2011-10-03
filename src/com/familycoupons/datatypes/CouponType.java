package com.familycoupons.datatypes;

public class CouponType {
	public static final String TABLE_NAME = "couponTypes";
	
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_DESC = "description";
	public static final String COLUMN_IMAGE = "imageName";
	
	public static final String TABLE_CREATE_SQL = "create table "
		+ TABLE_NAME
		+ "(" + COLUMN_ID + " integer primary key autoincrement, "
		+ COLUMN_NAME + " text not null, "
		+ COLUMN_DESC + " text, "
		+ COLUMN_IMAGE + " text not null"
		+ ");";
	
	public static final String DEFAULT_TYPE_STAR = "insert into "
		+ TABLE_NAME
		+ " (" + COLUMN_NAME + ", " + COLUMN_DESC + ", " + COLUMN_IMAGE + ") values "
		+ "(\"Star\", \"Stay up 15 minutes longer past your bedtime\", \"star\");";
	
	public static final String DEFAULT_TYPE_DESSERT =
		"insert into "
		+ TABLE_NAME
		+ " (" + COLUMN_NAME + ", " + COLUMN_DESC + ", " + COLUMN_IMAGE + ") values "
		+ "(\"Dessert\", \"Dessert of your choosing for the whole family\", \"dessert\");";
		
	public static final String DEFAULT_TYPE_HAIR =
		"insert into "
		+ TABLE_NAME
		+ " (" + COLUMN_NAME + ", " + COLUMN_DESC + ", " + COLUMN_IMAGE + ") values "
		+ "(\"Hair Drying\", \"Adult will dry your hair after a washing\", \"hairdryer\");";
}
