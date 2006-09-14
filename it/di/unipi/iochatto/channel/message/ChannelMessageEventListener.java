/* FileName: it/di/unipi/iochatto/channel/message/ChannelMessageEventListener.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.channel.message;

import java.util.EventListener;

public interface ChannelMessageEventListener extends EventListener {
public void MessageEvent(ChannelMessageEvent ev);
}
