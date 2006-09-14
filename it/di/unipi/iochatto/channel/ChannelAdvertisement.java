/* FileName: it/di/unipi/iochatto/channel/ChannelAdvertisement.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.channel;


import net.jxta.document.Advertisement;
import net.jxta.id.ID;
import net.jxta.protocol.PipeAdvertisement;


/**
 * An abstract class defining an advertisement containing the elements used 
 * to describe a user's presence status. A user is assumed to be uniquely 
 * described by his or her email address.
 */
public abstract class ChannelAdvertisement extends Advertisement
{
    /**
     * The root element for the advertisement's XML document.
     */
    private static final String advertisementType = "jxta:ChannelAdvertisement";

    /**
     * The email address identifying the user whose presence information
     * this advertisement describes.
     */
   // private PipeAdvertisement pipeAdv = null;
    private String pipeID = null;

    /**
     * A simple name for the user specified by the advertisement's 
     * email address.
     */
    private String founderName = null;
    private String founderEmailAddress = null;
    private String founderPeerID = null;
    /**
     * The Peer ID locating the peer on the network.
     */
    private String channelName = null;

    /**
     * A simple descriptor identifying the user's presence status.
     * The user can indicate that he or she is online, offline, busy, or
     * away.
     */
    public String getFounderEmailAddress()
    {
    	return founderEmailAddress;
    }
    public String getFounderPeerID()
    {
      return founderPeerID;	
    }
    public void setFounderEmailAddress(String mail)
    {
    	founderEmailAddress = mail;
    }
    public void setFounderPeerID (String pid)
    {
    	founderPeerID = pid;
    }
    public ID getID()
    {
    	return ID.nullID;
    }
     
    /**
     * Returns the advertisement type for the advertisement's document.
     *
     * @return  the advertisement type String.
     */
    public static String getAdvertisementType() {
    	   return advertisementType;
    	
		
	}
    
    

    /**
     * Returns the email address String describing the user whose presence
     * status is described by this advertisement.
     *
     * @return  the email address for the advertisement.
     */
    public String getFounderName()
    {
        return founderName;
    }

   
    /**
     * Returns the simple name for the user described by this advertisement.
     *
     * @return  the user's name.
     */
    public String getName()
    {
        return this.channelName;
    }
    public void setPipeID(String s)
    {
    	pipeID = s;
    }

    /**
     * Returns the Peer ID of the user described by this advertisement.
     *
     * @return  the Peer ID of the user.
     */
   
    /**
     * Sets the email address String describing the user whose presence
     * status is described by this advertisement.
     *
     * @param   emailAddress the email address for the advertisement.
     */
    public void setFounderName(String name)
    {
        this.founderName = name;
    }

    /**
     * Sets the simple name for the user described by this advertisement.
     *
     * @param   name the user's name.
     */
    public void setName(String name)
    {
        this.channelName = name;
    }

	public String getPipeID() {
		// TODO Auto-generated method stub
		return pipeID;
	}

    /**
     * Sets the Peer ID identifying the peer's location on the P2P network.
     *
     * @param   peerID the Peer ID for the advertisement.
     */
    
}

