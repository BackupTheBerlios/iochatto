/* FileName: it/di/unipi/iochatto/chat/InitiateChatResponse.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.chat;

import java.io.InputStream;
import java.io.IOException;
import java.io.StringWriter;

import java.util.Enumeration;

import net.jxta.document.AdvertisementFactory;
import net.jxta.document.Element;
import net.jxta.document.Document;
import net.jxta.document.MimeMediaType;
import net.jxta.document.StructuredDocument;
import net.jxta.document.StructuredDocumentUtils;
import net.jxta.document.StructuredDocumentFactory;
import net.jxta.document.StructuredTextDocument;
import net.jxta.document.TextElement;

import net.jxta.protocol.PipeAdvertisement;



/**
 * An implementation of the InitiateChatResponseMessage abstract class. 
 * This class is responsible for parsing and formatting the XML document 
 * used to define a response to a request to initiate a chat session.
 */
public class InitiateChatResponse extends InitiateChatResponseMessage
{
    /**
     * The root element for the response's XML document.
     */
    private static final String documentRootElement = 
        "InitiateChatResponse";
    
    /**
     * A convenient constant for the XML MIME type.
     */
    private static final String mimeType = "text/xml";

    /**
     * The element name for the display name info.
     */
    private static final String tagName = "Name";

    /**
     * The element name for the email address info.
     */
    private static final String tagEmailAddress = "EmailAddress";

        
    /**
     * Creates new response object.
     */
    public InitiateChatResponse()
    {
        super();
    }

    /**
     * Creates a new Initiate Chat Response Message by parsing the given 
     * stream.
     *
     * @param       stream the InputStream source of the response data.
     * @exception   IOException if the response can't be parsed from the
     *             stream.
     * @exception   IllegalArgumentException thrown if the data does not
     *             contain a Presence Response Message.
     */
    public InitiateChatResponse(InputStream stream) throws IOException,
        IllegalArgumentException
    {
        super();

        StructuredTextDocument document = (StructuredTextDocument)
            StructuredDocumentFactory.newStructuredDocument(
                new MimeMediaType(mimeType), stream);

        readDocument(document);
    }

    /**
     * Returns a Document object containing the response's document tree.
     *
     * @param       asMimeType the desired MIME type for the response 
     *             rendering.
     * @return      the Document containing the response's document 
     *             object tree.
     * @exception   IllegalArgumentException thrown if the Pipe
     *             Advertisement 
     *              or the name is null.
     */
    public Document getDocument(MimeMediaType asMimeType) 
        throws IllegalArgumentException
    {
        // Check that the required elements are present. 
        if ((null != getPipeAdvertisement()) && (null != getName()))
        {
            StructuredDocument document = (StructuredTextDocument) 
                StructuredDocumentFactory.newStructuredDocument(
                    asMimeType, documentRootElement);
            Element element;

            PipeAdvertisement pipeAdv = getPipeAdvertisement();
            if (pipeAdv != null)
            {
                StructuredTextDocument advDoc = (StructuredTextDocument)
                    pipeAdv.getDocument(asMimeType);
                StructuredDocumentUtils.copyElements(
                    document, document, advDoc);
            }

            element = document.createElement(tagName, getName());
            document.appendChild(element);
            
            element = document.createElement(tagEmailAddress,
                getEmailAddress());
            document.appendChild(element);
            
            return document;
        }
        else
        {
            throw new IllegalArgumentException("Missing pipe ID or name!");
        }
    }

    /**
     * Parses the given document tree for the response.
     *
     * @param       document the object containing the response data.
     * @exception   IllegalArgumentException if the document is not a 
     *             response, as expected.
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
                
                if (element.getName().equals(tagName))
                {
                    setName(element.getTextValue());
                    continue;
                }
                
                if (element.getName().equals(tagEmailAddress))
                {
                    setEmailAddress(element.getTextValue());
                    continue;
                }

                if (element.getName().equals(
                    PipeAdvertisement.getAdvertisementType()))
                {
                    try
                    {
                        PipeAdvertisement pipeAdv = (PipeAdvertisement)
                            AdvertisementFactory.newAdvertisement(element);
                        setPipeAdvertisement( pipeAdv );
                    }
                    catch ( ClassCastException wrongAdv )
                    {
                        throw new IllegalArgumentException(
                            "Bad pipe advertisement in advertisement" );
                    }

                    continue;
                }
            }
        }
        else
        {
            throw new IllegalArgumentException(
                "Not a InitiateChatResponse document!");
        }
    }

    /**
     * Returns an XML String representation of the response.
     *
     * @return  the XML String representing this response.
     */
    public String toString()
    {
        try
        {
            StringWriter out = new StringWriter();
            StructuredTextDocument doc = (StructuredTextDocument) 
                getDocument(new MimeMediaType(mimeType));
            doc.sendToWriter(out);

            return out.toString();
        }
        catch (Exception e)
        {
            return "";
        }
    }
}