package com.familycoupons;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ExpandableListActivity;
import android.content.Context;
import android.content.DialogInterface;
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
	private static final int WHICH_MEMBER_DIALOG = 0;
	
	private MembersAdapter dbHelper;
	long[] memberIdArray;
	String[] memberNamesArray;
	private Intent editMemberIntent;
	MenuItem addMemberMenuItem;
	MenuItem editCouponsMenuItem;
	Dialog whichMemberDialog;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.member_list);
		getExpandableListView().setDividerHeight(2);
		dbHelper = new MembersAdapter(this);
		dbHelper.open();
		editMemberIntent = new Intent(this, EditMemberActivity.class);

		getWindow().setBackgroundDrawableResource(R.drawable.fadedarchetype);

		fillData();
		registerForContextMenu(getExpandableListView());
	}

	@Override
	public void finish() {
		super.finish();
		dbHelper.close();
	}

	private void fillData() {
		Cursor cursor = dbHelper.fetchAllMembers();
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
		addMemberMenuItem = menu.findItem(R.id.addMember);
		editCouponsMenuItem = menu.findItem(R.id.editCoupons);
		MenuItem editMemberMenuItem = menu.findItem(R.id.editMember);
		addMemberMenuItem.setIntent(new Intent(this, AddMemberActivity.class));
		editCouponsMenuItem.setIntent(new Intent(this, EditCouponsListActivity.class));
		editMemberMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				showDialog(WHICH_MEMBER_DIALOG);
				return true;
			}
		});
		return true;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		if (id == WHICH_MEMBER_DIALOG) {
			dialog = buildWhichMemberDialog();
		}
		return dialog;
	}

	private Dialog buildWhichMemberDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.which_member);
		loadMembersList();
		builder.setItems(memberNamesArray, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				if (item != -1) {
					editMemberIntent.putExtra("memberId", memberIdArray[item]);
					removeDialog(WHICH_MEMBER_DIALOG);
					startActivity(editMemberIntent);
				}
			}
		});
		return builder.create();
	}

	private void loadMembersList() {
		Cursor membersCursor = dbHelper.fetchAllMembers();
		memberIdArray = new long[membersCursor.getCount()];
		memberNamesArray = new String[membersCursor.getCount()];
		int i = 0;
		if (membersCursor.moveToFirst()) {
			while (!membersCursor.isAfterLast()) {
				memberIdArray[i] = membersCursor.getLong(membersCursor.getColumnIndex(FamilyMembers.COLUMN_ID));
				memberNamesArray[i] = membersCursor.getString(membersCursor
						.getColumnIndex(FamilyMembers.COLUMN_MEMBER_NAME));
				i++;
				membersCursor.moveToNext();
			}
		}
		membersCursor.close();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		if (item.equals(addMemberMenuItem) || item.equals(editCouponsMenuItem)) {
			startActivity(item.getIntent());
		}
		return true;
	}

	private int subtractCoupon(final long memberId, final int couponType) {
		return dbHelper.subtractCoupon(couponType, memberId);
	}
	
	private int addCoupon(final long memberId, final int couponType) {
		return dbHelper.addCoupon(couponType, memberId);
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
					int value = addCoupon(memberId, couponType);
					couponQtyView.setText(String.valueOf(value));
				}
			});

			minusButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					int value = subtractCoupon(memberId, couponType);
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