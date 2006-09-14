/* FileName: it/di/unipi/iochatto/presence/MainPresence.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.presence;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import it.di.unipi.iochatto.core.Status;
import it.di.unipi.iochatto.core.StdChatGroup;
import it.di.unipi.iochatto.core.StdChatGroup.StdChatException;
import it.di.unipi.iochatto.presence.resolver.ResolverStatus;
import gnu.getopt.Getopt;
import net.jxta.exception.PeerGroupException;
import net.jxta.exception.ServiceNotFoundException;
import net.jxta.id.IDFactory;
import net.jxta.peer.PeerID;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.platform.ModuleClassID;
import net.jxta.rendezvous.RendezvousEvent;
import net.jxta.rendezvous.RendezvousListener;
import net.jxta.resolver.ResolverService;

import org.apache.log4j.Level;

public class MainPresence implements PresenceListener {
    private PeerID pid;
    private StdChatGroup stdGroup;
    private PresenceService presence;
    private static Logger log = Logger.getLogger(MainPresence.class.getName());
	private int[] pArray = {PresenceService.OFFLINE, PresenceService.ONLINE, PresenceService.AWAY, PresenceService.BUSY };
;
	private static String address ="nobody@penguin.it";	
	private static String addressFound = "zoppi@penguin.it";
	private String[] pStatus = new String[4];
    /**
	 * @param args
	 */
    public MainPresence()
    {
      pStatus[PresenceService.OFFLINE] = "User OnLine";
      pStatus[PresenceService.ONLINE] = "User OffLine";
      pStatus[PresenceService.AWAY] = "User Away";
      pStatus[PresenceService.BUSY] = "User Busy";
      
      pid = IDFactory.newPeerID(PeerGroupID.defaultNetPeerGroupID);
      stdGroup = StdChatGroup.getInstance();
      
    }
    private void lookupService()
    {
    	ModuleClassID classID = null;
    	try {
			classID = (ModuleClassID) IDFactory.fromURI(URI.create(StdChatGroup.mcID));
		} catch (URISyntaxException use){ log.info("Syntax Module ClassID ID Error");}
    	try {
    		PeerGroup pg = stdGroup.getPeerGroup();
			presence = (PresenceService) pg.lookupService(classID);
			presence.addListener(this);
			PresenceServiceImpl pImpl = (PresenceServiceImpl) presence;
			pImpl.initResolver();
		
			//
		} catch (ServiceNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    private void initPresence(String user, String email)
    {
    	String peerID =pid.getUniqueValue().toString();
    	presence.announcePresence(pArray[0],user, email, peerID);
    	Status s = Status.getInstance();
    	s.setEmailAddress(email);
    	s.setName(user);
    	s.setPeerID(peerID);
    	s.setStatus(pArray[0]);
    }
    private void checkPresence(String mail)
    {
    	if (presence!=null)
    	{
    		presence.findPresence(mail);
    	}
    }
    private void loop(String username, String email)
    {
    	int sz = pArray.length -1;
    	int k = 0;
    	while (true)
    	{
    		try {
				if (presence != null)
				{
				
					presence.announcePresence(pArray[k],email, username, pid.getUniqueValue().toString());
				}
				else {
					log.info("Failed to loead presence service");
				}
				Thread.sleep(10000);	
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			++k;
			k = (k==sz) ?  0:  k;
		}
    	
    }
    private void init(String username, String password, String email, int minPort, int maxPort, boolean RDV)
    {
    	stdGroup.startPlatform(pid, username, password,email ,minPort, maxPort, RDV, Level.ERROR);
    }
    private void connect(long timeout)
    {
    	// mi collego al rdv
    	stdGroup.waitForRendezvousConn(timeout);
    }
    private void join(String address)
    {
    	
    	// per fare il join devo
    	try {
			if (stdGroup.groupExist() == null)
			{
				try {
					stdGroup.createPeerGroup(WatchDog.PIPEIDSTR);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					log.severe("I cannot create standard chat group! Bye...");
					e.printStackTrace();
					System.exit(1);
					
				}
			}
			try {
				stdGroup.join(address);
			} catch (StdChatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(1);
			}
		} catch (PeerGroupException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.severe("I cannot create standard chat group! Bye...");
			System.exit(0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (StdChatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int startPort = 9701;
		 int endPort = 9799;
		 Getopt g = new Getopt("MainPresence", args, "a:b:e:ru:");
		 boolean RDV = false;
		 int c = 0;
		 String arg = null;
		 String username="nobody";
		
		 MainPresence mainApp = new MainPresence();
		 while ((c = g.getopt()) != -1)
		   {
			 switch(c) {
		     	case 'a':
		     	{
		    	 arg = g.getOptarg();
		    	 if (arg!=null)
		    		 address = arg;
		    	 break;
		     	}
		     case 'u':
		     	{
		    	 arg = g.getOptarg();
		    	 if (arg!=null)
				    	username = arg;
		    	 break;
		     	}
		    case 'r':
		          {
		        	 RDV = true;
		        	 break;
		          }
		    case 'b':
		          {
		        	arg = g.getOptarg();
			        if (arg!=null)
					   startPort = Integer.parseInt(arg);
		          }
		          case 'e':
		          {
		        arg = g.getOptarg();
			    	 if (arg!=null)
					    	endPort = Integer.parseInt(arg);
		        	break;
		          }
		          } // end switch
	} //end while
		// inizializzo la configurazione e la piattagform
		 mainApp.init(username, "pipposki",address,startPort, endPort, RDV);
		 // mi collego al RDV Peer.
		 mainApp.connect(20000);
		 // faccio il join allo StdChatGroip
		 mainApp.join(address);
		 mainApp.lookupService();
		 mainApp.initPresence(username, address);
		 while (true) {
			 mainApp.checkPresence(addressFound);
			 try {
				log.info("Seeking Presence by Resolver: "+address);
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 }
		 //mainApp.loop(username,address);
	}
	public void presenceUpdated(PresenceEvent ev) {
		// TODO Auto-generated method stub
		log.info("User found: "+ ev.getName());
		log.info("Email Address: "+ ev.getEmailAddress());
		log.info("Status: "+ ev.getStatus());
		if (!(ev.getEmailAddress().equals(address)))
		{
			addressFound = ev.getEmailAddress();
		}
	}
	public void rendezvousEvent(RendezvousEvent arg0) {
		// TODO Auto-generated method stub
		log.info(arg0.toString());
	}

}
