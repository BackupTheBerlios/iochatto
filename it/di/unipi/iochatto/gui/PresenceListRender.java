/* FileName: it/di/unipi/iochatto/gui/PresenceListRender.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.gui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class PresenceListRender extends JLabel implements ListCellRenderer {
/**
	 * 
	 */
	  private  String[] imgNames = new String[]
                                              {
                                           	   "pixmaps/busy.png",
                                           	   "pixmaps/away.png",
                                           	   "pixmaps/online.png",
                                           	   "pixmaps/offline.png"
                                           	   
                                              };
   private   ImageIcon[] images = new ImageIcon[]
                                                {
   												new ImageIcon(imgNames[3]),
   												new ImageIcon(imgNames[2]),
   												new ImageIcon(imgNames[0]),
   												new ImageIcon(imgNames[1])
                                                };
 
	private static final long serialVersionUID = 681912287479583673L;
public PresenceListRender(){
	super();
}

public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
	// TODO Auto-generated method stub
	//super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
	UserInfo l = (UserInfo) value;
	//setIcon(images[l.getStatus()]);
	if (isSelected)
	{
		setBackground(list.getSelectionBackground());
		setForeground(list.getSelectionForeground());
	} else {
		setBackground(list.getBackground());
		setForeground(list.getForeground());
	}
	setIcon(images[l.getStatus()]);
	setFont(list.getFont());
	setText(l.getUser());
	return this;

}
}