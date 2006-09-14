/* FileName: it/di/unipi/iochatto/presence/PresenceEvent.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.presence;

import java.util.EventObject;

import net.jxta.document.Advertisement;

public class PresenceEvent extends EventObject {
	private Advertisement eventAdv;
	private  int presence;
	private String name = new String();
	private String emailAddress = new String();
	public PresenceEvent()
	{
		super("presenceevent");
	}
	public String getName()
	{
		return name;
	}
	public String getEmailAddress()
	{
		return emailAddress;	
	}
	public int getStatus()
	{
		return presence;
	}
	/*
	public PresenceEvent(Object source) {
		super(source);
		// TODO Auto-generated constructor stub
	}
	*/
	public PresenceEvent(Advertisement doc)
	{
		super(doc);
		eventAdv = doc;
		parseAdv(doc);
	}
	public void setAdvertisement(Advertisement doc)
	{
		eventAdv = doc;
		parseAdv(doc);
	}
	private void parseAdv(Advertisement doc)
	{
		PresenceAdvertisement pa = (PresenceAdvertisement) doc;
	   emailAddress = pa.getEmailAddress();
	   presence = pa.getPresenceStatus();
	   name = pa.getName();
	   
	}
	
	public Advertisement getAdvertisement()
	{
		return eventAdv;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -7396668260952668958L;

}
