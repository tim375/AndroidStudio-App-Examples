package edu.sdsmt.csc476.addressbookfragmentapp;

import edu.sdsmt.csc476.addressbookfragmentapp.ContactModel.Contact;

/**
 * Interface defined for communication and callback handling between
 * the Fragments and Activity in this application.
 * @author brianb
 *
 */
public interface IContactControlListener
{
	public void contactSelect(Contact contact);
	public void contactInsert();
	public void contactInsert(Contact contact);
	public void contactUpdate(Contact contact);
	public void contactDelete(Contact contact);

}
