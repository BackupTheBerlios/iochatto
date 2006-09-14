/* FileName: it/di/unipi/iochatto/presence/PeerCache.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.presence;
import java.util.*;
/* TODO implement cache format:
   key   values
   PeerID Status Email
  Protocol for caching.
  Actors: Watchdog, Discovery
  
  Watchdog                            Discovery     
     getRemoteAdverisement
                 ---------------------->
 
    foreach PeerID in DiscoveredList :
            
              connectTo(PeerID)
	      sendMessage(PeerID,"PING");
	      // result should be so : PONG (emailAddress,Status)
	      result = getResultFrom(PeerID);
	      PeerCache.put(PeerID,emailAddress,Status);

 */
public class PeerCache implements Map
{
    public PeerCache(){ super(); }

	public void clear() {
		// TODO Auto-generated method stub
		
	}

	public boolean containsKey(Object key) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean containsValue(Object value) {
		// TODO Auto-generated method stub
		return false;
	}

	public Set entrySet() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object get(Object key) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	public Set keySet() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object put(Object key, Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	public void putAll(Map t) {
		// TODO Auto-generated method stub
		
	}

	public Object remove(Object key) {
		// TODO Auto-generated method stub
		return null;
	}

	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Collection values() {
		// TODO Auto-generated method stub
		return null;
	};
}
