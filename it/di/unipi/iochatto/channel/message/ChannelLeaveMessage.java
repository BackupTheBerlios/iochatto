/* FileName: it/di/unipi/iochatto/channel/message/ChannelLeaveMessage.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.channel.message;

import java.io.IOException;
import java.io.StringReader;
import java.util.Date;
import java.util.Enumeration;

import net.jxta.document.MimeMediaType;
import net.jxta.document.StructuredDocumentFactory;
import net.jxta.document.XMLDocument;
import net.jxta.document.XMLElement;
import it.di.unipi.iochatto.util.DateTime;

public class ChannelLeaveMessage implements ChannelMessage {
	private XMLDocument doc = null;
	private DateTime dt = null;
	private String address = null;
	private String name = null;
	private String peerID = null;
	private String channel = null;
	public ChannelLeaveMessage(String s) {
		StringReader reader = new StringReader(s);
		try {
			doc = (XMLDocument) StructuredDocumentFactory.newStructuredDocument(MimeMediaType.XMLUTF8,reader);
		} catch (IOException e) {
			doc = null;
			reader = null;
		}
		}

	public void accept(MessageVisitor v) {
		// TODO Auto-generated method stub

	}
	public String channelName()
	{
		return this.channel;
	}
	public DateTime getDateTime() {
		// TODO Auto-generated method stub
		dt = new DateTime(new Date());
		return dt;
	}

	public String senderAddress() {
		// TODO Auto-generated method stub
		return address;
	}

	public String senderName() {
		// TODO Auto-generated method stub
		return name;
	}

	public String senderPeerID() {
		// TODO Auto-generated method stub
		return peerID;
	}

	public String toHTML() {
		// TODO Auto-generated method stub
		return null;
	}

	public String toXML() {
		if (doc!=null)
				return doc.toString();
		return null;
	}

	public void parse() {
		XMLElement root = (XMLElement) doc.getRoot();
		Enumeration en = root.getChildren();
		String itemName = null;
		while ( (en!=null) && (en.hasMoreElements()))
				{
			     XMLElement item = (XMLElement) en.nextElement();
			     itemName = (item!=null) ? item.getName() : null; 
			     if (itemName !=null)
			     {
			    	// do real parsing
			    	if (itemName.equals("UserName"))
			    			name = item.getTextValue();
			    	if (itemName.equals("Email"))
		    			address = item.getTextValue();
			    	if (itemName.equals("PeerID"))
		    			peerID = item.getTextValue();
			    	if (itemName.equals("ChannelName"))
			    	{
			    		channel = item.getTextValue().trim();
			    	}
			     }
				}
		
		
	}

}
