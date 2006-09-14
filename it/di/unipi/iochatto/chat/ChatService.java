/* FileName: it/di/unipi/iochatto/chat/ChatService.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.chat;

import net.jxta.protocol.PipeAdvertisement;

import net.jxta.service.Service;


/**
 * An interface for the Chat service, a service that allows peers to
 * request and approve chat sessions. This interface defines the operations
 * that a developer can expect to use to manipulate the Chat service, 
 * regardless of which underlying implementation of the service is being
 * used.
 */
public interface ChatService extends Service
{
    /**
     * The module class ID for the Presence class of service.
     */
    public static final String refModuleClassID = 
        "urn:jxta:uuid-F84F9397891240B496D1B5754CCC933105";

    
    /**
     * Add a listener object to the service. When a new Initiate Chat 
     * Request or Response Message arrives, the service will notify each 
     * registered listener.
     *
     * @param   listener the listener object to register with the service.
     */
    public void addListener(ChatListener listener);

    /**
     * Approve a chat session.
     * 
     * @param   pipeAdvertisement the advertisement for the pipe that will 
     *          be used to set up the chat session.
     * @param   emailAddress the emailAddress of the user associated with
     *         local peer.
     * @param   displayName the name of the user associated with the 
     *         local peer.
     * @param   queryID the query ID to use to send to the Resolver 
     *         Response Message containing the response, allowing the 
     *         remote peer to match the response to an initial request.
     */
    public void approveChat(PipeAdvertisement pipeAdvertisement, 
        String emailAddress, String displayName, int queryID);

    /**
     * Removes a given listener object from the service. Once removed, 
     * a listener will no longer be notified when a new Initiate Chat 
     * Request or Response Message arrives. 
     *
     * @param   listener the listener object to unregister.
     */
    public boolean removeListener(ChatListener listener);

    /**
     * Send a request to chat to the peer specified.
     *
     * @param   peerID the Peer ID of the remote peer to request for a chat
     *         session.
     * @param  emailAddress the email address of the user associated with
     *         the local peer.
     * @param   displayName the display name  of the user associated with 
     *         the local peer.
     * @param   listener the listener to notify when a response to this
     *         request is received.
     */
    public void requestChat(String peerID, String emailAddress, 
        String displayName, ChatListener listener);
}