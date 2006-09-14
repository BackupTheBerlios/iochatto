/* FileName: it/di/unipi/iochatto/presence/PeersUpdateListener.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.presence;

import java.util.EventListener;


public interface PeersUpdateListener extends EventListener {
	public void update(PeersUpdate ev);
}
