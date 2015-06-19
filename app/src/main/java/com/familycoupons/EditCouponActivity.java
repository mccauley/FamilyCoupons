package com.familycoupons;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.familycoupons.database.MembersAdapter;
import com.familycoupons.datatypes.CouponType;

public class EditCouponActivity extends AppCompatActivity {

	private MembersAdapter dbHelper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_coupon_detail);
		dbHelper = new MembersAdapter(this);
		dbHelper.open();
		
		final long couponTypeId = this.getIntent().getExtras().getInt("couponTypeId");
		Cursor couponType = dbHelper.fetchCoupon(couponTypeId);
		final EditText couponNameEditText = (EditText) findViewById(R.id.editCouponNameDetail);
		final EditText couponDescEditText = (EditText) findViewById(R.id.editCouponDescDetail);
		final ImageView iconImage = (ImageView) findViewById(R.id.couponImageView);
		if (couponType.moveToFirst()) {
			couponNameEditText.setText(couponType.getString(couponType.getColumnIndex(CouponType.COLUMN_NAME)));
			couponDescEditText.setText(couponType.getString(couponType.getColumnIndex(CouponType.COLUMN_DESC)));
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
				finish();
			}
		});
		
		saveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String name = couponNameEditText.getText().toString();
				String desc = couponDescEditText.getText().toString();
				dbHelper.open();
				dbHelper.updateCoupon(couponTypeId, name, desc);
				dbHelper.close();
				finish();
			}
		});
	}
	
	@Override
	public void finish() {
		super.finish();
		dbHelper.close();
	}

}
