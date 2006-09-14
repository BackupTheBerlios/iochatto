/* FileName: it/di/unipi/iochatto/channel/PeerDiscovery.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.channel;

import it.di.unipi.iochatto.core.Status;
import it.di.unipi.iochatto.core.StatusEvent;
import it.di.unipi.iochatto.core.StdChatGroup;
import it.di.unipi.iochatto.presence.PresenceEvent;
import it.di.unipi.iochatto.presence.PresenceListener;
import it.di.unipi.iochatto.presence.PresenceService;
import it.di.unipi.iochatto.presence.PresenceServiceImpl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Collections;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

import net.jxta.discovery.DiscoveryEvent;
import net.jxta.discovery.DiscoveryListener;
import net.jxta.discovery.DiscoveryService;
import net.jxta.document.Advertisement;
import net.jxta.exception.ServiceNotFoundException;
import net.jxta.id.IDFactory;
import net.jxta.peergroup.PeerGroup;
import net.jxta.platform.ModuleClassID;
import net.jxta.protocol.DiscoveryResponseMsg;
import net.jxta.protocol.PeerAdvertisement;

public class PeerDiscovery implements Runnable,PresenceListener,DiscoveryListener {
	private PeerGroup pg = null;
	private DiscoveryService discovery = null;
	private String lock = "lock";
	private long TIME_OUT = 1*30*1000; // do discovery every 30 seconds
	private long TTL = 2*60*1000; // 30 sec
	private ConcurrentLinkedQueue<UserInfoListener> handlers= new ConcurrentLinkedQueue<UserInfoListener>();
	private Logger log = Logger.getLogger(PeerDiscovery.class.getName());
	private ConcurrentMap<String, UserInfo> map = new ConcurrentHashMap<String,UserInfo>();
	private ConcurrentMap<String, Long> expireCache = new ConcurrentHashMap<String,Long>();
	private PresenceService presence = null;
	//private ConcurrentLinkedQueue<UserInfo>  userToFire = new ConcurrentLinkedQueue<UserInfo>();
	public PeerDiscovery(PeerGroup mainpg)
	{
		pg = mainpg;
		discovery = pg.getDiscoveryService();
	}
	public synchronized void registerHandler(UserInfoListener l) {
		handlers.add(l);
	}
	public synchronized void removeHandlers()
	{
		for (UserInfoListener l: handlers)
		{
			handlers.remove(l);
		}
	}
	public void run() {
		
		StdChatGroup std = StdChatGroup.getInstance();
		ModuleClassID classID = null;
		try {
			classID = (ModuleClassID) IDFactory.fromURI(URI.create(StdChatGroup.mcID));
		} catch (URISyntaxException use){ log.info("Syntax Module ClassID ID Error");}
		try {
			PeerGroup Mpg = std.getPeerGroup();
			presence = (PresenceService) Mpg.lookupService(classID);
			
			PresenceServiceImpl pImpl = (PresenceServiceImpl) presence;
			pImpl.initResolver();
		} catch (ServiceNotFoundException e) {
			e.printStackTrace();
		}
		presence.addListener(this);
		discovery.addDiscoveryListener(this);
		while(true)
		{
			log.info("Searching Peers in the channel");
			std.fireEventStatus(new StatusEvent(this,"Searching peers for channel "+pg.getPeerGroupName()));
			discovery.getRemoteAdvertisements(null, DiscoveryService.PEER, null,null,  5);
			
			try {
				/*
				 Iterator<UserInfo> it = userToFire.iterator();
				 while ((it!=null) && (it.hasNext()))
				 {
				 UserInfo nfo = it.next();
				 userToFire.remove(nfo);
				 
				 }*/
				// collect old stuff
				Set<String> keySet = map.keySet();
				for (String s: keySet)
				{
					collect(s);
				}
				synchronized(lock)
				{
					lock.wait(TIME_OUT);
				}
	
			} catch (InterruptedException e) {
				Set<String> keySet = map.keySet();
				for (String s: keySet)
				{
					Object p = map.remove(s);
					Object k = expireCache.remove(s);
					p = null;
					k = null;
					removeHandlers();
								
				}
				pg = null;
				discovery.removeDiscoveryListener(this);
				presence.removeListener(this);
				discovery = null;
				presence = null;
				return;
			//	e.printStackTrace();
			}
			
		}
	}
	public void discoveryEvent(DiscoveryEvent event) {
		// TODO Auto-generated method stub
		DiscoveryResponseMsg res = event.getResponse();
		// now print out each discovered peer group
		Advertisement adv = null;
		Enumeration en = res.getAdvertisements();
		UserInfo info = null;
		if (en != null ) {
			while (en.hasMoreElements()) {
				adv = (Advertisement) en.nextElement();
				if (adv instanceof PeerAdvertisement)
				{
					
					info = new UserInfo();
					
					log.info("Got a peer Advertisement");
					
					PeerAdvertisement padv = (PeerAdvertisement) adv;
					//		log.info("PeerName: " + padv.getDescription());
					log.info("PeerEmail: "+ padv.getName());
					//		log.info("Peer Group ID = "+ padv.getPeerGroupID());
					//		log.info("Is in the map:"+map.containsKey(padv.getName()));
					
					if (!map.containsKey(padv.getName()))
					{	
						info.setAddress(padv.getName());
						info.setName(padv.getName());
						info.setPeerID(padv.getPeerID().toString());
						map.put(padv.getName(), info);
						if (presence!=null)
							presence.findPresence(padv.getName());
						
						
						//			collect(padv.getName());
					}
				}
				
			}
			
		} //end if
		// garbage collection
		
		
	}
	private synchronized boolean collect(String EmailAddress)
	{
		Status s = Status.getInstance();
		if (s.getEmailAddress().equals(EmailAddress))
				return true;
		if (expireCache.containsKey(EmailAddress))
		{
			// check ttl
			//Date advDate = expiredCache.get(pEv.getEmailAddress());
			//DateTime now = new DateTime(new Date());
			Long time = expireCache.get(EmailAddress);
			long diff = System.currentTimeMillis() - time.longValue();
			if (diff > TTL)
			{
				expireCache.remove(EmailAddress);
				map.remove(EmailAddress);
				return true;
			}
			
		} else {
			expireCache.put(EmailAddress, new Long(System.currentTimeMillis()));
			
		}
		return false;
	}
	private synchronized void fireUpdate(PresenceEvent ev)
	{
		if (map.containsKey(ev.getEmailAddress()))
		{
			
			
			log.info("Upcall to channel info for user: "+ ev.getEmailAddress());
			UserInfo test = map.get(ev.getEmailAddress());
			test.setStatus(ev.getStatus());
			for (UserInfoListener handler: handlers)
			{
				test.setName(ev.getName());
				handler.UserInfoUpdate(test);	
			}
			
		}
		
	}
	public void presenceUpdated(PresenceEvent ev) {
		// TODO Auto-generated method stub
		log.info("Presence Information Retrieved for: "+ev.getEmailAddress());
		log.info("User is in the map: "+map.containsKey(ev.getEmailAddress()));
		fireUpdate(ev);

		
	}
}