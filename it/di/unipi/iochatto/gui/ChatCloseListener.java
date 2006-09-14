/* FileName: it/di/unipi/iochatto/gui/ChatCloseListener.java Date: 2006/09/13 22:01
* IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.swing.JButton;

import it.di.unipi.iochatto.core.ChatModel;
import it.di.unipi.iochatto.gui.tabbedpane.CloseAndMaxTabbedPane;
import it.di.unipi.iochatto.gui.tabbedpane.CloseListener;

public class ChatCloseListener implements CloseListener,ActionListener {
    private ChatModel model = null;
    private CloseAndMaxTabbedPane tabpane = null;
	private Map tabhash = null;
	private Logger L = Logger.getLogger(ChatCloseListener.class.getName());
	private int tabIndex = -1;
	private String name = null;
	public ChatCloseListener(CloseAndMaxTabbedPane tabPane2, ChatModel m) {
		 this.tabpane = tabPane2;
		 this.model = m;
	}
	public ChatCloseListener(CloseAndMaxTabbedPane parent, ChatModel m, int tabIndex) {
		 this.tabpane = parent;
		 this.model = m;
		 this.tabIndex = tabIndex;
	}
	public ChatCloseListener(String name, CloseAndMaxTabbedPane parent, ChatModel m) {
		this.name = name;
		 this.tabpane = parent;
		 this.model = m;
		
	}
	public void setHashRef(Map map)
	{
		tabhash = map;
	}
	public void updateTabPane(CloseAndMaxTabbedPane tabPane)
	{
		this.tabpane = tabPane;
	}

	public void closeOperation(MouseEvent e) {
		int idx = tabpane.getOverTabIndex();
		L.info("Object Name: " +e.getSource().toString());
		L.info("Tabpane index =" + idx);
		ChannelPanel pan = (ChannelPanel) tabpane.getComponentAt(idx);
		String chName = pan.getName();
		L.info("Component Name = " + chName);
		model.leaveChannel(chName);
		model.deleteObserver(pan);
		L.info("Removing index = " + idx + " ChannelName = "+ chName);
		tabpane.remove(idx);		
		if (tabhash!=null)
			tabhash.remove(chName);
	}
	//  per i button...
	public void actionPerformed(ActionEvent e) {
		
		int idx = -1;
		String nome ="";
		for (int k = 0; k < tabpane.getTabCount(); ++k)
		{
			nome = tabpane.getTitleAt(k).trim();
			if (nome.equals(name))
			{
				idx = k;
				break;
			}
		}
		L.info("Tabpane index =" + idx);
		ChannelPanel pan = (ChannelPanel) tabpane.getComponentAt(idx);
		String chName = pan.getName();
		L.info("Component Name = " + chName);
		model.leaveChannel(chName);
		model.deleteObserver(pan);
		L.info("Removing index = " + idx + " ChannelName = "+ chName);
		tabpane.remove(idx);		
	}

}
