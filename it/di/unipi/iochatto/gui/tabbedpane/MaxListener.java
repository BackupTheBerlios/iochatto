/* FileName: it/di/unipi/iochatto/gui/tabbedpane/MaxListener.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
/*
 * David Bismut, davidou@mageos.com
 * Intern, SETLabs, Infosys Technologies Ltd. May 2004 - Jul 2004
 * Ecole des Mines de Nantes, France
 */


package it.di.unipi.iochatto.gui.tabbedpane;

import java.awt.event.MouseEvent;
import java.util.EventListener;

public interface MaxListener extends EventListener {
	public void maxOperation(MouseEvent e);
}
