package com.familycoupons.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.familycoupons.datatypes.CouponType;
import com.familycoupons.datatypes.Coupons;
import com.familycoupons.datatypes.FamilyMembers;

public class MembersAdapter {
	private Context context;
	private SQLiteDatabase database;
	private MembersDatabase dbHelper;

	public MembersAdapter(Context context) {
		this.context = context;
	}

	public MembersAdapter open() throws SQLException {
		dbHelper = new MembersDatabase(context);
		return open(dbHelper);
	}
	
	protected MembersAdapter open(MembersDatabase db) {
		database = db.getWritableDatabase();
		return this;
	}

	public void close() {
		if (database.isOpen())
			database.close();
	}

	/**
	 * Create a new member If the member is successfully created return the new
	 * rowId for that member, otherwise return a -1 to indicate failure.
	 */
	public long createMember(String name) {
		long newMemberId = -1;
		ContentValues initialValues = createContentValues(name);
		database.beginTransaction();
		try {
			newMemberId = database.insert(FamilyMembers.TABLE_NAME, null, initialValues);
			Cursor cursor = this.fetchCouponTypes();
			if (cursor.moveToFirst()) {
				while (!cursor.isAfterLast()) {
					int couponType = cursor.getInt(cursor.getColumnIndex(CouponType.COLUMN_ID));
					createMemberCoupon(couponType, newMemberId);
					cursor.moveToNext();
				}
			}
			cursor.close();
			database.setTransactionSuccessful();
		}
		finally {
			database.endTransaction();
		}
		return newMemberId;
	}

	/**
	 * Update the member
	 */
	public boolean updateMember(long memberId, String name) {
		ContentValues updateValues = updateContentValues(name);
		return database.update(FamilyMembers.TABLE_NAME, updateValues, FamilyMembers.COLUMN_ID + "= ?", makeStringArray(memberId)) > 0;
	}

	public boolean updateOrCreateMemberCoupon(int couponType, long memberId, int value) {
		if (updateMemberCoupon(couponType, memberId, value)) {
			return true;
		} else {
			createMemberCoupon(couponType, memberId);
			return updateMemberCoupon(couponType, memberId, value);
		}
	}

	public boolean updateMemberCoupon(int couponType, long memberId, int value) {
		ContentValues values = new ContentValues();
		values.put(Coupons.COLUMN_COUPON_QTY, value);
		String whereClause = Coupons.COLUMN_MEMBER_ID + "= ? " + memberId + " AND "
				+ Coupons.COLUMN_COUPON_TYPE_ID + "= ?";
		return database.update(Coupons.TABLE_NAME, values, whereClause, makeStringArray(memberId, couponType)) > 0;
	}
	
	public void updateCouponTypeActive(int couponTypeId, boolean active) {
		ContentValues values = new ContentValues();
		values.put(CouponType.COLUMN_ACTIVE, (active ? 1 : 0));
		Log.d("MembersAdapter", "updating coupon active status id=" + couponTypeId + " with {'" + active + "'}");
		database.update(CouponType.TABLE_NAME, values, CouponType.COLUMN_ID + "=?", makeStringArray(couponTypeId));
	}

	public int addCoupon(int couponType, long memberId) {
		String sql = "UPDATE " + Coupons.TABLE_NAME + " SET " + Coupons.COLUMN_COUPON_QTY + " = "
				+ Coupons.COLUMN_COUPON_QTY + " + 1 WHERE " + Coupons.COLUMN_COUPON_TYPE_ID + " = ? AND "
				+ Coupons.COLUMN_MEMBER_ID + " = ?";
		database.execSQL(sql, makeStringArray(couponType,memberId));
		return fetchQtyForCouponTypeAndMember(couponType, memberId);
	}

	public int subtractCoupon(int couponType, long memberId) {
		String sql = "UPDATE " + Coupons.TABLE_NAME + " SET " + Coupons.COLUMN_COUPON_QTY + " = "
				+ Coupons.COLUMN_COUPON_QTY + " - 1 WHERE " + Coupons.COLUMN_COUPON_TYPE_ID + " = ? AND "
				+ Coupons.COLUMN_MEMBER_ID + " = ? AND " + Coupons.COLUMN_COUPON_QTY + " > 0";
		database.execSQL(sql, makeStringArray(couponType, memberId));
		return fetchQtyForCouponTypeAndMember(couponType, memberId);
	}

	public int fetchQtyForCouponTypeAndMember(int couponType, long memberId) {
		int newValue = 0;
		String sql = "SELECT " + Coupons.COLUMN_COUPON_QTY + " FROM " + Coupons.TABLE_NAME + " WHERE "
				+ Coupons.COLUMN_COUPON_TYPE_ID + " = ? AND " + Coupons.COLUMN_MEMBER_ID + " = ?";
		Cursor c = database.rawQuery(sql, makeStringArray(couponType, memberId));
		if (c.moveToFirst()) {
			newValue = c.getInt(c.getColumnIndex(Coupons.COLUMN_COUPON_QTY));
		}
		c.close();
		return newValue;
	}

	public long createCoupon(String name, String desc, String imageFile) {
		ContentValues values = new ContentValues();
		values.put(CouponType.COLUMN_NAME, name);
		values.put(CouponType.COLUMN_DESC, desc);
		values.put(CouponType.COLUMN_IMAGE, imageFile);
		return database.insert(CouponType.TABLE_NAME, null, values);
	}
	
	public void updateCoupon(long id, String name, String desc) {
		Log.d("MembersAdapter", "updating coupon id=" + id + " with {'" + name + "', '" + desc + "'}");
		ContentValues values = new ContentValues();
		values.put(CouponType.COLUMN_NAME, name);
		values.put(CouponType.COLUMN_DESC, desc);
		database.update(CouponType.TABLE_NAME, values, CouponType.COLUMN_ID + "=" + id, null);
	}

	public long createMemberCoupon(int couponType, long memberId) {
		ContentValues values = new ContentValues();
		values.put(Coupons.COLUMN_COUPON_QTY, 0);
		values.put(Coupons.COLUMN_COUPON_TYPE_ID, couponType);
		values.put(Coupons.COLUMN_MEMBER_ID, memberId);
		return database.insert(Coupons.TABLE_NAME, null, values);
	}

	/**
	 * Deletes member
	 */
	public boolean deleteMember(long memberId) {
		String[] memberIdStr = makeStringArray(memberId);
		int deleteResult = 0;
		try {
			database.beginTransaction();
			database.delete(Coupons.TABLE_NAME, Coupons.COLUMN_MEMBER_ID + "= ?", memberIdStr);
			deleteResult = database.delete(FamilyMembers.TABLE_NAME, FamilyMembers.COLUMN_ID + "= ?", memberIdStr);
			database.setTransactionSuccessful();
		} finally {
			database.endTransaction();
		}
		return deleteResult > 0;
	}

	/**
	 * Return a Cursor over the list of all member in the database
	 * 
	 * @return Cursor over all members
	 */
	public Cursor fetchAllMembers() {
		return database.query(FamilyMembers.TABLE_NAME, new String[] { FamilyMembers.COLUMN_ID,
				FamilyMembers.COLUMN_MEMBER_NAME, FamilyMembers.COLUMN_CREATE_DATE,
				FamilyMembers.COLUMN_MODIFICATION_DATE }, null, null, null, null, null);
	}

	public Cursor fetchMember(long rowId) throws SQLException {
		Cursor mCursor = database.query(true, FamilyMembers.TABLE_NAME, new String[] { FamilyMembers.COLUMN_ID,
				FamilyMembers.COLUMN_MEMBER_NAME, FamilyMembers.COLUMN_CREATE_DATE,
				FamilyMembers.COLUMN_MODIFICATION_DATE }, FamilyMembers.COLUMN_ID + "=" + rowId, null, null, null,
				null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	
	public Cursor fetchCoupon(long rowId) throws SQLException {
		Cursor mCursor = database.query(CouponType.TABLE_NAME, new String[] { CouponType.COLUMN_ID, CouponType.COLUMN_DESC,
				CouponType.COLUMN_NAME, CouponType.COLUMN_IMAGE, CouponType.COLUMN_ACTIVE }, CouponType.COLUMN_ID + "=" + rowId, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	public Cursor fetchCouponTypes() {
		return database.query(CouponType.TABLE_NAME, new String[] { CouponType.COLUMN_ID, CouponType.COLUMN_DESC,
				CouponType.COLUMN_NAME, CouponType.COLUMN_IMAGE, CouponType.COLUMN_ACTIVE }, null, null, null, null, null);
	}

	public Cursor fetchCouponsForMember(long memberId) throws SQLException {
		String sql = "SELECT * FROM " + Coupons.TABLE_NAME + ", " + CouponType.TABLE_NAME + " WHERE "
				+ Coupons.TABLE_NAME + "." + Coupons.COLUMN_COUPON_TYPE_ID + " = " + CouponType.TABLE_NAME + "."
				+ CouponType.COLUMN_ID + " AND " + Coupons.TABLE_NAME + "." + Coupons.COLUMN_MEMBER_ID + " = ? AND " + CouponType.COLUMN_ACTIVE + " = 1";
		return database.rawQuery(sql, makeStringArray(memberId));
	}

	private ContentValues createContentValues(String name) {
		ContentValues values = new ContentValues();
		values.put(FamilyMembers.COLUMN_MEMBER_NAME, name);
		values.put(FamilyMembers.COLUMN_CREATE_DATE, System.currentTimeMillis());
		values.put(FamilyMembers.COLUMN_MODIFICATION_DATE, System.currentTimeMillis());
		return values;
	}

	private ContentValues updateContentValues(String name) {
		ContentValues values = new ContentValues();
		values.put(FamilyMembers.COLUMN_MEMBER_NAME, name);
		values.put(FamilyMembers.COLUMN_MODIFICATION_DATE, System.currentTimeMillis());
		return values;
	}
	
	private String[] makeStringArray(long... items) {
		String[] values = new String[items.length];
		for(int i = 0; i < items.length; i++) {
			values[i] = String.valueOf(items[i]);
		}
		return values;
	}
}
