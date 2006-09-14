/* FileName: it/di/unipi/iochatto/presence/resolver/ResolverStatus.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.presence.resolver;

import it.di.unipi.iochatto.core.SearchEvent;
import it.di.unipi.iochatto.core.Status;
import it.di.unipi.iochatto.core.StdChatGroup;
import it.di.unipi.iochatto.presence.PresenceAdvertisement;
import it.di.unipi.iochatto.util.DateTime;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Logger;

import net.jxta.credential.*;
import net.jxta.document.AdvertisementFactory;
import net.jxta.document.Attributable;
import net.jxta.document.Element;
import net.jxta.document.MimeMediaType;
import net.jxta.document.StructuredDocumentFactory;
import net.jxta.document.StructuredTextDocument;
import net.jxta.document.XMLDocument;
import net.jxta.document.XMLElement;
import net.jxta.exception.PeerGroupException;
import net.jxta.exception.ProtocolNotSupportedException;
import net.jxta.impl.protocol.ResolverQuery;
import net.jxta.impl.protocol.ResolverResponse;
import net.jxta.impl.resolver.ResolverServiceImpl;
import net.jxta.membership.*;
import net.jxta.peergroup.PeerGroup;
import net.jxta.protocol.ResolverQueryMsg;
import net.jxta.protocol.ResolverResponseMsg;
import net.jxta.resolver.QueryHandler;
import net.jxta.resolver.ResolverService;
public class ResolverStatus  implements QueryHandler {
	private PeerGroup stdChatGroup;
	private ResolverService resolver;
	private String handlerName = "ResolverStatus";
	private Vector<PresenceStatusListener> listeners;
	private XMLDocument cred;
	private Logger log = Logger.getLogger(ResolverStatus.class.getName());
	public ResolverStatus(PeerGroup pg)
	{
		stdChatGroup = pg;
		listeners = new Vector<PresenceStatusListener>();
		try {
			cred =  mkCreds(pg);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		init();
	}
	public void addResolverStatusListener(PresenceStatusListener o)
	{
		listeners.add(o);
	}
	public void removeResolverStatusListener(PresenceStatusListener o)
	{
		listeners.remove(o);
	}
	private XMLDocument mkCreds(PeerGroup grp)
	{
		XMLDocument doc= null;

		AuthenticationCredential authCred =
			new AuthenticationCredential( grp, null, null );

		// Get the MembershipService from the peer group
		MembershipService membership = grp.getMembershipService();

		// Get the Authenticator from the Authentication creds
		Authenticator auth = null;
		try {
			auth = membership.apply( authCred );
		} catch (PeerGroupException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProtocolNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Check if everything is okay to join the group
		if (auth.isReadyForJoin()){
			Credential myCred = null;
			try {
				myCred = membership.join(auth);
			} catch (PeerGroupException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				doc = (XMLDocument)
				myCred.getDocument(MimeMediaType.XMLUTF8);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return doc;
		}
		return doc;
	}
	private void init()
	{
		resolver =  stdChatGroup.getResolverService();
		resolver.registerHandler(handlerName, this);
	}
	
	public void sendMessage(String address)
	{
		XMLDocument doc = (XMLDocument) StructuredDocumentFactory.newStructuredDocument(MimeMediaType.XMLUTF8, "jxta:PresenceRequest");
		Attributable attr = (Attributable) doc;
		attr.addAttribute("xmlns:jxta", "http://jxta.org");
		Element item0 = doc.createElement("email",address);
		doc.appendChild(item0);
		
		StdChatGroup grp = StdChatGroup.getInstance();
		grp.fireEventStatus(new SearchEvent(this,"Preparing Resolver Query for: "+ address));
		StringWriter out = new StringWriter();
		try {
			doc.sendToWriter(out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ResolverQuery msg = new ResolverQuery();
		msg.setHandlerName(handlerName);
		msg.setHopCount(1);
		msg.setQueryId(stdChatGroup.getPeerID().getUniqueValue().hashCode());
		msg.setQuery(out.toString());
		msg.setSrc(stdChatGroup.getPeerID().toString());
		msg.setCredential(cred);
		log.info("Sending resover query = "+msg.getQuery());
		grp.fireEventStatus(new SearchEvent(this,"Sending Resolver Query for: "+ address));

		resolver.sendQuery(null, msg);
	}
	public int processQuery(ResolverQueryMsg query) {
		// TODO Auto-generated method stub
		String msg  = query.getQuery();
		log.info("Received a presence query from: " + query.getSrc());
		
		XMLDocument doc = null;
		try {
			doc = (XMLDocument) StructuredDocumentFactory.newStructuredDocument(MimeMediaType.XMLUTF8,
					new ByteArrayInputStream(msg.getBytes()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		boolean answer = false;
		if (doc!=null)
		{	
			Enumeration en = doc.getChildren();
			Status s = Status.getInstance();
			while ((en!=null) && en.hasMoreElements())
			{
				XMLElement item = (XMLElement) en.nextElement();
				if (item.getName().equals("email"))
				{

					String value = (String) item.getValue();
					answer = s.getEmailAddress().matches(value);
				//	answer =  value.equals(s.getEmailAddress());	
				}
			}
			// check i have to answer
			if (answer)
			{
				log.info("Answering the query!");
				PresenceAdvertisement adv = (PresenceAdvertisement) AdvertisementFactory.newAdvertisement(PresenceAdvertisement.getAdvertisementType());
				adv.setEmailAddress(s.getEmailAddress());
				adv.setName(s.getName());
				adv.setPeerID(s.getPeerID());
				adv.setPresenceStatus(s.getStatus());
				XMLDocument toSend = (XMLDocument) adv.getDocument(MimeMediaType.XMLUTF8);
				String msgToSend = toSend.toString();
				ResolverResponse message = new ResolverResponse();
				message.setHandlerName(handlerName);
				message.setQueryId(query.getQueryId());
				message.setResponse(msgToSend);
				message.setCredential(cred);
				log.info("Sent response!");
				resolver.sendResponse(query.getSrc(), message);
				return ResolverService.OK;
			} else {
				return ResolverService.Repropagate;
			}
			//resolver.sendResponse(query.getSrc(), response);
			//return ResolverService.OK;
		}
		return ResolverService.OK;
	}
	public void processResponse(ResolverResponseMsg response) {
		String doc  = response.getResponse();
		log.info("Got a query");
		StdChatGroup grp = StdChatGroup.getInstance();
		grp.fireEventStatus(new SearchEvent(this,"Got a Resolver Answer for what i seeked"));
		ByteArrayInputStream in = new ByteArrayInputStream(doc.getBytes());
		XMLDocument xmldoc = null;
		try {
			xmldoc = (XMLDocument) StructuredDocumentFactory.newStructuredDocument(MimeMediaType.XMLUTF8, in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		XMLElement item = (XMLElement) xmldoc.getRoot();
		PresenceAdvertisement adv = (PresenceAdvertisement) AdvertisementFactory.newAdvertisement(item);
		Status s = Status.getInstance();
		// make no sense that i answer to myself
		if (!(s.getEmailAddress().equals(adv.getEmailAddress())))
		{
			grp.fireEventStatus(new SearchEvent(this,"I'll update the table"));
			PresenceStatusEvent event = new PresenceStatusEvent();
			event.setAdvertisement(adv);
			for (PresenceStatusListener l : listeners)
				l.fireStatusUpdate(event);
		}
		else {	log.info("Got a query for myself. I know my address"); }

	}
}
