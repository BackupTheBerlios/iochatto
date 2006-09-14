/* FileName: it/di/unipi/iochatto/presence/WatchDogEvent.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.presence;

import java.util.EventObject;

import net.jxta.document.Advertisement;

public class WatchDogEvent extends EventObject {
	private Advertisement eventAdv;
	public WatchDogEvent(Advertisement doc)
	{
		super("watchdogevent");
		eventAdv = doc;
	}
	public WatchDogEvent(Object source) {
		super(source);
		// TODO Auto-generated constructor stub
	}
	public void setAdvertisement(Advertisement doc)
	{
		eventAdv = doc;
	}
	public Advertisement getAdvertisement()
	{
		return eventAdv;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -7396668260952668951L;

}
