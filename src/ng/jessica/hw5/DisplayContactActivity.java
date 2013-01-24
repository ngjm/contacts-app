package ng.jessica.hw5;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class DisplayContactActivity extends Activity {

	private ContactsDB contactsDB;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.display);
	}

	private TextView displayNameView;
	private TextView lastNameView;
	private TextView firstNameView;
	private TextView homeNoView;
	private TextView workNoView;
	private TextView mobileNoView;
	private TextView emailView;
	private TextView addressView;
	private long contactID;
	private TextView birthdayView;
	private TextView sTimeView;
	private TextView eTimeView;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.display_contact_menu, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.editContact:
			Intent intent = new Intent(DisplayContactActivity.this,
					EditContactActivity.class);
			intent.putExtra("isNewContact", false);
			intent.putExtra("id", contactID);
			startActivity(intent);
			return true;
		default:
			return super.onMenuItemSelected(featureId, item);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (contactsDB == null) {
			contactsDB = new ContactsDB(this);
		}
		contactID = getIntent().getLongExtra("id", -1);
		Contact contact = contactsDB.getContact(contactID);
		if (contact != null) {
			displayNameView = (TextView) findViewById(R.id.displayName);
			displayNameView.setText(contact.getDisplayName());
			
			lastNameView = (TextView) findViewById(R.id.lastName);
			lastNameView.setText(contact.getLastName());
			firstNameView = (TextView) findViewById(R.id.firstName);
			firstNameView.setText(contact.getFirstName());
			
			Date d = new Date();
			birthdayView = (TextView) findViewById(R.id.birthday);
			d.setTime(contact.getBirthday().toMillis(true));
			birthdayView.setText(new SimpleDateFormat("MM-dd-yyyy").format(d));
			
			sTimeView = (TextView) findViewById(R.id.startTime);
			d.setTime(contact.getContactStartTime().toMillis(true));
			sTimeView.setText(new SimpleDateFormat("HH:mm").format(d));
			
			eTimeView = (TextView) findViewById(R.id.endTime);
			d.setTime(contact.getContactEndTime().toMillis(true));
			eTimeView.setText(new SimpleDateFormat("HH:mm").format(d));
			
			homeNoView = (TextView) findViewById(R.id.homeNo);
			homeNoView.setText(contact.getHomePhone());
			workNoView = (TextView) findViewById(R.id.workNo);
			workNoView.setText(contact.getWorkPhone());
			mobileNoView = (TextView) findViewById(R.id.mobileNo);
			mobileNoView.setText(contact.getMobilePhone());
			emailView = (TextView) findViewById(R.id.email);
			emailView.setText(contact.getEmailAddress());
			addressView = (TextView) findViewById(R.id.postalAddress);
			addressView.setText(contact.getPostalAddress());
		}
	}
}