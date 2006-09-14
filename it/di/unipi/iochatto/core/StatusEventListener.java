/* FileName: it/di/unipi/iochatto/core/StatusEventListener.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.core;

import java.util.EventListener;

public interface StatusEventListener extends EventListener {
public void fireEvent(StatusEvent ev);
}
