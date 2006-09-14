/* FileName: it/di/unipi/iochatto/gui/tabbedpane/CloseTabbedPaneEvent.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
/*
 * Created on Jun 23, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package it.di.unipi.iochatto.gui.tabbedpane;

import java.awt.Event;
import java.awt.event.MouseEvent;

/**
 * @author David_211245
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CloseTabbedPaneEvent extends Event {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4416505962683042042L;
	private String description;
	private MouseEvent e;
	private int overTabIndex;
	

	public CloseTabbedPaneEvent(MouseEvent e, String description, int overTabIndex){
		super(null, 0, null);
		this.e = e;
		this.description = description;
		this.overTabIndex = overTabIndex;
	}
	
	public String getDescription(){
		return description;
	}

	public MouseEvent getMouseEvent(){
		return e;
	}
	
	public int getOverTabIndex(){
		return overTabIndex;
	}
}
