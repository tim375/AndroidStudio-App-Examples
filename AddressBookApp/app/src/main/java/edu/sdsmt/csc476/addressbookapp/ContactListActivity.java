package edu.sdsmt.csc476.addressbookapp;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class ContactListActivity extends ListActivity
{

	private CursorAdapter _adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// Insert Sample Contacts
		// AddressBookModel model = new AddressBookModel(this);
		// model.insertSampleContacts();

		// Wire up the CursorAdapter to a null Cursor which will be populated
		// via an AsyncTask.
		_adapter = new SimpleCursorAdapter(this,
				R.layout.contact_list_item,
				null,
				new String[] { AddressBookModel.KEY_NAME },
				new int[] { R.id.textViewContactName },
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

		setListAdapter(_adapter);

	}

	@Override
	protected void onResume()
	{
		super.onResume();

		// Create the GetContactsTask and execute to retrieve the
		// results into a Cursor and assign to adapter.
		// There is nothing to pass to the AsyncTask for a parameter.
		new LoadContactsTask().execute((Object[]) null);
	}

	@Override
	protected void onStop()
	{

		// Get the current Cursor from the CursorAdapter and
		// and close it.

		Cursor cursor = _adapter.getCursor();

		if (cursor != null)
		{
			cursor.close();
		}

		// Adapter no longer should have a populated cursor.
		_adapter.changeCursor(null);

		super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);

		// Inflate the Menu resource to be associated with
		// this activity.
		getMenuInflater().inflate(R.menu.menu_addressbook_activity, menu);

		return true;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{

		// Create the Intent to be passed to the ViewContactActivity.
		Intent viewContactIntent = new Intent(ContactListActivity.this, ViewContactActivity.class);

		// Pass the selected Contact ID along with the Intent
		// so that ViewContactActivity can retrieve it.
		viewContactIntent.putExtra(AddressBookModel.KEY_ID, id);

		// Start the ViewContactActivity.
		startActivity(viewContactIntent);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.action_add_contact:
			{
				// Create the Intent to be passed to the ViewContactActivity.
				Intent viewContactIntent = new Intent(ContactListActivity.this, ViewContactActivity.class);

				// Start the ViewContactActivity.
				startActivity(viewContactIntent);
			}
			default:
			{
				return super.onOptionsItemSelected(item);
			}
		}
	}

	private class LoadContactsTask extends AsyncTask<Object, Object, Cursor>
	{

		AddressBookModel _model;

		@Override
		protected void onPreExecute()
		{
			// NOTE: onPreExecute executes on the UI (Main) Thread.
			// Instance the AddressBookModel object.
			_model = new AddressBookModel(ContactListActivity.this);
		}

		@Override
		protected Cursor doInBackground(Object... params)
		{

			Cursor returnCursor;

			// NOTE: doInBackground will not execute on the UI Thread.

			// Calls the AddressBookModel to retrieve a Cursor populated
			// with all of the Contacts.

			// Open connection to the database.
			_model.openDBConnection();

			// Retrieve all (-1) of the contacts from the database.
			returnCursor = _model.getContact(-1);

			return returnCursor;
		}

		@Override
		protected void onPostExecute(Cursor result)
		{
			// NOTE: onPostExecute executes on the UI (Main) Thread.

			// Assign the populated Cursor from the database query
			// in the background thread.
			_adapter.changeCursor(result);

			// Close the database connection. Never leave a
			// database connection open longer than actually
			// required.
			_model.closeDBConnection();

		}

	}

}
