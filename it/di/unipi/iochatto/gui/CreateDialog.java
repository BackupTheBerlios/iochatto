/* FileName: it/di/unipi/iochatto/gui/CreateDialog.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.gui;

import it.di.unipi.iochatto.core.ChatModel;
import it.di.unipi.iochatto.core.mainApp;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;
public class CreateDialog extends JDialog {
	private JTextField nomeCanale = new JTextField(20);
	private JTextField topic = new JTextField(20);
	private JButton makeButton = new JButton("Crea...");
	private JButton cancelButton = new JButton("Cancella..");
	private ChatModel model = null;
	//private JComboBox comboItem = new 
	public CreateDialog() throws HeadlessException {
		// TODO Auto-generated constructor stub
		setResizable(false);
	}

	public CreateDialog(Frame owner) throws HeadlessException {
		super(owner);
		setResizable(false);
		setPreferredSize(new Dimension(320,200));
	//	setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		Container c = getContentPane();
		setLayout(new BoxLayout(c,BoxLayout.Y_AXIS));
		//c.add(logo);
		c.add(initContentPanel());
		c.add(initButtonGroup());
		// TODO Auto-generated constructor stub
	}

	public CreateDialog(Dialog owner) throws HeadlessException {
		super(owner);
		setResizable(false);
		// TODO Auto-generated constructor stub
	}

	public CreateDialog(Frame owner, boolean modal) throws HeadlessException {
		super(owner, modal);
		setResizable(false);
		// TODO Auto-generated constructor stub
	}

	public CreateDialog(Frame owner, String title) throws HeadlessException {
		super(owner, title);
		setResizable(false);
		// TODO Auto-generated constructor stub
	}

	public CreateDialog(Dialog owner, boolean modal) throws HeadlessException {
		super(owner, modal);
		setResizable(false);
		// TODO Auto-generated constructor stub
	}

	public CreateDialog(Dialog owner, String title) throws HeadlessException {
		super(owner, title);
		setResizable(false);
		// TODO Auto-generated constructor stub
	}

	public CreateDialog(Frame owner, String title, boolean modal)
			throws HeadlessException {
		
		super(owner, title, modal);
		setResizable(false);
		// TODO Auto-generated constructor stub
	}

	public CreateDialog(Dialog owner, String title, boolean modal)
			throws HeadlessException {
		super(owner, title, modal);
		setResizable(false);
		// TODO Auto-generated constructor stub
	}

	public CreateDialog(Frame owner, String title, boolean modal,
			GraphicsConfiguration gc) {
		super(owner, title, modal, gc);
		setResizable(false);
		// TODO Auto-generated constructor stub
	}

	public CreateDialog(Dialog owner, String title, boolean modal,
			GraphicsConfiguration gc) throws HeadlessException {
		super(owner, title, modal, gc);
		setResizable(false);
	
		// TODO Auto-generated constructor stub
	}
	public CreateDialog(JFrame mainFrame, ChatModel model) {
		// TODO Auto-generated constructor stub
		super(mainFrame);
		this.model = model;
		setResizable(false);
		setPreferredSize(new Dimension(320,200));
	//	setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		Container c = getContentPane();
		setLayout(new BoxLayout(c,BoxLayout.Y_AXIS));
		//c.add(logo);
		c.add(initContentPanel());
		c.add(initButtonGroup());

	}

	private JPanel initContentPanel()
	{
		JPanel pane = new JPanel();
		//GridBagLayout GBL = new GridBagLayout();
		//GridBagConstraints GBC = new GridBagConstraints();
		pane.setLayout(new FlowLayout());
		JLabel channelLabel = new JLabel("Nome Canale:");
		JLabel topicLabel = new JLabel("Topic:");
	//	JLabel logo = new JLabel();
	//	logo.setIcon(new ImageIcon("pixmaps/loganim.gif"));
/*
		GBC.gridx = 2;
		GBC.gridy = 0;
		GBC.gridheight = 1;
		GBC.gridwidth = 1;
		GBC.fill = GridBagConstraints.BOTH;
		GBC.anchor = GridBagConstraints.NORTH;
		GBL.setConstraints(logo, GBC);
		pane.add(logo);
		// userlabel pos 0,0
		GBC.gridx = 0;
		GBC.gridy = 2;
		GBC.gridheight = 1;
		GBC.gridwidth = 2;
		GBC.fill = GridBagConstraints.HORIZONTAL;
		GBC.anchor = GridBagConstraints.NORTH;
		GBL.setConstraints(channelLabel, GBC);
		*/
		pane.add(channelLabel);
	/*	GBC.gridx = 2;
		GBC.gridy = 2;
		GBC.gridheight = 1;
		GBC.gridwidth = 5;
		GBC.fill = GridBagConstraints.HORIZONTAL;
		GBL.setConstraints(nomeCanale, GBC);
		*/
		pane.add(nomeCanale);
		// next
		/*
		GBC.gridx = 0;
		GBC.gridy = 4;
		GBC.gridheight = 1;
		GBC.gridwidth = 2;
		GBC.fill = GridBagConstraints.HORIZONTAL;
		GBL.setConstraints(topicLabel, GBC);
		*/
		pane.add(topicLabel);
		/*
		GBC.gridx = 2;
		GBC.gridy = 4;
		GBC.gridheight = 1;
		GBC.gridwidth = 5;
		GBC.fill = GridBagConstraints.HORIZONTAL;
		GBL.setConstraints(topic, GBC);
		*/
		pane.add(topic, BorderLayout.CENTER);
		/* passwordlabel
		GBC.gridx = 0;
		GBC.gridy = 5;
		GBC.gridheight = 1;
		GBC.gridwidth = 2;
		GBC.fill = GridBagConstraints.HORIZONTAL;
		GBL.setConstraints(passwordLabel, GBC);
		pane.add(passwordLabel);
		GBC.gridx = 2;
		GBC.gridy = 5;
		GBC.gridheight = 1;
		GBC.gridwidth = 1;
		GBC.fill = GridBagConstraints.HORIZONTAL;
		GBL.setConstraints(password, GBC);
		pane.add(password);
		GBC.gridx = 0;
		GBC.gridy = 6;
		GBC.gridheight = 1;
		GBC.gridwidth = 2;
		GBC.fill = GridBagConstraints.HORIZONTAL;
		GBL.setConstraints(statusLabel, GBC);
		pane.add(statusLabel);
		GBC.gridx = 2;
		GBC.gridy = 6;
		GBC.gridheight = 1;
		GBC.gridwidth = 5;
		GBC.fill = GridBagConstraints.HORIZONTAL;
		GBL.setConstraints(box, GBC);
		pane.add(box);
		*/
		return pane;
	}
	private JPanel initButtonGroup()
	{
		JPanel pane = new JPanel(new FlowLayout());
		pane.add(makeButton);
		pane.add(cancelButton);
		ActionListener cancelButtonAction = new ActionListener()
		{

			public void actionPerformed(ActionEvent e) {
				dispose();
				
			}
			
		};
		cancelButton.addActionListener(cancelButtonAction);
	
		ActionListener makeButtonAction = new ActionListener()
		{

			public void actionPerformed(ActionEvent e) {
				CreateDialog.this.setVisible(false);
				String channel = nomeCanale.getText(); 
				String topic0 = topic.getText();
				topic0.trim();
				boolean valid = (channel!=null) && (topic0!=null) && (channel.length()>0)  && (topic0.length()>0); 
				
				
				if (valid)
				{
					AsyncMakeChannel builder = new AsyncMakeChannel();
					builder.setChannelName(channel);
					builder.setTopic(topic0);
					builder.start();
			
				} else {
					JOptionPane.showMessageDialog(CreateDialog.this, "Dati canale non validi", "Errore creazione canale", JOptionPane.ERROR_MESSAGE);
				}
				 dispose();
					
			       
			}
		};
		//super.getParent().repaint();
		makeButton.addActionListener(makeButtonAction);
		return pane;
		
	}
	

}
