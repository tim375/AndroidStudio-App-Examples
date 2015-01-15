package edu.sdsmt.csc476.addressbookfragmentapp;

import edu.sdsmt.csc476.addressbookfragmentapp.ContactModel.Contact;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

public class MainActivity extends Activity implements IContactControlListener
{

	private final static String FRAGMENT_CONTACT_LIST_TAG = "ContactListTag";
	private final static String FRAGMENT_CONTACT_VIEW_TAG = "ContactViewTag";
	
	private FragmentManager _fragmentManager;
	private ListContactFragment _fragmentContactList;
	private ViewContactFragment _fragmentViewContact;
	
	private ContactModel _model;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		// Associate the layout with host activity.
		setContentView(R.layout.activity_main);
		
		// Get a reference to the fragment manager to 
		// be used for adding/replacing fragments.
		_fragmentManager = getFragmentManager();
		
		_fragmentContactList = (ListContactFragment) _fragmentManager.findFragmentByTag(FRAGMENT_CONTACT_LIST_TAG);
		if (_fragmentContactList == null)
		{
			_fragmentContactList = new ListContactFragment();
		}
		
		_fragmentViewContact = (ViewContactFragment) _fragmentManager.findFragmentByTag(FRAGMENT_CONTACT_VIEW_TAG);
		if (_fragmentViewContact == null)
		{
			_fragmentViewContact = new ViewContactFragment();
		}
		
		if (savedInstanceState == null)
		{
			_fragmentManager.beginTransaction()
			                .replace(R.id.fragmentContainerFrame, _fragmentContactList, FRAGMENT_CONTACT_LIST_TAG)
			                .commit();
		}
		
		// Get single instance to the model to handle
		// all database activity.
		_model = ContactModel.getInstance(this);
	}
	
	@Override
	public void contactSelect(Contact contact)
	{
		// Display selected contact.
		showContactViewFragment(contact);
	}

	@Override
	public void contactInsert()
	{
		// Display empty contact.
		Contact contact = new Contact();
		showContactViewFragment(contact);
	}

	@Override
	public void contactInsert(Contact contact)
	{
		_model.insertContact(contact);
		_fragmentManager.popBackStackImmediate();
	}

	@Override
	public void contactUpdate(Contact contact)
	{
		_model.updateContact(contact);
		_fragmentManager.popBackStackImmediate();
	}

	@Override
	public void contactDelete(Contact contact)
	{
		_model.deleteContact(contact);
		_fragmentManager.popBackStackImmediate();
	}

	private void showContactViewFragment(Contact contact)
	{
		
		_fragmentViewContact._contact = contact;
		
		_fragmentManager.beginTransaction()
				        .replace(R.id.fragmentContainerFrame, _fragmentViewContact, FRAGMENT_CONTACT_VIEW_TAG)
				        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
				        .addToBackStack(null)
				        .commit();
		
	}
}
