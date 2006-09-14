/* FileName: it/di/unipi/iochatto/gui/ChannelPanel.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.gui;
import it.di.unipi.iochatto.gui.UserInfo;
import it.di.unipi.iochatto.gui.tabbedpane.CloseAndMaxTabbedPane;
import it.di.unipi.iochatto.gui.tabbedpane.CloseListener;
import it.di.unipi.iochatto.util.DateTime;
import it.di.unipi.iochatto.channel.message.ChannelInfoMessage;
import it.di.unipi.iochatto.channel.message.ChannelMessage;
import it.di.unipi.iochatto.channel.message.ChannelMessageEvent;
import it.di.unipi.iochatto.channel.message.ChatMessage;
import it.di.unipi.iochatto.core.ChatModel;
import it.di.unipi.iochatto.core.Status;

import javax.swing.text.*;
import javax.swing.text.html.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javax.swing.*;

public class ChannelPanel extends JPanel implements Observer {

	private GridBagLayout GBL=	new GridBagLayout();
    private GridBagConstraints GBC = null;
    private String name = null;
    private JLabel message = new JLabel("Message:");
    private JLabel utenti = new JLabel("Utenti");
    private JButton chiudi = new JButton("Chiudi");
    private int tabIndex = 0;
    private JTextField textMessage = new JTextField(20);
    private JList userList = new JList();
    private JScrollPane sp = null;
    private JScrollPane scrollList = null;
    private JEditorPane editPane =  null;
    //	new JTextPane();
    private HtmlBuffer buf = new HtmlBuffer();
    private Status s = Status.getInstance();
    private UserInfo[] infos = new UserInfo[] { new UserInfo(s.getName(),s.getStatus())};
    private UserListModel users = new UserListModel(infos);
    private ChatModel m = null;
    private CloseAndMaxTabbedPane parent;
    public ChannelPanel(String name, CloseAndMaxTabbedPane pane, ChatModel m, int index)
    {
	super();
	this.name = name;
	this.parent = pane;
	this.m = m;
	this.tabIndex = index;
	chiudi.setName(Integer.toString(index));
    initGUI();
	}
    public String getName()
    {
    	return this.name;
    }
    
    private void initGUI()
    {
    	setLayout(GBL);
    	setSize(640,480);
    	editPane = new JEditorPane();
    	sp = new JScrollPane(editPane);
    	editPane.setContentType("text/html");
    	editPane.setBackground(Color.WHITE);
    	editPane.setEditable(false);
    	sp.setPreferredSize(new Dimension(420,350));
    	ListCellRenderer presenceRender = new PresenceListRender();
    	userList.setCellRenderer(presenceRender);
    	userList.setLayoutOrientation(JList.VERTICAL);
    	userList.setSelectionForeground(Color.BLUE);
    	
    	userList.setModel(users);
    	scrollList = new JScrollPane(userList);
    	scrollList.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    	scrollList.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
     	GBC = new GridBagConstraints();
    	//GBC.anchor = GridBagConstraints.NORTHWEST;
    	GBC.gridx = 0;
    	GBC.gridy = 0;
    	GBC.gridwidth = 5;
    	GBC.gridheight = 5;
    	GBC.weightx = 0.5;
    	GBC.weighty = 0.5;
    	GBC.ipadx = 40;
    	GBC.fill = GridBagConstraints.BOTH;
    	GBC.anchor = GridBagConstraints.NORTHWEST;
    	GBC.insets = new Insets(2,2,2,2);
    	// GBC.fill = GridBagConstraints.BOTH;
    	//GBC.ipadx = 300;
    //	GBC.ipady = 100;
    	//	GBC.gridheight = 5;
    	//	GBC.gridwidth = 6;
    	GBL.setConstraints(sp, GBC);
    	add(sp);
    	GBC = new GridBagConstraints();
    	GBC.gridx = 6;
    	GBC.gridy = 0;
    	GBC.gridheight = 1;
    	GBC.gridwidth = 2;
    	GBL.setConstraints(utenti, GBC);
    	add(utenti);
    	GBC = new GridBagConstraints();
    	GBC.gridx = 6;
    	GBC.gridy = 1;
    	GBC.gridheight = 5;
    	GBC.gridwidth = 2;
    	GBC.weightx = 0.5;
    	GBC.weighty = 0.5;
    
    	GBC.fill = GridBagConstraints.BOTH;
    	GBC.anchor = GridBagConstraints.NORTHWEST;
    	GBC.ipadx = 50;
    	GBL.setConstraints(scrollList, GBC);
    	add(scrollList);
    
    	GBC = new GridBagConstraints();
    	GBC.gridx = 0;
    	GBC.gridwidth = 1;
    	GBC.gridy = 6;
    	GBC.gridheight = 1;
    	GBL.setConstraints(message, GBC);
    	add(message);
    	GBC = new GridBagConstraints();
    	GBC.gridx = 2;
    	GBC.gridy = 6;
    	GBC.fill = GridBagConstraints.HORIZONTAL;
    	GBC.gridwidth = 2;
    	GBC.gridheight = 1;
    	//GBC.ipadx = 150;
    	GBC.weightx = 1;
    	
    	GBL.setConstraints(textMessage, GBC);
    	add(textMessage);
    	GBC = new GridBagConstraints();
    	GBC.gridx = 6;
    	GBC.gridy = 6;
    	GBC.fill = GridBagConstraints.HORIZONTAL;
    	GBC.gridwidth = 1;
    	GBC.gridheight = 1;
    	GBC.anchor = GridBagConstraints.SOUTHEAST;
    	GBL.setConstraints(chiudi, GBC);
    	add(chiudi);
    	updateBuffer();
    	ActionListener textListener = new ActionListener() {

			public void actionPerformed(ActionEvent e) {
							m.sendMessage(name, textMessage.getText().trim());
							DateTime dt = new DateTime(new Date());
							Calendar c = dt.getCalendar();
							int h = c.get(Calendar.HOUR_OF_DAY);
							int m = c.get(Calendar.MINUTE);
							int s = c.get(Calendar.SECOND);
							String hour = (h<10) ? "0"+h: Integer.toString(h);
							String minute = (m<10) ? "0"+m  : Integer.toString(m);
							String second = (s<10) ? "0"+s  : Integer.toString(s);
							String time = hour+":"+minute+":"+second;
							Status state = Status.getInstance();
							String msgHtml = "<tr><td><font color=\"blue\">"+time+"</font>"+"</td><td><font color=\"green\">"+state.getName()+"</font></td><td>"+textMessage.getText()+"</td></tr>";
							addMessage(msgHtml);
			}
    		
    	};
    	textMessage.addActionListener(textListener);
    	// fare il chiudi...
    	
    	/*
    	 public ChatCloseListener(String name, ChannelPanel cpane ,ChatModel model, CloseAndMaxTabbedPane pane, int index)
   
    	 */
    	ChatCloseListener chClose = new ChatCloseListener(getName(),parent,m);
    	chClose.updateTabPane(parent);
    	chiudi.addActionListener(chClose);
    }
    private void addMessage(String message)
    {
    	HTMLEditorKit kit = (HTMLEditorKit) editPane.getEditorKit();
    	HTMLDocument doc = (HTMLDocument) editPane.getDocument();
    	StringReader readerHTML =  new StringReader(message);
        try {
			kit.read(readerHTML, doc, doc.getLength());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
   
   
    }
    private void updateBuffer()
    {
    	buf.appendHTMLLine("<tr><td width=\"10%\"><font color=\"purple\">Benvenuto nel canale:"+name+"</font></td></tr>");
    	String tmpBuf = buf.toString();
    	editPane.setText(tmpBuf);
   }
	/**
	 * 
	 */
	private static final long serialVersionUID = 2808568232228794817L;

	public void update(Observable o, Object arg) {
	
		if (arg instanceof Status)
		{
			
		}
		if (arg instanceof ChannelMessageEvent)
		{
			ChannelMessage msg = ((ChannelMessageEvent) arg).getMessage();
			if ((msg!=null) && (!msg.channelName().equals(name)))
				 return;
			if (msg instanceof ChatMessage)
			{
				ChatMessage cmsg = (ChatMessage) msg;
			
				addMessage(cmsg.toHTML());
			}
			if (msg instanceof ChannelInfoMessage)
			{
			ChannelInfoMessage msg0 = (ChannelInfoMessage) msg;
			HashMap<String, it.di.unipi.iochatto.channel.UserInfo> users0 = msg0.getUsers();
			Set<String> name = users0.keySet();
			infos = null;
			users = null;
			infos = new UserInfo[name.size()];
			int z = 0;
			for (String n : name)
			{
				it.di.unipi.iochatto.channel.UserInfo tmp = users0.get(n);
				if (tmp!=null)
				{
					System.out.println("Nome "+tmp.getName()+ "Stato "+tmp.getStatus()+ "mail:"+tmp.getAddress()+".");
					// infos[z] = null;
					infos[z] = new it.di.unipi.iochatto.gui.UserInfo(tmp.getName(),tmp.getStatus());
					++z;
				}
			}
			users = new UserListModel(infos);
	    	userList.setModel(users);
	    	userList.repaint();
			}
			
	
		}
	}
	

	
}
