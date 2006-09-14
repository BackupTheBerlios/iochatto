/* FileName: it/di/unipi/iochatto/presence/PresenceServiceImpl.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.presence;



import it.di.unipi.iochatto.core.Status;
import it.di.unipi.iochatto.core.StdChatGroup;
import it.di.unipi.iochatto.presence.resolver.*;
import it.di.unipi.iochatto.util.DateTime;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;


import net.jxta.discovery.DiscoveryEvent;
import net.jxta.discovery.DiscoveryListener;
import net.jxta.discovery.DiscoveryService;

import net.jxta.document.Advertisement;
import net.jxta.document.AdvertisementFactory;
import net.jxta.document.MimeMediaType;

import net.jxta.endpoint.Message;
import net.jxta.endpoint.StringMessageElement;
import net.jxta.exception.PeerGroupException;
import net.jxta.exception.ServiceNotFoundException;

import net.jxta.id.ID;

import net.jxta.impl.protocol.DiscoveryResponse;

import net.jxta.peergroup.PeerGroup;
import net.jxta.pipe.PipeService;

import net.jxta.protocol.ModuleImplAdvertisement;

import net.jxta.resolver.ResolverService;
import net.jxta.service.Service;
import java.util.logging.*;
import it.di.unipi.iochatto.presence.resolver.ResolverStatus;


/**
 * The implementation of the PresenceService interface. This service
 * builds on top of the Discovery service to provide the functionality 
 * for requesting and providing presence information.
 */
public class PresenceServiceImpl implements PresenceService, PresenceStatusListener, WatchDogListener
{
	private Logger log =  Logger.getLogger(PresenceServiceImpl.class.getName());
    /**
     * The Module Specification ID for the Presence service.
     */
    public static final String refModuleSpecID = "urn:jxta:uuid-E695B6712268421E900AB1076706F16B3A9755535C7F4FC08500B124403B";
    
    /**
     * The default expiration timeout for published presence advertisements.
     * Set to 1 minute.
     */
    private Thread t1;
    private Thread t2;
    private static final int DEFAULT_EXPIRATION = 1000 * 60 * 1;
    private final int TTL = 60*1000*1;
    /**
     * The default lifetime for published presence advertisements.
     * Set to 1 minutes.
     */
    private boolean synFound = false;
    private static final int DEFAULT_LIFETIME = 1000 * 60 * 5;
    
    /**
     * The element name for the presence advertisement's email address info.
     */

    /**
     * The Discovery service used to publish presence information.
     */
    private DiscoveryService discovery = null;

    /**
     * The Module Implementation advertisement for this service.
     */
    private Advertisement implAdvertisement = null;
    private Vector<PresenceListener> registeredListeners = new Vector<PresenceListener>();
    private String checkPresenceLock = "Locked";
    /**
     * The local Peer ID.
     */
    private String localPeerID = null;
    
    /**
     * The peer group to which the service belongs.
     */
    private PeerGroup peerGroup = null;

    /**
     * A unique query ID that can be used to track a query.
     */
    private int queryID = 0;
    private PipeService pipeService;
   
    private WatchDog watchdog = null;
    private Pinger pinger;
    private HashMap<String,Integer> cache;
    private HashMap<String, Long > expiredCache;
    private ResolverService res = null;
    private ResolverStatus resolverStatus;
    /**
     * The set of listener objects registered with the service.



    /**
     * PresenceServiceImpl constructor comment.
     */
    
    public PresenceServiceImpl()
    {
        super();
        cache = new HashMap<String,Integer>();
        expiredCache = new HashMap<String,Long>();
        
    }
    private void initResolver(PeerGroup pg)
    {
    	int k = 0;
    
    	do {
    		res = pg.getResolverService();
    		try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			++k;
    	} while((res == null) && (k < 10));
    	if (res == null)
    		log.severe("Resolver not started");
    }
    public synchronized void addListener(PresenceListener listener)
    {
        registeredListeners.addElement(listener);
    }


    /**
     * Add a listener object to the service. When a new Presence Response 
     * Message arrives, the service will notify each registered listener.
     * This method is synchronized to prevent multiple threads from 
     * altering the set of registered listeners simultaneously.
     *
     * @param   listener the listener object to register with the service.
     */
    
    /**
     * Announce presence status information to the peer group.
     *
     * @param   presenceStatus the current status to announce.
     * @param   emailAddress the user's email address.
     * @param   name the user's display name.
     */
    public boolean announcePresence(int presenceStatus, String emailAddress, String name, String peerID)
    { // devo cambiare lo stato al pinger
    	Status s = Status.getInstance();
    	s.setStatus(presenceStatus);
    	PresenceAdvertisement presenceInfo = (PresenceAdvertisement) AdvertisementFactory.newAdvertisement(PresenceAdvertisement.getAdvertisementType());
    	presenceInfo.setEmailAddress(emailAddress);
    	presenceInfo.setName(name);
    	presenceInfo.setPeerID(peerID);
    	presenceInfo.setPresenceStatus(presenceStatus);
    	pinger.setAdvertisement(presenceInfo);
    	return true;
    }

      /**
     * Sends a query to find presence information for the user specified
     * by the given email address. Any response received by the service 
     * will be dispatched to registered PresenceListener objects.
     *
     * @param   emailAddress the email address to use to find presence info.
     */
    public void findPresence(String emailAddress)
    {
    	resolverStatus.sendMessage(emailAddress);
    }

    /**
     * Returns the advertisement for this service. In this case, this is 
     * the ModuleImplAdvertisement passed in when the service was 
     * initialized.
     *
     * @return  the advertisement describing this service.
     */
    public Advertisement getImplAdvertisement()
    {
        return implAdvertisement;
    }

    /**
     * Returns an interface used to protect this service.
     *
     * @return  the wrapper object to use to manipulate this service.
     */
    public Service getInterface()
    {
        // We don't really need to provide an interface object to protect
        // this service, so this method simply returns the service itself.
        return this;
    }

    /**
     * Initialize the service.
     *
     * @param       group the PeerGroup containing this service.
     * @param       assignedID the identifier for this service.
     * @param       implAdv the advertisement specifying this service.
     * @exception   PeerGroupException is not thrown ever by this
     *              implementation.
     */
    public void init(PeerGroup group, ID assignedID, Advertisement implAdv)
        throws PeerGroupException
    {
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
            PresenceAdvertisement.getAdvertisementType(),
                new PresenceAdv.Instantiator());
       pipeService = this.peerGroup.getPipeService();
    }

    
    /**
     * Start the service.
     *
     * @param   args the arguments to the service. Not used.
     * @return  0 to indicate the service started.
     */
    
    public int startApp(String[] args)
    {
        // Now that the service is being started, set the DiscoveryService
        // object to use to publish presence information.
        discovery = peerGroup.getDiscoveryService();
        StdChatGroup std = StdChatGroup.getInstance();
      
        watchdog = new WatchDog(std.getPeerGroup());
        watchdog.addWatchDogListener(this);
        pinger = new Pinger(std.getPeerGroup());
        
         t1 = new Thread(watchdog);
         t2 = new Thread(pinger);
        t1.setDaemon(true);
        t2.setDaemon(true);
        t1.start();
        t2.start();
       return 0;
    }
    /* This Presence implementation use the Resolver Service to request 
     *  a presence from a user or to check it in a synchronous way.
     *  So we need to init the resolver using a spinlock, because we don't 
     *  know when the CHAT group (and all its services) is really initialized.
     */
    public void initResolver() throws ServiceNotFoundException
    {
        PeerGroup mainGroup= null;
        do {
        StdChatGroup std = StdChatGroup.getInstance();
        mainGroup = std.getPeerGroup();
        try {
			Thread.sleep(10000);
			log.info("Resolver init spinlock group = "+ mainGroup);
		} catch (InterruptedException e) {
	
		 throw new ServiceNotFoundException();
		}	
        }while (mainGroup == null);
        log.info("Resolver init spinlock passed!");
        res = mainGroup.getResolverService();
        resolverStatus = new ResolverStatus(mainGroup);
        resolverStatus.addResolverStatusListener(this);
    }
    
    /**
     * Stop the service.
     */
    public void stopApp()
    {
    	
    	if (t1!=null)
    		t1.interrupt();
    	
    	if (t2!=null)
    		t2.interrupt();
    	t1 = null;
    	t2 = null;
    	if (discovery != null)
        {
            // Unregister ourselves as a listener.
            //discovery.removeDiscoveryListener(this);
            discovery = null;
            
            // Empty the set of listeners.
            registeredListeners.removeAllElements();
        }
    }


	public  void fireUpdate(WatchDogEvent ev) {
		// TODO Auto-generated method stub
		boolean fire = true;
		PresenceEvent pEv = new PresenceEvent();
		pEv.setAdvertisement(ev.getAdvertisement());
		if (cache.containsKey(pEv.getEmailAddress()) && !isExpired(pEv.getEmailAddress()))
				{
				Integer status = cache.get(pEv.getEmailAddress());
				int k = status.intValue();
				fire = (k != pEv.getStatus());
				}
		else {
			cache.put(pEv.getEmailAddress(), new Integer(pEv.getStatus()));
		}
		if (fire)
		{
		for (PresenceListener listener: registeredListeners)
		{
			if (listener!=null)
				listener.presenceUpdated(pEv);
			 
		}
		}
		
		
	}
	private boolean isExpired(String EmailAddress)
	{
		if (expiredCache.containsKey(EmailAddress))
		{
		// check ttl
			//Date advDate = expiredCache.get(pEv.getEmailAddress());
			//DateTime now = new DateTime(new Date());
			Long time = expiredCache.get(EmailAddress);
			long diff = System.currentTimeMillis() - time.longValue();
			if (diff > TTL)
			{
				expiredCache.remove(EmailAddress);
				cache.remove(EmailAddress);
				return true;
			}
			
		} else {
			expiredCache.put(EmailAddress, new Long(System.currentTimeMillis()));
						
		}
		return false;
	}

	
public boolean clearListener() {
		// TODO Auto-generated method stub
      
		Enumeration listeners = registeredListeners.elements();
        while (listeners.hasMoreElements())
        {
            PresenceListener listener = 
                (PresenceListener) listeners.nextElement();
            registeredListeners.remove(listener);
        }
		
		return false;
	}
	public boolean removeListener(PresenceListener listener) {
		// TODO Auto-generated method stub
		registeredListeners.remove(listener);
		return false;
	}
	public void fireStatusUpdate(PresenceStatusEvent ev) {
		// TODO Auto-generated method stub
		synchronized(checkPresenceLock)
		{
			synFound = true;
			checkPresenceLock.notifyAll();
		}
		log.info("Got a find presence resolver response!");
		log.info("Translating event");
		PresenceEvent newEvent = new PresenceEvent(ev.getAdvertisement());
		
		for (PresenceListener l: registeredListeners)
		{
			// log.info ("Objstr"+l.toString());
			l.presenceUpdated(newEvent);
		}
	}
	public boolean checkPresence(String emailAddress) {
		boolean ok = false;
		synchronized (checkPresenceLock) {
			synFound = false;
			resolverStatus.sendMessage(emailAddress);
			try {
				checkPresenceLock.wait(20000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	     if(synFound)
	     {
	    	 ok = synFound;
	     }
		}
	
		return ok;
	}
	public void findPeerID(String PeerID) {
		// TODO Auto-generated method stub
		
	}
	

}
