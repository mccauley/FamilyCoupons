package com.familycoupons;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.familycoupons.database.MembersAdapter;
import com.familycoupons.datatypes.FamilyMembers;

public class EditMemberActivity extends Activity {
	MembersAdapter dbHelper;
	EditMemberActivity me;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_member);
		me = this;
		
		dbHelper = new MembersAdapter(this);
		dbHelper.open();
		
		final long memberId = this.getIntent().getExtras().getLong("memberId");
		Cursor member = dbHelper.fetchMember(memberId);
		if (member.moveToFirst()) {
			EditText memberNameEditText = (EditText)findViewById(R.id.edit_name);
			memberNameEditText.setText(member.getString(member.getColumnIndex(FamilyMembers.COLUMN_MEMBER_NAME)));
		}
		
		Button cancelButton = (Button)findViewById(R.id.edit_cancel_btn);
		Button saveButton = (Button)findViewById(R.id.edit_save_btn);
		Button deleteButton = (Button)findViewById(R.id.edit_delete_btn);
		
		cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				me.finish();
			}
		});
		
		saveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText memberNameEditText = (EditText)findViewById(R.id.edit_name);
				String memberName = memberNameEditText.getText().toString();
				dbHelper.updateMember(memberId, memberName);
				me.finish();
			}
		});
		
		deleteButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dbHelper.deleteMember(memberId);
				me.finish();
			}
		});
	}
	
	public void finish() {
		super.finish();
		dbHelper.close();
	}

}
