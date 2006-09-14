/* FileName: it/di/unipi/iochatto/presence/PresenceAdvertisement.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.presence;


import net.jxta.document.Advertisement;
import net.jxta.id.ID;


/**
 * An abstract class defining an advertisement containing the elements used 
 * to describe a user's presence status. A user is assumed to be uniquely 
 * described by his or her email address.
 */
public abstract class PresenceAdvertisement extends Advertisement
{
    /**
     * The root element for the advertisement's XML document.
     */
    private static final String advertisementType = "jxta:PresenceAdvertisement";

    /**
     * The email address identifying the user whose presence information
     * this advertisement describes.
     */
    private String emailAddress = null;

    /**
     * A simple name for the user specified by the advertisement's 
     * email address.
     */
    private String name = null;

    /**
     * The Peer ID locating the peer on the network.
     */
    private String peerID = null;

    /**
     * A simple descriptor identifying the user's presence status.
     * The user can indicate that he or she is online, offline, busy, or
     * away.
     */
    private int presenceStatus = PresenceService.OFFLINE;

     
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
    public String getEmailAddress()
    {
        return emailAddress;
    }

    /**
     * Returns a unique identifier for this document. There is none for
     * this advertisement type, so this method returns the null ID.
     *
     * @return  the null ID.
     */
    public ID getID()
    {
        return ID.nullID;
    }

    /**
     * Returns the simple name for the user described by this advertisement.
     *
     * @return  the user's name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Returns the Peer ID of the user described by this advertisement.
     *
     * @return  the Peer ID of the user.
     */
    public String getPeerID()
    {
        return peerID;
    }

    /**
     * Returns the presence status information of the user described by 
     * this advertisement.
     *
     * @return  the user's status information.
     */
    public int getPresenceStatus()
    {
        return presenceStatus;
    }

    /**
     * Sets the email address String describing the user whose presence
     * status is described by this advertisement.
     *
     * @param   emailAddress the email address for the advertisement.
     */
    public void setEmailAddress(String emailAddress)
    {
        this.emailAddress = emailAddress;
    }

    /**
     * Sets the simple name for the user described by this advertisement.
     *
     * @param   name the user's name.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Sets the Peer ID identifying the peer's location on the P2P network.
     *
     * @param   peerID the Peer ID for the advertisement.
     */
    public void setPeerID(String peerID)
    {
        this.peerID = peerID;
    }

    /**
     * Sets the presence status information of the user described by this 
     * advertisement.
     *
     * @param   presenceStatus the user's status information.
     */
    public void setPresenceStatus(int presenceStatus)
    {
        this.presenceStatus = presenceStatus;
    }
}

