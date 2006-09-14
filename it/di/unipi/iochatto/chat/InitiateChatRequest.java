/* FileName: it/di/unipi/iochatto/chat/InitiateChatRequest.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.chat;

import java.io.InputStream;
import java.io.IOException;
import java.io.StringWriter;

import java.util.Enumeration;

import net.jxta.document.Element;
import net.jxta.document.Document;
import net.jxta.document.MimeMediaType;
import net.jxta.document.StructuredDocument;
import net.jxta.document.StructuredDocumentFactory;
import net.jxta.document.StructuredTextDocument;
import net.jxta.document.TextElement;




/**
 * An implementation of the InitiateChatRequestMessage abstract class. This 
 * class is responsible for parsing and formatting the XML document used to 
 * define a request to initiate a chat session.
 */
public class InitiateChatRequest extends InitiateChatRequestMessage
{
    /**
     * The root element for the request's XML document.
     */
    private static final String documentRootElement = "InitiateChatRequest";
    
    /**
     * A convenient constant for the XML MIME type.
     */
    private static final String mimeType = "text/xml";

    /**
     * The element name for the email address info.
     */
    private static final String tagEmailAddress = "EmailAddress";

    /**
     * The element name for the display name info.
     */
    private static final String tagName = "Name";

    
    /**
     * Creates a new request object.
     */
    public InitiateChatRequest()
    {
        super();
    }

    /**
     * Creates a new Initiate Chat Request Message by parsing the 
     * given stream.
     *
     * @param       stream the InputStream source of the query data.
     * @exception   IOException if the query can't be parsed from the 
     *             stream.
     * @exception   IllegalArgumentException thrown if the data does not 
     *             contain a Presence Query Message.
     */
    public InitiateChatRequest(InputStream stream) 
        throws IOException, IllegalArgumentException
    {
        super();

        StructuredTextDocument document = (StructuredTextDocument)
            StructuredDocumentFactory.newStructuredDocument(
                new MimeMediaType(mimeType), stream);

        readDocument(document);
    }

    /**
     * Returns a Document object containing the request's document tree.
     *
     * @param       asMimeType the desired MIME type for the 
     *             request rendering.
     * @return      the Document containing the request's document 
     *             object tree.
     * @exception   IllegalArgumentException thrown if the email address 
     *             is null.
     */
    public Document getDocument(MimeMediaType asMimeType) 
        throws IllegalArgumentException
    {
        // Check that the required elements are present. 
        if (null != getEmailAddress())
        {
            StructuredDocument document = (StructuredTextDocument) 
                StructuredDocumentFactory.newStructuredDocument(
                    asMimeType, documentRootElement);
            Element element;
            
            element = document.createElement(tagEmailAddress,
                getEmailAddress());
            document.appendChild(element);
            
            element = document.createElement(tagName, getName());
            document.appendChild(element);
            
            return document;
        }
        else
        {
            throw new IllegalArgumentException("Missing email address");
        }
    }

    /**
     * Parses the given document tree for the request.
     *
     * @param       document the object containing the request data.
     * @exception   IllegalArgumentException if the document is not a chat
     *              request, as expected.
     */
    public void readDocument(TextElement document) 
        throws IllegalArgumentException
    {
        if (document.getName().equals(documentRootElement))
        {
            Enumeration elements = document.getChildren();

            while (elements.hasMoreElements())
            {
                TextElement element = (TextElement) elements.nextElement();
                
                if (element.getName().equals(tagEmailAddress))
                {
                    setEmailAddress(element.getTextValue());
                    continue;
                }
                
                if (element.getName().equals(tagName))
                {
                    setName(element.getTextValue());
                    continue;
                }
            }
        }
        else
        {
            throw new IllegalArgumentException(
                "Not a InitiateChatRequest document!");
        }
    }

    /**
     * Returns an XML String representation of the request.
     *
     * @return  the XML String representing this request.
     */
    public String toString()
    {
        try
        {
            StringWriter out = new StringWriter();
            StructuredTextDocument doc = 
                (StructuredTextDocument) getDocument(
                    new MimeMediaType(mimeType));
            doc.sendToWriter(out);

            return out.toString();
        }
        catch (Exception e)
        {
            return "";
        }
    }
}