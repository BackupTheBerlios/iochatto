/* FileName: it/di/unipi/iochatto/gui/StatusPanel.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.gui; 

import it.di.unipi.iochatto.core.ChatModel;
import it.di.unipi.iochatto.core.StatusEvent;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JEditorPane;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ScrollPaneConstants;

public class StatusPanel extends JPanel implements Observer {
	ChatModel model = null;
	JScrollPane sp;
	JEditorPane editPane;
	HtmlBuffer buf = new HtmlBuffer();
	private StatusPanel ref = null;
	public StatusPanel(ChatModel m)
	{	super();
		model = m;
		initGUI();
	}
	private void initGUI()
    {
    	//setLayout(GBL);
		//Container c = getRootPane();
    	setPreferredSize(new Dimension(640,480));
    	setLayout(new BorderLayout());
    	editPane = new JEditorPane();
    	sp = new JScrollPane(editPane);
    	editPane.setContentType("text/html");
    	editPane.setBackground(Color.WHITE);
    	editPane.setEditable(false);
    	editPane.setText("Status Information");
    	//sp.setPreferredSize(super.getParent().getSize());
    	add(sp, BorderLayout.CENTER);
    }
	
	
	public void update(Observable o, Object arg) {
		if (arg instanceof StatusEvent)
		{
			StatusEvent ev = (StatusEvent) arg;
			buf.appendHTMLLine("<tr><td><font size=\"3\" face=\"Verdana,Geneva,Arial,Helvetica,sans-serif\" color=\"red\">"+ev.reason+"</font></td></tr>");
			editPane.setText(buf.toString());
		}
	}

}
