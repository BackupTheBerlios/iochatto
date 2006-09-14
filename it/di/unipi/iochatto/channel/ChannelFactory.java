/* FileName: it/di/unipi/iochatto/channel/ChannelFactory.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.channel;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

import org.jdom.JDOMException;

import net.jxta.exception.PeerGroupException;
import net.jxta.exception.ServiceNotFoundException;
import net.jxta.id.IDFactory;
import net.jxta.impl.protocol.PeerGroupAdv;
import net.jxta.peergroup.PeerGroup;
import net.jxta.pipe.PipeID;
import net.jxta.platform.ModuleClassID;
import it.di.unipi.iochatto.core.StdChatGroup;
import it.di.unipi.iochatto.channel.*;
/* sono stati forniti due modelli per il discovery dei peer nei canali
 * 1 thread per canale per il discovery dei peer
 * un workpool di 5 thread per il discovery dei peer
 * Il modello di default e' il modello 1 thread per canale che pero' ha il grosso difetto di non scalare sul
 * numero dei canali.Considerato che in genere un utente non tiene piu di 5 canali di chat attivi,
 * cio non costituisce un problema, per maggiore flessibilita' e' stato fornito anche l'altro modello che va
 * specificatamente indicato nel file di configurazione.
 */

public class ChannelFactory  extends Channel {
private static ChannelFactory instance = null;
private static Logger log = Logger.getLogger(ChannelFactory.class.getName());
private static boolean workPoolModel = false;
private static PeerDiscoveryWorkPool pool = null;
/* la varibaile changed ha l'effetto collaterale di far si che :
 *  quando un utente decida di impostare il modello a workpool di thread (farm di thread) , 
 *  non abbia creato altri canali con il modello precedente.
 *  Se cio si è verificato il modello workpool non viene impostato,
 *  questo e' stato fatto solo per ragioni di coeerenza nel design. In realta peer evitare che thread siano di discovery
 *  siano creati in un modo o in un altro.
 */
private static boolean changed = false;
private static boolean started = false;
private ChannelFactory()
{
}
public void getInstance()
{
	if (instance == null)
		   instance = new ChannelFactory();

}
public boolean setWorkPoolModel()
{
	if (changed)
		  return false;
	workPoolModel = true;
	pool = new PeerDiscoveryWorkPool(); 
	changed = true;
	return true;
}
public static Channel newChannel(String channelName, UserInfo founder, String topic) throws Exception
{
	
	if (instance == null)
		   instance = new ChannelFactory();
	changed = true;
 Channel ch = null;
	if (!workPoolModel)
 {
 ch = new Channel();
 ch.create(channelName, topic, founder);
 
 } else {
	ch = new Channel(true);
	ch.create(channelName,topic,founder);
	pool.addChannel(ch);
	if (!started)
	{
		started = true;
		pool.startPool();
	}
 }
return ch;
}
public static Channel newChannel(String channelName) throws Exception
{
	changed = true;
    if (instance == null) 
	{
	    instance = new ChannelFactory();
	}
    Channel ch = null;
    if (!workPoolModel)
    {
    ch = new Channel();
    ch.instantiate(channelName);
    } else {
    	ch = new Channel(true);
    	try {
    	ch.instantiate(channelName);
    	} catch (ChannelNotFound notFound)
    	{
    		return null;
    	}
    	pool.addChannel(ch);
    	if (!started)
    	{
    		started = true;
    		pool.startPool();
    	}	
    }
    return ch;
}
}


