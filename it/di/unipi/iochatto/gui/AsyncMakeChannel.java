/* FileName: it/di/unipi/iochatto/gui/AsyncMakeChannel.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.gui;
import java.net.URISyntaxException;

import it.di.unipi.iochatto.channel.Channel;
import it.di.unipi.iochatto.channel.ChannelDiscovery;
import it.di.unipi.iochatto.channel.ChannelFactory;
import it.di.unipi.iochatto.channel.ChannelInfo;
import it.di.unipi.iochatto.channel.ChannelNotFound;
import it.di.unipi.iochatto.channel.UserInfo;
import it.di.unipi.iochatto.core.Status;
import it.di.unipi.iochatto.core.StatusEvent;
import it.di.unipi.iochatto.core.StdChatGroup;
public class AsyncMakeChannel extends Thread {
	private String topic;
	private String name;
	public void setTopic(String t) { topic = t;}
	public void setChannelName(String t) { name = t;}
	public void run()
	{
		UserInfo info = new UserInfo();
		Status s = Status.getInstance();
		StdChatGroup group = StdChatGroup.getInstance();
		info.setAddress(s.getEmailAddress());
		info.setName(s.getName());
		info.setPeerID(s.getPeerID());
		info.setStatus(s.getStatus());
		if ((topic == null) || (name == null))
					return;
		Channel ch = null;
		ChannelDiscovery disco = new ChannelDiscovery();
		boolean create = false;
		group.fireEventStatus(new StatusEvent(this,"Creating a channel: Founding if the channel exist"));
		try {
			ChannelInfo chInfo = disco.search(name);
		} catch (ChannelNotFound e1) {
			group.fireEventStatus(new StatusEvent(this,"Channel not found...CREATING A NEW ONE"));
			
			create = true;
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (create)
		{
		try {
			ch = ChannelFactory.newChannel(name,info,topic);
		} catch (Exception e) {
			group.fireEventStatus(new StatusEvent(this,"Error on created channel"));
		
			e.printStackTrace();
			return;
		}
		group.fireEventStatus(new StatusEvent(this,"Channel " + name + " successfully created!"));
		 return;
		}
		group.fireEventStatus(new StatusEvent(this,"Channel " + name + " not created!"));
		
	}
}
