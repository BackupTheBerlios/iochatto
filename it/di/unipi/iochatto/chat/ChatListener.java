/* FileName: it/di/unipi/iochatto/chat/ChatListener.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.chat;




/**
 * An interface to encapsulate an object that listens for notification
 * from the ChatService of newly arrived requests for a chat session and
 * responds to requests for a chat session.
 */
public interface ChatListener
{
    /**
     * Notify the listener that a chat session has been approved.
     *
     * @param   response the response to the request for a chat session.
     */
    public void chatApproved(InitiateChatResponseMessage response);

    /**
     * Notify the listener that a chat session has been requested.
     *
     * @param   request the object containing the chat session request info.
     * @param   queryID the query ID from the Resolver Query Message used to
     *         send the request.
     */
    public void chatRequested(InitiateChatRequestMessage request, 
        int queryID);
}

