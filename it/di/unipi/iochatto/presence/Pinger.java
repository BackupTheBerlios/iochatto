/* FileName: it/di/unipi/iochatto/presence/Pinger.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.presence;
import net.jxta.document.MimeMediaType;
import net.jxta.document.XMLDocument;
import net.jxta.endpoint.Message;
import net.jxta.endpoint.TextDocumentMessageElement;
import net.jxta.peergroup.PeerGroup;
import net.jxta.pipe.OutputPipe;
import net.jxta.pipe.PipeService;
import net.jxta.protocol.PipeAdvertisement;

import it.di.unipi.iochatto.core.StdChatGroup;

import java.io.IOException;
import java.util.logging.Logger;

public class Pinger implements Runnable  {
    private long PING_INTERVAL = 20000L;
    private PipeService pipeService;
    private PipeAdvertisement pipeAdv;
    private PresenceAdvertisement pa;
    private OutputPipe out = null;
    private Logger log = Logger.getLogger(Pinger.class.getName());
    private PeerGroup pg0;
    public Pinger(PeerGroup pg)
    {
    this.pg0 = pg;	
    this.pa = null;
	}
    public void setPipeAdv(PipeAdvertisement p)
    {
    	pipeAdv = p;
    }
    private void PingHosts()
    {
    	pipeService =  null;
    	PeerGroup pg = null;
    	StdChatGroup std = null;
    	
    	int tries = 0;
    	try { 
    	while ((( pg==null) || (pipeService==null)) && (tries<100) ) {
	    	 
    		 if (pg != null)
	    	  {	  pg0 = pg;
	    	  
	    		  pipeService =(pg!=null) ? pg.getPipeService() : null;
	    	  }
	    	  else {
	    				Thread.sleep(10000);
	    		  std = StdChatGroup.getInstance();
	    		  pg = std.getPeerGroup();
	    	  }
	       ++tries;
	       } // end while SPINLOCK
	     if (pg0 == null)
	     {
	    	log.severe("Cannot init Pinger: Failed Group Initialization - Bye."); 
	    	 System.exit(1);
	     }
	    
	   
	
    	if (pa == null)
    	{
    		log.info("Presence Advertisement Invalid!");
    			return;
    	}
    	if (pipeService == null)
    	{
    		log.severe("Fatal error pipe service not init..Exiting");
    		System.exit(1);
    	}
    	try {
    		
    		TextDocumentMessageElement  me = new TextDocumentMessageElement("PresenceInfo",(XMLDocument) pa.getDocument(MimeMediaType.XMLUTF8),null);
    		Message msg = new Message();
			
    		msg.addMessageElement(me);
    		pipeAdv = WatchDog.getPipeAdvertisement();
    		if (pipeService!=null)
    		{
    			out = pipeService.createOutputPipe(pipeAdv, 30000);
				// non riesco a connettermi
			out.send(msg);
    		}
    		} catch (IOException e) {
				// non riesco a connettermi togliere l'host dalla cache
				log.info("Ping Error");
				}
				// TODO Auto-generated catch block
			// e.printStackTrace();
    		out.close();
    	} catch (InterruptedException e)
    	{
    		return;
    	}
	    
	}
    public void setSleep(long l)
    {
	PING_INTERVAL = l;
    }
    public long getSleep()
    {
	return PING_INTERVAL;
    }
    public void run()
    {
	while(true)
	    {
		try {
			PingHosts();
			Thread.sleep(PING_INTERVAL);
		} catch (InterruptedException e) { 
			return; };
		
	    }
    }
	public void setAdvertisement(PresenceAdvertisement presenceInfo) {
		// TODO Auto-generated method stub
		pa = presenceInfo;
	}
	
		//notifyAll();
}

