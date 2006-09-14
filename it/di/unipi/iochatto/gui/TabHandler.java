/* FileName: it/di/unipi/iochatto/gui/TabHandler.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.gui;

import java.awt.event.MouseEvent;
import java.util.HashMap;

import it.di.unipi.iochatto.gui.tabbedpane.CloseAndMaxTabbedPane;
import it.di.unipi.iochatto.gui.tabbedpane.CloseListener;

public class TabHandler implements CloseListener {

	private CloseAndMaxTabbedPane tab = null;
	public TabHandler(CloseAndMaxTabbedPane tab)
	{
		this.tab = tab;
	}
	public void closeOperation(MouseEvent e) {
		ChannelPanel panel = (ChannelPanel) e.getComponent();
		String nome = panel.getName();
		tab.remove(panel);

	}

}
