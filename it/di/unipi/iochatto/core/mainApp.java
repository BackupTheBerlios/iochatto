/* FileName: it/di/unipi/iochatto/core/mainApp.java Date: 2006/09/13 22:01
* IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.core;

import gnu.getopt.Getopt;
import it.di.unipi.iochatto.gui.AboutDialog;
import it.di.unipi.iochatto.gui.ChannelListDialog;
import it.di.unipi.iochatto.gui.ChatViewer;
import it.di.unipi.iochatto.gui.FindDialog;
import it.di.unipi.iochatto.gui.LoginDialog;
import it.di.unipi.iochatto.gui.tabbedpane.CloseAndMaxTabbedPane;
import it.di.unipi.iochatto.util.IPAddressDetector;

import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.Event;
import java.awt.BorderLayout;

import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.KeyStroke;
import java.awt.Point;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JMenuItem;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JFrame;
import javax.swing.JDialog;
import javax.swing.UIManager;

public class mainApp implements Observer{

	private JFrame jFrame = null;

	private JPanel jContentPane = null;

	private JMenuBar jJMenuBar = null;

	private JMenu fileMenu = null;
	private JMenuItem joinMenu = null;
	private JMenu toolMenu = null;

	private JMenu helpMenu = null;
	private JMenu personalState = null;
	private JMenuItem exitMenuItem = null;
	private JMenuItem aboutMenuItem = null;
	private JMenuItem searchMenuItem = null;
	private JMenuItem listMenuItem = null;
	private JMenuItem connectMenuItem = null;
	private IPAddressDetector ipdetect = new IPAddressDetector();
	private String ipAddress = ipdetect.getIPAddress();
	private JDialog aboutDialog = null;

	private JPanel aboutContentPane = null;
	private JCheckBoxMenuItem[] presenceItem = new JCheckBoxMenuItem[] { 
			new JCheckBoxMenuItem("Offline"),
			new JCheckBoxMenuItem("Online"),
			new JCheckBoxMenuItem("Busy"),
			new JCheckBoxMenuItem("Away")
	}; 
	private JLabel aboutVersionLabel = null;
//	private ChannelPanel mainPanel = new ChannelPanel();
	private CloseAndMaxTabbedPane tabPane = new CloseAndMaxTabbedPane(true);
	private final ChatModel model  = new ChatModel();
	private String[] arg;
	/**
	 * @param args
	 */
	public mainApp(String[] argv)
	{
		int startPort = 9701;
		int endPort = 9799;
		Getopt g = new Getopt("mainApp", argv, "b:e:r");
		boolean RDV = false;
		int c = 0;
		String arg = null;

		while ((c = g.getopt()) != -1)
		{
			switch(c) {
			case 'r':
			{
				RDV = true;
				break;
			}
			case 'b':
			{
				arg = g.getOptarg();
				if (arg!=null)
					startPort = Integer.parseInt(arg);
			}
			case 'e':
			{
				arg = g.getOptarg();
				if (arg!=null)
					endPort = Integer.parseInt(arg);
				break;
			}
			} // end switch
		}
		model.setRDV(RDV);
		model.addObserver(this);
		model.setPortRange(startPort, endPort);
	}
	public static void main(String[] args) {

		try {

			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e)
		{ System.err.println("Cant using this look and feel");}
		MainRun mainRun = new MainRun();
		mainRun.setArgs(args);
		mainApp application = mainRun.getApp();
		SwingUtilities.invokeLater(mainRun);

	}

	/**
	 * This method initializes jFrame
	 * 
	 * @return javax.swing.JFrame
	 */
	JFrame getJFrame() {
		if (jFrame == null) {
			jFrame = new JFrame();
			ImageIcon icon = new ImageIcon("pixmaps/iochatto.gif");
			jFrame.setIconImage(icon.getImage());
			jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			jFrame.setJMenuBar(getJJMenuBar());
			jFrame.setSize(640, 480);
			jFrame.setContentPane(getJContentPane());
			jFrame.setTitle("IoChatto!   - jxta@"+ipAddress);
			ipAddress = null;
			ipdetect = null;
			WindowListener listner = new WindowListener() {

				public void windowActivated(WindowEvent e) {
					// TODO Auto-generated method stub

				}

				public void windowClosed(WindowEvent e) {
					model.disconnect();
					System.exit(0);

				}

				public void windowClosing(WindowEvent e) {
					// TODO Auto-generated method stub

				}

				public void windowDeactivated(WindowEvent e) {
					// TODO Auto-generated method stub

				}

				public void windowDeiconified(WindowEvent e) {
					// TODO Auto-generated method stub

				}

				public void windowIconified(WindowEvent e) {
					// TODO Auto-generated method stub

				}

				public void windowOpened(WindowEvent e) {
					// TODO Auto-generated method stub

				}

			};
			jFrame.addWindowListener(listner);
			ChatViewer chat = new ChatViewer(jFrame, model);
		}
		return jFrame;
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			//	tabPane.setLayout(new BorderLayout());
			//tabPane.setSize(jContentPane.getSize());
		}
		return jContentPane;
	}

	/**
	 * This method initializes jJMenuBar	
	 * 	
	 * @return javax.swing.JMenuBar	
	 */
	private JMenuBar getJJMenuBar() {
		if (jJMenuBar == null) {
			jJMenuBar = new JMenuBar();
			jJMenuBar.add(getFileMenu());
			jJMenuBar.add(getStrumentiMenu());
			jJMenuBar.add(getHelpMenu());
		}
		return jJMenuBar;
	}

	/**
	 * This method initializes jMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getFileMenu() {
		if (fileMenu == null) {
			fileMenu = new JMenu();
			fileMenu.setText("File");
			fileMenu.add(getConnectMenuItem());
			fileMenu.add(getStateMenu());
			fileMenu.add(getJoinMenu());
			fileMenu.add(getExitMenuItem());
		}
		return fileMenu;
	}

	/**
	 * This method initializes jMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getStrumentiMenu() {
		if (toolMenu == null) {
			toolMenu = new JMenu();
			toolMenu.setText("Strumenti");
			//toolMenu.add(getNewChannelMenuItem());
			toolMenu.add(getSearchMenuItem());
			//toolMenu.add(getListMenuItem());
			//toolMenu.add(getSearchUserMenuItem());
		}
		return toolMenu;
	}
	private int calcState(String s)
	{
		String[] state = new String[]
		                            {
				"Offline","Online","Busy","Away"
		                            };
		for (int k = 0; k < state.length; k++)
		{
			if (state[k].equals(s))
				return k;
		}
		return 0;
	}

	private JMenuItem getJoinMenu()
	{
		if (joinMenu == null)
		{
			joinMenu = new JMenuItem("Join..");
			ActionListener lst = new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					JOptionPane pane = new JOptionPane("Cambia stato della presenza");
					String chanName = (String) pane.showInputDialog(getJFrame(),"Entra nel canale:", "Join..", JOptionPane.INFORMATION_MESSAGE, 
							null,null,"#");

					char ch0='#';
					if ( (chanName != null) && (chanName.charAt(0) == ch0) )
					{
						Thread joinRun = new Thread(chanName) {
							public void run() {
								model.joinChannel(getName());
							}
						};
						joinRun.start();

					}
					else {
						JOptionPane.showMessageDialog(getJFrame(), "Nome Canale non valido", "Errore", JOptionPane.ERROR_MESSAGE);
					}

				};


			};
			joinMenu.setEnabled(false);
			joinMenu.addActionListener(lst);
		}
		return joinMenu;
	}
	private  JMenu getStateMenu()
	{
		if (personalState == null)
		{
			ActionListener lst = new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					Object objMenu = e.getSource();
					if (objMenu instanceof JCheckBoxMenuItem)
					{
						JCheckBoxMenuItem item = (JCheckBoxMenuItem) objMenu;
						String s = item.getText();
						int state = calcState(s);
						model.setPresenceState(state);
					}
				}

			};
			personalState = new JMenu("Stato..");
			presenceItem[0].setSelected(true);
			for (int k = 0; k < presenceItem.length; ++k)
			{
				presenceItem[k].addActionListener(lst);
				personalState.add(presenceItem[k]);
			}


		}
		return personalState;
	}

	/**
	 * This method initializes jMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getHelpMenu() {
		if (helpMenu == null) {
			helpMenu = new JMenu();
			helpMenu.setText("Help");
			helpMenu.add(getAboutMenuItem());
		}
		return helpMenu;
	}

	/**
	 * This method initializes jMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getExitMenuItem() {
		if (exitMenuItem == null) {
			exitMenuItem = new JMenuItem();
			exitMenuItem.setText("Esci");
			exitMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					model.disconnect();
					
					System.exit(0);
				}
			});
		}
		return exitMenuItem;
	}

	/**
	 * This method initializes jMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getAboutMenuItem() {
		if (aboutMenuItem == null) {
			aboutMenuItem = new JMenuItem();
			aboutMenuItem.setText("About");
			aboutMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JDialog aboutDialog = new AboutDialog(getJFrame());
					aboutDialog.pack();
					Point loc = getJFrame().getLocation();
					loc.translate(20, 20);
					aboutDialog.setLocation(loc);
					aboutDialog.setVisible(true);
				}
			});
		}
		return aboutMenuItem;
	}

	/**
	 * This method initializes aboutDialog	
	 * 	
	 * @return javax.swing.JDialog
	 */
	private JDialog getAboutDialog() {
		if (aboutDialog == null) {
			aboutDialog = new AboutDialog(getJFrame());
			aboutDialog.setTitle("About");
		}
		return aboutDialog;
	}

	/**
	 * This method initializes aboutContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getAboutContentPane() {
		if (aboutContentPane == null) {
			aboutContentPane = new JPanel();
			aboutContentPane.setLayout(new BorderLayout());
			aboutContentPane.add(getAboutVersionLabel(), BorderLayout.CENTER);
		}
		return aboutContentPane;
	}

	/**
	 * This method initializes aboutVersionLabel	
	 * 	
	 * @return javax.swing.JLabel	
	 */
	private JLabel getAboutVersionLabel() {
		if (aboutVersionLabel == null) {
			aboutVersionLabel = new JLabel();
			aboutVersionLabel.setText("Version 1.0");
			aboutVersionLabel.setHorizontalAlignment(SwingConstants.CENTER);
		}
		return aboutVersionLabel;
	}
	private JMenuItem getListMenuItem()
	{
		if (listMenuItem == null) {
			listMenuItem = new JMenuItem();
			listMenuItem.setText("Elenco canali..");
			//KeyStroke.getK
			// searchMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F7, null));
			listMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JDialog channelListDialog = new ChannelListDialog(getJFrame(),model);
					channelListDialog.setTitle("Mostra tutti i canali");
					channelListDialog.pack();
					Point loc = getJFrame().getLocation();
					loc.translate(20, 20);
					channelListDialog.setLocation(loc);
					channelListDialog.setVisible(true);
				}
			});}

		return listMenuItem; 
	}


	private JMenuItem getConnectMenuItem()
	{
		if (connectMenuItem == null) {
			connectMenuItem = new JMenuItem();
			connectMenuItem.setText("Connetti..");
			//KeyStroke.getK
			// searchMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F7, null));
			connectMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JDialog connectDialog = new LoginDialog(getJFrame(), model);
					connectDialog.setTitle("Connessione ad IoChatto!");
					connectDialog.pack();
					Point loc = getJFrame().getLocation();
					loc.translate(20, 20);
					connectDialog.setLocation(loc);
					connectDialog.setVisible(true);
				}
			});}

		return connectMenuItem; 
	}

	private JMenuItem getSearchMenuItem() {
		if (searchMenuItem == null) {
			searchMenuItem = new JMenuItem();
			searchMenuItem.setText("Cerca..");
			searchMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F7,
					Event.CTRL_MASK, true));
			searchMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					FindDialog searchDialog = new FindDialog(getJFrame(),model);

					searchDialog.setTitle("Ricerca Canali/Utenti..");
					searchDialog.pack();
					Point loc = getJFrame().getLocation();
					loc.translate(20, 20);
					searchDialog.setLocation(loc);
					searchDialog.setVisible(true);
				}
			});
		}
		return searchMenuItem;
	}
	/*
	private JMenuItem getSearchMenuItem() {
		if (searchMenuItem == null) {
			searchMenuItem = new JMenuItem();
			searchMenuItem.setText("Cerca canale..");
			searchMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F7,
					Event.CTRL_MASK, true));
			searchMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JDialog channelFindDialog = new ChannelFindDialog();
					channelFindDialog.setTitle("Cerca canale..");
					channelFindDialog.pack();
					Point loc = getJFrame().getLocation();
					loc.translate(20, 20);
					channelFindDialog.setLocation(loc);
					channelFindDialog.setVisible(true);
				}
			});
		}
		return searchMenuItem;
	}*/

	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		if (arg instanceof Status)
		{
			Status s = (Status) arg;

			for (int k = 0; k< presenceItem.length; k++)
			{
				if (k!=s.getStatus())
					presenceItem[k].setSelected(false);
			}
			presenceItem[s.getStatus()].setSelected(true);
		}
		if (arg instanceof StatusEvent)
		{
			StatusEvent ev = (StatusEvent)	arg;
			if (ev.reason.equals("CONNECTED."))
			{

				joinMenu.setEnabled(true);
			}
		}

	}



}