package com.familycoupons;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.familycoupons.database.MembersAdapter;

public class AddMemberActivity extends AppCompatActivity {
	private MembersAdapter dbHelper;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_member);
		dbHelper = new MembersAdapter(this);
		dbHelper.open();
		
		Button saveButton = (Button) findViewById(R.id.addMemberSaveBtn);
		saveButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				EditText memberNameText = (EditText) findViewById(R.id.memberNameField);
				String memberName = memberNameText.getText().toString();
				dbHelper.createMember(memberName);
				finish();
			}
		});
		Button cancelButton = (Button) findViewById(R.id.addMemberCancelBtn);
		cancelButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();

			}
		});
	}

	public void finish() {
		super.finish();
		dbHelper.close();
	}

}
