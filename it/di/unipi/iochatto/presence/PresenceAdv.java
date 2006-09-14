/* FileName: it/di/unipi/iochatto/presence/PresenceAdv.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.presence;



import java.io.InputStream;
import java.io.IOException;
import java.io.StringWriter;

import java.util.Enumeration;


import net.jxta.document.Advertisement;
import net.jxta.document.AdvertisementFactory;
import net.jxta.document.Element;
import net.jxta.document.Document;
import net.jxta.document.MimeMediaType;
import net.jxta.document.StructuredDocument;
import net.jxta.document.StructuredDocumentFactory;
import net.jxta.document.StructuredDocumentUtils;
import net.jxta.document.StructuredTextDocument;
import net.jxta.document.TextElement;
import net.jxta.document.XMLElement;

import net.jxta.protocol.PipeAdvertisement;
import net.jxta.document.Attributable;

/**
 * An implementation of the PresenceAdvertisement abstract class. This 
 * class is responsible for parsing and formatting the XML document 
 * used to define presence information for a peer.
 */
public class PresenceAdv extends PresenceAdvertisement
{
    /**
     * A convenient constant for the XML MIME type.
     */
    private static final String mimeType = "text/xml";
    
    /**
     * The element name for the presence advertisement's email address info.
     */
    private static final String tagEmailAddress = "EmailAddress";
    
    /**
     * The element name for the presence advertisement's simple name info.
     */
    private static final String tagName = "Name";

    /**
     * The element name for the presence advertisement's Peer ID.
     */
    private static final String tagPeerID = "PeerID";

    /**
     * The element name for the presence advertisement's status info.
     */
    private static final String tagPresenceStatus = "PresenceStatus";

    
    /**
     * An Instantiator used by the AdvertisementFactory to instantiate
     * this class in an abstract fashion.
     */
    public static class Instantiator 
        implements AdvertisementFactory.Instantiator
    {
        /**
         * Returns the identifying type of this advertisement.
         *
         * @return  the name of the advertisement's root element.
         */
        public String getAdvertisementType()
        {
            return PresenceAdvertisement.getAdvertisementType();
        }
        
        /**
         * Returns a new PresenceAdvertisement implementation instance.
         *
         * @return  a new presence advertisement instance.
         */
        public Advertisement newInstance()
        {
            return new PresenceAdv();
        }
        
        /**
         * Instantiates a new PresenceAdvertisement implementation instance
         * populated from the given root element.
         *
         * @param   root the root of the object tree to use to populate the
         *          advertisement object.
         * @return  a new populated presence advertisement instance.
         */
        public Advertisement newInstance(Element root)
        {
            return new PresenceAdv(root);
        }
    };

    /**
     * Creates a new presence advertislog.info("Instantiated a Group Found!")ement.
     */
    public PresenceAdv()
    {
        super();
    }

    /**
     * Creates a new presence advertisement by parsing the given stream.
     *
     * @param       stream the InputStream source of the advertisement data.
     * @exception   IOException if the advertisement can't be parsed from 
     *             the stream.
     */
    public PresenceAdv(InputStream stream) throws IOException
    {
        super();

        StructuredTextDocument document = (StructuredTextDocument)
            StructuredDocumentFactory.newStructuredDocument(
                new MimeMediaType(mimeType), stream);

        readAdvertisement(document);
    }

    /**
     * Creates a new presence advertisement by parsing the given document.
     *
     * @param   document the source of the advertisement data.
     */
    public PresenceAdv(Element document) throws IllegalArgumentException
    {
        super();

        readAdvertisement((TextElement) document);
    }

    /**
     * Returns a Document object containing the advertisement's 
     * document tree.
     *
     * @param       asMimeType the desired MIME type for the 
     *              advertisement rendering.
     * @return      the Document containing the advertisement's document
     *              object tree.
     * @exception   IllegalArgumentException thrown if either the email
     *              address or the Peer ID is null.
     */
    public Document getDocument(MimeMediaType asMimeType) 
        throws IllegalArgumentException
    {
        // Check that the required elements are present. 
        if ((null != getEmailAddress()) && (null != getPeerID()))
        {
           
            StructuredDocument document = (StructuredTextDocument)
                StructuredDocumentFactory.newStructuredDocument(
                    asMimeType, getAdvertisementType());
            if (document instanceof Attributable)
            {
            	Attributable athDoc =(Attributable) document;
            	athDoc.addAttribute("xmlns:jxta", "http://jxta.org");
            }
            Element element;

            // Add the Peer ID information.            
            element = document.createElement(tagPeerID, getPeerID());
            document.appendChild(element);
            
            // Add the email address information.            
            element = document.createElement(
                tagEmailAddress, getEmailAddress());
            document.appendChild(element);

            // Add the display name information, if any.
            if (null != getName())
            {
                element = document.createElement(tagName, getName());
                document.appendChild(element);
            }

            // Add the presence status information.
            element = document.createElement(tagPresenceStatus,
                Integer.toString(getPresenceStatus()));
            document.appendChild(element);

            return document;
        }
        else
        {
            throw new IllegalArgumentException(
                "Missing email address or peer ID!");
        }
    }

    /**
     * Returns the set of fields that should be used for indexing this 
     * advertisement when it is being cached.
     *
     * @return  an array of Strings to use for indexing.
     */
    public String[] getIndexFields()
    {
        return new String[0];
    }
           
    public void readAdvertisement(XMLElement document) 
    throws IllegalArgumentException
{
    if (document.getName().equals(getAdvertisementType()))
    {
        Enumeration elements = document.getChildren();

        while (elements.hasMoreElements())
        {
            TextElement element = (TextElement) elements.nextElement();

            // Check for the email address element.                
            if (element.getName().equals(tagEmailAddress))
            {
                setEmailAddress(element.getTextValue());
                continue;
            }

            // Check for the display name element.
            if (element.getName().equals(tagName))
            {
                setName(element.getTextValue());
                continue;
            }

            // Check for the email address element.
            if (element.getName().equals(tagPresenceStatus))
            {
                setPresenceStatus(
                    Integer.parseInt(element.getTextValue()));
                continue;
            }

            // Check for the Peer ID element.                
            if (element.getName().equals(tagPeerID))
            {
                setPeerID(element.getTextValue());
                continue;
            }
        }
    }
    else
    {
        throw new IllegalArgumentException(
            "Not a PresenceAdvertisement document!");
    }
}

    /**
     * Parses the given document tree for the presence advertisement.
     *
     * @param       document the object containing the presence 
     *              advertisement data.
     * @exception   IllegalArgumentException if the document is not a
     *              presence advertisement, as expected.
     */
    public void readAdvertisement(TextElement document) 
        throws IllegalArgumentException
    {
        if (document.getName().equals(getAdvertisementType()))
        {
            Enumeration elements = document.getChildren();

            while (elements.hasMoreElements())
            {
                TextElement element = (TextElement) elements.nextElement();

                // Check for the email address element.                
                if (element.getName().equals(tagEmailAddress))
                {
                    setEmailAddress(element.getTextValue());
                    continue;
                }

                // Check for the display name element.
                if (element.getName().equals(tagName))
                {
                    setName(element.getTextValue());
                    continue;
                }

                // Check for the email address element.
                if (element.getName().equals(tagPresenceStatus))
                {
                    setPresenceStatus(
                        Integer.parseInt(element.getTextValue()));
                    continue;
                }

                // Check for the Peer ID element.                
                if (element.getName().equals(tagPeerID))
                {
                    setPeerID(element.getTextValue());
                    continue;
                }
            }
        }
        else
        {
            throw new IllegalArgumentException(
                "Not a PresenceAdvertisement document!");
        }
    }

    /**
     * Returns an XML String representation of the advertisement.
     *
     * @return  the XML String representing this advertisement.
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