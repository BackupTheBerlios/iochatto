/* FileName: it/di/unipi/iochatto/chat/ChatServiceImpl.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.chat;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.util.Hashtable;
import java.util.Vector;

import net.jxta.document.Advertisement;

import net.jxta.exception.PeerGroupException;

import net.jxta.id.ID;

import net.jxta.impl.protocol.ResolverQuery;
import net.jxta.impl.protocol.ResolverResponse;

import net.jxta.peergroup.PeerGroup;

import net.jxta.pipe.PipeID;

import net.jxta.protocol.ModuleImplAdvertisement;
import net.jxta.protocol.PipeAdvertisement;
import net.jxta.protocol.ResolverQueryMsg;
import net.jxta.protocol.ResolverResponseMsg;

import net.jxta.resolver.QueryHandler;
import net.jxta.resolver.ResolverService;

import net.jxta.service.Service;



/**
 * The implementation of the ChatService interface. This service
 * builds on top of the Resolver service to provide the functionality 
 * for requesting and approving a chat session.
 */
public class ChatServiceImpl implements ChatService, QueryHandler
{
    /**
     * The Module Specification ID for the Chat service.
     */
    public static final String refModuleSpecID = 
        "urn:jxta:uuid-F84F9397891240B496D1B5754CCC9331DFFD"
        + "10CDD5A140A6B8A1BC18CD65582106";
    
    /**
     * The set of listener objects registered with the service
     * to handle an approval to start a chat session. These
     * listeners are associated with a specific query ID used
     * to send a request to start a chat session to a remote user.
     */
    private Hashtable approvedListeners = new Hashtable();
    
    /**
     * The handler name used to register the Resolver handler.
     */
    private String handlerName = null;
    
    /**
     * The Module Implementation advertisement for this service.
     */
    private Advertisement implAdvertisement = null;

    /**
     * The local Peer ID.
     */
    private String localPeerID = null;
    
    /**
     * The peer group to which the service belongs.
     */
    private PeerGroup peerGroup = null;
    
    /**
     * A unique query ID that can be used to track a query.
     * This is constant across instances of the service on the
     * same peer to ensure queryID uniqueness for the peer.
     */
    private static int queryID = 0;
    
    /**
     * The set of listener objects registered with the service
     * to handle requests to start a chat session.
     */
    private Vector requestListeners = new Vector();
 
    /**
     * The Resolver service used to handle queries and responses.
     */
    private ResolverService resolver = null;


    /**
     * Create a new ChatServiceImpl object.
     */
    public ChatServiceImpl()
    {
        super();
    }

    /**
     * Add a listener object to the service. When a new Initiate Chat
     * Request or Response Message arrives, the service will notify each 
     * registered listener. This method is synchronized to prevent multiple 
     * threads from altering the set of registered listeners simultaneously.
     *
     * @param   listener the listener object to register with the service.
     */
    public synchronized void addListener(ChatListener listener)
    {
        requestListeners.addElement(listener);
    }

    /**
     * Approve a chat session.
     * 
     * @param   pipeAdvertisement the advertisement for the pipe that will 
     *         be used to set up the chat session.
     * @param   emailAddress the emailAddress of the user associated with
     *         local peer.
     * @param   displayName the name of the user associated with the local
     *         peer.
     * @param   queryID the query ID to use to send to the Resolver Response
     *          Message containing the response, allowing the remote peer to
     *         match the response to an initial request.
     */
    public void approveChat(PipeAdvertisement pipeAdvertisement, 
        String emailAddress, String displayName, int queryID)
    {
        // Make sure that the service has been started.
        if (resolver != null)
        {
            ResolverResponse response;
            
            // Create the response message and populate it with the
            // given Pipe ID.
            InitiateChatResponse reply = new InitiateChatResponse();
            reply.setPipeAdvertisement(pipeAdvertisement);
            reply.setEmailAddress(emailAddress);
            reply.setName(displayName);

            // Wrap the response message in a resolver response message.
            // The following is the old API way of creating a ResolverResponse,
            // eliminated in the new (build 65e) stable release.
            //response = new ResolverResponse(handlerName, "JXTACRED", 
            //    queryID, reply.toString());
            // Here is the new way:
            response = new ResolverResponse(handlerName, null, 
                queryID, reply.toString());

            // Send the request using the Resolver service.    
            resolver.sendResponse(null, response);
        }
    }

    /**
     * Returns the advertisement for this service. In this case, this is 
     * the ModuleImplAdvertisement passed in when the service was 
     * initialized.
     *
     * @return  the advertisement describing this service.
     */
    public Advertisement getImplAdvertisement()
    {
        return implAdvertisement;
    }

    /**
     * Returns an interface used to protect this service.
     *
     * @return  the wrapper object to use to manipulate this service.
     */
    public Service getInterface()
    {
        // We don't really need to provide an interface object to protect
        // this service, so this method simply returns the service itself.
        return this;
    }

    /**
     * Initialize the service.
     *
     * @param       group the PeerGroup containing this service.
     * @param       assignedID the identifier for this service.
     * @param       implAdv the advertisement specifying this service.
     * @exception   PeerGroupException is not thrown ever by this
     *             implementation.
     */
    public void init(PeerGroup group, ID assignedID, Advertisement implAdv)
        throws PeerGroupException
    {
        // Save a reference to the group of which that this service is 
        // a part.
        peerGroup = group;
        
        // Use the assigned ID as the Resolver handler name.
        handlerName = assignedID.toString();

        // Save the module's implementation advertisement.
        implAdvertisement = (ModuleImplAdvertisement) implAdv;

        // Get the local Peer ID.
        localPeerID = group.getPeerID().toString();
    }

    /**
     * Processes the Resolver query message and returns a response. 
     *
     * @param       query the Resolver Query Message to be processed.
     * @return      an integer representing the success of processing,
     *              or need to repropagate the query.
     */
    public int processQuery(ResolverQueryMsg query)
    {
        ResolverResponse response;
        InitiateChatRequest request;

        try
        {
            // Extract the request message.
            request = new InitiateChatRequest(
                new ByteArrayInputStream((query.getQuery()).getBytes()));

            // Notify each of the registered listeners.
            if (requestListeners.size() > 0)
            {
                ChatListener listener = null;

                for (int i = 0; i < requestListeners.size(); i++)
                {
                    listener = (ChatListener) requestListeners.elementAt(i);
                    listener.chatRequested(request, query.getQueryId());
                }
            }

            // Return ResolverService.OK - note that this service has not 
            // produced a InitiateChatResponse. It's the responsibility of a 
            // ChatListener to decide whether to accept the request to chat 
            // and inform the requestor of the Pipe ID to use to send chat
            // messages.
            return resolver.OK;
        }
        catch (IOException e)
        {
            // Not the expected format of the message.
            // Just ignore the message.
            return resolver.OK;
        }
    }

    /**
     * Process a Resolver response message.
     *
     * @param   response a response message to be processed.
     */
    public void processResponse(ResolverResponseMsg response)
    {
        InitiateChatResponse reply;
        ChatListener listener = null;
        String responseString = response.getResponse();

        if (null != responseString)
        {
            try
            {
                // Extract the message from the Resolver response.
                reply = new InitiateChatResponse(
                    new ByteArrayInputStream(responseString.getBytes()));

                // Notify the listener associated with the response's
                // queryID.
                listener = (ChatListener) approvedListeners.get(
                    new Integer(response.getQueryId()));
                if (listener != null)
                {
                    listener.chatApproved(reply);
                }
            }
            catch (Exception e)
            {
                // This is not the right type of response message, or
                // the message is improperly formed. Ignore the exception;
                // do nothing with the message.
                System.out.println("Error in response: " + e);
            }
        }
    }

    /**
     * Remove a given listener object from the service. Once removed, 
     * a listener will no longer be notified when a new Initiate Chat 
     * Request or Response Message arrives. This method is synchronized to 
     * prevent multiple threads from altering the set of registered 
     * listeners simultaneously.
     *
     * @param   listener the listener object to unregister.
     */
    public synchronized boolean removeListener(ChatListener listener)
    {
        return requestListeners.removeElement(listener);
    }

    /**
     * Send a request to chat to the peer specified.
     *
     * @param   peerID the Peer ID of the remote peer to request for a 
     *          chat session.
     * @param   emailAddress the email address of the user associated 
     *          with the local peer.
     * @param   displayName the display name of the user associated with 
     *          the local peer.
     * @param   listener the listener to notify when a response to this 
     *          request is received.
     */
    public void requestChat(String peerID, String emailAddress, 
        String displayName, ChatListener listener)
    {
        // Make sure that the service has been started.
        if (resolver != null)
        {
            // Create the request object.
            String localPeerID = peerGroup.getPeerID().toString();
            InitiateChatRequest request = new InitiateChatRequest();

            // Configure the request.
            request.setEmailAddress(emailAddress);
            request.setName(displayName);
            
            // Wrap the query in a Resolver Query Message.
            // The following is the old API way of creating a ResolverResponse,
            // eliminated in the new (build 65e) stable release.
            //ResolverQuery query = new ResolverQuery(handlerName,
            //    "JXTACRED", localPeerID, request.toString(), queryID++);
            // Here is the new way:
            ResolverQuery query = new ResolverQuery(handlerName,
                null, localPeerID, request.toString(), queryID++);

            // Add the given listener to the set of approved listeners.
            // This will be used to ensure that only responses to actual
            // queries sent by this service will be passed to the given
            // listener.
            approvedListeners.put(
                new Integer(query.getQueryId()), listener);
            
            // Send the request to the peer using the Resolver service.    
            resolver.sendQuery(peerID, query);
        }
    }

    /**
     * Start the service.
     *
     * @param   args the arguments to the service. Not used.
     * @return  0 to indicate the service started.
     */
    public int startApp(String[] args)
    {
        // Now that the service is being started, set the ResolverService
        // object to use handle our queries and send responses.
        resolver = peerGroup.getResolverService();

        // Add ourselves as a handler using the uniquely constructed
        // handler name.
        resolver.registerHandler(handlerName, this);
 
        return 0;
    }

    /**
     * Stop the service.
     */
    public void stopApp()
    {
        if (resolver != null)
        {
            // Unregister ourselves as a listener.
            resolver.unregisterHandler(handlerName);
            resolver = null;

            // Empty the set of request and approved listeners.
            requestListeners.removeAllElements();
        }
    }
}