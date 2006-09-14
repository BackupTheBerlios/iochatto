/* FileName: it/di/unipi/iochatto/presence/WatchDog.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.presence;

import it.di.unipi.iochatto.core.StdChatGroup;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

import net.jxta.document.AdvertisementFactory;
import net.jxta.document.StructuredDocumentFactory;
import net.jxta.document.XMLDocument;
import net.jxta.document.XMLElement;
import net.jxta.endpoint.Message;
import net.jxta.endpoint.MessageElement;
import net.jxta.endpoint.TextDocumentMessageElement;
import net.jxta.id.IDFactory;
import net.jxta.impl.pipe.PipeServiceImpl;
import net.jxta.peergroup.PeerGroup;
import net.jxta.pipe.InputPipe;
import net.jxta.pipe.PipeID;
import net.jxta.pipe.PipeMsgEvent;
import net.jxta.pipe.PipeMsgListener;
import net.jxta.pipe.PipeService;
import net.jxta.protocol.PipeAdvertisement;


public class WatchDog implements PipeMsgListener, Runnable {
    /**
     *  Source PeerID
     */
    public final static String SRCIDTAG = "Jxta";
    /**
     *  Source Peer Name
     */
    public final static String SRCNAMETAG = "SRCNAME";
    /**
     *  Pong TAG name
     */
    public final static String PONGTAG = "PONG";
    /**
     *  Tutorial message name space
     */
    public final static String NAMESPACE = "PROPTUT";
    private static PeerGroup netPeerGroup = null;
    /**
     *  Common propagated pipe id
     */
    public final static String PIPEIDSTR = "urn:jxta:uuid-59616261646162614E504720503250336FA944D18E8A4131AA74CE6F4BE85DEF04";
    private final static String completeLock = "completeLock";
    private static PipeAdvertisement pipeAdv = null;
    private static PipeService pipeService = null;
    private static Logger log = Logger.getLogger(WatchDog.class.getName());
    private ArrayList<WatchDogListener> observers = new ArrayList<WatchDogListener>();
    /**
     *  Gets the pipeAdvertisement attribute of the PropagatedPipeServer class
     *
     * @return    The pipeAdvertisement value
     */
    
    public WatchDog(PeerGroup npg)
    {
    	netPeerGroup = npg;
    }
   
    public static PipeAdvertisement getPipeAdvertisement() {
        PipeID pipeID = null;
        try {
            pipeID = (PipeID) IDFactory.fromURI(new URI(PIPEIDSTR));
        } catch (URISyntaxException use) {
            use.printStackTrace();
        }
        PipeAdvertisement advertisement = (PipeAdvertisement)
                AdvertisementFactory.newAdvertisement(PipeAdvertisement.getAdvertisementType());
        advertisement.setPipeID(pipeID);
        advertisement.setType(PipeService.PropagateType);
        advertisement.setName("Presence Pipe Message");
        return advertisement;
    }
    public synchronized void addWatchDogListener(WatchDogListener l)
    {
    	observers.add(l);
    }
    public synchronized void removeWatchDogListener(WatchDogListener l)
    {
    	observers.remove(l);
    }
    public void fireWatchDogEvent(WatchDogEvent ev)
    {
    	Iterator it = observers.iterator();
    	while ((it!=null) && (it.hasNext()))
    	{
    		WatchDogListener tmp = (WatchDogListener) it.next();
    		if (tmp!=null)
    			tmp.fireUpdate(ev);
    	}
    }
    /**
     * {@inheritDoc}
     */
    public void pipeMsgEvent(PipeMsgEvent event) {

        Message message = event.getMessage();
        if (message == null) {
            return;
        }
       
        MessageElement item0 = message.getMessageElement("PresenceInfo");
        XMLDocument doc = null;
		try {
			doc = (XMLDocument) StructuredDocumentFactory.newStructuredDocument(item0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        if (doc == null) {
            return;
        }
        log.info("MessageElement:"+doc.toString());
        XMLDocument xmldoc = (XMLDocument) doc;
        XMLElement item = (XMLElement) xmldoc.getRoot();
        PresenceAdvertisement adv = (PresenceAdvertisement) AdvertisementFactory.newAdvertisement(item);
       if (adv == null)
    	   	return;
        // log.info("Received a Ping from address:" + adv.getEmailAddress());
       	 log.info("Source User :" + adv.getName());
      //  log.info("Presence Status: "+adv.getPresenceStatus());
      //  log.info("Upcall to listeners!");
        WatchDogEvent ev = new WatchDogEvent(adv);
        fireWatchDogEvent(ev);
      }

    /**
     * Keep running, avoids existing
     */
    private void waitForever(InputPipe pipe) {
        try {
            System.out.println("Waiting for Messages.");
          
            synchronized (completeLock) {
                completeLock.wait();
            }
            System.out.println("Done.");
        } catch (InterruptedException e) {
      	  	netPeerGroup = null;
      	  	pipeService = null;
      	  	observers.clear();
      	  	pipe.close();
      	  	System.gc();
            return;
        }
    }

    public void setPeerGroup(PeerGroup pg)
    {
    	netPeerGroup = pg;
    	    
    }
	public void run() {
	      InputPipe inputPipe = null;
	      StdChatGroup std = null; 
	      PeerGroup pg = null;
	      int tries = 0;
	      // start SPINLOCK
	      while ((( pg==null) || (pipeService==null)) && (tries<100) ) {
	    	  try {
	    	  if (pg != null)
	    	  {	  
	    		  netPeerGroup = pg;
	    		  pipeService = pg.getPipeService();
	    		  
	    	  }
	    	  else {
	    		  
	    				Thread.sleep(10000);
	    			
	    		  std = StdChatGroup.getInstance();
	    		  pg = std.getPeerGroup();
	    	  }
	       ++tries;
	    	  } catch (InterruptedException e)
	    	  {
	    		  netPeerGroup = null;
	    		  pipeService = null;
	    		  observers.clear();
	    		  inputPipe = null;
	    		  pg = null;
	    		  System.gc();
	    		  return;
	    	  }
	       } // end while SPINLOCK
	     if (pg == null)
	     {
	    	log.severe("Cannot init Watchdog: Failed Group Initialization - Bye."); 
	    	 System.exit(1);
	     }
	     if (pipeService!=null)
	     {
		 log.info("WatchDog init Spinlock passed! Group: "+ pg.getPeerGroupName() + "PipeService: on");
		// TODO Auto-generated method stub
	    	 pipeAdv = getPipeAdvertisement();
		 try {
			 // just to be sure;
			 	//pipeService.startApp(null);
	            inputPipe = pipeService.createInputPipe(pipeAdv, this);
	        } catch (IOException e) {
	            e.printStackTrace();
	            System.exit(-1);
	        }
	       waitForever(inputPipe);
           inputPipe.close();
           inputPipe = null;
	     }
	}

    
}

