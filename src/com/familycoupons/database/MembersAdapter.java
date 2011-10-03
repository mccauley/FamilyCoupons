package com.familycoupons.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

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
		database = dbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		dbHelper.close();
	}

	/**
	 * Create a new member If the member is successfully created return the new
	 * rowId for that member, otherwise return a -1 to indicate failure.
	 */
	public long createMember(String name) {
		ContentValues initialValues = createContentValues(name);

		long newMemberId = database.insert(FamilyMembers.TABLE_NAME, null, initialValues);
		Cursor cursor = this.fetchCouponTypes();
		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast()) {
				int couponType = cursor.getInt(cursor.getColumnIndex(CouponType.COLUMN_ID));
				this.createMemberCoupon(couponType, newMemberId);
				cursor.moveToNext();
			}
		}
		return newMemberId;
	}

	/**
	 * Update the member
	 */
	public boolean updateMember(long rowId, String name) {
		ContentValues updateValues = updateContentValues(name);

		return database.update(FamilyMembers.TABLE_NAME, updateValues, FamilyMembers.COLUMN_ID + "=" + rowId, null) > 0;
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
		return database.update(Coupons.TABLE_NAME, values, Coupons.COLUMN_MEMBER_ID + "=" + memberId + " AND "
				+ Coupons.COLUMN_COUPON_TYPE_ID + "=" + couponType, null) > 0;
	}

	public int addCoupon(int couponType, long memberId) {
		database.execSQL("UPDATE " + Coupons.TABLE_NAME + " SET " + Coupons.COLUMN_COUPON_QTY + " = "
				+ Coupons.COLUMN_COUPON_QTY + " + 1 WHERE " + Coupons.COLUMN_COUPON_TYPE_ID + " = ? AND "
				+ Coupons.COLUMN_MEMBER_ID + " = ?", new String[] { String.valueOf(couponType),
				String.valueOf(memberId) });
		return fetchQtyForCouponTypeAndMember(couponType, memberId);
	}

	public int subtractCoupon(int couponType, long memberId) {
		database.execSQL("UPDATE " + Coupons.TABLE_NAME + " SET " + Coupons.COLUMN_COUPON_QTY + " = "
				+ Coupons.COLUMN_COUPON_QTY + " - 1 WHERE " + Coupons.COLUMN_COUPON_TYPE_ID + " = ? AND "
				+ Coupons.COLUMN_MEMBER_ID + " = ?", new String[] { String.valueOf(couponType),
				String.valueOf(memberId) });
		return fetchQtyForCouponTypeAndMember(couponType, memberId);
	}

	public int fetchQtyForCouponTypeAndMember(int couponType, long memberId) {
		int newValue = 0;
		Cursor c = database.rawQuery("SELECT " + Coupons.COLUMN_COUPON_QTY + " FROM " + Coupons.TABLE_NAME + " WHERE "
				+ Coupons.COLUMN_COUPON_TYPE_ID + " = ? AND " + Coupons.COLUMN_MEMBER_ID + " = ?", new String[] {
				String.valueOf(couponType), String.valueOf(memberId) });
		if (c.moveToFirst()) {
			newValue = c.getInt(c.getColumnIndex(Coupons.COLUMN_COUPON_QTY));
		}
		return newValue;
	}

	public long createCoupon(String name, String desc, String imageFile) {
		ContentValues values = new ContentValues();
		values.put(CouponType.COLUMN_NAME, name);
		values.put(CouponType.COLUMN_DESC, desc);
		values.put(CouponType.COLUMN_IMAGE, imageFile);
		return database.insert(CouponType.TABLE_NAME, null, values);
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
	public boolean deleteMember(long rowId) {
		return database.delete(FamilyMembers.TABLE_NAME, FamilyMembers.COLUMN_ID + "=" + rowId, null) > 0;
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

	/**
	 * Return a Cursor positioned at the defined todo
	 */
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

	public Cursor fetchCouponTypes() {
		return database.query(CouponType.TABLE_NAME, new String[] { CouponType.COLUMN_ID, CouponType.COLUMN_DESC,
				CouponType.COLUMN_NAME, CouponType.COLUMN_IMAGE }, null, null, null, null, null);
	}

	public Cursor fetchCouponsForMember(long memberId) throws SQLException {
		String sql = "SELECT * FROM " + Coupons.TABLE_NAME + ", " + CouponType.TABLE_NAME + " WHERE "
				+ Coupons.TABLE_NAME + "." + Coupons.COLUMN_COUPON_TYPE_ID + " = " + CouponType.TABLE_NAME + "."
				+ CouponType.COLUMN_ID + " AND " + Coupons.TABLE_NAME + "." + Coupons.COLUMN_MEMBER_ID + " = ?";
		return database.rawQuery(sql, new String[] { String.valueOf(memberId) });
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
}
