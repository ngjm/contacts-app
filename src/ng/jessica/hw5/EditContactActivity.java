package ng.jessica.hw5;

import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

public class EditContactActivity extends Activity {
	private EditText displayNameField;
	private EditText firstNameField;
	private EditText lastNameField;
	private DatePicker birthdayField;
	private TimePicker startTimeField;
	private TimePicker endTimeField;
	private EditText homeNoField;
	private EditText workNoField;
	private EditText mobileNoField;
	private EditText emailField;
	private EditText postalAddressField;

	private ContactsDB contactsDB;
	private long contactID;
	private boolean isNewContact;
	
	private static int CONTACT_UPDATED_CODE = 42; 
	private static int CONTACT_CREATED_CODE = 45;
		

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit);
		displayNameField = (EditText) findViewById(R.id.displayName);
		firstNameField = (EditText) findViewById(R.id.firstName);
		lastNameField = (EditText) findViewById(R.id.lastName);

		birthdayField = (DatePicker) findViewById(R.id.birthday);
		startTimeField = (TimePicker) findViewById(R.id.startTime);
		endTimeField = (TimePicker) findViewById(R.id.endTime);

		homeNoField = (EditText) findViewById(R.id.homeNo);
		workNoField = (EditText) findViewById(R.id.workNo);
		mobileNoField = (EditText) findViewById(R.id.mobileNo);
		emailField = (EditText) findViewById(R.id.email);
		postalAddressField = (EditText) findViewById(R.id.postalAddress);

		if (contactsDB == null) {
			contactsDB = new ContactsDB(this);
		}

		isNewContact = getIntent().getBooleanExtra("isNewContact", true);
		contactID = getIntent().getLongExtra("id", -1);
		final Contact contact = contactsDB.getContact(contactID);

		// set contact info if exists
		if (!isNewContact) {
			contact.setId(contactID);
			displayNameField.setText(contact.getDisplayName());
			firstNameField.setText(contact.getFirstName());
			lastNameField.setText(contact.getLastName());

			Calendar bday = Calendar.getInstance();
			bday.setTimeInMillis(contact.getBirthday().toMillis(true));
			birthdayField.init(bday.getTime().getYear() + 1900, bday.getTime()
					.getMonth(), bday.getTime().getDate(), null);

			Calendar sTime = Calendar.getInstance();
			sTime.setTimeInMillis(contact.getContactStartTime().toMillis(true));
			startTimeField.setCurrentHour(sTime.getTime().getHours());
			startTimeField.setCurrentMinute(sTime.getTime().getMinutes());

			Calendar eTime = Calendar.getInstance();
			eTime.setTimeInMillis(contact.getContactEndTime().toMillis(true));
			endTimeField.setCurrentHour(eTime.getTime().getHours());
			endTimeField.setCurrentMinute(eTime.getTime().getMinutes());

			homeNoField.setText(contact.getHomePhone());
			workNoField.setText(contact.getWorkPhone());
			mobileNoField.setText(contact.getMobilePhone());
			emailField.setText(contact.getEmailAddress());
			postalAddressField.setText(contact.getPostalAddress());
		}

		Button saveButton = (Button) findViewById(R.id.saveButton);
		Button revertButton = (Button) findViewById(R.id.revertButton);
		saveButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (displayNameField.getText().toString().length() == 0) {
					Toast toast = Toast.makeText(getApplicationContext(),
							R.string.EMPTY_DISPLAY_NAME_ERROR,
							Toast.LENGTH_SHORT);
					toast.show();
					return;
				}
				if (!isNewContact) {
					contact.setDisplayName(displayNameField.getText()
							.toString());
					contact.setLastName(lastNameField.getText().toString());
					contact.setFirstName(firstNameField.getText().toString());

					Calendar bday = Calendar.getInstance();
					bday.set(birthdayField.getYear(), birthdayField.getMonth(),
							birthdayField.getDayOfMonth());

					contact.setBirthday(createTime(bday.getTimeInMillis()));

					Calendar sTime = Calendar.getInstance();
					sTime.set(2000, 1, 1, startTimeField.getCurrentHour(),
							startTimeField.getCurrentMinute());
					contact.setContactStartTime(createTime(sTime
							.getTimeInMillis()));

					Calendar eTime = Calendar.getInstance();
					eTime.set(2000, 1, 1, endTimeField.getCurrentHour(),
							endTimeField.getCurrentMinute());
					contact.setContactEndTime(createTime(eTime
							.getTimeInMillis()));

					contact.setHomePhone(homeNoField.getText().toString());
					contact.setWorkPhone(workNoField.getText().toString());
					contact.setMobilePhone(mobileNoField.getText().toString());
					contact.setEmailAddress(emailField.getText().toString());
					contact.setPostalAddress(postalAddressField.getText()
							.toString());
					contact.setId(contactID);
					contactsDB.updateContact(contact);
					getIntent().putExtra("id", contactID);

					Toast toast = Toast.makeText(getApplicationContext(),
							R.string.contact_updated, Toast.LENGTH_LONG);
					toast.show();
					String detail = getString(R.string.contact_text_label) + contact.getDisplayName() + getString(R.string.contact_text_updated);
					sendNotification(
							detail,
							DisplayContactActivity.class, contactID, 0,
							getString(R.string.contact_updated), detail, CONTACT_UPDATED_CODE);

					finish();
				} else {
					Contact c = new Contact();
					c.setDisplayName(displayNameField.getText().toString());
					c.setLastName(lastNameField.getText().toString());
					c.setFirstName(firstNameField.getText().toString());

					Calendar bday = Calendar.getInstance();
					bday.set(birthdayField.getYear(), birthdayField.getMonth(),
							birthdayField.getDayOfMonth());
					c.setBirthday(createTime(bday.getTimeInMillis()));

					Calendar sTime = Calendar.getInstance();
					sTime.set(2000, 1, 1, startTimeField.getCurrentHour(),
							startTimeField.getCurrentMinute());
					c.setContactStartTime(createTime(sTime.getTimeInMillis()));

					Calendar eTime = Calendar.getInstance();
					eTime.set(2000, 1, 1, endTimeField.getCurrentHour(),
							endTimeField.getCurrentMinute());
					c.setContactEndTime(createTime(eTime.getTimeInMillis()));

					c.setHomePhone(homeNoField.getText().toString());
					c.setWorkPhone(workNoField.getText().toString());
					c.setMobilePhone(mobileNoField.getText().toString());
					c.setEmailAddress(emailField.getText().toString());
					c.setPostalAddress(postalAddressField.getText().toString());
					long newContactId = contactsDB.insertContact(c);
					getIntent().putExtra("id", newContactId);

					Toast toast = Toast.makeText(getApplicationContext(),
							R.string.contact_created, Toast.LENGTH_LONG);
					toast.show();
					String detail = getString(R.string.contact_text_label) + displayNameField.getText() + getString(R.string.conact_text_added);
					sendNotification(detail,
							DisplayContactActivity.class, newContactId, 0,
							getString(R.string.contact_created), detail, CONTACT_CREATED_CODE);
					
					finish();
				}
			}
		});
		revertButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
	}

	private Time createTime(long value) {
		Time time = new Time();
		time.set(value);
		return time;
	}

	private void sendNotification(String scrollingText,
			Class<? extends Activity> targetActivity, long contactId, 
			int pendingIntentFlags, String notificationTitle,
			String notificationText, int appNotificationId) {
		Intent notificationIntent = new Intent(getApplicationContext(),
				targetActivity);
		notificationIntent.putExtra("id", contactId);
		PendingIntent pendingIntent = PendingIntent.getActivity(
				getApplicationContext(), appNotificationId, notificationIntent,
				pendingIntentFlags);
		if (pendingIntent == null)
			return;
		Notification notification = new Notification(R.drawable.ic_launcher,
				scrollingText, new Date().getTime());
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.setLatestEventInfo(getApplicationContext(),
				notificationTitle, notificationText, pendingIntent);
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notificationManager.notify(appNotificationId, notification);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("hour1", startTimeField.getCurrentHour());
		outState.putInt("minute1", startTimeField.getCurrentMinute());
		outState.putInt("hour2", endTimeField.getCurrentHour());
		outState.putInt("minute2", endTimeField.getCurrentMinute());
		outState.putInt("year", birthdayField.getYear());
		outState.putInt("month", birthdayField.getMonth());
		outState.putInt("date", birthdayField.getDayOfMonth());
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		startTimeField.setCurrentHour(savedInstanceState.getInt("hour1"));
		endTimeField.setCurrentHour(savedInstanceState.getInt("hour2"));
		startTimeField.setCurrentMinute(savedInstanceState.getInt("minute1"));
		endTimeField.setCurrentMinute(savedInstanceState.getInt("minute2"));
		int y = savedInstanceState.getInt("year");
		int m = savedInstanceState.getInt("month");
		int d = savedInstanceState.getInt("date");
		birthdayField.init(y, m, d, null);
	}
}