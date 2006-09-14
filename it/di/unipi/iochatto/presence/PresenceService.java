/* FileName: it/di/unipi/iochatto/presence/PresenceService.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.presence;


import java.util.HashMap;

import net.jxta.service.Service;


/**
 * An interface for the Presence service, a service that allows peers to
 * exchange presence status information specifying their current status
 * (offline, online, busy, away). This interface defines the operations
 * that a developer can expect to use to manipulate the Presence service, 
 * regardless of which underlying implementation of the service is being 
 * used.
 */
public interface PresenceService extends Service
{
    /**
     * The module class ID for the Presence class of service.
     Module Class ID: urn:jxta:uuid-9B66496088724DA98139DABD163B635B05
Module Spec ID: urn:jxta:uuid-9B66496088724DA98139DABD163B635B459FCBF45A0748C78B219739DFCD39D706

     */
    //public static final String refModuleClassID = "urn:jxta:uuid-0A28FD03D56043E3A1E9FAEC74A69A1905";
	public static final String refModuleClassID = "urn:jxta:uuid-E695B6712268421E900AB1076706F16B05";
	//urn:jxta:uuid-9B66496088724DA98139DABD163B635B05";
    /**
     * A status value indicating that a user is currently online but
     * is temporarily away from the device.
     */
    public static final int AWAY= 3;

    /**
     * A status value indicating that a user is currently online but
     * is busy and does not want to be disturbed.
     */
    public static final int BUSY = 2;

    /**
     * A status value indicating that a user is currently offline.
     */
    public static final int OFFLINE = 0;

    /**
     * A status value indicating that a user is currently online.
     */
    public static final int ONLINE = 1;


    /**
     * Add a listener object to the service. When a new Presence Response 
     * Message arrives, the service will notify each registered listener.
     *
     * @param   listener the listener object to register with the service.
     */
    public void addListener(PresenceListener listener);

    /**
     * Announce updated presence information within the peer group.
     *
     * @param   presenceStatus the updated status for the user identified 
     *          by the email address.
     * @param   emailAddress the email address used to identify the user 
     *          associated with the presence info.
     * @param   name a display name for the user associated with the 
     *          presence info.
     */
    public boolean announcePresence(int presenceStatus, String emailAddress, 
        String name, String peerID);

    public boolean checkPresence(String emailAddress);
    /**
     * Sends a query to find presence information for the user specified
     * by the given email address. Any response received by the service 
     * will be dispatched to registered PresenceListener objects.
     *
     * @param   emailAddress the email address to use to find presence info.
     */
    
    public void findPresence(String emailAddress);
    /**
     * Removes a given listener object from the service. Once removed, 
     * a listener will no longer be notified when a new Presence Response
     * Message arrives. 
     *
     * @param   listener the listener object to unregister.
     */
    public boolean removeListener(PresenceListener listener);
    public boolean clearListener();
    
}