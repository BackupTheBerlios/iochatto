/* FileName: it/di/unipi/iochatto/util/AdvBuilder.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownServiceException;

import net.jxta.document.AdvertisementFactory;
import net.jxta.id.IDFactory;
import net.jxta.platform.ModuleClassID;
import net.jxta.platform.ModuleSpecID;
import net.jxta.protocol.ModuleClassAdvertisement;
import net.jxta.protocol.ModuleImplAdvertisement;
import net.jxta.protocol.ModuleSpecAdvertisement;

public final class AdvBuilder {
    private static AdvBuilder instance = null;
	private AdvBuilder() {
    }
	public static AdvBuilder getInstance()
	{
		if (instance == null)
    	{
			instance = new AdvBuilder();
			
    	}
    
	return instance;	
	}
	public ModuleClassAdvertisement createModuleClassAdv(
			String moduleClassID, String name, String description) 
	throws  URISyntaxException
	{
		// Create the Module Class Advertisement.
		ModuleClassAdvertisement moduleClassAdv = 
			(ModuleClassAdvertisement) 
			AdvertisementFactory.newAdvertisement(
					ModuleClassAdvertisement.getAdvertisementType());
		 ModuleClassID classID = (ModuleClassID) IDFactory.fromURI(
			        new URI((moduleClassID)));
		// Configure the Module Class Advertisement.
		moduleClassAdv.setDescription(description);
		moduleClassAdv.setModuleClassID(classID);
		moduleClassAdv.setName(name);


		// Return the advertisement to the caller.
		return moduleClassAdv;
	}

	/**
	 * Creates a Module Implementation Advertisement for the service using
	 * the specification ID in the passed in ModuleSpecAdvertisement 
	 * advertisement.  Use the given ModuleImplAdvertisement to create the 
	 * compatibility element of the Module Implementation Advertisement.
	 *
	 * @param       groupImpl the ModuleImplAdvertisement of the parent 
	 *             peer group.
	 * @param       moduleSpecAdv the source of the specification ID.
	 * @param       description of the module implementation.
	 * @param       code the fully qualified name of the module
	 *             implementation's class.
	 * @return      the generated Module Implementation Advertisement.
	 */
	public ModuleImplAdvertisement createModuleImplAdv(
			ModuleImplAdvertisement groupImpl, 
			ModuleSpecAdvertisement moduleSpecAdv, 
			String description, String code)
	{
		// Get the specification ID from the passed advertisement.
		ModuleSpecID specID = moduleSpecAdv.getModuleSpecID();

		// Create the Module Implementation Advertisement.
		ModuleImplAdvertisement moduleImplAdv = 
			(ModuleImplAdvertisement) AdvertisementFactory.newAdvertisement(
					ModuleImplAdvertisement.getAdvertisementType());

		// Configure the Module Implementation Advertisement.
		moduleImplAdv.setCode(code);
		moduleImplAdv.setCompat(groupImpl.getCompat());
		moduleImplAdv.setDescription(description);
		moduleImplAdv.setModuleSpecID(specID);
		moduleImplAdv.setProvider("Giorgio Zoppi");
		moduleImplAdv.setUri("http://www.penguin.it/~giorgio/jxta.jar");
		// Return the advertisement to the caller.
		return moduleImplAdv;
	}

	/**
	 * Creates a Module Specification Advertisement using the 
	 * given parameters.
	 *
	 * @param       moduleSpecID the Module Specification ID for 
	 *             the advertisement.
	 * @param       name the symbolic name of the advertisement.
	 * @param       description the description of the advertisement.
	 * @exception   UnknownServiceException if the moduleSpecID string 
	 *             is malformed.
	 * @exception   MalformedURLException if the moduleSpecID string 
	 *             is malformed.
	 */
	public ModuleSpecAdvertisement createModuleSpecAdv(String moduleSpecID,
			String name, String description) 
	throws URISyntaxException
	{
		// Create the specification ID from the refModuleSpecID string.
		 ModuleSpecID specID = (ModuleSpecID) IDFactory.fromURI(
	        new URI((moduleSpecID)));
		
		// Create the Module Specification Advertisement.
		ModuleSpecAdvertisement moduleSpecAdv = 
			(ModuleSpecAdvertisement) AdvertisementFactory.newAdvertisement(
					ModuleSpecAdvertisement.getAdvertisementType());

		// Configure the Module Specification Advertisement.
		moduleSpecAdv.setCreator("Giorgio Zoppi");
		moduleSpecAdv.setModuleSpecID(specID);
		moduleSpecAdv.setDescription(description);
		moduleSpecAdv.setName(name);
		moduleSpecAdv.setSpecURI(
				"http://www.penguin.it/~giorgio/jxta.jar");
		moduleSpecAdv.setVersion("1.0");

		// Return the advertisement to the caller.
		return moduleSpecAdv;
	}

}
