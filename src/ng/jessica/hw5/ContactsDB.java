package ng.jessica.hw5;

import java.util.ArrayList;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.text.format.Time;

public class ContactsDB {
	private final Context context;

	public static interface JessicaNgExtension {
		String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.ng.jessica.hw5.extension";
		String DISPLAY_NAME = "data1";
		String BIRTHDAY = "data2";
		String START_CONTACT = "data3";
		String END_CONTACT = "data4";
	}

	public ContactsDB(Context context) {
		this.context = context;
	}

	public long insertContact(Contact contact) {
		ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
		int rawContactIndex = 0;

		operations.add(ContentProviderOperation
				.newInsert(RawContacts.CONTENT_URI)
				.withValue(RawContacts.ACCOUNT_TYPE, null)
				.withValue(RawContacts.ACCOUNT_NAME, null).build());

		// names
		operations
				.add(ContentProviderOperation
						.newInsert(ContactsContract.Data.CONTENT_URI)
						.withValueBackReference(Data.RAW_CONTACT_ID,
								rawContactIndex)
						.withValue(
								Data.MIMETYPE,
								ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
						.withValue(StructuredName.FAMILY_NAME,
								contact.getFirstName())
						.withValue(StructuredName.GIVEN_NAME,
								contact.getLastName()).build());

		// phone - mobile
		operations.add(ContentProviderOperation
				.newInsert(ContactsContract.Data.CONTENT_URI)
				.withValueBackReference(Data.RAW_CONTACT_ID, rawContactIndex)
				.withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
				.withValue(Phone.NUMBER, contact.getMobilePhone())
				.withValue(Phone.TYPE, Phone.TYPE_MOBILE).build());

		// phone - home
		operations.add(ContentProviderOperation
				.newInsert(ContactsContract.Data.CONTENT_URI)
				.withValueBackReference(Data.RAW_CONTACT_ID, rawContactIndex)
				.withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
				.withValue(Phone.NUMBER, contact.getHomePhone())
				.withValue(Phone.TYPE, Phone.TYPE_HOME).build());

		// phone - work
		operations.add(ContentProviderOperation
				.newInsert(ContactsContract.Data.CONTENT_URI)
				.withValueBackReference(Data.RAW_CONTACT_ID, rawContactIndex)
				.withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
				.withValue(Phone.NUMBER, contact.getWorkPhone())
				.withValue(Phone.TYPE, Phone.TYPE_WORK).build());

		// custom fields
		operations.add(ContentProviderOperation
				.newInsert(Data.CONTENT_URI)
				.withValueBackReference(Data.RAW_CONTACT_ID, rawContactIndex)
				.withValue(Data.MIMETYPE, JessicaNgExtension.CONTENT_ITEM_TYPE)
				.withValue(JessicaNgExtension.DISPLAY_NAME,
						contact.getDisplayName())
				.withValue(JessicaNgExtension.BIRTHDAY,
						contact.getBirthday().toMillis(true))
				.withValue(JessicaNgExtension.START_CONTACT,
						contact.getContactStartTime().toMillis(true))
				.withValue(JessicaNgExtension.END_CONTACT,
						contact.getContactEndTime().toMillis(true)).build());

		// email: Email.CONTENT_ITEM_TYPE
		operations
				.add(ContentProviderOperation
						.newInsert(ContactsContract.Data.CONTENT_URI)
						.withValueBackReference(Data.RAW_CONTACT_ID,
								rawContactIndex)
						.withValue(
								Data.MIMETYPE,
								ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
						.withValue(ContactsContract.CommonDataKinds.Email.DATA,
								contact.getEmailAddress()).build());

		// postal address: StructuredPostal.CONTENT_ITEM_TYPE
		operations
				.add(ContentProviderOperation
						.newInsert(ContactsContract.Data.CONTENT_URI)
						.withValueBackReference(Data.RAW_CONTACT_ID,
								rawContactIndex)
						.withValue(
								Data.MIMETYPE,
								ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
						.withValue(
								ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS,
								contact.getPostalAddress()).build());

		try {
			ContentProviderResult[] results = context.getContentResolver()
					.applyBatch(ContactsContract.AUTHORITY, operations);
			return Long.parseLong(results[0].uri.getLastPathSegment()); // RAW
																		// CONTACT
																		// ID
		} catch (Exception e) {
			throw new RuntimeException("Error inserting contact", e);
		}

	}

	private Time createTime(long value) {
		Time time = new Time();
		time.set(value);
		return time;
	}

	private static final String[] NAME_COLUMNS = { StructuredName.GIVEN_NAME,
			StructuredName.FAMILY_NAME };

	private static final String[] EMAIL_COLUMNS = { Email.DATA };

	private static final String[] PHONE_COLUMNS = { Phone.NUMBER };

	private static final String[] ADDRESS_COLUMNS = { StructuredPostal.FORMATTED_ADDRESS };

	private static final String[] CUSTOM_COLUMNS = {
			JessicaNgExtension.DISPLAY_NAME, JessicaNgExtension.BIRTHDAY,
			JessicaNgExtension.START_CONTACT, JessicaNgExtension.END_CONTACT };

	public Contact getContact(long id) {
		ContentResolver contentResolver = context.getContentResolver();
		Contact contact = new Contact();
		contact.setId(id);

		Cursor cursor = contentResolver.query(Data.CONTENT_URI, NAME_COLUMNS,
				Data.RAW_CONTACT_ID + "=? and " + Data.MIMETYPE + "=?",
				new String[] { String.valueOf(id),
						StructuredName.CONTENT_ITEM_TYPE }, null);
		try {
			if (cursor.moveToFirst()) {
				contact.setLastName(cursor.getString(0));
				contact.setFirstName(cursor.getString(1));
			}
		} finally {
			cursor.close();
		}

		cursor = contentResolver.query(Email.CONTENT_URI, EMAIL_COLUMNS,
				Email.RAW_CONTACT_ID + "=?",
				new String[] { String.valueOf(id) }, null);
		try {
			if (cursor.moveToFirst()) {
				contact.setEmailAddress(cursor.getString(0));
			}
		} finally {
			cursor.close();
		}

		cursor = contentResolver.query(
				Phone.CONTENT_URI,
				PHONE_COLUMNS,
				Phone.RAW_CONTACT_ID + "=? and " + Phone.TYPE + "=?",
				new String[] { String.valueOf(id),
						String.valueOf(Phone.TYPE_WORK) }, null);
		try {
			if (cursor.moveToFirst()) {
				contact.setWorkPhone(cursor.getString(0));
			}
		} finally {
			cursor.close();
		}

		cursor = contentResolver.query(
				Phone.CONTENT_URI,
				PHONE_COLUMNS,
				Phone.RAW_CONTACT_ID + "=? and " + Phone.TYPE + "=?",
				new String[] { String.valueOf(id),
						String.valueOf(Phone.TYPE_MOBILE) }, null);
		try {
			if (cursor.moveToFirst()) {
				contact.setMobilePhone(cursor.getString(0));
			}
		} finally {
			cursor.close();
		}

		cursor = contentResolver.query(
				Phone.CONTENT_URI,
				PHONE_COLUMNS,
				Phone.RAW_CONTACT_ID + "=? and " + Phone.TYPE + "=?",
				new String[] { String.valueOf(id),
						String.valueOf(Phone.TYPE_HOME) }, null);
		try {
			if (cursor.moveToFirst()) {
				contact.setHomePhone(cursor.getString(0));
			}
		} finally {
			cursor.close();
		}

		cursor = contentResolver.query(StructuredPostal.CONTENT_URI,
				ADDRESS_COLUMNS, StructuredPostal.RAW_CONTACT_ID + "=?",
				new String[] { String.valueOf(id) }, null);
		try {
			if (cursor.moveToFirst()) {
				contact.setPostalAddress(cursor.getString(0));
			}
		} finally {
			cursor.close();
		}

		cursor = contentResolver.query(Data.CONTENT_URI, CUSTOM_COLUMNS,
				Data.RAW_CONTACT_ID + "=?  and " + Data.MIMETYPE + "=?",
				new String[] { String.valueOf(id),
						JessicaNgExtension.CONTENT_ITEM_TYPE }, null);
		try {
			if (cursor.moveToFirst()) {
				contact.setDisplayName(cursor.getString(0));
				contact.setBirthday(createTime(cursor.getLong(1)));
				contact.setContactStartTime(createTime(cursor.getLong(2)));
				contact.setContactEndTime(createTime(cursor.getLong(3)));
			}
		} finally {
			cursor.close();
		}
		return contact;
	}

	public Cursor getAllContacts() {
		String[] projection = { Contacts._ID, Contacts.DISPLAY_NAME };
		return context.getContentResolver().query(RawContacts.CONTENT_URI,
				projection, null, null, Contacts.DISPLAY_NAME);
	}

	public void updateContact(Contact contact) {
		ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
		String where = Data.RAW_CONTACT_ID + "=? and " + Data.MIMETYPE + "=?";

		operations
				.add(ContentProviderOperation
						.newUpdate(Data.CONTENT_URI)
						.withSelection(
								where,
								new String[] {
										String.valueOf(contact.getId()),
										ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE })
						.withValue(StructuredName.FAMILY_NAME,
								contact.getFirstName())
						.withValue(StructuredName.GIVEN_NAME,
								contact.getLastName()).build());

		operations
				.add(ContentProviderOperation
						.newUpdate(Data.CONTENT_URI)
						.withSelection(
								where + " and " + Phone.TYPE + "=?",
								new String[] { contact.getId() + "",
										Phone.CONTENT_ITEM_TYPE,
										Phone.TYPE_MOBILE + "" })
						.withValue(Phone.NUMBER, contact.getMobilePhone())
						.build());

		operations
				.add(ContentProviderOperation
						.newUpdate(Data.CONTENT_URI)
						.withSelection(
								where + " and " + Phone.TYPE + "=?",
								new String[] { contact.getId() + "",
										Phone.CONTENT_ITEM_TYPE,
										Phone.TYPE_WORK + "" })
						.withValue(Phone.NUMBER, contact.getWorkPhone())
						.build());

		operations
				.add(ContentProviderOperation
						.newUpdate(Data.CONTENT_URI)
						.withSelection(
								where + " and " + Phone.TYPE + "=?",
								new String[] { contact.getId() + "",
										Phone.CONTENT_ITEM_TYPE,
										Phone.TYPE_HOME + "" })
						.withValue(Phone.NUMBER, contact.getHomePhone())
						.build());

		operations.add(ContentProviderOperation
				.newUpdate(Data.CONTENT_URI)
				.withSelection(
						where,
						new String[] { contact.getId() + "",
								JessicaNgExtension.CONTENT_ITEM_TYPE })
				.withValue(JessicaNgExtension.DISPLAY_NAME,
						contact.getDisplayName())
				.withValue(JessicaNgExtension.BIRTHDAY,
						contact.getBirthday().toMillis(true))
				.withValue(JessicaNgExtension.START_CONTACT,
						contact.getContactStartTime().toMillis(true))
				.withValue(JessicaNgExtension.END_CONTACT,
						contact.getContactEndTime().toMillis(true)).build());

		operations
				.add(ContentProviderOperation
						.newUpdate(Data.CONTENT_URI)
						.withSelection(
								where,
								new String[] {
										contact.getId() + "",
										ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE })
						.withValue(ContactsContract.CommonDataKinds.Email.DATA,
								contact.getEmailAddress()).build());

		operations
				.add(ContentProviderOperation
						.newUpdate(Data.CONTENT_URI)
						.withSelection(
								where,
								new String[] {
										contact.getId() + "",
										ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE })
						.withValue(
								ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS,
								contact.getPostalAddress()).build());

		try {
			context.getContentResolver().applyBatch(ContactsContract.AUTHORITY,
					operations);
		} catch (Exception e) {
			throw new RuntimeException("Error inserting contact", e);
		}

	}

	public void delete(Contact p) {
		// FOR THE INTERESTED READER
	}

}
