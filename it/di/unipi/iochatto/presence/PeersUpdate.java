/* FileName: it/di/unipi/iochatto/presence/PeersUpdate.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.presence;
import java.util.EventObject;
import java.util.Vector;

public class PeersUpdate extends EventObject {


		/**
	 * 
	 */
	private static final long serialVersionUID = -755634339325528776L;
		private String eventName;
		/**
		 * 
		 */
		
		public PeersUpdate(String evname)
		{
			super(evname);
			eventName = evname;
		}
		public PeersUpdate(Object source) {
			super(source);
			// TODO Auto-generated constructor stub
		}
		public String getName() { return eventName;}
		public void setName(String name) { eventName = name; }
		

	}
