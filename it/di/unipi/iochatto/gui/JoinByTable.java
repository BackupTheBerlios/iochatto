/* FileName: it/di/unipi/iochatto/gui/JoinByTable.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.gui;

import it.di.unipi.iochatto.core.ChatModel;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;

public class JoinByTable extends MouseAdapter {
	ChatModel model = null;
	FindDialog mainframe = null;
	public JoinByTable(ChatModel m, FindDialog main)
	{
		model = m;
		mainframe = main;
	}
	   public void mousePressed(MouseEvent e)
	    {
	        JTable table = (JTable)e.getSource();
	        Point p = e.getPoint();
	        if(e.getClickCount() == 2)
	        {
	        int row = table.rowAtPoint(p);
	       // int col = table.columnAtPoint(p);
	        String chanName = (String)table.getValueAt(row,0);
	    	char ch0='#';
	    	if ( (chanName != null) && (chanName.charAt(0) == ch0) )
			{
	    		mainframe.setStatusBar("Joining the channel " + chanName);
	    		Thread joinRun = new Thread(chanName) {
				public void run() {
					model.joinChannel(getName());
				}
				};
				joinRun.start();
				
			}
			else {
				JOptionPane.showMessageDialog(mainframe, "Nome Canale non valido", "Errore", JOptionPane.ERROR_MESSAGE);
			}
	        }
	        //ChannelTableData model = (ChannelTableData) table.getModel();
	        //model.getV
	    }
}
