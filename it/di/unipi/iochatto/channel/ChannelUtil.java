/* FileName: it/di/unipi/iochatto/channel/ChannelUtil.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.channel;

import java.util.Enumeration;

import net.jxta.document.Advertisement;
import net.jxta.document.AdvertisementFactory;
import net.jxta.document.Element;
import net.jxta.document.MimeMediaType;
import net.jxta.document.TextElement;
import net.jxta.document.XMLElement;
import net.jxta.platform.ModuleSpecID;
import net.jxta.protocol.ModuleImplAdvertisement;
import java.util.logging.*;

public final class ChannelUtil {
	private static Logger log = Logger.getLogger(ChannelUtil.class.getName());
public static Advertisement parseModuleAdv(ModuleImplAdvertisement mia, String msID)
	{
    	Element param = (Element) ((ModuleImplAdvertisement) mia).getParam();
    	Element pel = null;
    if (param != null) {
        Enumeration list = param.getChildren();
        while (list!=null && list.hasMoreElements())
        {
        	// Get services
        	pel = (Element) list.nextElement();
        	Enumeration list1 = pel.getChildren();
        	// Get module implementation adv for services
        	XMLElement pel2 = null;
        	
        	if (list1!=null)
        	{
        		pel2 = (XMLElement) list1.nextElement();
        		ModuleImplAdvertisement tmp = (ModuleImplAdvertisement) AdvertisementFactory.newAdvertisement(pel2);
        		if (tmp!=null)
        		{
        			ModuleSpecID tmpSpec = tmp.getModuleSpecID();
        			if (tmpSpec.toString().equals(msID))
        			{
        				log.info("Found the Channel Service SpecID");
        				XMLElement chanParam = (XMLElement) tmp.getParam();
        				Enumeration chanChild = chanParam.getChildren();
        				if ((chanChild!=null) && chanChild.hasMoreElements())
        				{
        					XMLElement chanItem = (XMLElement) chanChild.nextElement();
        					Advertisement adv0 = AdvertisementFactory.newAdvertisement(chanItem);
        					if (adv0!=null)
        					log.info("Element = "+adv0.toString());
                			
        					return adv0;
        				}
        			}
        		}
        			
                
        	}
        	//Advertisement padv = (Advertisement) pel; 
            	
            	
        }
    }
    //Advertisement adv = (Advertisement)
      //      AdvertisementFactory.newAdvertisement((TextElement) pel);

		return null;
		
	}
}
