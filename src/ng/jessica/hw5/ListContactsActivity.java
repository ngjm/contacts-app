package ng.jessica.hw5;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class ListContactsActivity extends Activity {
	private Cursor allCursor;

	private ListView list;

	private ContactsDB contactsDB;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);
		list = (ListView) findViewById(R.id.list);
		list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> list, View itemView,
					int position, long id) {
				Intent intent = new Intent(ListContactsActivity.this,
						DisplayContactActivity.class);
				intent.putExtra("id", id);
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.list_contacts_menu, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.createContact:
			Intent intent = new Intent(ListContactsActivity.this,
					EditContactActivity.class);
			intent.putExtra("isNewContact", true);
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
		allCursor = contactsDB.getAllContacts();
		list.setAdapter(new SimpleCursorAdapter(this, R.layout.list_item,
				allCursor, new String[] { Contacts.DISPLAY_NAME },
				new int[] { R.id.displayName }));
		
	}
}