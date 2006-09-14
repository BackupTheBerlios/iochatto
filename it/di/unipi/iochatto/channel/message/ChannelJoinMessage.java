/* FileName: it/di/unipi/iochatto/channel/message/ChannelJoinMessage.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.channel.message;

import java.io.IOException;
import java.io.StringReader;
import java.util.Date;
import java.util.Enumeration;

import net.jxta.document.Attributable;
import net.jxta.document.Element;
import net.jxta.document.MimeMediaType;
import net.jxta.document.StructuredDocumentFactory;
import net.jxta.document.XMLDocument;
import net.jxta.document.XMLElement;
import it.di.unipi.iochatto.util.DateTime;

public class ChannelJoinMessage implements ChannelMessage {
	private XMLDocument doc = null;
	private DateTime dt = null;
	private String address = null;
	private String name = null;
	private String peerID = null;
	private String channel = null;
	public ChannelJoinMessage(String s) {
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
		return channel;
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
		/*
		 	private XMLDocument mkJoinAdv(String user, String PeerID, String Email)
	{
		XMLDocument doc = (XMLDocument) StructuredDocumentFactory.newStructuredDocument(MimeMediaType.XMLUTF8, "jxta:ChanMsg");
		Attributable attr = (Attributable) doc;
		attr.addAttribute("xmlns:jxta", "http://jxta.org");
		Element item1 = doc.createElement("ChanCommand","JOIN");
		Element item0 = doc.createElement("ChannelName",metadata.getName());
		Element item2 = doc.createElement("UserName",user);
		Element item3 = doc.createElement("Email", Email);
		Element item4 = doc.createElement("PeerID", PeerID);
		doc.appendChild(item0);
		doc.appendChild(item1);
		doc.appendChild(item2);
		doc.appendChild(item3);
		doc.appendChild(item4);

		return doc;
	}
		 */
		
		//XMLElement userName = root.getChildren("UserName");
		//XMLElement eMail = root.getChildren("Email");
		//XMLElement pid = root.getChildren("PeerID");
		Enumeration en = root.getChildren();
		String itemName = null;
		while ( (en!=null) && (en.hasMoreElements()))
				{
			     XMLElement item = (XMLElement) en.nextElement();
			     itemName = (item!=null) ? item.getName() : null; 
			     if (itemName !=null)
			     {
			    	 itemName.trim();
			    	 //System.out.println("Item:"+ itemName);
			    	// do real parsing
			    	if (itemName.equals("UserName"))
			    			name = item.getTextValue();
			    	if (itemName.equals("Email"))
		    			address = item.getTextValue().trim();
			    	if (itemName.equals("PeerID"))
		    			peerID = item.getTextValue().trim();
			    	if (itemName.equals("ChannelName"))
			    	{
			    		channel = item.getTextValue().trim();
			    	
			    	}
			     }
				}
		
		
	}

}
