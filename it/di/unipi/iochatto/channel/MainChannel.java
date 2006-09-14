/* FileName: it/di/unipi/iochatto/channel/MainChannel.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.channel;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import it.di.unipi.iochatto.channel.message.*;

import it.di.unipi.iochatto.core.Status;
import it.di.unipi.iochatto.core.StdChatGroup;
import it.di.unipi.iochatto.core.StdChatGroup.StdChatException;
import it.di.unipi.iochatto.presence.PresenceEvent;
import it.di.unipi.iochatto.presence.PresenceListener;
import it.di.unipi.iochatto.presence.PresenceService;
import it.di.unipi.iochatto.presence.PresenceServiceImpl;
import it.di.unipi.iochatto.presence.WatchDog;
import it.di.unipi.iochatto.presence.resolver.ResolverStatus;
import gnu.getopt.Getopt;
import net.jxta.discovery.DiscoveryEvent;
import net.jxta.discovery.DiscoveryListener;
import net.jxta.discovery.DiscoveryService;
import net.jxta.document.Advertisement;
import net.jxta.exception.PeerGroupException;
import net.jxta.exception.ServiceNotFoundException;
import net.jxta.id.IDFactory;
import net.jxta.impl.protocol.PeerGroupAdv;
import net.jxta.peer.PeerID;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.platform.ModuleClassID;
import net.jxta.protocol.DiscoveryResponseMsg;
import net.jxta.protocol.PeerAdvertisement;
import net.jxta.protocol.PeerGroupAdvertisement;
import net.jxta.rendezvous.RendezvousEvent;
import net.jxta.rendezvous.RendezvousListener;
import net.jxta.resolver.ResolverService;

import org.apache.log4j.Level;

public class MainChannel implements ChannelMessageEventListener {
    private static PeerID pid;
    private StdChatGroup stdGroup;
    private PeerGroup testGroup;
    private PresenceService presence;
    private static Logger log = Logger.getLogger(MainChannel.class.getName());
	private int[] pArray = {PresenceService.OFFLINE, PresenceService.ONLINE, PresenceService.AWAY, PresenceService.BUSY };
	private static String address ="nobody@penguin.it";	
	private static String addressFound = "zoppi@penguin.it";
	private String[] pStatus = new String[4];
    /**
	 * @param args
	 */
    public MainChannel()
    {
    //  pStatus[PresenceService.OFFLINE] = "User OnLine";
     // pStatus[PresenceService.ONLINE] = "User OffLine";
     // pStatus[PresenceService.AWAY] = "User Away";
     // pStatus[PresenceService.BUSY] = "User Busy";
      
      pid = IDFactory.newPeerID(PeerGroupID.defaultNetPeerGroupID);
      stdGroup = StdChatGroup.getInstance();
      
    }
    public Channel mkChannel(String name, String username, String address)
    {
    	UserInfo info = new UserInfo();
		info.setAddress("daniele@pippo.it");
		info.setName("dany");
		info.setPeerID(this.pid.toString());
		Channel test = null;
		try {
			test = ChannelFactory.newChannel("#italia", info, "campioni!");
			test.join(username,address,pid.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.info("Eccezzione!!!!!!!!!!!!");
			System.exit(1);
		}
		return test;
    }
    public void mkTestChannels()
    {
    	String chName="Channel #";
    	String founder =" Name #";
    	String topic = "Topic #"; 
    	Channel[] in = new Channel[10];
    	for (int k= 0; k < 10; ++k)
    	{
    		UserInfo info = new UserInfo();
    		info.setAddress(founder+k);
    		info.setName(founder+k);
    		info.setPeerID(founder+k);
    		try {
				Channel test = ChannelFactory.newChannel(chName, info, topic);
				in[k] = test;
				test.join("jo", "jo@penguin.it", pid.getUniqueValue().toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
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
			//presence.addListener(this);
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
    	presence.announcePresence(pArray[0],email, user, peerID);
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
    private void init(String username,String password,String email, int minPort, int maxPort, boolean RDV)
    {
    	stdGroup.startPlatform(pid, username, password, email, minPort, maxPort, RDV, Level.ERROR);
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
		
		 MainChannel mainApp = new MainChannel();
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
		 mainApp.init(username, "pipposki", address ,startPort, endPort, RDV);
		 mainApp.connect(20000);
		 // faccio il join allo StdChatGroip
		 mainApp.join(address);
		 mainApp.lookupService();
		 mainApp.initPresence(username, address);
		 Channel test = mainApp.mkChannel("#italia", username,address);
		 ChannelPoller poller = new ChannelPoller();
		 poller.addChannel(test);
		 Thread t = new Thread(poller);
		 t.setDaemon(true);

		 t.start();
		try {
		test.sendMessage(username, address,pid.toString() , "Ciao!");
		} catch (Throwable e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
			
		 while(true)
		 {
		//	try {
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
		//	} catch (IOException e) {
		//		// TODO Auto-generated catch block
		//		e.printStackTrace();
		//	} 
		 }
	}
	
	public void rendezvousEvent(RendezvousEvent arg0) {
		// TODO Auto-generated method stub
		log.info(arg0.toString());
	}
	public void discoveryEvent(DiscoveryEvent event) {
		// TODO Auto-generated method stub
		
		log.info("Got a discovery event");
		DiscoveryResponseMsg res = event.getResponse();
                // now print out each discovered peer group
        Advertisement adv = null;
        Enumeration en = res.getAdvertisements();

        if (en != null ) {
            while (en.hasMoreElements()) {
                adv = (Advertisement) en.nextElement();
                if (adv instanceof PeerAdvertisement)
                {
                	log.info("Got a peer Advertisement");
                	
                	PeerAdvertisement padv = (PeerAdvertisement) adv;
                	log.info("PeerName: " + padv.getDescription());
                	log.info("PeerEmail: "+ padv.getName());
                	log.info("Peer Group ID = "+ padv.getPeerGroupID());
                	log.info("Italia Group ID = "+testGroup.getPeerGroupID());
                	PeerGroupID pag = testGroup.getPeerGroupID();
                	if (pag.equals(padv.getPeerGroupID()))
                	{
                		log.info("Italia User");
                	}
                }
            }
        }
    }
	public void MessageEvent(ChannelMessageEvent ev) {
		// TODO Auto-generated method stub
		System.out.println("Got a message:"+ev.getMessage().toString());
	}


}
