/* FileName: it/di/unipi/iochatto/channel/Channel.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it
*/
package it.di.unipi.iochatto.channel;
import it.di.unipi.iochatto.channel.message.ChannelInfoMessage;
import it.di.unipi.iochatto.channel.message.ChannelMessageEvent;
import it.di.unipi.iochatto.channel.message.ChannelMessageEventListener;
import it.di.unipi.iochatto.core.Status;
import it.di.unipi.iochatto.core.StatusEvent;
import it.di.unipi.iochatto.core.StdChatGroup;
import it.di.unipi.iochatto.presence.PresenceService;

import it.di.unipi.iochatto.util.AdvBuilder;
import it.di.unipi.iochatto.util.DateTime;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import org.jdom.JDOMException;

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
import net.jxta.endpoint.Message;
import net.jxta.endpoint.MessageElement;
import net.jxta.endpoint.StringMessageElement;
import net.jxta.endpoint.TextDocumentMessageElement;
import net.jxta.exception.PeerGroupException;
import net.jxta.id.ID;
import net.jxta.id.IDFactory;
import net.jxta.impl.membership.none.NoneMembershipService;
import net.jxta.impl.peergroup.StdPeerGroupParamAdv;
import net.jxta.impl.protocol.PeerGroupAdv;
import net.jxta.membership.Authenticator;
import net.jxta.membership.MembershipService;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.pipe.InputPipe;
import net.jxta.pipe.OutputPipe;
import net.jxta.pipe.PipeID;
import net.jxta.pipe.PipeService;
import net.jxta.platform.ModuleSpecID;
import net.jxta.protocol.ModuleClassAdvertisement;
import net.jxta.protocol.ModuleImplAdvertisement;
import net.jxta.protocol.ModuleSpecAdvertisement;
import net.jxta.protocol.PeerGroupAdvertisement;
import net.jxta.protocol.PipeAdvertisement;
import java.util.logging.*;
public class Channel  implements UserInfoListener {

	private ChannelInfo metadata = new ChannelInfo();
	private PipeID id;
	private InputPipe inPipe = null;
	private OutputPipe outPipe = null;
	private PeerGroup group;
	private PeerGroup parentGroup; 
	private PeerGroupID gid;
	private PipeAdvertisement pipeAdv;
	private ModuleSpecID groupSpecID;
	private Logger log; 
	private Map<String, UserInfo> users = new ConcurrentHashMap<String,UserInfo>();
	private Map<String, Long> expireCache = new ConcurrentHashMap<String,Long>();
	private MembershipService membership = null;
	private	PeerDiscovery userDiscovery = null;
	private Thread userDiscoveryThread = null;
	private DiscoveryService discovery = null;
	private boolean workpoolModel = false;
	public String getName() { return metadata.getName(); }
	public PipeAdvertisement getPipeAdv(){ return pipeAdv;}
	private Vector<ChannelMessageEventListener> listeners = new Vector<ChannelMessageEventListener>();
	protected Channel()
	{
	this.Init(false);
	}
	protected Channel(boolean workPoolModel)
	{
	this.Init(workPoolModel);
	}
	    /* togli l'utente dai dati utente relativi al canale,
	       e ricostruisci i metadati che rappresentano il canale:
	       l'oggetto ChannelInfo rappresenta tutti i dati del canale*/

    public synchronized void removeUser(String userAddress)
    {
	if ((users!=null) && (users.containsKey(userAddress)))
	{
	    users.remove(userAddress);
	    metadata.removeUser(userAddress);
	    metadata.buildXML();
	}
	    
    }
	private void Init(boolean workPoolModel)
	{
		StdChatGroup std = StdChatGroup.getInstance();
		parentGroup = std.getPeerGroup();
		groupSpecID = IDFactory.newModuleSpecID(
				PeerGroup.allPurposePeerGroupSpecID.getBaseClass());
		gid = IDFactory.newPeerGroupID();
		workpoolModel = workPoolModel;
	}
	    /* se il canale non esite fai lse seguenti fasi:
	       1) imposta la propagate pipe associata al canale
	       2) imposta tutti i metadati del canale
	       3) publica i metadati
	       se il canale esite...scopri la pipe e i metadati 
	       relativi al canale per instanziare il canale.
	    */
	protected void create(String channelName, String topic, UserInfo founder) throws Exception
	{
		if (channelName==null)
				return;
		if (topic == null)
				return;
		if (founder == null)
				return;
		log = Logger.getLogger(channelName);
		StdChatGroup std = StdChatGroup.getInstance();
		parentGroup = std.getPeerGroup();
		group = channelExists(channelName,parentGroup);
		if (group == null){
			id = IDFactory.newPipeID(gid);
			metadata.setPipeUri(id.toString());
			metadata.setChannelName(channelName);
			metadata.setTopic(topic);
			metadata.setFounderName(founder.getName());
			metadata.setFounderEmailAddress(founder.getAddress());
			metadata.setFounderPeerID(founder.getPeerID());
			metadata.buildXML();
			group = createPeerGroup(channelName,parentGroup);
			pipeAdv = mkPropagatePipeAdv(metadata.getName(), (ID) id);
			PipeService pipeService = group.getPipeService();
			outPipe = pipeService.createOutputPipe(pipeAdv, 30000);
			DiscoveryService disco = group.getDiscoveryService();
			disco.publish(pipeAdv);
		}
		else {
			discoverMetadata(group);
			id = (PipeID) IDFactory.fromURI(URI.create(metadata.getPipeUri()));
			pipeAdv = mkPropagatePipeAdv(metadata.getName(), (ID) id);
			
		}

		//log.info(metadata.toString());
		if (!workpoolModel)
		{
		userDiscovery = new PeerDiscovery(group);
		userDiscovery.registerHandler(this);
		}
		PipeService pipeService = group.getPipeService();
		outPipe = pipeService.createOutputPipe(pipeAdv, 30000);
		inPipe = pipeService.createInputPipe(pipeAdv);
		//DiscoveryService disco = group.getDiscoveryService();
		// disco.publish(pipeAdv);
	
		publishMetaData();
	}
	public void clear()
	{
		//String todo = "lock";
		group = null;
		id = null;
		if (userDiscovery!=null)
			userDiscovery.removeHandlers();
		
		userDiscovery = null;
		if (userDiscoveryThread!=null)
				userDiscoveryThread.interrupt();
		userDiscoveryThread = null;
		if (users!=null)
				users.clear();
		synchronized(listeners)
		{
		if (listeners!=null)
		{
			Enumeration<ChannelMessageEventListener> en = listeners.elements();
			while (en!=null && en.hasMoreElements())
			{
				ChannelMessageEventListener l = en.nextElement();
				listeners.remove(l);
			}
		}
		}
	
		pipeAdv = null;
		// TODO other cleanup when ended stuffz.
	}
	protected void instantiate(String channelName) throws ChannelNotFound, URISyntaxException
	{
		StdChatGroup std = StdChatGroup.getInstance();
		parentGroup = std.getPeerGroup();
		try {
		try {
			group = channelExists(channelName,parentGroup);
		} catch (IOException e) {
			throw new ChannelNotFound();	
		}
		} catch (PeerGroupException e) {
				throw new ChannelNotFound();	
		}
		if (group == null)
			throw new ChannelNotFound("Null group");
		discoverMetadata(group);
		id = (PipeID) IDFactory.fromURI(URI.create(metadata.getPipeUri()));
		if (!workpoolModel)
		{
		userDiscovery = new PeerDiscovery(group);
		userDiscovery.registerHandler(this);
		}
		publishMetaData();
	}
	private void discoverMetadata(PeerGroup group2) {
		int TRIES = 30;
		discovery = group2.getDiscoveryService();
		StdChatGroup grp = StdChatGroup.getInstance();
		Enumeration en0 = null;
		ChannelAdvertisement cAdv = null;
		boolean found = false;
		String msgState0;
		while((TRIES-->0) && (!found))
		{
			msgState0 = "Discovering Channel = "+ group2.getPeerGroupName() + "Metadata - Try: "+ TRIES;
			grp.fireEventStatus(new StatusEvent(this,msgState0));
			log.info(msgState0);
			try {
				en0 = discovery.getLocalAdvertisements(DiscoveryService.ADV, null, null);
			}
			catch (IOException ioe) {
				ioe.printStackTrace();
			}
			cAdv = handleDiscovered(en0);
			if (cAdv == null)
			{
				discovery.getRemoteAdvertisements(null, DiscoveryService.ADV, null ,null,  5);
			}  else {
				break;
			}

			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		if (cAdv != null)
		{
			metadata.setChannelName(cAdv.getName());
			metadata.setFounderName(cAdv.getFounderName());
			metadata.setFounderEmailAddress(cAdv.getFounderEmailAddress());
			metadata.setFounderPeerID(cAdv.getFounderPeerID());
			metadata.setPipeUri(cAdv.getPipeID());
			metadata.buildXML();
		}

	}
  
	public Message Poll(int pollTimeOut) throws InterruptedException
	{
		if (inPipe!=null)
		{
			log.info("Doing polling for = "+inPipe.getAdvertisement().getPipeID().toString());
			return inPipe.poll(pollTimeOut);
		} else {
			log.info("Input pipe is null for :"+id.toString());
		}
		
		return null;
	}
	public synchronized void cleanPollData()
	{
		if (inPipe!=null)
		{
			inPipe.close();
			inPipe = null;
		}
	}

	private ChannelAdvertisement handleDiscovered(Enumeration en)
	{
		Advertisement adv;
		ChannelAdvertisement advertisement = null;
		while (en.hasMoreElements()) {
			adv = (Advertisement) en.nextElement();
			//log.info(adv.toString());
			if (adv instanceof ModuleImplAdvertisement)
			{

				log.info("Got a peer ModuleImplAdvertisement");

				ModuleImplAdvertisement mia = (ModuleImplAdvertisement) adv;

				advertisement = (ChannelAdvertisement) ChannelUtil.parseModuleAdv(mia,ChannelService.msID);
				if (advertisement != null)
					return advertisement;
			}

		}
		return advertisement;
	}
	private PeerGroup channelExists(String Name, PeerGroup parent) throws IOException, PeerGroupException
	{

		DiscoveryService disco = parent.getDiscoveryService();
		Enumeration en = null;
		PeerGroup PG = null;

		int k = 1;
		boolean found = false;
		PeerGroupAdv pga = null;
		//ArrayList returns = new ArrayList();
		StdChatGroup grpstate = StdChatGroup.getInstance();
		while ((k < 30) && (!found))
		{
			en = disco.getLocalAdvertisements(DiscoveryService.GROUP,"Name" ,Name);
			if ((en!=null)&&(en.hasMoreElements()))
			{
				Advertisement adv = (Advertisement) en.nextElement();
				if ((adv instanceof PeerGroupAdv) && (!found ))
				{
					pga = (PeerGroupAdv) adv;
					log.info("Found Group = "+ pga.getName());
					log.info("GroupPeer ID = "+pga.getPeerGroupID());
					found = true;
					PG = parent.newGroup(pga.getPeerGroupID());
					return PG;
				}
			} // endif2
			String msgState0 = "Searching Remotely a new group with name = "+ Name+ " Try: "+k;
			grpstate.fireEventStatus(new StatusEvent(this,msgState0));
			
			log.info(msgState0);
			disco.getRemoteAdvertisements(null, DiscoveryService.GROUP, "Name", Name , 1);	

			++k;
			try { Thread.sleep(1000);} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			};
		} // end while
		return null;
	}

	private XMLDocument mkJoinAdv(String user, String Email, String PeerID)
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
	private XMLDocument mkLeaveAdv(String user, String Email, String PeerID)
	{
		XMLDocument doc = (XMLDocument) StructuredDocumentFactory.newStructuredDocument(MimeMediaType.XMLUTF8, "jxta:ChanMsg");
		Attributable attr = (Attributable) doc;
		attr.addAttribute("xmlns:jxta", "http://jxta.org");
		Element item1 = doc.createElement("ChanCommand","LEAVE");
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
	private XMLDocument mkChatMessage(String sender, String email, String peerID, String message)
	{
		XMLDocument doc = (XMLDocument) StructuredDocumentFactory.newStructuredDocument(MimeMediaType.XMLUTF8, "jxta:ChanMsg");
		Attributable attr = (Attributable) doc;
		attr.addAttribute("xmlns:jxta", "http://jxta.org");
		DateTime dt = new DateTime(new Date());
		Element item1 = doc.createElement("ChanCommand","MSG");
		Element item0 = doc.createElement("ChannelName",metadata.getName());
		Element item2 = doc.createElement("UserName",sender);
		Element item3 = doc.createElement("Email", email);
		Element item4 = doc.createElement("PeerID", peerID);
		Element item5 = doc.createElement("Message",message);
		Element item6 = doc.createElement("DateTime", dt.toString());
		doc.appendChild(item0);
		doc.appendChild(item1);
		doc.appendChild(item2);
		doc.appendChild(item3);
		doc.appendChild(item4);
		doc.appendChild(item5);
		doc.appendChild(item6);

		return doc;
	}
	/*
	private XMLDocument mkDoc()
	{

		XMLDocument doc = (XMLDocument) StructuredDocumentFactory.newStructuredDocument(MimeMediaType.XMLUTF8, "jxta:ChanInfo");
		Attributable attr = (Attributable) doc;
		attr.addAttribute("xmlns:jxta", "http://jxta.org");
		DateTime dt = new DateTime(new Date());
		Element item0 = doc.createElement("timestamp",dt.toString());
		Element item3 = doc.createElement("name",metadata.getName());
		Element item1 = doc.createElement("pipeid",id.toString());
		Element item2 = doc.createElement("founderMail",metadata.getFounderEmailAddress());
		Element item4 = doc.createElement("founderPeerID",metadata.getFounderPeerID());
		doc.appendChild(item0);
		doc.appendChild(item3);
		doc.appendChild(item2);
		doc.appendChild(item4);
		doc.appendChild(item1);
		return doc;
	}
	 */
	/*
	public void init(String channelName, ChannelInfo metadata, PipeID id, PeerGroupAdvertisement gAdv ) throws JDOMException, IOException, PeerGroupException {
		this.id = id;
		gid = gAdv.getPeerGroupID();
		group = parentGroup.newGroup(gAdv.getPeerGroupID());
		setMetaData(metadata.toString());
		this.metadata.buildXML();
		discovery = group.getDiscoveryService();
	}
	 */
	private PeerGroup createPeerGroup(String name, PeerGroup parent) throws Exception
	{
		String description = "Channel: "+ name;

		// The Discovery service to use to publish the module and peer 
		// group advertisements.
		DiscoveryService discovery = parent.getDiscoveryService();
		AdvBuilder build = AdvBuilder.getInstance();
		StdChatGroup std = StdChatGroup.getInstance();
		PeerGroup pg0 = std.getNetPeerGroup();
		ModuleImplAdvertisement implAdv =
			pg0.getAllPurposePeerGroupImplAdvertisement();

		// Create the module advertisements for the Channel service.
		ModuleClassAdvertisement ChannelClassAdv = build.createModuleClassAdv(
				ChannelService.mcID, "Channel Service", 
		"A service to provide channel information.");
		ModuleSpecAdvertisement ChannelSpecAdv = build.createModuleSpecAdv(
				ChannelService.msID, "Channel Service", 
		"A Channel service specification");
		pipeAdv = mkPropagatePipeAdv(name);
		ChannelSpecAdv.setPipeAdvertisement(pipeAdv);

		ModuleImplAdvertisement ChannelImplAdv = build.createModuleImplAdv(
				implAdv, ChannelSpecAdv, 
				"Reference Implementation of the Channel Service",
				"it.di.unipi.iochatto.channel."
				+ "ChannelServiceImpl");

		/*public PeerGroup createPeerGroup(String pipeURI) throws Exception
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


		 * */
		//Element pEl = (Element) mkDoc();
		ChannelAdv cAdv = new ChannelAdv();
		cAdv.setFounderName(metadata.getFounderName());
		cAdv.setFounderEmailAddress(metadata.getFounderEmailAddress());
		cAdv.setFounderPeerID(metadata.getFounderPeerID());
		cAdv.setName(metadata.getName());
		cAdv.setPipeID(metadata.getPipeUri());
		XMLDocument doc0 = (XMLDocument) cAdv.getDocument(MimeMediaType.XMLUTF8);
		Element pEl = doc0.getRoot();
		StructuredDocument svcParm =
			StructuredDocumentFactory.newStructuredDocument(
					MimeMediaType.XMLUTF8,
			"Parm");
		StructuredDocumentUtils.copyElements(svcParm, svcParm, pEl);
		ChannelImplAdv.setParam(svcParm);
		/**/

		// Get the parameters for the peer group's Module Implementation 
		// Advertisement to which we will add our service.
		StdPeerGroupParamAdv params = 
			new StdPeerGroupParamAdv(implAdv.getParam());

		// Get the services from the parameters.
		Map services = params.getServices();

		// Add the Channel services to the set of services.
		services.put(ChannelClassAdv.getModuleClassID(), ChannelImplAdv);

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
		implAdv.setModuleSpecID(groupSpecID);
		// Publish the Channel module class and spec advertisements.
		discovery.publish(ChannelClassAdv);
		discovery.remotePublish(ChannelClassAdv, DiscoveryService.ADV);
		discovery.publish(ChannelSpecAdv);
		discovery.remotePublish(ChannelSpecAdv, DiscoveryService.ADV);


		// Publish the Peer Group implementation advertisement.
		discovery.publish(implAdv);
		discovery.remotePublish(implAdv, DiscoveryService.ADV);

		// Create the Peer Group ID.
		PeerGroupID groupID = gid;

		// Create the new group using the group ID, advertisement, name, 
		// and description.
		group = parent.newGroup(groupID, implAdv, name,
				description);

		// Need to publish the group remotely only because newGroup()
		// handles publishing to the local peer.
		PeerGroupAdvertisement groupAdv = 
			group.getPeerGroupAdvertisement();

		discovery.publish(groupAdv);
		discovery.remotePublish(groupAdv, DiscoveryService.GROUP);

		// Start the peer group's applications.
		group.startApp(null);
		return group;
	}
	public void updateMetaData(ChannelInfo mt)
	{
		if (metadata.getTimeStamp()< mt.getTimeStamp())
		{
			metadata = null;
			metadata = mt;
			metadata.buildXML();
		}
	}
	private void join(String identity) throws Exception
	{
		PeerGroup ng = group;

		try {
			AuthenticationCredential authCred = new AuthenticationCredential(group,null,null);
			log.info(authCred.toString());
			// Get the MembershipService from the peer group
			log.info("Joining "+ng.getPeerGroupName()+ "with identity = "+ identity);
			membership = group.getMembershipService();
			if (membership == null)
				log.info("Membership Service is empty or not started");
			Authenticator auth = membership.apply( authCred );
			NoneMembershipService.NoneAuthenticator infoAuth = (NoneMembershipService.NoneAuthenticator) auth;
			infoAuth.setAuth1Identity(identity); 
			// Check if everything is okay to join the group
			if (auth.isReadyForJoin()){
				membership.join(auth);

				log.info("Successfully joined channel " + ng.getPeerGroupName());
				if (!workpoolModel)
				{
				userDiscoveryThread = new Thread(userDiscovery);
				userDiscovery.registerHandler(this);
				//userDiscoveryThread.setDaemon(true);
				userDiscoveryThread.start();
				
				log.info("User Discovery Thread Started!");
				}
			} else
				log.info("Failure: unable to join group");
		} catch (Exception e1){
			log.info("Failure in authentication.");
			e1.printStackTrace();
			//System.exit(1);

		}
	}
	private PipeAdvertisement mkPropagatePipeAdv(String channelName)
	{
		PipeAdvertisement pipeAdv = 
			(PipeAdvertisement) AdvertisementFactory.newAdvertisement(
					PipeAdvertisement.getAdvertisementType());

		String name = "ServicePipe"+channelName;
		// Create a propagate Pipe Advertisement.
		pipeAdv.setName(name);

		pipeAdv.setPipeID((ID)id);
		pipeAdv.setType(PipeService.PropagateType);
		return pipeAdv;
	}

	public void join(String name,String address, String peerID) {
		metadata.addUser(name, address, peerID, PresenceService.ONLINE);
		metadata.buildXML();

		try {
			join(address);
			publishMemberShip(name,address,peerID, ChannelAction.JOIN);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.info("Exception joining the group!");
			e.printStackTrace();
		}

	}
	private void publishMemberShip(String name, String address, String peerID, ChannelAction action)
	{
		OutputPipe out = null;
		PipeService pipeService = group.getPipeService();
		TextDocumentMessageElement  me = null;
		Message msg = null;
		try {
			if (action==ChannelAction.JOIN)
			{
				XMLDocument xmldoc = mkJoinAdv(name,address,peerID);
				me = new TextDocumentMessageElement("ChannelJoinMessage",(XMLDocument) 
						xmldoc,null);
				msg = formatMsg(me,"ChannelJoin");
			} else {
				XMLDocument xmldoc = mkLeaveAdv(name,address,peerID);
				me = new TextDocumentMessageElement("ChannelLeaveMessage",(XMLDocument) 
						xmldoc,null);
				msg = formatMsg(me,"ChannelLeave");
			}

		//	Message msg = formatMsg(me,"ChannelState");
			if ((pipeAdv == null) && (msg == null))
				pipeAdv = mkPropagatePipeAdv(metadata.getName(), (ID) id);
			//out = pipeService.createOutputPipe(pipeAdv, 30000);
			// non riesco a connettermi
			//out.send(msg);
			outPipe.send(msg);
		} catch (IOException e) {
			// non riesco a connettermi togliere l'host dalla cache
		}
		// TODO Auto-generated catch block
		// e.printStackTrace();
//		out.close();
	}

	private PipeAdvertisement mkPropagatePipeAdv(String channelName, ID id)
	{
		PipeAdvertisement pipeAdv = 
			(PipeAdvertisement) AdvertisementFactory.newAdvertisement(
					PipeAdvertisement.getAdvertisementType());

		String name = "ServicePipe"+channelName;
		// Create a propagate Pipe Advertisement.
		pipeAdv.setName(name);
		pipeAdv.setPipeID(id);
		pipeAdv.setType(PipeService.PropagateType);
		return pipeAdv;
	}
	private void publishMetaData()
	{
		OutputPipe out = null;
		PipeService pipeService = group.getPipeService();
		try {
			metadata.buildXML();
			//log.info(metadata.toString());
			ByteArrayInputStream bin = new ByteArrayInputStream(metadata.toString().getBytes());
			XMLDocument xmldoc = (XMLDocument) StructuredDocumentFactory.newStructuredDocument(MimeMediaType.XMLUTF8, bin);
			TextDocumentMessageElement  me = new TextDocumentMessageElement("ChannelInfoMessage",(XMLDocument) 
					xmldoc,null);
			//Message msg = new Message();
			Message msg = formatMsg(me, "ChannelInfo");
			//msg.adMessageElement(me);
			//pipeAdv = mkPropagatePipeAdv(metadata.getName(), (ID) id);
			//out = pipeService.createOutputPipe(pipeAdv, 30000);
			// non riesco a connettermi
			outPipe.send(msg);
		} catch (IOException e) {
			// non riesco a connettermi togliere l'host dalla cache
		} finally {
	//		out.close();
		//	out = null;
		}

		
	}
	public XMLDocument sendMessage(String name, String address, String peerID, String message) throws IOException {
		OutputPipe out = null;
		log.info("Sending output message:"+message);
		PipeService pipeService = group.getPipeService();
		XMLDocument xmldoc = mkChatMessage(name,address,peerID,message);
		TextDocumentMessageElement me = new TextDocumentMessageElement("ChatMessage",(XMLDocument) 
				xmldoc,null);
		
		Message msg = formatMsg(me,"ChatMessage");
		// non riesco a connettermi
		outPipe.send(msg);
		return xmldoc;
		// out.close();
	}

	private Message formatMsg(MessageElement me, String type)
	{

		StringMessageElement  typeMessage  = new StringMessageElement("Type",type,null);
		Message msg = new Message();
		msg.addMessageElement("Type",typeMessage);
		msg.addMessageElement(type,me);
		return msg;

	}
	public void leave(String emailAddress) throws PeerGroupException{
		membership.resign();
		if (userDiscoveryThread==null)
			return;
		userDiscoveryThread.interrupt();
		userDiscoveryThread = null;
		metadata.buildXML();
		UserInfo info = metadata.findUser(emailAddress);
		publishMemberShip(info.getName(), info.getAddress(), info.getPeerID(), ChannelAction.LEAVE);
		metadata.clear();
		outPipe.close();
		inPipe.close();
		outPipe = null;
		inPipe = null;
		users.clear();
		users = null;
		// other clean up: free manually all hashmaps and calling garbage collector
		System.gc();
	}
	public void setMetaData(String s) throws JDOMException, IOException
	{
		metadata.parse(s);
		metadata.buildXML();
	}
	public ChannelInfo getMetadata()
	{
		metadata.buildXML();
		return metadata;
	}
	public PeerGroup getPeerGroup() {

		return group;
	}
	private synchronized boolean collect(String EmailAddress)
	{
		// I dont' remove myself...
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
			if (diff > 60*1000)
			{
				expireCache.remove(EmailAddress);
				users.remove(EmailAddress);
				metadata.removeUser(EmailAddress);
				return true;
			}
			
		} else {
			expireCache.put(EmailAddress, new Long(System.currentTimeMillis()));
			
		}
		return false;
	}

	public void UserInfoUpdate(UserInfo ev) {
		if (!users.containsKey(ev.getAddress()))
		{
			log.info("Adding a new user with name = " +ev.getName() + " address = "+ev.getAddress());
			users.put(ev.getAddress(), ev);
			metadata.addUser(ev.getName(), ev.getAddress(), ev.getPeerID(), ev.getStatus());
		} else
		{
			UserInfo info = metadata.findUser(ev.getAddress());
			if (info.getStatus()!=ev.getStatus())
			{
				users.put(ev.getAddress(), info);
				metadata.addUser(ev.getName(), ev.getAddress(), ev.getPeerID(), ev.getStatus());

			}
		}
		collect(ev.getAddress());
		metadata.buildXML();
		ChannelMessageEvent ev0 = new ChannelMessageEvent(this);
		ChannelInfoMessage msg0 = new ChannelInfoMessage(metadata);
		//System.out.println(msg0.toString());
		ev0.setMessage(msg0);
		ev0.setChannelName(metadata.getName());
		metadata.buildXML();
		ev0.setMetaData(metadata);
		fireMessageEvent(ev0);
	}
	public synchronized void addMessageListener(ChannelMessageEventListener m)
	{
		listeners.add(m);
	}
	public synchronized void removeMessageListener(ChannelMessageEventListener m)
	{
		listeners.remove(m);
	}
	public void showUsers()
	{
		metadata.buildXML(); 
		log.info(metadata.toString());

	}
	public void fireMessageEvent(ChannelMessageEvent ev)
	{
		for (ChannelMessageEventListener l: listeners)
			l.MessageEvent(ev);
	}
	
	
}


