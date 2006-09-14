/* FileName: it/di/unipi/iochatto/channel/ChannelServiceImpl.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.channel;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.*;
import net.jxta.discovery.DiscoveryEvent;
import net.jxta.discovery.DiscoveryListener;
import net.jxta.discovery.DiscoveryService;
import net.jxta.document.Advertisement;
import net.jxta.document.AdvertisementFactory;
import net.jxta.exception.PeerGroupException;
import net.jxta.id.ID;
import net.jxta.peergroup.PeerGroup;
import net.jxta.protocol.DiscoveryResponseMsg;
import net.jxta.protocol.ModuleImplAdvertisement;
import net.jxta.protocol.PeerAdvertisement;
import net.jxta.protocol.PeerGroupAdvertisement;
import net.jxta.service.Service;
import it.di.unipi.iochatto.core.StdChatGroup;

public class ChannelServiceImpl implements ChannelService, DiscoveryListener {
	private DiscoveryService discovery;
	private HashMap<String,ArrayList> usermap = new HashMap<String,ArrayList>();
	private Logger log = Logger.getLogger(ChannelServiceImpl.class.getName());
	private Vector<ChannelServiceListener> infoListeners;
	private PeerGroup peerGroup;
	private ModuleImplAdvertisement implAdvertisement;
	private String localPeerID;
	public void addChannelServiceListener(ChannelServiceListener l)
	{ 
		infoListeners.add(l);
	}
	public void removeChannelServiceListener(ChannelServiceListener l)
	{ 
		infoListeners.remove(l);}
	/*
	private void fireInfo(ChanInfoEvent ev)
	{
		for (ChanInfoListener l: infoListeners)
		{
			l.fireInfo(ev);
		}
	}
	*/
	public void searchInfo(String channelName) {
		// TODO Auto-generated method stub
		/* Gli chiedo mediante le informazioni */                                                            
	}
	public ChannelServiceImpl()
	{
		super();
		StdChatGroup chat = StdChatGroup.getInstance();
		discovery = chat.getPeerGroup().getDiscoveryService(); 
	}

	public void searchGroups(int maxTries) {
		    try {
	            // Add ourselves as a DiscoveryListener for DiscoveryResponse events
	            discovery.addDiscoveryListener(this);
	            while (maxTries-->0) {
	                log.info("Sending a Discovery Message");
	                
	                
	                
	                discovery.getRemoteAdvertisements(null, DiscoveryService.GROUP,
	                                                  "Name", "#*", 5);
	                // wait 5 sec
	                try {
	                    Thread.sleep( 5 * 1000);
	                } catch(Exception e) {}

	            }
	        }
	        catch(Exception e) {
	            e.printStackTrace();
	        }
	    }

	    /**
	     * by implementing DiscoveryListener we must define this method
	     * to deal to discovery responses 
	     */

	    public void discoveryEvent(DiscoveryEvent ev) {

	        DiscoveryResponseMsg res = ev.getResponse();
	        String name = "unknown";

	        // Get the responding peer's advertisement
	        PeerAdvertisement peerAdv = res.getPeerAdvertisement();
	    
	        // some peers may not respond with their peerAdv
	        if (peerAdv != null) {
	            name = peerAdv.getName();
	        }
	        log.info (" Got a Discovery Response [" +
	                            res.getResponseCount()+ " elements]  from peer : " +
	                            name);
	        // now print out each discovered peer group
	        PeerGroupAdvertisement adv = null;
	        Enumeration en = res.getAdvertisements();

	        if (en != null ) {
	            while (en.hasMoreElements()) {
	                adv = (PeerGroupAdvertisement) en.nextElement();
	                log.info(" Peer Group = " + adv.getName());
	            }
	        }
	    }
		public Advertisement getImplAdvertisement() {
			// TODO Auto-generated method stub
			  return implAdvertisement;
		}
		public Service getInterface() {
			// TODO Auto-generated method stub
			return this;
		}
		public void init(PeerGroup group, ID assignedID, Advertisement implAdv) throws PeerGroupException {
		
	        // Save the module's implementation advertisement.
	        implAdvertisement = (ModuleImplAdvertisement) implAdv;

	        // Save a reference to the group of which that this service is 
	        // a part.
	        peerGroup = group;

	        // Get the local Peer ID.
	        localPeerID = group.getPeerID().toString();
	        
	        // Register the advertisement type.
	        // In some earlier versions of JXTA, registering the advertisement
	        // doesn't work properly. To work around this, you can instead 
	        // simply instantiate the advertisement implementation directly. 
	        // This is not the recommended way to get an advertisement.
	        // In this class, I've used the workaround in the discoveryEvent
	        // and accouncePresence methods, but I've provided the 
	        // as well.*/
	        AdvertisementFactory.registerAdvertisementInstance(
	            ChannelAdvertisement.getAdvertisementType(),
	                new ChannelAdv.Instantiator());
	
	    }

		public int startApp(String[] args) {
			// TODO Auto-generated method stub
			return 0;
		}
		public void stopApp() {
			// TODO Auto-generated method stub
			if (discovery != null)
	        {
	            // Unregister ourselves as a listener.
	            //discovery.removeDiscoveryListener(this);
	            discovery = null;
	            
	            // Empty the set of listeners.
	         //   registeredListeners.removeAllElements();
	        }
			
		}

	

}
