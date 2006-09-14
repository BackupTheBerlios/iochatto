/* FileName: it/di/unipi/iochatto/channel/message/ChannelMessageEvent.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.channel.message;

import net.jxta.endpoint.Message;
import it.di.unipi.iochatto.channel.ChannelEvent;
import it.di.unipi.iochatto.channel.ChannelInfo;

public class ChannelMessageEvent extends ChannelEvent {
	private ChannelMessage msg;
	private String chName;
	private ChannelInfo metadata;
	public ChannelMessageEvent(Object source) {
		super(source);
		// TODO Auto-generated constructor stub
	}
	public void setMetaData(ChannelInfo mt)
	{
		metadata = mt;
	}
	public ChannelInfo getMetadata()
	{
		return metadata;
	}
	public void setChannelName(String name)
	{
		chName = name;
	}
	public String getChannelName()
	{
		return chName;
	}
	public void setMessage(ChannelMessage m0)
	{
		msg = m0;
	}
	public ChannelMessage getMessage()
	{
		return msg;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 6175731073084657402L;


}
