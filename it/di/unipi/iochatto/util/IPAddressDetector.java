/* FileName: it/di/unipi/iochatto/util/IPAddressDetector.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.util;
import java.net.*;
import java.util.Enumeration;
public class IPAddressDetector {
	private String ipAddress = "127.0.0.1";
	public IPAddressDetector()
	{
		init();
	}
private void init()
{
    Enumeration interfaces = null;
    try {
         interfaces = java.net.NetworkInterface.getNetworkInterfaces();
    } catch (Exception e) {
    	ipAddress = "127.0.0.1";
    }
    if (interfaces!=null)
    {
    	while (interfaces.hasMoreElements())
    	{
    		java.net.NetworkInterface iface = (java.net.NetworkInterface) (interfaces.nextElement());
			if (!iface.getDisplayName().startsWith("lo"))
			{
    		Enumeration ee = iface.getInetAddresses();
    		System.out.println(iface.getDisplayName());
			while (ee.hasMoreElements()) {

				InetAddress addr = (InetAddress) (ee.nextElement());
				if (addr!=null)
					ipAddress = addr.getHostAddress();
			}
			}
    	}
    }
}
public String getIPAddress()
{
	return ipAddress;
}
}
