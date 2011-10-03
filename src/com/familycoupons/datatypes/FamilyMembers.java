package com.familycoupons.datatypes;

public class FamilyMembers {
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

}
