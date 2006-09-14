/* FileName: it/di/unipi/iochatto/channel/ChannelNotFound.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.channel;

public final class ChannelNotFound extends Exception {
  /**
	 * 
	 */
	private String name;
	private static final long serialVersionUID = 6496734806342572345L;

public ChannelNotFound()
  {
	  super();
  }
public ChannelNotFound(String name)
{
	super();
	this.name = name;
}

public String getChannelName()
{
	return name;
}

}
