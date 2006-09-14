/* FileName: it/di/unipi/iochatto/gui/ChatViewer.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.gui;

import it.di.unipi.iochatto.channel.message.ChannelJoinMessage;
import it.di.unipi.iochatto.channel.message.ChannelLeaveMessage;
import it.di.unipi.iochatto.channel.message.ChannelMessage;
import it.di.unipi.iochatto.channel.message.ChannelMessageEvent;
import it.di.unipi.iochatto.core.ChatModel;
import it.di.unipi.iochatto.core.Status;
import it.di.unipi.iochatto.core.StatusEvent;
import it.di.unipi.iochatto.core.StdChatGroup;
import it.di.unipi.iochatto.gui.tabbedpane.CloseAndMaxTabbedPane;
import it.di.unipi.iochatto.gui.tabbedpane.CloseListener;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.*;

import net.jxta.exception.ConfiguratorException;

public class ChatViewer implements Observer {
	private JFrame mainFrame;
	private JToolBar toolBar;
	private Container mainPanel;
	private static ChatViewer instance = null;
	private static ChatCloseListener close = null;
	private ChatModel model;
	private boolean connState = false;
	private String cwd = new File("").getAbsolutePath();
	private String path = cwd+File.separatorChar+"pixmaps"+File.separatorChar;

	private ImageIcon tabClose = new ImageIcon(path+"tab_close.png");
	private CloseAndMaxTabbedPane tabPane = new CloseAndMaxTabbedPane(tabClose);
	private Object[] presenceItems = new String[] {"Offline","Online","Busy","Away"};
	private JButton connectButton;
	private int tabindex = 0 ;
	private final ImageIcon disconnect = new ImageIcon(path+"disconnetti.png");
	private Map<String, ChannelPanel> tabbed = new ConcurrentHashMap<String,ChannelPanel>();
	private ImageIcon[] imageIcon = new ImageIcon[] {
			new ImageIcon(new File("pixmaps/online.png").getAbsolutePath()),
			new ImageIcon(new File("pixmaps/away.png").getAbsolutePath()),
			new ImageIcon(new File("pixmaps/busy.png").getAbsolutePath()),
			new ImageIcon(new File("pixmaps/offline.png").getAbsolutePath())
	};
	public ChatViewer(JFrame mF, ChatModel m)
	{
		mainFrame = mF;
		mainPanel = mainFrame.getContentPane();
		model = m;
		model.addObserver(this);
		initGUI();
		initToolBar();
		StatusPanel mainPanel = new StatusPanel(model);
		model.addObserver(mainPanel);
		addStatusTab(mainPanel);
		//addTab(new ChannelPanel(),"#italia");
		//addTab(new ChannelPanel(),"#linux");
		instance = this;

	}
	public JToolBar getToolBar()
	{
		return toolBar;	
	}
	public static ChatViewer getInstance()
	{
		return instance;
	}
	private void initToolBar()
	{
		String init = new File("").getAbsolutePath();
		String path = init+File.separatorChar+"pixmaps"+File.separatorChar;
	
		toolBar = new JToolBar();
		final ImageIcon connectIcon = new ImageIcon(path+"connetti.png");
		//connectIcon.setDimension(new Dimension(32,32));
		final ImageIcon cerca = new ImageIcon(path+"cerca.png");
		final ImageIcon prefs = new ImageIcon(path+"prefs.png");
		final ImageIcon stato = new ImageIcon(path+"stato.png");
		final ImageIcon crea = new ImageIcon(path+"crea.png");
		final Action actionConnect = new AbstractAction("Connetti",connectIcon){

			/**
			 * 
			 */
			private static final long serialVersionUID = 83982984384L;

			public void actionPerformed(ActionEvent e) {
				if (!connState)
				{
				JDialog connectDialog = new LoginDialog(mainFrame, model);
				connectDialog.setTitle("Connessione ad IoChatto!");
				connectDialog.pack();
				Point loc = mainFrame.getLocation();
				loc.translate(20, 20);
				connectDialog.setLocation(loc);
				connectDialog.setVisible(true);
				connectButton.setEnabled(false);
				} else {
					model.disconnect();
					Collection<ChannelPanel> tabSet = tabbed.values();
					for (ChannelPanel c: tabSet)
					{
						tabPane.remove(c);
					}
					tabbed.clear();
					connectButton.setIcon(connectIcon);
					connectButton.setPreferredSize(new Dimension(48,48));
					connectButton.setToolTipText("Connetti..");
					connState = false;
				}
			}


		};
		final Action actionCreate = new AbstractAction("Crea nuova chat..",crea){

			/**
			 * 
			 */
			private static final long serialVersionUID = 83982984384L;

			public void actionPerformed(ActionEvent e) {
				if (connState)
				{
				CreateDialog makeChan = new CreateDialog(mainFrame,model);
				makeChan.setTitle("Crea nuova chat");
				makeChan.pack();
				Point loc = mainFrame.getLocation();
				loc.translate(20, 20);
				makeChan.setLocation(loc);
				makeChan.setVisible(true);
				}
			};


		};


		final Action actionSearch = new AbstractAction("Cerca",cerca)
		{
			public void actionPerformed(ActionEvent e){
				FindDialog searchDialog = new FindDialog(mainFrame,model);
				//model.addObserver(searchDialog);
				searchDialog.setTitle("Ricerca Canali/Utenti..");
				searchDialog.pack();
				Point loc = mainFrame.getLocation();
				loc.translate(20, 20);
				searchDialog.setLocation(loc);
				searchDialog.setVisible(true);
			}
		};
		final Action actionState = new AbstractAction("Cambia Stato",stato)
		{
			public void actionPerformed(ActionEvent e){
				JOptionPane pane = new JOptionPane("Cambia stato della presenza");
				String value = (String)	pane.showInputDialog(mainFrame,"Seleziona il tuo stato di presenza:","Stato presenza",JOptionPane.INFORMATION_MESSAGE,imageIcon[0],presenceItems,"Online");
				for (int k = 0; k < presenceItems.length; ++k)
				{
					if (presenceItems[k].equals(value))
					{
						model.setPresenceState(k);
					}
				}
			}
		};
		final Action actionPrefs = new AbstractAction("Preferenze",prefs)
		{
			public void actionPerformed(ActionEvent e){
				try {
					StdChatGroup grp = StdChatGroup.getInstance();
					String fileURI = "file://"+grp.getPath();
					
					new net.jxta.ext.config.ui.Configurator(new URI(fileURI)).configure();
				} catch (ConfiguratorException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (URISyntaxException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
			}
		};
		drawToolBar(actionConnect,actionCreate,actionSearch, actionState, actionPrefs);
		mainPanel.add(toolBar,BorderLayout.NORTH);
	} 
	private void drawToolBar(Action actionConnect, Action actionCreate, Action actionSearch,Action actionState, Action actionPrefs)
	{
		JButton button = null;
		if (toolBar==null)
			return;
		connectButton = toolBar.add(actionConnect);
		connectButton.setPreferredSize(new Dimension(48,48));
		connectButton.setToolTipText("Connetti..");
		button = toolBar.add(actionCreate);
		button.setToolTipText("Crea nuova chat...");
		button.setPreferredSize(new Dimension(48,48));

		button = toolBar.add(actionSearch);
		button.setToolTipText("Cerca...");
		button.setPreferredSize(new Dimension(48,48));
		button = toolBar.add(actionState);
		button.setToolTipText("Stato..");
		button.setPreferredSize(new Dimension(48,48));
		button = toolBar.add(actionPrefs);
		button.setPreferredSize(new Dimension(48,48));
		button.setToolTipText("Preferenze..");
		toolBar.setBackground(new Color(239,239,239));

	}
	private void addStatusTab(StatusPanel sp)
	{
		tabPane.addTab("Status", sp);
	}
	
	private void initGUI()
	{
		tabPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		mainPanel.add(tabPane, BorderLayout.CENTER);
	}
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		if (arg instanceof StatusEvent)
		{
		 StatusEvent ev = (StatusEvent)	arg;
		 if (ev==null)
			 	 return;
		 if (ev.reason.equals("CONNECTED."))
		 {
			 	connectButton.setEnabled(true);
				connectButton.setIcon(disconnect);
				connectButton.setPreferredSize(new Dimension(48,48));
				connectButton.setToolTipText("Disconnetti..");
				connState = true;
			 
		 }
		}
		if (arg instanceof ChannelMessageEvent)
		{
			
			ChannelMessage msg = ((ChannelMessageEvent) arg).getMessage();
			System.out.println("Got a message from channel. Sender: " + msg.senderAddress());
			Status s = Status.getInstance();
			// se non sono io....non cambio la mia UI
			
			if (!msg.senderAddress().equals(s.getEmailAddress()))
				return;
			// sono io che entro o creo canali.
			
			if (msg instanceof ChannelJoinMessage)
			{
			
			ChannelPanel pan0 = new ChannelPanel(msg.channelName(),tabPane,model,tabPane.getTabCount());
			
			model.addObserver(pan0);
			System.out.println("Name Channel:"+msg.channelName());
			String name = msg.channelName();
			/*
	    	 public ChatCloseListener(String name, ChannelPanel cpane ,ChatModel model, CloseAndMaxTabbedPane pane, int index)
	   
	    	 */
			if (close==null)
			{
				close = new ChatCloseListener(tabPane,model);
				tabPane.addCloseListener(close);
			}
			
			tabPane.addTab(name,null, pan0, "Canale "+name);
			close.setHashRef(tabbed);
			close.updateTabPane(tabPane);
			tabbed.put(msg.channelName(), pan0);
			}
			
			if (msg instanceof ChannelLeaveMessage)
			{
				ChannelPanel pan1 = tabbed.remove(msg.channelName());
				if (pan1!=null)
				{
					tabPane.remove(pan1);
					model.deleteObserver(pan1);
				}
			}
			
		}

	}

}
