/* FileName: it/di/unipi/iochatto/core/StdChatGroup.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.core;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownServiceException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import it.di.unipi.iochatto.channel.message.ChannelMessageEvent;
import it.di.unipi.iochatto.channel.message.ChannelMessageEventListener;
import it.di.unipi.iochatto.presence.WatchDog;
import it.di.unipi.iochatto.util.DateTime;
import org.apache.log4j.Level;

import contrib.rendezvous.ConfigurationFactory;

import net.jxta.credential.AuthenticationCredential;
import net.jxta.discovery.DiscoveryService;
import net.jxta.document.Advertisement;
import net.jxta.document.AdvertisementFactory;
import net.jxta.document.Attributable;
import net.jxta.document.Element;
import net.jxta.document.MimeMediaType;
import net.jxta.document.StructuredDocument;
import net.jxta.document.StructuredDocumentFactory;
import net.jxta.document.StructuredDocumentUtils;
import net.jxta.document.XMLDocument;
import net.jxta.exception.PeerGroupException;
import net.jxta.id.IDFactory;
import net.jxta.impl.membership.none.NoneMembershipService;
import net.jxta.impl.peergroup.StdPeerGroup;
import net.jxta.impl.peergroup.StdPeerGroupParamAdv;
import net.jxta.impl.protocol.PeerGroupAdv;
import net.jxta.membership.Authenticator;
import net.jxta.membership.MembershipService;
import net.jxta.peer.PeerID;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupFactory;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.platform.ModuleClassID;
import net.jxta.platform.ModuleSpecID;
import net.jxta.protocol.ModuleClassAdvertisement;
import net.jxta.protocol.ModuleImplAdvertisement;
import net.jxta.protocol.ModuleSpecAdvertisement;
import net.jxta.protocol.PeerGroupAdvertisement;
import net.jxta.rendezvous.RendezVousService;
import net.jxta.rendezvous.RendezvousEvent;
import net.jxta.rendezvous.RendezvousListener;
import it.di.unipi.iochatto.util.AdvBuilder;
public class StdChatGroup implements RendezvousListener{
	private String rdvLock = "RDV_LOCK";
	public class StdChatException extends Exception {
		/**
		 * 
		 */
		private static final long serialVersionUID = 138389L;

		public StdChatException(){
			super("Invalid Group Init Sequence");
		}
	}
	private final String groupName = "CHAT";
	/**
	 * The Module Specification ID for the peer group's Module 
	 * Implementation Advertisement.
	 */
	private final int GROUP_SEARCH_TRIES = 30;
	public static final String refPeerGroupSpec ="urn:jxta:uuid-DEADBEEFDEAFBABAFEEDBABE000000011A5C2F6C5E1F4AFAADE98FD2387A2DCC06"; 

	public static final String refPeerGroupID = 
		"urn:jxta:uuid-B201EEBB14E64AF68463FFE3DBD1CC7102";
	/* module class ID for presence service */
	public static final String mcID = "urn:jxta:uuid-DBAFCADBF42D4F49AC4CC56C3CA2836005";
	/* module specification ID for presence service */
	public static final String msID = "urn:jxta:uuid-DBAFCADBF42D4F49AC4CC56C3CA2836065F33B17DC1C4096839092548ED8E03B06";
	private ModuleClassID classID = null;
	private ModuleSpecID specID = null;
	/* the reference to the netPeerGroup */
	private PeerGroup netPeerGroup = null;
	private RendezVousService rdv;
	/* the mainGrop */
	private PeerGroup chatGroup = null;
	private PeerID pid = null;
	private PeerGroupID pgid = null;
	private static Logger log = Logger.getLogger(StdChatGroup.class.getName());
	private File  home = null;
	private FileHandler logHandle = null;
	private boolean[] states = {false,false,false};
	private static StdChatGroup instance;
	private long  RDV_TIMEOUT = 1000*60*3; // TODO implement a way to user defined timeout
	private boolean rdvFlag = false;
	private MembershipService ms = null;
	private Vector<StatusEventListener> status = new Vector<StatusEventListener>();
	private StdChatGroup()
	{
		this.netPeerGroup = null;
		this.initConfigDir();
		try {
			classID = (ModuleClassID) IDFactory.fromURI(URI.create(StdChatGroup.mcID));
		} catch (URISyntaxException use){ log.info("Syntax Module ClassID ID Error");}
		try {
			specID = (ModuleSpecID) IDFactory.fromURI(URI.create(StdChatGroup.msID));
		} catch (URISyntaxException use){ log.info("Syntax Module SpecID ID Error");}

	}
	public synchronized void addListener(StatusEventListener l)
	{
		status.add(l);
	}
	public synchronized void removeListener(StatusEventListener l)
	{
		status.remove(l);
	}
	public synchronized void fireEventStatus(StatusEvent ev)
	{
			for (StatusEventListener l: status)
				l.fireEvent(ev);
	}

	public static StdChatGroup getInstance()
	{
		if (instance == null)
		{
			synchronized(StdChatGroup.class)
			{
				if (instance == null)
				{
					instance = new StdChatGroup();
				}
			}
		}
		return instance;
	}
	public String getPath()
	{
		if (home!=null)
				return home.getAbsolutePath();
		return null;
	}
	 public static boolean deleteDir(File dir) {
	        if (dir.isDirectory()) {
	            String[] children = dir.list();
	            for (int i=0; i<children.length; i++) {
	                boolean success = deleteDir(new File(dir, children[i]));
	                if (!success) {
	                    return false;
	                }
	            }
	        }
	    
	     
	        return dir.delete();
	    }
	public PeerGroup getPeerGroup()
	{
		return chatGroup;
	}
	public PeerGroupID getPeerGroupID()
	{
		if (pgid == null) {
			try {
				pgid = (PeerGroupID) IDFactory.fromURI(URI.create(StdChatGroup.refPeerGroupID));
			} catch (URISyntaxException use){ log.info("Syntax PeerGroup ID Error");}
		}
		return pgid;
	}
	public void join(String identity) throws StdChatGroup.StdChatException
	{
		PeerGroup ng = chatGroup;
		/*	if (!states[1])
		{
			throw new StdChatGroup.StdChatException();
		}
		 */
		for (int k = 0 ; k < states.length; ++k)
			states[k] = false;
		try {
			AuthenticationCredential authCred = new AuthenticationCredential( ng,null,null);

			// Get the MembershipService from the peer group
			String debugMsg = "Joining "+ng.getPeerGroupName()+ " with identity = "+ identity;
			ms = ng.getMembershipService();
			MembershipService membership = ng.getMembershipService();
			fireEventStatus(new StatusEvent(this,debugMsg));
			
			if (membership == null)
				log.info("Membership Service is empty or not started");
			Authenticator auth = membership.apply( authCred );
			NoneMembershipService.NoneAuthenticator infoAuth = (NoneMembershipService.NoneAuthenticator) auth;
			infoAuth.setAuth1Identity(identity); 
			// Check if everything is okay to join the group
			if (auth.isReadyForJoin()){
				membership.join(auth);

	//			debugMsg = "Successfully joined group " + ng.getPeerGroupName();
	//			fireEventStatus(new StatusEvent(this,debugMsg));
				
			} else
				debugMsg = "Failure: unable to join group";
				fireEventStatus(new StatusEvent(this,debugMsg));
		} catch (Exception e1){
			log.info("Failure in authentication.");
			e1.printStackTrace();
			System.exit(1);

		}
	}
	private void initConfigDir()
	{
		File currentPath = new File(".");
		// if i'am on *nix
		String homePath = System.getProperty("HOME");
		if ((homePath != null) && !homePath.trim().equals(""))
		{
			currentPath = new File(homePath); 
			// good one
		}			
		else {
			// bad one
			System.setProperty("HOME", currentPath.getAbsolutePath());
		}
		home = new File(currentPath.getAbsolutePath()+File.separatorChar+"iochatto");
		if (!home.exists()){
			home.mkdirs();
		}
		File handle = new File(home.getAbsolutePath()+File.separatorChar+"iochatto.log");
		try {
			logHandle = new FileHandler(handle.getAbsolutePath());
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		//Formatter. Seek on the Net.
		//logHandle.setFormatter(newFormatter)
		log.addHandler(logHandle);
		String pathHome = home.getAbsolutePath();
		String debugMsg = "Setting config path = " + pathHome;
		fireEventStatus(new StatusEvent(this,debugMsg));
		log.info(debugMsg);
		ConfigurationFactory.setHome(home);

	}
	/* @param String username  Nome utente che usa la chat
	 * @param String password  password utente che usa la chat
	 * @param int minPort  porta che inizia il range da configurare
	 * @param int maxPort porta che finisce da confiurare
	 * @param boolean RDV l'host e' un rendezvous peer
	 * @param int level livello di debug;
	 * @param PeerID new PeerID
	 */
	public void startPlatform(PeerID pd, String username, String password, String email, int minPort, int maxPort, boolean RDV, Level level )
	{
		pid = pd;
		log.info("Setting PeerID = "+ pd.toString());
		log.info("Setting PeerName =" + email);
		String msg0 = "Setting PeerID = "+ pd.toString();
		String msg1 = "Setting PeerName =" + email;
		fireEventStatus(new StatusEvent(this,msg0));
		fireEventStatus(new StatusEvent(this,msg1));
		ConfigurationFactory.setPeerID(pid);
		ConfigurationFactory.setName(email);
		ConfigurationFactory.setPrincipal(username);
		ConfigurationFactory.setPassword(password);
		ConfigurationFactory.setDescription(username);
		ConfigurationFactory.setDebugLevel(level);
		ConfigurationFactory.setTCPPortRange(minPort, maxPort);
		if (RDV)
		{
			ConfigurationFactory.setMode(ConfigurationFactory.RDV);

		}
		try
		{
			ConfigurationFactory.setRdvSeedingURI(new URI("http://rdv.jxtahosts.net/cgi-bin/rendezvous.cgi?2"));
			ConfigurationFactory.setRelaySeedingURI(new URI("http://rdv.jxtahosts.net/cgi-bin/relays.cgi?2"));
		}
		catch(URISyntaxException use)
		{
			use.printStackTrace();
			log.info("URI Syntax Exception");
		}
		Advertisement config = ConfigurationFactory.newPlatformConfig();
		try
		{
			ConfigurationFactory.save(config, true);
			log.info("Configuration Saved!");
		}
		catch(IOException io)
		{
			io.printStackTrace();
		}
		try
		{
			netPeerGroup = PeerGroupFactory.newNetPeerGroup();
		
			rdv = netPeerGroup.getRendezVousService();
		}
		catch(Exception pge)
		{
			pge.printStackTrace();
			System.exit(1);
		}
		if (RDV)
		{
			rdv.setAutoStart(true);
		}
	}

	public PeerGroup groupExist() throws IOException, PeerGroupException, StdChatGroup.StdChatException
	{

		DiscoveryService disco = netPeerGroup.getDiscoveryService();
		Enumeration en = null;
		PeerGroup PG = null;
		String PGA = StdChatGroup.refPeerGroupID; 
		// i do at max 10 tries: 1 try every 2 seconds
		int k = 1;
		boolean found = false;
		PeerGroupAdv pga = null;
		Enumeration en2 = null;
		// state 0 : seeking the PeerGroupAdvertisement.
		boolean state2 = false;
		while ((k < GROUP_SEARCH_TRIES) && (!found))
		{
			en = disco.getLocalAdvertisements(DiscoveryService.GROUP,"Name" , groupName);
			en2 = disco.getLocalAdvertisements(DiscoveryService.ADV,"MSID" ,PGA);
			if ((en!=null)&&(en.hasMoreElements()))
			{
				Advertisement adv = (Advertisement) en.nextElement();
				if ((adv instanceof PeerGroupAdv) && (!found ))
				{
					//		pid.getPeerGroupID()
					log.info(adv.toString());
					pga = (PeerGroupAdv) adv;
					log.info("Found Group = "+ pga.getName());
					log.info("GroupPeer ID = "+pga.getPeerGroupID());
					PG = netPeerGroup.newGroup(pga.getPeerGroupID());
					//PG.init(netPeerGroup, pga.getPeerGroupID(), impl);
					PG.startApp(null);
					String msg0 = "Found Group = "+ pga.getName();
					String msg1 = "GroupPeer ID = "+pga.getPeerGroupID();
					fireEventStatus(new StatusEvent(this,msg0));
					fireEventStatus(new StatusEvent(this,msg1));
			//		disco.flushAdvertisement(adv);
					//log.info("PeerGroup Advertisement = "+pga.toString());
					//StdPeerGroup oldPG = new StdPeerGroup();
					//oldPG.init(netPeerGroup, pga.getPeerGroupID(), pga);
					//oldPG.
					//oldPG.publishGroup("CHAT", "Standard Chat Group");
					found = true;
				}
			} // endif2
			// state 1 : seeking group impl advertisement
			/*
			if ((found == true) && ((en2!=null)&&(en2.hasMoreElements())))
			{
				Advertisement adv0 = (Advertisement) en2.nextElement();	 
				if (adv0 instanceof ModuleImplAdvertisement)
				{
					ModuleImplAdvertisement impl = (ModuleImplAdvertisement) adv0;
				
				log.info(impl.toString());			
					if (impl.getModuleSpecID().toString().equals(PGA))
					{
						PG = netPeerGroup.newGroup(pga.getPeerGroupID(),impl,pga.getName(),pga.getDescription());
						chatGroup = PG;
						String evMsg = "Instantiated a Group Found ="+PG.getPeerGroupName();
						fireEventStatus(new StatusEvent(this,evMsg));
						state2 = true;
						//PG.startApp(null);
						return PG;
					}

				}
			}
*/
			try {
				/*
				 * per ricordarselo null --> broadcast
				 */
				String evMsg = "Searching Remotely a new group with name = "+ groupName+ " Try: "+k;
				fireEventStatus(new StatusEvent(this,evMsg));
				
				log.info("Searching Remotely a new group with name = "+ groupName+ " Try: "+k);
				disco.getRemoteAdvertisements(null, DiscoveryService.GROUP, "Name", groupName , 1);	
				disco.getRemoteAdvertisements(null,DiscoveryService.ADV,"MSID" ,PGA,1);

			} catch (Exception e){};
			++k;
			try { Thread.sleep(1000);} catch (Exception e) {};
		} // end while
		//states[1] = true;

		return null;
	}
	public void leave()
	{
		String msg0 = "Starting groups shutdown";
		fireEventStatus(new StatusEvent(this,msg0));
		/* sanity checks */
		if (chatGroup == null)
				return;
		if (ms == null)
			return;
		if (netPeerGroup == null)
				return;
		if (rdv == null)
				return;
		try {
		msg0 = "Leaving membership for CHAT Group";
			if (ms!=null)
			{
			ms.resign();
			fireEventStatus(new StatusEvent(this,msg0));
			}
		} catch (PeerGroupException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ms.stopApp();
		chatGroup.stopApp();
		chatGroup.unref();
		chatGroup = null;
		netPeerGroup.stopApp();
		netPeerGroup.unref();
		System.gc();
		msg0 = "All groups stopped!";
		if (rdv.isConnectedToRendezVous())
				rdv.disconnectFromRendezVous(pid);
		fireEventStatus(new StatusEvent(this,msg0));

		instance = null;
		netPeerGroup = null;
		chatGroup = null;
		if (home!=null)
			StdChatGroup.deleteDir(home);
		System.gc();
	}
	public void waitForRendezvousConn(long timeout) {
		rdv.addListener(this);
		if (rdv.isRendezVous())
		{
			String msg0 = "I am a rendevous peer!";
			fireEventStatus(new StatusEvent(this,msg0));
			
			log.info("I am a rendevous peer!");
			return;
		}
		else {
			String msg0 = "Host is not a rendevouz peer.. Searching one!";
			fireEventStatus(new StatusEvent(this,msg0));
			
			log.warning("Host is not a rendevouz peer.. Searching one!");
		}
		if (rdv.isConnectedToRendezVous())
		{
			String msg0 = "Found Rendezvous Peer :)";
			fireEventStatus(new StatusEvent(this,msg0));
		
			log.info("Rendevouz Peer Found!");
			rdvFlag = true;

			return;
		}
		log.severe("Rendevouz Not Found! Waiting..");
		if  (!rdv.isConnectedToRendezVous() && !rdv.isRendezVous())
		{
			log.info("Still waiting....");
			synchronized (rdvLock)
			{

				try {
					rdvLock.wait(RDV_TIMEOUT);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		rdvFlag = rdv.isConnectedToRendezVous();

		if (!rdvFlag)
		{ 
			log.severe("Rendevouz not found!");
			System.exit(1);

		}

		log.info("Going on . Status RendzVousConnection = " + rdv.isConnectedToRendezVous());
		states[0] = true;
	}
	public PeerGroup getNetPeerGroup() { return netPeerGroup; }
	public void rendezvousEvent(RendezvousEvent event) {
		// TODO Auto-generated method stub
		log.info("Got a rendevouz connection!");
		if ((RendezvousEvent.RDVRECONNECT == event.getType()) || (RendezvousEvent.RDVCONNECT == event.getType()))
		{
			if (!rdvFlag) {
				synchronized(rdvLock)
				{
					rdvLock.notifyAll();
				}
			}
			rdvFlag = true;
		}
	}

	private XMLDocument mkDoc(String pipeID)
	{

		XMLDocument doc = (XMLDocument) StructuredDocumentFactory.newStructuredDocument(MimeMediaType.XMLUTF8, "jxta:PresenceInfo");
		Attributable attr = (Attributable) doc;
		attr.addAttribute("xmlns:jxta", "http://jxta.org");
		DateTime dt = new DateTime(new Date());
		Element item0 = doc.createElement("timestamp",dt.toString());
		Element item1 = doc.createElement("pipeid",pipeID);
		doc.appendChild(item0);
		doc.appendChild(item1);
		return doc;
	}
	@SuppressWarnings("deprecation")
	public PeerGroup createPeerGroup(String pipeURI) throws Exception
	{
		String description = "Standard Chat Group";

		// The Discovery service to use to publish the module and peer 
		// group advertisements.
		DiscoveryService discovery = netPeerGroup.getDiscoveryService();
		AdvBuilder build = AdvBuilder.getInstance();

		ModuleImplAdvertisement implAdv =
			netPeerGroup.getAllPurposePeerGroupImplAdvertisement();

		// Create the module advertisements for the Presence service.
		ModuleClassAdvertisement presenceClassAdv = build.createModuleClassAdv(
				mcID, "Presence Service", 
		"A service to provide presence information.");
		ModuleSpecAdvertisement presenceSpecAdv = build.createModuleSpecAdv(
				msID, "Presence Service", 
		"A Presence service specification");
		presenceSpecAdv.setPipeAdvertisement(WatchDog.getPipeAdvertisement());

		ModuleImplAdvertisement presenceImplAdv = build.createModuleImplAdv(
				implAdv, presenceSpecAdv, 
				"Reference Implementation of the Presence Service",
				"it.di.unipi.iochatto.presence."
				+ "PresenceServiceImpl");

		Element pEl = (Element) mkDoc(pipeURI);
		StructuredDocument svcParm =
			StructuredDocumentFactory.newStructuredDocument(
					MimeMediaType.XMLUTF8,
			"Parm");
		StructuredDocumentUtils.copyElements(svcParm, svcParm, pEl);
		presenceImplAdv.setParam(svcParm);

		// Get the parameters for the peer group's Module Implementation 
		// Advertisement to which we will add our service.
		StdPeerGroupParamAdv params = 
			new StdPeerGroupParamAdv(implAdv.getParam());

		// Get the services from the parameters.
		Map services = params.getServices();

		// Add the Chat and Presence services to the set of services.
		services.put(presenceClassAdv.getModuleClassID(), presenceImplAdv);

		// Set the services on the parameters.
		params.setServices(services);
		//no apps.
		Hashtable mp = new Hashtable();
		params.setApps(mp);

		XMLDocument doc = (XMLDocument) params.getDocument(new MimeMediaType("text","xml"));
		implAdv.setParam(doc);



		// VERY IMPORTANT! You must change the module specification ID for 
		// the implementation advertisement. If you don't, the new peer 
		// group's module specification ID will still point to the old 
		// specification, and the new service will not be loaded.
		implAdv.setModuleSpecID((ModuleSpecID) IDFactory.fromURI(
				new URI(refPeerGroupSpec)));
		// Publish the Presence module class and spec advertisements.
		discovery.publish(presenceClassAdv);
		discovery.remotePublish(presenceClassAdv, DiscoveryService.ADV);
		discovery.publish(presenceSpecAdv);
		discovery.remotePublish(presenceSpecAdv, DiscoveryService.ADV);


		// Publish the Peer Group implementation advertisement.
		discovery.publish(implAdv);
		discovery.remotePublish(implAdv, DiscoveryService.ADV);

		// Create the Peer Group ID.
		PeerGroupID groupID = (PeerGroupID) IDFactory.fromURI(
				new URI((refPeerGroupID)));;

				// Create the new group using the group ID, advertisement, name, 
				// and description.
				chatGroup = netPeerGroup.newGroup(groupID, implAdv, "CHAT",
						description);

				// Need to publish the group remotely only because newGroup()
				// handles publishing to the local peer.
				PeerGroupAdvertisement groupAdv = 
					chatGroup.getPeerGroupAdvertisement();

				discovery.publish(groupAdv);
				discovery.remotePublish(groupAdv, DiscoveryService.GROUP);

				// Start the peer group's applications.
				chatGroup.startApp(null);
				return chatGroup;
	}
}
