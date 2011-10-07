package com.familycoupons;

import android.app.ListActivity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
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
	EditCouponsListActivity me;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_coupons_list);
		getListView().setDividerHeight(2);
		me = this;

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

		public CouponListSimpleCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
			super(context, layout, c, from, to);
			localTo = to;
			localFrom = from;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			final int count = localTo.length;

			for (int i = 0; i < count; i++) {
				final View v = view.findViewById(localTo[i]);
				if (v != null) {
					int columnIndex = cursor.getColumnIndex(localFrom[i]);
					String text = cursor.getString(columnIndex);
					if (text == null) {
						text = "";
					}

					if (v instanceof CheckBox) {
						int couponTypeId = cursor.getInt(cursor.getColumnIndex(CouponType.COLUMN_ID)); 
						setViewCheckBox((CheckBox) v, text, couponTypeId);
					} else if (v instanceof TextView) {
						setViewText((TextView) v, text);
					} else if (v instanceof ImageView) {
						setViewImage((ImageView) v, text);
					} else {
						throw new IllegalStateException(v.getClass().getName() + " is not a "
								+ " view that can be bound by this SimpleCursorAdapter");
					}
				}
			}
		}

		private void setViewCheckBox(CheckBox v, String text, final int couponTypeId) {
			boolean checked = Integer.parseInt(text) > 0;
			v.setChecked(checked);
			v.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					dbHelper.updateCouponTypeActive(couponTypeId, isChecked);
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
