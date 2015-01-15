package edu.sdsmt.csc476.addressbookfragmentapp;


import java.util.List;

import edu.sdsmt.csc476.addressbookfragmentapp.ContactModel.Contact;
import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ListContactFragment extends ListFragment
{

	private IContactControlListener _listener;
	
	private List<Contact> _contacts;
	private ArrayAdapter<Contact> _adapter;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		// Insert Sample Contacts
		//ContactModel model = new ContactModel(getActivity());
		//model.insertSampleContacts();
		
		setHasOptionsMenu(true);

	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflator)
	{
		// Inflate the Menu resource to be associated with
		// this activity.
		getActivity().getMenuInflater().inflate(R.menu.menu_contact_list, menu);

		super.onCreateOptionsMenu(menu, menuInflator);
	}


	@Override
	public void onAttach(Activity activity)
	{
		try
		{
			// Assign listener reference from hosting activity.
			_listener = (IContactControlListener) activity;
		}
		catch (ClassCastException e)
		{
			throw new ClassCastException(activity.toString());
		}
		
		super.onAttach(activity);
	}


	@Override
	public void onResume()
	{
		super.onResume();

		// Just get the list of contacts from the database again.
		refreshContactList();
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id)
	{
		Contact contact = null;
		
		contact = (Contact) getListAdapter().getItem(position);
		if (contact != null)
		{
			_listener.contactSelect(contact);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.action_add_contact:
			{
				_listener.contactInsert();
			}
			default:
			{
				return super.onOptionsItemSelected(item);
			}
		}
	}

	private void refreshContactList()
	{
		// Get an Array List of Contact objects.
		_contacts = ContactModel.getInstance(getActivity()).getContacts();
		
		// Assign list to ArrayAdapter to be used with assigning
		// to the ListFragment list adapter.
		_adapter = new ArrayAdapter<Contact>(getActivity(),
		        						     R.layout.fragment_contact_list_item, 
		        						     _contacts);
		
		// Assign the adapter.
		setListAdapter(_adapter);	
	}
}