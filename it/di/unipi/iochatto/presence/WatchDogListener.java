/* FileName: it/di/unipi/iochatto/presence/WatchDogListener.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.presence;

import java.util.EventListener;

public interface WatchDogListener extends EventListener {
public void  fireUpdate(WatchDogEvent ev);
}
