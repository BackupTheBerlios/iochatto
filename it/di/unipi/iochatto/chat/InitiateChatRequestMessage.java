/* FileName: it/di/unipi/iochatto/chat/InitiateChatRequestMessage.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.chat;

import net.jxta.document.Document;
import net.jxta.document.MimeMediaType;


/**
 * An abstract class defining a request to begin a chat session. This 
 * request is responsible for sending information on a peer/user requesting 
 * a chat session so that the recipient can use it to determine whether 
 * to send a response containing a Pipe Advertisement to use to 
 * start the chat session.
 */
public abstract class InitiateChatRequestMessage
{
    /**
     * The email address of the user requesting the chat session. This
     * is used to identify the user requesting the chat session.
     */
    private String emailAddress = null;

    /**
     * A display name to use to represent the user making the request
     * during the chat session.
     */
    private String name = null;


    /**
     * Returns a Document object containing the query's document tree.
     *
     * @param       asMimeType the desired MIME type for the query 
     *              rendering.
     * @return      the Document containing the query's document object 
     *              tree.
     */
    public abstract Document getDocument(MimeMediaType asMimeType);

    /**
     * Retrieve the email address of the user associated with the local 
     * peer.
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
     *         chat session.
     */
    public String getName()
    {
        return name;
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
     * @param   name the display name used to identify the user during 
     *          the chat session.
     */
    public void setName(String name)
    {
        this.name = name;
    }
}