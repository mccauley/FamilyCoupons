package com.familycoupons.datatypes;

public class Coupons {
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
}
