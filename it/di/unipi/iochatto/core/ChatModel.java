/* FileName: it/di/unipi/iochatto/core/ChatModel.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.core;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.jxta.document.XMLDocument;
import net.jxta.endpoint.TextDocumentMessageElement;
import net.jxta.exception.PeerGroupException;
import net.jxta.exception.ServiceNotFoundException;
import net.jxta.id.IDFactory;
import net.jxta.peer.PeerID;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.platform.ModuleClassID;
import it.di.unipi.iochatto.channel.Channel;
import it.di.unipi.iochatto.channel.ChannelDiscovery;
import it.di.unipi.iochatto.channel.ChannelDiscoveryEvent;
import it.di.unipi.iochatto.channel.ChannelDiscoveryListener;
import it.di.unipi.iochatto.channel.ChannelFactory;
import it.di.unipi.iochatto.channel.UserInfo;
import it.di.unipi.iochatto.channel.message.ChannelMessageEvent;
import it.di.unipi.iochatto.channel.message.ChannelMessageEventListener;
import it.di.unipi.iochatto.channel.message.ChannelPoller;
import it.di.unipi.iochatto.channel.message.ChatMessage;
import it.di.unipi.iochatto.core.StdChatGroup.StdChatException;
import it.di.unipi.iochatto.presence.PresenceAdvertisement;
import it.di.unipi.iochatto.presence.PresenceEvent;
import it.di.unipi.iochatto.presence.PresenceListener;
import it.di.unipi.iochatto.presence.PresenceService;
import it.di.unipi.iochatto.presence.PresenceServiceImpl;
import it.di.unipi.iochatto.presence.WatchDog;
import java.util.logging.*;
import java.util.Observable;

import org.apache.log4j.Level;
/**
 *  ChatModel: This is the model for the JXTA P2P Chat
 *  IoChatto! The pattern that i follow is Model View Controller,
 *  but merging Views with Controller.
 *
 *
 * Created: Sun Sep  3 21:03:26 2006
 *
 * @author Giorgio Zoppi <a href="mailto:zoppi@cli.di.unipi.it"></a>
 * @version 0.1
 */
public class ChatModel extends Observable implements PresenceListener, ChannelMessageEventListener,StatusEventListener, ChannelDiscoveryListener {
	private PeerID peerID = null;
	private String PID = null;
    /* singleton that I use for referencing the init Group
     * Standard Chat Group : chat group for IoChatto
     * every peer in order to use IoChatto Chat Services needs to 
     * join that chat group.
     */
	private StdChatGroup stdGroup =  StdChatGroup.getInstance();
	private int[] presenceState = {PresenceService.OFFLINE, PresenceService.ONLINE, PresenceService.AWAY, PresenceService.BUSY };
	private int myPresenceState = PresenceService.OFFLINE;
	private PresenceService presence = null;
	private Status status;
	private String userName = "nobody";
	private int startPort = 9000;
	private int endPort = 10000;
	private long TIMEOUT = 20000;
	private ChannelPoller poller = null;
	private ChannelDiscovery chsearch = null;
	private Thread chSearchThread = null;
	private Thread pollThread = null;
	private String emailAddress = "nobody@nowhere.com";
	private String password = "gofyski";
	private Thread initThread = null;
	private Map<String,Channel> channelsJoined =  new HashMap<String,Channel>();
	private final Logger log = Logger.getLogger(ChatModel.class.getName());
	private boolean RDV = false;
	/**;
	 * Creates a new <code>ChatModel</code> instance.
	 *
	 */
	public ChatModel() {
	}
	public void disconnect()
	{
		if (pollThread!=null)
			pollThread.interrupt();
		if (channelsJoined != null)
		{
		Collection<Channel> channels = channelsJoined.values();
		for (Channel ch : channels)
		{
			try {
				ch.leave(emailAddress);
			} catch (PeerGroupException e) {
			    
				e.printStackTrace();
			}
			ch.cleanPollData();
			ch.clear();
			ch = null;
		}
		}
		/*
		 stdGroup.getPeerGroup().stopApp();
		 stdGroup.getNetPeerGroup().stopApp();
		 stdGroup.getPeerGroup().unref();
		 stdGroup.getNetPeerGroup().unref();
		 */
		presence = null;
		poller = null;
		if (stdGroup!=null)
			stdGroup.leave();
		//presence.stopApp();
		if (initThread!=null)
			initThread.interrupt();
		initThread = null;
		stdGroup = null;
		presence = null;
		peerID = null;
		PID = null;
		System.gc();
		
	}
	public void init()
	{
		peerID = IDFactory.newPeerID(PeerGroupID.defaultNetPeerGroupID);
		PID = peerID.getUniqueValue().toString();
		stdGroup = StdChatGroup.getInstance();
		stdGroup.addListener(this);
		
		init(userName, password, emailAddress ,startPort, endPort, RDV);
		connect(TIMEOUT);
		// faccio il join allo StdChatGroip
		join(emailAddress);
		lookupService();
		initPresence(userName, emailAddress);
		poller = new ChannelPoller();
		pollThread = new Thread(poller);
		pollThread.setDaemon(true);
		pollThread.start();
		setPresenceState(PresenceService.ONLINE);
		
	}
	public  void setCredentials(String userName, String emailAddress, String password)
	{
		this.userName = userName;
		this.emailAddress = emailAddress;
		this.password = password;
		
	}
	public void searchChannel(String chName)
	{
		chsearch = new ChannelDiscovery();
		chsearch.addListener(this);
		chsearch.searchFor(chName);
		chSearchThread = new Thread(chsearch);
		chSearchThread.start();
	}
	public void stopChannelSearch()
	{
		chsearch.removeListener(this);
		chSearchThread.interrupt();
		chSearchThread = null;
		System.gc();
	}
	public void searchUser(String emailAddress)
	{
		
		if (presence!=null){
			presence.addListener(this);
			presence.findPresence(emailAddress);
			
		}
	}
	public void stopSearchUser()
	{
		presence.removeListener(this);
	}
	public void listChannels()
	{
		ChannelDiscovery chsearch0 = new ChannelDiscovery();
		chsearch0.addListener(this);
		chsearch0.searchFor("*");
		Thread chSearchThread0 = new Thread(chsearch0);
		chSearchThread0.start();	
		
	}
	
	public void sendMessage(String channel, String text)
	{
		if(channelsJoined.containsKey(channel))
		{
			Channel tmp = channelsJoined.get(channel);
			
			try {
				XMLDocument me = tmp.sendMessage(userName, emailAddress,PID, text);
				//		ChatMessage chm = new ChatMessage(me);
				//		ChannelMessageEvent ev0 = new ChannelMessageEvent(this);
				//	ev0.setChannelName(channel);
				//	ev0.setMessage(chm);
				//setChanged();
				//	notifyObservers(ev0);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public void mkChannel(String chName,String topic, String username, String address)
	{
		UserInfo info = new UserInfo();
		info.setAddress(address);
		info.setName(username);
		info.setPeerID(PID);
		Channel test = null;
		try {
			test = ChannelFactory.newChannel(chName, info, topic);
			test.join(username,address,PID);
			test.addMessageListener(this);
			poller.addChannel(test);
			channelsJoined.put(chName, test);
		} catch (Exception e) {
			StdChatGroup grp = StdChatGroup.getInstance();
			grp.fireEventStatus(new StatusEvent("Error making the new channel"));
		}
	}
	/*
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
	  }
	  */
	private void lookupService()
	{
		
		ModuleClassID classID = null;
		try {
			classID = (ModuleClassID) IDFactory.fromURI(URI.create(StdChatGroup.mcID));
		} catch (URISyntaxException use){ log.info("Syntax Module ClassID ID Error");}
		try {
			PeerGroup pg = stdGroup.getPeerGroup();
			stdGroup.fireEventStatus(new StatusEvent(this,"Looking Presence Service"));
			presence = (PresenceService) pg.lookupService(classID);
			//presence.addListener(this);
			PresenceServiceImpl pImpl = (PresenceServiceImpl) presence;
			pImpl.initResolver();
			stdGroup.fireEventStatus(new StatusEvent(this,"Found Presence Service"));
			stdGroup.fireEventStatus(new StatusEvent(this,"CONNECTED."));
			
			//
		} catch (ServiceNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private void initPresence(String user, String email)
	{
		presence.announcePresence(myPresenceState,email, user, PID);
		stdGroup.fireEventStatus(new StatusEvent(this,"Announcing that I'm online"));
		
		Status s = Status.getInstance();
		s.setEmailAddress(email);
		s.setName(user);
		s.setPeerID(PID);
		s.setStatus(myPresenceState);
	}
	
	
	private void init(String username,String password,String email, int minPort, int maxPort, boolean RDV)
	{
		
		stdGroup.startPlatform(peerID, username, password, email, minPort, maxPort, RDV, Level.ERROR);
	}
	private void connect(long timeout)
	{
		stdGroup.waitForRendezvousConn(timeout);
	}
	private void join(String address)
	{
		
		// per fare il join devo
		try {
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
			} catch (StdChatException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
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
		} 
	}
	
	public void leaveChannel(String channel)
	{
		if (channelsJoined.containsKey(channel))
		{
			Channel ch = channelsJoined.get(channel);
			try {
				ch.leave(emailAddress);
			} catch (PeerGroupException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ch.cleanPollData();
			ch.clear();
			poller.removeChannel(ch);
			channelsJoined.remove(channel);
			ch = null;
		}
	}
	public void setPresenceState(int state)
	{
		Status status = Status.getInstance();
		myPresenceState = state;
		status.setStatus(state);
		UserInfo info = new UserInfo();
		info.setAddress(status.getEmailAddress());
		info.setName(status.getName());
		info.setPeerID(status.getPeerID());
		info.setStatus(state);
		stdGroup.fireEventStatus(new StatusEvent(this,"Changed my presence status to: "+myPresenceState));
		presence.announcePresence(myPresenceState,status.getEmailAddress(), status.getName(), status.getPeerID());
		Set<String> keyset = channelsJoined.keySet();
		for (String key : keyset)
		{
			Channel ch = channelsJoined.get(key);
			ch.UserInfoUpdate(info);
		}
		setChanged();
		notifyObservers(status);
	}
	public void presenceUpdated(PresenceEvent ev) {
		setChanged();
		PresenceAdvertisement adv = (PresenceAdvertisement)ev.getAdvertisement();
		notifyObservers(adv);
	}
	public void MessageEvent(ChannelMessageEvent ev) {
		// TODO Auto-generated method stub
		
		if (ev == null)
			return;
		stdGroup.fireEventStatus(new StatusEvent(this,"Channel "+ ev.getChannelName() + "is speaking to me"));
		setChanged();
		notifyObservers(ev);
		stdGroup.fireEventStatus(new StatusEvent(this,"Notifying for channel "+ ev.getChannelName() + " all views"));
		
	}
	public void fireEvent(StatusEvent ev) {
		// TODO Auto-generated method stub
		setChanged();
		notifyObservers(ev);
		
	}
	public void setInitThread(Thread work) {
		// TODO Auto-generated method stub
		initThread  = work;
		
	}
	public void joinChannel(String chName) {
		// TODO Auto-generated method stub
		stdGroup.fireEventStatus(new StatusEvent(this,"Joining channel "+ chName + "..wait..."));
		mkChannel(chName, "",userName , emailAddress);
	}
	public void channelResult(ChannelDiscoveryEvent disco) {
		ChannelDiscoveryEvent ev0 = new ChannelDiscoveryEvent(disco.getSource(),disco.item(), disco.found(),disco.getMetaData());
		setChanged();
		notifyObservers(ev0);
		
	}
	public void leaveAll() {
		if (channelsJoined == null)
				return;
		Collection<Channel> channels = channelsJoined.values();
		for (Channel ch : channels)
		{
			try {
				ch.leave(emailAddress);
			} catch (PeerGroupException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ch.cleanPollData();
			ch.clear();
			ch = null;
		}
		
	}
	public void setRDV(boolean rdv) {
		// TODO Auto-generated method stub
		RDV = rdv;
	}
	public void setPortRange(int start, int end)
	{
	startPort = start;
	endPort = end;
	}
	
}
