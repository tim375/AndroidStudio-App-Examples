package edu.sdsmt.csc476.addressbookapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ViewContactActivity extends Activity implements android.view.View.OnClickListener
{

	private EditText _editTextName;
	private EditText _editTextPhone;
	private EditText _editTextEmail;
	private EditText _editTextStreet;
	private EditText _editTextCity;
	private Button _buttonSaveContact;

	private long _contactID = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// Inflate the UI.
		setContentView(R.layout.activity_view_contact);

		// Assign instances of Views from the Layout Resource.
		_editTextName = (EditText) findViewById(R.id.editTextName);
		_editTextPhone = (EditText) findViewById(R.id.editTextPhone);
		_editTextEmail = (EditText) findViewById(R.id.editTextEmail);
		_editTextStreet = (EditText) findViewById(R.id.editTextStreet);
		_editTextCity = (EditText) findViewById(R.id.editTextCityStateZip);
		_buttonSaveContact = (Button) findViewById(R.id.buttonSaveContact);

		_buttonSaveContact.setOnClickListener(this);

		// Retrieve the "bundle" of data that was added to the intent
		// and passed to the activity.
		Bundle extras = getIntent().getExtras();

		if (extras != null)
		{
			// If there are extras, it is known to be the ContactID.
			// In this case, retrieve the ContactID and disable
			// all of the views because this is only a "view"
			// of the contact.
			_contactID = extras.getLong(AddressBookModel.KEY_ID);

			// Do not allow editing.
			_editTextName.setEnabled(false);
			_editTextPhone.setEnabled(false);
			_editTextEmail.setEnabled(false);
			_editTextStreet.setEnabled(false);
			_editTextCity.setEnabled(false);

			// Do not display the Save Contact button initially,
			// since the user is only viewing the contact.
			_buttonSaveContact.setVisibility(View.INVISIBLE);
		}

	}

	@Override
	protected void onResume()
	{
		super.onResume();

		if (_contactID > 0)
		{
			// If there is a ContactID, execute the AsyncTask
			// to retrieve all of the Contact details to be
			// displayed.
			new LoadContactTask().execute(_contactID);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_view_contact_activity, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{

		switch (item.getItemId())
		{
			case R.id.action_update_contact:
			{
				// Allow for editing on the fields.
				_editTextName.setEnabled(true);
				_editTextPhone.setEnabled(true);
				_editTextEmail.setEnabled(true);
				_editTextStreet.setEnabled(true);
				_editTextCity.setEnabled(true);

				// Display the Save Contact button.
				_buttonSaveContact.setVisibility(View.VISIBLE);

				return true;
			}
			case R.id.action_delete_contact:
			{
				// Do not allow editing.
				_editTextName.setEnabled(false);
				_editTextPhone.setEnabled(false);
				_editTextEmail.setEnabled(false);
				_editTextStreet.setEnabled(false);
				_editTextCity.setEnabled(false);

				_buttonSaveContact.setVisibility(View.INVISIBLE);

				// Call AsyncTask to delete the contact from the database.
				new DeleteContactTask().execute(_contactID);

				return true;
			}
			default:
			{
				return super.onOptionsItemSelected(item);
			}
		}

	}

	@Override
	public void onClick(View v)
	{
		if (_editTextName.getText().toString().isEmpty() == false)
		{
			// Call the AsyncTask to update the contact in the database.
			new InsertUpdateContactTask().execute(_contactID);
		}
		else
		{
			// Alert the user of missing Name field.

			AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
			alertBuilder.setTitle(R.string.alert_title_missinginfo);
			alertBuilder.setMessage(R.string.alert_message_missing_name);
			alertBuilder.setPositiveButton(R.string.alert_button_OK, null);
			alertBuilder.show();
		}
	}

	private class LoadContactTask extends AsyncTask<Long, Object, Cursor>
	{

		AddressBookModel _model;

		@Override
		protected void onPreExecute()
		{
			// NOTE: onPreExecute executes on the UI (Main) Thread.
			// Instance the AddressBookModel object.
			_model = new AddressBookModel(ViewContactActivity.this);
		}

		@Override
		protected Cursor doInBackground(Long... params)
		{
			Cursor returnCursor;

			// NOTE: doInBackground will not execute on the UI Thread.

			// Calls the AddressBookModel to retrieve a Cursor populated
			// with the specific Contact details.

			// Open connection to the database.
			_model.openDBConnection();

			// Retrieve the contact from the database.
			returnCursor = _model.getContact(params[0]);

			// Returning the populated cursor to the
			// onPostExecute() method which runs on the UI
			// thread, so the UI views can be populated with
			// the data.
			return returnCursor;
		}

		@Override
		protected void onPostExecute(Cursor result)
		{
			// NOTE: onPostExecute executes on the UI (Main) Thread.

			if (result.moveToFirst() == true)
			{
				_editTextName.setText(result.getString(result.getColumnIndex(AddressBookModel.KEY_NAME)));
				_editTextPhone.setText(result.getString(result.getColumnIndex(AddressBookModel.KEY_PHONE)));
				_editTextEmail.setText(result.getString(result.getColumnIndex(AddressBookModel.KEY_EMAIL)));
				_editTextStreet.setText(result.getString(result.getColumnIndex(AddressBookModel.KEY_STREET)));
				_editTextCity.setText(result.getString(result.getColumnIndex(AddressBookModel.KEY_CITY)));
			}

			// Close the Cursor since the data fields have been extracted.
			result.close();

			// Close the database connection.
			_model.closeDBConnection();

		}

	}

	private class InsertUpdateContactTask extends AsyncTask<Long, Object, Object>
	{

		AddressBookModel _model;
		String _name;
		String _phone;
		String _email;
		String _street;
		String _city;

		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();

			// NOTE: onPreExecute executes on the UI (Main) Thread.
			// Instance the AddressBookModel object.
			_model = new AddressBookModel(ViewContactActivity.this);

			_name = _editTextName.getText().toString();
			_phone = _editTextPhone.getText().toString();
			_email = _editTextEmail.getText().toString();
			_street = _editTextStreet.getText().toString();
			_city = _editTextCity.getText().toString();
		}

		@Override
		protected Object doInBackground(Long... params)
		{
			// Open connection to the database.
			_model.openDBConnection();

			if (params[0] > 0)
			{
				// Update the contact in the database.
				_model.updateContact(params[0], _name, _phone, _email, _street, _city);
			}
			else
			{
				// Insert the contact into the database.
				_model.insertContact(_name, _phone, _email, _street, _city);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Object result)
		{
			_model.closeDBConnection();
			finish();
		}

	}

	private class DeleteContactTask extends AsyncTask<Long, Object, Object>
	{

		AddressBookModel _model;

		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();

			// NOTE: onPreExecute executes on the UI (Main) Thread.
			// Instance the AddressBookModel object.
			_model = new AddressBookModel(ViewContactActivity.this);
		}

		@Override
		protected Object doInBackground(Long... params)
		{
			// Open connection to the database.
			_model.openDBConnection();

			// Delete the contact into the database.
			_model.deleteContact(params[0]);

			return null;
		}

		@Override
		protected void onPostExecute(Object result)
		{
			_model.closeDBConnection();
			finish();
		}

	}

}
