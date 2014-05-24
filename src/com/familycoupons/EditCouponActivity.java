package com.familycoupons;

import com.familycoupons.database.MembersAdapter;
import com.familycoupons.datatypes.CouponType;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class EditCouponActivity extends Activity {

	private MembersAdapter dbHelper;
	EditCouponActivity me;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		me = this;
		setContentView(R.layout.edit_coupon_detail);
		dbHelper = new MembersAdapter(this);
		dbHelper.open();
		
		final long couponTypeId = this.getIntent().getExtras().getInt("couponTypeId");
		Cursor couponType = dbHelper.fetchCoupon(couponTypeId);
		if (couponType.moveToFirst()) {
			EditText couponNameEditText = (EditText) findViewById(R.id.editCouponNameDetail);
			couponNameEditText.setText(couponType.getString(couponType.getColumnIndex(CouponType.COLUMN_NAME)));
			EditText couponDescEditText = (EditText) findViewById(R.id.editCouponDescDetail);
			couponDescEditText.setText(couponType.getString(couponType.getColumnIndex(CouponType.COLUMN_DESC)));
			ImageView iconImage = (ImageView) findViewById(R.id.couponImageView);
			Context context = iconImage.getContext();
			String value = couponType.getString(couponType.getColumnIndex(CouponType.COLUMN_IMAGE));
			int id = context.getResources()
					.getIdentifier(value, "drawable", context.getString(R.string.package_string));

			if (id != 0x0) {
				iconImage.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), id));
			}
		}
		couponType.close();
		dbHelper.close();
		
		Button cancelButton = (Button) findViewById(R.id.couponEditDetailCancel);
		Button saveButton = (Button) findViewById(R.id.couponEditDetailDoneButton);
		cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				me.finish();
			}
		});
	}
	
	@Override
	public void finish() {
		super.finish();
		dbHelper.close();
	}

}
