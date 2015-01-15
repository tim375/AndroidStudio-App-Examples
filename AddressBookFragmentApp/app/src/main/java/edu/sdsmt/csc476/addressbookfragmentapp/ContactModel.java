package edu.sdsmt.csc476.addressbookfragmentapp;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ContactModel extends SQLiteOpenHelper
{

	public static final String KEY_ID = "ContactID";
	public static final String KEY_NAME = "Name";
	public static final String KEY_PHONE = "Phone";
	public static final String KEY_EMAIL = "Email";
	public static final String KEY_STREET = "Street";
	public static final String KEY_CITY = "City";
	
	private static final String TAG = "AddressBookFragmentApp";

	private static final String DATABASE_NAME = "MyAddressBook.db";
	private static final int DATABASE_VERSION = 1;
	private static final String TABLE_MYCONTACTS = "MyContacts";

	private static final String TABLE_CREATE_MYCONTACTS =
			        "CREATE TABLE " +
					TABLE_MYCONTACTS +
					"(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					KEY_NAME + " TEXT, " +
					KEY_PHONE + " TEXT, " +
					KEY_EMAIL + " TEXT, " +
					KEY_STREET + " TEXT, " +
					KEY_CITY + " TEXT);";

	private SQLiteDatabase _db;
	private static ContactModel _instance;
	
	/**
	 * Class defined to allow for the passing of contact data between
	 * the model, activity, and fragments.  Also, used to define the 
	 * contents of an ArrayAdapter.
	 * @author brianb
	 *
	 */
	public static class Contact
	{

		public Contact()
		{
			ContactID = -1;
		}
		
		public Contact(long contactID)
		{
			ContactID = contactID;
		}
		
		public long ContactID;
		public String Name;
		public String Phone;
		public String Email;
		public String Street;
		public String City;
		
		
		// Used by ArrayAdapter to determine
		// what to display in the list.
		@Override
		public String toString() 
		{
		    return Name;
		}
	}

	public ContactModel(Context context)
	{
		// Call the parent class and pass the actual name and version of the
		// database to be created. The version will be used in the future for
		// determine whether onUpgrade() is called from the SQLiteOpenHelper
		// extension.
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		// Execute the CREATE TABLE statement defined as a const.
		db.execSQL(TABLE_CREATE_MYCONTACTS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		// If there is ever a need to upgrade the database and/or table.
		// Compare old and new versions to determine if modifications
		// to the database are necessary. Typically, this will be done with
		// ALTER TABLE or CREATE TABLE SQL statements depending on the
		// change required.

		if (newVersion == 2)
		{
			// No version 2 upgrade process yet.
		}
	}
	
	public static synchronized ContactModel getInstance(Context context)
	{
		// Used to synchronize access and force singleton on the 
		// database helper object.
		if (_instance == null)
		{
			_instance = new ContactModel(context);
		}
		
		return _instance;
	}

	public void insertContact(Contact contact)
	{
		// Take parameters and pass to method to populate the
		// ContentValues data structure.
		ContentValues values = populateContentValues(contact);

		// Open the database connect, keep it close to the actual operation.
		openDBConnection();

		// Execute query to update the specified contact.
		long id = _db.insert(TABLE_MYCONTACTS, null, values);

		Log.d(TAG, "ContactID inserted = " + String.valueOf(id));

		// Close the database connection as soon as possible.
		closeDBConnection();

	}

	public void insertSampleContacts()
	{
		Contact contact;
		
		contact = new Contact();
		contact.Name = "Brian Butterfield";
		contact.Phone = "605-390-0395";
		contact.Email = "brianb@innovsys.com";
		contact.Street = "123 Main Street";
		contact.City = "Rapid City, SD";
		insertContact(contact);
		
		contact = new Contact();
		contact.Name = "Toni Logar";
		contact.Phone = "605-555-1212";
		contact.Email = "toni@sdsmt.edu";
		contact.Street = "123 Main Street";
		contact.City = "Somewhere, HI";
		insertContact(contact);
		
		contact = new Contact();
		contact.Name = "David Springhetti";
		contact.Phone = "605-555-1212";
		contact.Email = "davids@innovsys.com";
		contact.Street = "123 Main Street";
		contact.City = "Somewhere, SD";
		insertContact(contact);
	}

	public void updateContact(Contact contact)
	{
		// Take parameters and pass to method to populate the
		// ContentValues data structure.
		ContentValues values = populateContentValues(contact);

		// Open the database connect, keep it close to the actual operation.
		openDBConnection();

		// Execute query to update the specified contact.
		int rowsAffected = _db.update(TABLE_MYCONTACTS,
				values,
				KEY_ID + " = ?",
				new String[] { String.valueOf(contact.ContactID) });

		// Close the database connection as soon as possible.
		closeDBConnection();

		if (rowsAffected == 0)
		{
			// The contact row was not updated, what should be done?
		}
	}

	public void deleteContact(Contact contact)
	{
		// Open the database connect, keep it close to the actual operation.
		openDBConnection();

		// Execute query to delete the specified contact.
		int rowsAffected = _db.delete(TABLE_MYCONTACTS,
				KEY_ID + " = ?",
				new String[] { String.valueOf(contact.ContactID) });

		// Close the database connection as soon as possible.
		closeDBConnection();

		if (rowsAffected == 0)
		{
			// The contact row was not deleted, what should be done?
		}
	}

	public Contact getContact(long contactID)
	{
		
		openDBConnection();
		
		// Return the specific contact row based on ID passed.
		Cursor cursor = _db.query(TABLE_MYCONTACTS,
							      new String[] { KEY_ID, KEY_NAME, KEY_PHONE, KEY_EMAIL, KEY_STREET, KEY_CITY },
								  KEY_ID + "=" + contactID,
								  null,
								  null,
								  null,
								  KEY_NAME);
		
		if (cursor.moveToFirst())
		{
			return cursorToContact(cursor);
		}
		
		cursor.close();
		closeDBConnection();

		return null;
	}
	
	public List<Contact> getContacts()
	{
		List<Contact> contacts = new ArrayList<Contact>();
		
		openDBConnection();
		
		Cursor cursor = _db.query(TABLE_MYCONTACTS,
								  new String[] { KEY_ID, KEY_NAME, KEY_PHONE, KEY_EMAIL, KEY_STREET, KEY_CITY},
								  null,
								  null,
								  null,
								  null,
								  KEY_NAME);
		
		
		cursor.moveToFirst();
	    while (cursor.isAfterLast() == false) 
	    {
	    	Contact contact = cursorToContact(cursor);
			contacts.add(contact);
		    cursor.moveToNext();
		}
		
		cursor.close();
		closeDBConnection();
		
		return contacts;
	}

	private void openDBConnection()
	{
		// Opens connection to the database for writing specifically.
		_db = getWritableDatabase();
	}

	private void closeDBConnection()
	{
		if (_db != null && _db.isOpen() == true)
		{
			// Close connection to database if open.
			_db.close();
		}
	}

	private Contact cursorToContact(Cursor cursor)
	{
		Contact contact = new Contact(cursor.getLong(cursor.getColumnIndex(KEY_ID))); 
		contact.Name = cursor.getString(cursor.getColumnIndex(KEY_NAME)); 
		contact.Phone = cursor.getString(cursor.getColumnIndex(KEY_PHONE)); 
		contact.Email = cursor.getString(cursor.getColumnIndex(KEY_EMAIL)); 
		contact.Street = cursor.getString(cursor.getColumnIndex(KEY_STREET)); 
		contact.City = cursor.getString(cursor.getColumnIndex(KEY_CITY)); 
		
		return contact;
	}

	private ContentValues populateContentValues(Contact contact)
	{
		// Common function used to populate the ContentValues to be used in SQL
		// insert or update methods.
		
		ContentValues values = new ContentValues();
		values.put(KEY_NAME, contact.Name);
		values.put(KEY_PHONE, contact.Phone);
		values.put(KEY_EMAIL, contact.Email);
		values.put(KEY_STREET, contact.Street);
		values.put(KEY_CITY, contact.City);

		return values;
	}

}
