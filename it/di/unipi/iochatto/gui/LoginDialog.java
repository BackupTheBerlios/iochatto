/* FileName: it/di/unipi/iochatto/gui/LoginDialog.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.gui;

import it.di.unipi.iochatto.core.ChatModel;
import it.di.unipi.iochatto.core.mainApp;

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
public class LoginDialog extends JDialog {
	private JTextField userName = new JTextField(20);
	private JTextField email = new JTextField(20);
	private JPasswordField password = new JPasswordField(20);
	private JButton connectButton = new JButton("Connetti...");
	private JButton cancelButton = new JButton("Cancella..");
	private Object presence[] = new String[] {
			"Online","Away","Offline","Busy"
	};
	private ChatModel model = null;
	private JComboBox box = null;
	//private JComboBox comboItem = new 
	public LoginDialog() throws HeadlessException {
		// TODO Auto-generated constructor stub
		setResizable(false);
	}

	public LoginDialog(Frame owner) throws HeadlessException {
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

	public LoginDialog(Dialog owner) throws HeadlessException {
		super(owner);
		setResizable(false);
		// TODO Auto-generated constructor stub
	}

	public LoginDialog(Frame owner, boolean modal) throws HeadlessException {
		super(owner, modal);
		setResizable(false);
		// TODO Auto-generated constructor stub
	}

	public LoginDialog(Frame owner, String title) throws HeadlessException {
		super(owner, title);
		setResizable(false);
		// TODO Auto-generated constructor stub
	}

	public LoginDialog(Dialog owner, boolean modal) throws HeadlessException {
		super(owner, modal);
		setResizable(false);
		// TODO Auto-generated constructor stub
	}

	public LoginDialog(Dialog owner, String title) throws HeadlessException {
		super(owner, title);
		setResizable(false);
		// TODO Auto-generated constructor stub
	}

	public LoginDialog(Frame owner, String title, boolean modal)
			throws HeadlessException {
		
		super(owner, title, modal);
		setResizable(false);
		// TODO Auto-generated constructor stub
	}

	public LoginDialog(Dialog owner, String title, boolean modal)
			throws HeadlessException {
		super(owner, title, modal);
		setResizable(false);
		// TODO Auto-generated constructor stub
	}

	public LoginDialog(Frame owner, String title, boolean modal,
			GraphicsConfiguration gc) {
		super(owner, title, modal, gc);
		setResizable(false);
		// TODO Auto-generated constructor stub
	}

	public LoginDialog(Dialog owner, String title, boolean modal,
			GraphicsConfiguration gc) throws HeadlessException {
		super(owner, title, modal, gc);
		setResizable(false);
	
		// TODO Auto-generated constructor stub
	}
	public LoginDialog(JFrame mainFrame, ChatModel model) {
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
		GridBagLayout GBL = new GridBagLayout();
		GridBagConstraints GBC = new GridBagConstraints();
		pane.setLayout(GBL);
		JLabel userLabel = new JLabel("Username:");
		JLabel emailLabel = new JLabel("Email:");
		JLabel passwordLabel = new JLabel("Password:");
		JLabel statusLabel = new JLabel("Status:");
		box = new JComboBox(presence);
		JLabel logo = new JLabel();
		logo.setIcon(new ImageIcon("pixmaps/loganim.gif"));
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
		GBL.setConstraints(userLabel, GBC);
		pane.add(userLabel);
		GBC.gridx = 2;
		GBC.gridy = 2;
		GBC.gridheight = 1;
		GBC.gridwidth = 5;
		GBC.fill = GridBagConstraints.HORIZONTAL;
		GBL.setConstraints(userName, GBC);
		pane.add(userName);
		// next
		GBC.gridx = 0;
		GBC.gridy = 4;
		GBC.gridheight = 1;
		GBC.gridwidth = 2;
		GBC.fill = GridBagConstraints.HORIZONTAL;
		GBL.setConstraints(emailLabel, GBC);
		pane.add(emailLabel);
		GBC.gridx = 2;
		GBC.gridy = 4;
		GBC.gridheight = 1;
		GBC.gridwidth = 5;
		GBC.fill = GridBagConstraints.HORIZONTAL;
		GBL.setConstraints(email, GBC);
		pane.add(email);
		// passwordlabel
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
		return pane;
	}
	private JPanel initButtonGroup()
	{
		JPanel pane = new JPanel(new FlowLayout());
		pane.add(connectButton);
		pane.add(cancelButton);
		ActionListener cancelButtonAction = new ActionListener()
		{

			public void actionPerformed(ActionEvent e) {
				dispose();
				
			}
			
		};
		cancelButton.addActionListener(cancelButtonAction);
	
		ActionListener connectButtonAction = new ActionListener()
		{

			public void actionPerformed(ActionEvent e) {
				//model.init();
				LoginDialog.this.setVisible(false);
				//LoginDialog.this.getParent().repaint();
				String user = userName.getText();
				String mail = email.getText();
				mail.trim();
				char[] pass = password.getPassword();
				String pass0 = new String(pass);
				boolean valid = (user!=null) && (mail!=null) && (pass!=null) && (user.length()>0)  && (mail.length()>0) && (pass.length>0) ; 
				//dispose();
				
				if (valid)
				{

				model.setCredentials(user, mail,pass0);
				Runnable service = new Runnable() {
					public void run() {
						model.init();

								} // end run

				
				};
	
				Thread work = new Thread(service);
				work.setDaemon(true);
				model.setInitThread(work);
				work.start();
				} else {
					JOptionPane.showMessageDialog(LoginDialog.this, "Dati di connessione non validi", "Errore connessione", JOptionPane.ERROR_MESSAGE);
				}
				 dispose();
					
			       
			}
		};
		//super.getParent().repaint();
		connectButton.addActionListener(connectButtonAction);
		return pane;
		
	}
	

}
