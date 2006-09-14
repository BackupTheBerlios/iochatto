/* FileName: it/di/unipi/iochatto/channel/PeerDiscoveryWorkPool.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.channel;
import java.util.concurrent.*;
import java.util.ArrayList;
public class PeerDiscoveryWorkPool {
	private ExecutorService service = Executors.newFixedThreadPool(5);
	private ArrayList<Channel> list = new ArrayList<Channel>();
	public synchronized void addChannel(Channel ch)
	{
		list.add(ch);
	}
	public synchronized void removeChannel(Channel ch)
	{
		list.remove(ch);
	}
	public void startPool()
	{
		while (true) 
		{
		for  (Channel l : list)
		{
		PeerDiscovery pdisc = new PeerDiscovery(l.getPeerGroup());
		pdisc.registerHandler(l);
		service.submit(pdisc);
		}
		}
	}
	public void shutDown()
	{
		service.shutdownNow();
	}

}
