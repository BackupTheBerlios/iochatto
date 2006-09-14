/* FileName: it/di/unipi/iochatto/channel/ChannelDiscoveryEvent.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.channel;

import java.util.EventObject;

public class ChannelDiscoveryEvent extends EventObject {
	private boolean found = false;
	private ChannelInfo mt = null;
	private String itemToSearch = null;
	public ChannelDiscoveryEvent(Object source) {
		super(source);
		// TODO Auto-generated constructor stub
	}
	public ChannelDiscoveryEvent(Object source,String search, boolean found, ChannelInfo metadata)
	{
		super(source);
		this.itemToSearch = search;
		this.mt = metadata;
		this.found = found;
	}
	public String item()
	{
		return itemToSearch;
	}
	public boolean found()
	{
		return found;
	}
	public ChannelInfo getMetaData()
	{
		return mt;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -8194959601115205070L;

}
