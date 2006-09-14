/* FileName: it/di/unipi/iochatto/channel/ChannelDiscovery.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.channel;

 
import it.di.unipi.iochatto.core.StatusEvent;
import it.di.unipi.iochatto.core.StdChatGroup;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Logger;

import net.jxta.discovery.DiscoveryEvent;
import net.jxta.discovery.DiscoveryListener;
import net.jxta.discovery.DiscoveryService;
import net.jxta.document.Advertisement;
import net.jxta.exception.PeerGroupException;
import net.jxta.impl.protocol.PeerGroupAdv;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupFactory;
import net.jxta.protocol.DiscoveryResponseMsg;
import net.jxta.protocol.ModuleImplAdvertisement;
import net.jxta.protocol.PeerAdvertisement;
import net.jxta.protocol.PeerGroupAdvertisement;
import net.jxta.rendezvous.RendezVousService;


public class ChannelDiscovery implements Runnable {
	private ChannelInfo metadata;
    private Logger log = Logger.getLogger(ChannelDiscovery.class.getName());
    private int TRIES = 30;
    private Vector<ChannelDiscoveryListener> listeners = new Vector<ChannelDiscoveryListener>();
    private String toFind = null;
    public synchronized void addListener(ChannelDiscoveryListener l)
    {
    	listeners.add(l);
    }
    public  synchronized void removeListener(ChannelDiscoveryListener l)
    {
    	listeners.remove(l);
    }
    public synchronized void clearListeners()
    {
    	for (ChannelDiscoveryListener ch : listeners)
    	{
    		listeners.remove(ch);
    	}
    }
    public void searchFor(String name)
    {
    	toFind = name;
    }
    private PeerGroupAdv channelExists(String Name, PeerGroup parent) throws IOException, PeerGroupException
	{

		DiscoveryService disco = parent.getDiscoveryService();
		Enumeration en = null;
		StdChatGroup grp = StdChatGroup.getInstance();

		int k = 1;
		boolean found = false;
		PeerGroupAdv pga = null;
		//ArrayList returns = new ArrayList();

		while ((k < TRIES) && (!found))
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
					//PG = parent.newGroup(pga.getPeerGroupID());
					return pga;
				}
			} // endif2
			String remote = "Searching Remotely a new group with name = "+ Name+ " Try: "+k;
			grp.fireEventStatus(new StatusEvent(remote));
			log.info(remote);
			disco.getRemoteAdvertisements(null, DiscoveryService.GROUP, "Name", Name , 1);	

			++k;
			try { Thread.sleep(1000);} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			};
		} // end while
		return null;
	}

    public ChannelInfo search(String channelName) throws ChannelNotFound, URISyntaxException
	{
		StdChatGroup std = StdChatGroup.getInstance();
		PeerGroup parentGroup = std.getPeerGroup();
		PeerGroupAdv groupAdv = null;
		try {
		try {
			groupAdv = channelExists(channelName,parentGroup);
		} catch (IOException e) {
			throw new ChannelNotFound();	
		}
		} catch (PeerGroupException e) {
				throw new ChannelNotFound();	
		}
		if (groupAdv == null)
			throw new ChannelNotFound("Null group");
		return discoverMetadata(groupAdv, parentGroup);
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
	private ChannelInfo discoverMetadata(PeerGroupAdv gAdv, PeerGroup parentGroup) {
		int TRIES = 30;
		PeerGroup group2 = null;
		try {
			group2 = parentGroup.newGroup(gAdv.getPeerGroupID());
		} catch (PeerGroupException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (group2==null)
			return null;
		DiscoveryService discovery = group2.getDiscoveryService();
		Enumeration en0 = null;
		ChannelAdvertisement cAdv = null;
		boolean found = false;
		while((TRIES-->0) && (!found))
		{
			log.info("Discovering Channel = "+ group2.getPeerGroupName() + "Metadata - Try: "+ TRIES);
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
			metadata = new ChannelInfo();
			metadata.setChannelName(cAdv.getName());
			metadata.setFounderName(cAdv.getFounderName());
			metadata.setFounderEmailAddress(cAdv.getFounderEmailAddress());
			metadata.setFounderPeerID(cAdv.getFounderPeerID());
			metadata.setPipeUri(cAdv.getPipeID());
			metadata.buildXML();
			return metadata;
		}
		
		return null;

	}
	private void fireUpdate(ChannelDiscoveryEvent ev)
	{
		for (ChannelDiscoveryListener ch : listeners)
    	{
    		ch.channelResult(ev);
    	}
	}
	public void run() {
		ChannelInfo mt = null;
		try {
			mt = search(toFind);
		} catch (ChannelNotFound e) {
			fireUpdate(new ChannelDiscoveryEvent(this,toFind,false,null));
		} catch (URISyntaxException e) {
			fireUpdate(new ChannelDiscoveryEvent(this,toFind,false,null));
		}
		if (mt==null)
		{
			fireUpdate(new ChannelDiscoveryEvent(this,toFind,false,null));
		} else {
		fireUpdate(new ChannelDiscoveryEvent(this,toFind,true,mt));
		}
	}
}    