/* FileName: it/di/unipi/iochatto/chat/InitiateChatResponseMessage.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.chat;

import net.jxta.document.Document;
import net.jxta.document.MimeMediaType;

import net.jxta.protocol.PipeAdvertisement;


/**
 * An abstract class defining a response to a request to begin a chat
 * session. This response is responsible for sending a Pipe Advertisement 
 * in response to a Initiate Chat Request Message to allow a remote peer 
 * to begin chatting with the local peer.
 */
public abstract class InitiateChatResponseMessage
{
    /**
     * The email address of the user associated with the local peer.
     * Used to map the user to presence information.
     */
    private String emailAddress = null;

    /**
     * A display name to use to represent the user associated with
     * the local peer during the chat session.
     */
    private String name = null;

    /**
     * A Pipe Advertisement to use to initiate the chat session. The
     * local peer will bind an input pipe to the pipe described by this
     * advertisement to set up the two-way chat communication channel 
     * using the BidirectionalPipeService.
     */
    private PipeAdvertisement pipeAdvertisement = null;

    
    /**
     * Returns a Document object containing the response's document tree.
     *
     * @param       asMimeType the desired MIME type for the response
     *              rendering.
     * @return      the Document containing the response's document 
     *              object tree.
     */
    public abstract Document getDocument(MimeMediaType asMimeType);

    /**
     * Retrieve the email address of the user associated with the 
     * local peer.
     * 
     * @return  the email address used to identify the user.
     */
    public String getEmailAddress()
    {
        return emailAddress;
    }

    /**
     * Retrieve the display name of the user associated with the local peer.
     * 
     * @return  the display name used to identify the user during the 
     * chat session.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Returns the Pipe Advertisement object that a remote peer can use to
     * initiate the chat session.
     *
     * @return  the Pipe Advertisement to use for setting up the chat
     *          session.
     */
    public PipeAdvertisement getPipeAdvertisement()
    {
        return pipeAdvertisement;
    }

    /**
     * Sets the email address of the user associated with the local peer.
     * 
     * @param   emailAddress the email address used to identify the user.
     */
    public void setEmailAddress(String emailAddress)
    {
        this.emailAddress = emailAddress;
    }

    /**
     * Sets the display name of the user associated with the local peer.
     * 
     * @param   name the display name used to identify the user during the 
     *          chat session.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Sets the Pipe Advertisement object that a remote peer can use to 
     * initiate the chat session.
     *
     * @param   pipeAdvertisement the Pipe Advertisement to use for setting 
     *          up the chat session.
     */
    public void setPipeAdvertisement(PipeAdvertisement pipeAdvertisement)
    {
        this.pipeAdvertisement = pipeAdvertisement;
    }
}