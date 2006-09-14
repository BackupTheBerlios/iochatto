/* FileName: it/di/unipi/iochatto/channel/message/ChatMessage.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.channel.message;

import java.io.IOException;
import java.io.StringReader;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.jxta.document.Element;
import net.jxta.document.MimeMediaType;
import net.jxta.document.StructuredDocumentFactory;
import net.jxta.document.XMLDocument;
import net.jxta.document.XMLElement;
import it.di.unipi.iochatto.util.DateTime;

public class ChatMessage implements ChannelMessage {
	private XMLDocument doc = null; 
	private DateTime dt = null;
	private String address = null;
	private String name = null;
	private String peerID = null;
	private String channel = null;
	private String message = null;
	private Map<String, String> emoticon = null;
	public ChatMessage(String s) {
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
	public void setEmoticonMap(Map<String,String> m)
	{
		emoticon = m;
	}
	public DateTime getDateTime() {
		
		return dt;
	}
	private String parseEmoticon(String message)
	{
		String htmlMessage = new String(message);
		if (emoticon == null)
		{
			return message;
		} else {
			Set<String> keys = emoticon.keySet();
			for (String k : keys)
			{
				String value = emoticon.get(k);
				if (value!=null)
					htmlMessage.replaceAll(k,value);
			}
		}
		return htmlMessage;
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
		Calendar c = dt.getCalendar();
		int h = c.get(Calendar.HOUR_OF_DAY);
		int m = c.get(Calendar.MINUTE);
		int s = c.get(Calendar.SECOND);
		String hour = (h<10) ? "0"+h: Integer.toString(h);
		String minute = (m<10) ? "0"+m  : Integer.toString(m);
		String second = (s<10) ? "0"+s  : Integer.toString(s);
		String time = hour+":"+minute+":"+second;
		String msgHtml = "<tr><td><font color=\"blue\">"+time+"</font>"+"</td><td<font color=\"red\">"+name+"</font></td><td>"+parseEmoticon(message)+"</td></tr>";
		return msgHtml;
	}

	public String toXML() {
		return doc.toString();
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
		    			address = item.getTextValue().trim();
			    	if (itemName.equals("PeerID"))
		    			peerID = item.getTextValue();
			    	if (itemName.equals("ChannelName"))
			    	{
			    		channel = item.getTextValue().trim();
			    	}
			    	if (itemName.equals("Message"))
			    	{
			    		message = item.getTextValue();
			    	}

			    	if (itemName.equals("DateTime"))
			    	{
			    		dt = new DateTime(item.getTextValue().trim());
			    	
			    	}
			     }
				}
		
		
	}

	public String channelName() {
		return channel;
	}

}
