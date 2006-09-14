/* FileName: it/di/unipi/iochatto/channel/ChannelService.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.channel;

import net.jxta.service.Service;

public interface ChannelService extends Service {
	public static final String mcID = "urn:jxta:uuid-B1431319AFC447798980AF45B491231A05";
	public static final String msID = "urn:jxta:uuid-B1431319AFC447798980AF45B491231A6B64E8A8D6E04CD0ACAF8F770AB258AD06";

	public void searchInfo(String channelName);
}
