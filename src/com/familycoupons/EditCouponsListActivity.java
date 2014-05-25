package com.familycoupons;

import java.util.HashMap;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.familycoupons.database.MembersAdapter;
import com.familycoupons.datatypes.CouponType;

public class EditCouponsListActivity extends ListActivity {
	private MembersAdapter dbHelper;
	private Intent editCouponDetailIntent;
	EditCouponsListActivity me;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_coupons_list);
		getListView().setDividerHeight(2);
		me = this;
		editCouponDetailIntent = new Intent(this, EditCouponActivity.class);

		dbHelper = new MembersAdapter(this);
		dbHelper.open();

		Button doneButton = (Button) findViewById(R.id.edit_coupons_done_btn);
		doneButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				me.finish();
			}
		});

		fillData();
	}

	@Override
	public void finish() {
		super.finish();
		dbHelper.close();
	}

	private void fillData() {
		Cursor cursor = dbHelper.fetchCouponTypes();
		startManagingCursor(cursor);

		String[] from = new String[] { CouponType.COLUMN_NAME, CouponType.COLUMN_DESC, CouponType.COLUMN_IMAGE,
				CouponType.COLUMN_ACTIVE };
		int[] to = new int[] { R.id.edit_coupons_item_name, R.id.edit_coupons_item_desc, R.id.edit_coupons_image,
				R.id.couponCheckBox };

		CouponListSimpleCursorAdapter coupons = new CouponListSimpleCursorAdapter(this, R.layout.edit_coupons_item,
				cursor, from, to);
		setListAdapter(coupons);
	}

	public class CouponListSimpleCursorAdapter extends SimpleCursorAdapter {
		private int[] localTo;
		private String[] localFrom;
		private HashMap<Integer, Boolean> mChecked = new HashMap<Integer, Boolean>();
		Cursor cursor;
		private Activity context;

		public CouponListSimpleCursorAdapter(Activity context, int layout, Cursor c, String[] from, int[] to) {
			super(context, layout, c, from, to);
			localTo = to;
			localFrom = from;
			this.cursor = c;
			this.context = context;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final int count = localTo.length;
			if (convertView == null) {
				LayoutInflater inflater = context.getLayoutInflater();
				convertView = inflater.inflate(R.layout.edit_coupons_item, null);
			}
			cursor.moveToPosition(position);

			for (int i = 0; i < count; i++) {
				final View v = convertView.findViewById(localTo[i]);
				if (v != null) {
					int columnIndex = cursor.getColumnIndex(localFrom[i]);
					String text = cursor.getString(columnIndex);
					if (text == null) {
						text = "";
					}
					final int couponTypeId = cursor.getInt(cursor.getColumnIndex(CouponType.COLUMN_ID));
					View.OnClickListener editDetailClickListener = new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							editCouponDetailIntent.putExtra("couponTypeId", couponTypeId);
							startActivity(editCouponDetailIntent);
						}
					};

					if (v instanceof CheckBox) {
						setViewCheckBox((CheckBox) v, text, couponTypeId);
					} else if (v instanceof TextView) {
						setViewText((TextView) v, text);
						v.setOnClickListener(editDetailClickListener);
					} else if (v instanceof ImageView) {
						setViewImage((ImageView) v, text);
						v.setOnClickListener(editDetailClickListener);
					} else {
						throw new IllegalStateException(v.getClass().getName() + " is not a "
								+ " view that can be bound by this SimpleCursorAdapter");
					}
				}
			}
			return convertView;
		}

		private void setViewCheckBox(CheckBox v, String text, final int couponTypeId) {
			Log.d("setViewCheckBox", "called with coupon id=" + couponTypeId + " and value of " + text);
			Boolean modifiedChecked = mChecked.get(couponTypeId);
			boolean checked = modifiedChecked == null ? Integer.parseInt(text) > 0 : modifiedChecked;
			v.setChecked(checked);
			v.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					CompoundButton button = (CompoundButton) v;
					boolean isChecked = button.isChecked();
					Log.d("EditCouponList", "onClick coupon id=" + couponTypeId + " to " + isChecked);
					dbHelper.updateCouponTypeActive(couponTypeId, isChecked);
					mChecked.put(couponTypeId, isChecked);
				}
			});
		}

		@Override
		public void setViewImage(ImageView v, String value) {
			Context context = v.getContext();
			int id = context.getResources()
					.getIdentifier(value, "drawable", context.getString(R.string.package_string));

			if (id != 0x0) {
				v.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), id));
			}
		}

	}

}
