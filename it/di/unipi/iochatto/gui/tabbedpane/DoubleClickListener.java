/* FileName: it/di/unipi/iochatto/gui/tabbedpane/DoubleClickListener.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/


package it.di.unipi.iochatto.gui.tabbedpane;

import java.awt.event.MouseEvent;
import java.util.EventListener;

public interface DoubleClickListener extends EventListener {
	public void doubleClickOperation(MouseEvent e);
}
