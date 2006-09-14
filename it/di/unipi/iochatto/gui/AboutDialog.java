/* FileName: it/di/unipi/iochatto/gui/AboutDialog.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;


public class AboutDialog extends JDialog {
	public AboutDialog(Frame frame)
	{
		super(frame,false);
		draw();
	}
	private void draw()
	{
		JLabel myfoto = new JLabel();
		//setPreferredSize(new Dimension(300,400));
		myfoto.setIcon(new ImageIcon(new File("pixmaps/smalljo.png").getAbsolutePath()));
		JPanel p = new JPanel();
		Border b1 = new BevelBorder(BevelBorder.LOWERED);
		Border b2 = new EmptyBorder(5,5,5,5);
		myfoto.setBorder(new CompoundBorder(b1,b2));
		p.add(myfoto);
		getContentPane().add(p, BorderLayout.WEST);
		String message = "IoChatto! A JXTA P2P Chat Client\n(c) 2006 - Giorgio Zoppi\nP2P Course Final Term\nPisa University\nemail: zoppi@cli.di.unipi.it";
		JTextArea txt = new JTextArea(message);
		txt.setEditable(false);
		txt.setFont(new Font("Helvetica",Font.BOLD,12));
		txt.setBackground(getBackground());
		p = new JPanel();
		p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
		p.add(txt);
		final JButton okButton = new JButton("OK");
		p.add(okButton);
		ActionListener lst = new ActionListener() {
		  public void actionPerformed(ActionEvent e)
		  {
			dispose();  
		  }
		};
		okButton.addActionListener(lst);
		getContentPane().add(p, BorderLayout.CENTER);
		
	}
	
}
