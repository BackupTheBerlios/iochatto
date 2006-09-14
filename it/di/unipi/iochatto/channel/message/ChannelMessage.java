/* FileName: it/di/unipi/iochatto/channel/message/ChannelMessage.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.channel.message;

import it.di.unipi.iochatto.util.DateTime;

public interface ChannelMessage {
	void accept(MessageVisitor v);
	public DateTime getDateTime();
	public String senderName();
	public String senderAddress();
	public String senderPeerID();
	public String toString();
	public String toHTML();
	public String toXML();
	void parse();
	public String channelName();
}
