package com.familycoupons;

import android.app.ExpandableListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.TextView;

import com.familycoupons.database.MembersAdapter;
import com.familycoupons.datatypes.CouponType;
import com.familycoupons.datatypes.Coupons;
import com.familycoupons.datatypes.FamilyMembers;

public class FamilyListActivity extends ExpandableListActivity {
	private MembersAdapter dbHelper;
	private Cursor cursor;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.member_list);
		this.getExpandableListView().setDividerHeight(2);
		dbHelper = new MembersAdapter(this);
		dbHelper.open();

		fillData();
		registerForContextMenu(getExpandableListView());
	}

	@Override
	public void finish() {
		super.finish();
		dbHelper.close();
	}

	private void fillData() {
		cursor = dbHelper.fetchAllMembers();
		startManagingCursor(cursor);

		String[] from = new String[] { FamilyMembers.COLUMN_MEMBER_NAME };
		int[] to = new int[] { R.id.member_name };

		String[] childFrom = new String[] { Coupons.COLUMN_COUPON_QTY, CouponType.COLUMN_IMAGE };
		int[] childTo = new int[] { R.id.emc_coupon_number, R.id.emc_coupon_image };

		NameListCursorTreeAdapter members = new NameListCursorTreeAdapter(this, cursor, R.layout.name_item, from, to,
				R.layout.adaptor_content, childFrom, childTo);
		setListAdapter(members);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.memberlistoptions, menu);
		menu.findItem(R.id.addMember).setIntent(new Intent(this, AddMemberActivity.class));
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		startActivity(item.getIntent());
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		fillData();

	}

	public class NameListCursorTreeAdapter extends SimpleCursorTreeAdapter {

		public NameListCursorTreeAdapter(Context context, Cursor cursor, int groupLayout, String[] groupFrom,
				int[] groupTo, int childLayout, String[] childFrom, int[] childTo) {
			super(context, cursor, groupLayout, groupFrom, groupTo, childLayout, childFrom, childTo);
		}

		@Override
		protected Cursor getChildrenCursor(Cursor groupCursor) {
			long memberId = groupCursor.getLong(groupCursor.getColumnIndex(FamilyMembers.COLUMN_ID));
			Cursor couponsCursor = dbHelper.fetchCouponsForMember(memberId);
			startManagingCursor(couponsCursor);
			return couponsCursor;
		}

		@Override
		protected void bindChildView(View view, Context context, Cursor cursor, boolean isLastChild) {
			super.bindChildView(view, context, cursor, isLastChild);
			final TextView couponQtyView = (TextView) view.findViewById(R.id.emc_coupon_number);
			Button plusButton = (Button) view.findViewById(R.id.emc_plus_btn);
			Button minusButton = (Button) view.findViewById(R.id.emc_minus_btn);
			final long memberId = cursor.getLong(cursor.getColumnIndex(Coupons.COLUMN_MEMBER_ID));
			final int couponType = cursor.getInt(cursor.getColumnIndex(Coupons.COLUMN_COUPON_TYPE_ID));

			plusButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					int value = dbHelper.addCoupon(couponType, memberId);
					couponQtyView.setText(String.valueOf(value));
				}
			});

			minusButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					int value = dbHelper.subtractCoupon(couponType, memberId);
					couponQtyView.setText(String.valueOf(value));
				}
			});
		}

		@Override
		protected void setViewImage(ImageView v, String value) {
			Context context = v.getContext();
			int id = context.getResources()
					.getIdentifier(value, "drawable", context.getString(R.string.package_string));

			if (id != 0x0) {
				v.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), id));
			}
		}
	}
}