/* FileName: it/di/unipi/iochatto/channel/message/ChannelInfoMessage.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.channel.message;

import java.io.IOException;
import java.io.StringReader;
import java.util.Date;
import java.util.HashMap;

import org.jdom.JDOMException;

import net.jxta.document.StructuredDocumentFactory;
import net.jxta.document.XMLDocument;
import net.jxta.document.XMLElement;
import it.di.unipi.iochatto.channel.ChannelInfo;
import it.di.unipi.iochatto.channel.UserInfo;
import it.di.unipi.iochatto.util.DateTime;
import net.jxta.document.MimeMediaType;
public class ChannelInfoMessage implements ChannelMessage {
	private XMLDocument doc = null;
	private ChannelInfo chInfo = new ChannelInfo();
	public ChannelInfoMessage(ChannelInfo info)
	{
		try {
			chInfo.parse(info.toString());
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public ChannelInfoMessage(String s) {
	StringReader reader = new StringReader(s);
	try {
		doc = (XMLDocument) StructuredDocumentFactory.newStructuredDocument(MimeMediaType.XMLUTF8,reader);
	} catch (IOException e) {
		doc = null;
	}
	}

	public void accept(MessageVisitor v) {
		// TODO Auto-generated method stub

	}
	public DateTime getDateTime() {
		// TODO Auto-generated method stub
		DateTime dt = null;
		if (chInfo!=null)
		{
			dt = new DateTime(new Date());
		}
		return dt;
	}
    
	public String senderAddress() {
		
		if (chInfo!=null)
				  return chInfo.getFounderEmailAddress().trim();
		return null;
	}

	public String senderName() {
		
		if (chInfo!=null)
		return chInfo.getFounderName();
		return null;
	}

	public String senderPeerID() {
		
		if (chInfo!=null)
			return chInfo.getFounderPeerID();
		return null;
	}
	public String pipeUri()
	{
		if (chInfo!=null)
				return chInfo.getPipeUri();
		return null;
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
		if (doc == null)
				return;
		XMLElement root = (XMLElement) doc.getRoot();

		try {
			
			chInfo = new ChannelInfo();
			chInfo.parse(doc.toString());
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			chInfo = null;
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			chInfo = null;
		}
		
		
		
	}
	public HashMap<String,UserInfo> getUsers()
	{
		return chInfo.getUsers();
	}
	public String channelName() {
		if (chInfo!=null)
				return chInfo.getName().trim();
		return null;
	}

}
