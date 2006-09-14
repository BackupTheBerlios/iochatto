/* FileName: it/di/unipi/iochatto/channel/ChannelDiscoveryListener.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.channel;

import java.util.EventListener;

public interface ChannelDiscoveryListener extends EventListener {
	public void channelResult(ChannelDiscoveryEvent disco) ;

}
