package com.familycoupons;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.familycoupons.database.MembersAdapter;
import com.familycoupons.datatypes.FamilyMembers;

public class EditMemberActivity extends Activity {
	private static final int CONFIRM_DELETE_DIALOG = 0;
	MembersAdapter dbHelper;
	EditMemberActivity me;
	long deleteMemberId = -1;

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
			EditText memberNameEditText = (EditText) findViewById(R.id.edit_name);
			memberNameEditText.setText(member.getString(member.getColumnIndex(FamilyMembers.COLUMN_MEMBER_NAME)));
		}
		member.close();
		dbHelper.close();

		Button cancelButton = (Button) findViewById(R.id.edit_cancel_btn);
		Button saveButton = (Button) findViewById(R.id.edit_save_btn);
		Button deleteButton = (Button) findViewById(R.id.edit_delete_btn);

		cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				me.finish();
			}
		});

		saveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText memberNameEditText = (EditText) findViewById(R.id.edit_name);
				String memberName = memberNameEditText.getText().toString();
				dbHelper.open();
				dbHelper.updateMember(memberId, memberName);
				dbHelper.close();
				me.finish();
			}
		});

		deleteButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				deleteMemberId = memberId;
				showDialog(CONFIRM_DELETE_DIALOG);
			}
		});
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		if (id == CONFIRM_DELETE_DIALOG) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Are you sure you want to Delete this member?").setCancelable(false).setPositiveButton(
					"Yes", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							if (deleteMemberId > -1) {
								dbHelper.open();
								dbHelper.deleteMember(deleteMemberId);
								dbHelper.close();
								deleteMemberId = -1;
								dismissDialog(CONFIRM_DELETE_DIALOG);
								me.finish();
							}
						}
					}).setNegativeButton("No", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
			dialog = builder.create();
		}
		return dialog;
	}

	public void finish() {
		super.finish();
		dbHelper.close();
	}

}
