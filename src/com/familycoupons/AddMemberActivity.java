package com.familycoupons;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.familycoupons.database.MembersAdapter;

public class AddMemberActivity extends Activity {
	private MembersAdapter dbHelper;
	private AddMemberActivity me;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_member);
		me = this;
		dbHelper = new MembersAdapter(this);
		dbHelper.open();

		Button saveButton = (Button) findViewById(R.id.addMemberSaveBtn);
		saveButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				EditText memberNameText = (EditText) findViewById(R.id.memberNameField);
				String memberName = memberNameText.getText().toString();
				dbHelper.createMember(memberName);
				me.finish();
			}
		});
		Button cancelButton = (Button) findViewById(R.id.addMemberCancelBtn);
		cancelButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				me.finish();

			}
		});
	}

	public void finish() {
		super.finish();
		dbHelper.close();
	}

}
