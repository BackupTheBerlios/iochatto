/* FileName: it/di/unipi/iochatto/gui/FindDialog.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.gui;

import it.di.unipi.iochatto.channel.ChannelDiscoveryEvent;
import it.di.unipi.iochatto.channel.ChannelInfo;
import it.di.unipi.iochatto.core.ChatModel;
import it.di.unipi.iochatto.core.SearchEvent;
import it.di.unipi.iochatto.presence.PresenceAdvertisement;
import it.di.unipi.iochatto.presence.PresenceEvent;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import java.util.regex.PatternSyntaxException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

public class FindDialog extends JDialog implements Observer {
    /**
	 * 
	 */
	private static final long serialVersionUID = 9104759921046945801L;
	private JTable userTable = null;
	private JTable channelTable = null;
	private JLabel nameLabel = null;
	private JTabbedPane findTab = new JTabbedPane();
	private JButton findButton = null;
	private JButton closeButton = null;
	private JButton stopButton = null;
	private JButton searchButton = null;
	private JTextField channelField = null;
	private GridBagLayout GBL=	new GridBagLayout();
    private GridBagConstraints GBC = null;
    private JLabel statusbar = new JLabel("Ready");
    private UserTableData uTableData =  null;
    private ChannelTableData chTableData = null;
    private JTextField channelToFind = null;
    private JTextField userToFind = null;
    private ChatModel model = null;
    private String searched = null;
    private FindDialog instance = null;
	public FindDialog( ChatModel m)
   {
	   super();
	   setResizable(false);
	   this.model = m;
	//	ImageIcon icon = new ImageIcon("pixmaps/iochatto.gif");
	//	super.set
		//setIconImage(icon.getImage());

	   initGUI();
	   instance = this;
	   m.addObserver(instance);
   }
	public FindDialog(Frame parent, ChatModel m) throws HeadlessException
	{
		  super(parent,false);
		  setResizable(false);
		//	ImageIcon icon = new ImageIcon("pixmaps/iochatto.gif");
		//	super.set
			//setIconImage(icon.getImage());
		  this.model = m;
		   initGUI();
		   instance = this;
		   m.addObserver(instance);

	}
	public void setStatusBar(String s)
	{
		statusbar.setText(s);
	}
	private JScrollPane initUserTable()
	{
		uTableData = new UserTableData();
		userTable = new JTable();
		userTable.setAutoCreateColumnsFromModel(false);
		userTable.setModel(uTableData);
		for (int k = 0; k < uTableData.getColumnCount(); k++)
		{
			DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
			renderer.setHorizontalAlignment(uTableData.getAlignmentAt(k));
			TableColumn col = new TableColumn(k,uTableData.getWidthAt(k),renderer,null);
			col.setHeaderValue(uTableData.m_columns[k].title);
			userTable.addColumn(col);
		}
		JTableHeader header = userTable.getTableHeader();
		header.setUpdateTableInRealTime(false);
		JScrollPane paneTable = new JScrollPane();
		paneTable.getViewport().setBackground(userTable.getBackground());
		paneTable.getViewport().add(userTable);
		return paneTable;
	}
	private JScrollPane initChannelTable()
	{
		chTableData = new ChannelTableData();
		channelTable = new JTable();
		channelTable.setAutoCreateColumnsFromModel(false);
		channelTable.setModel(chTableData);
		JoinByTable joiner = new JoinByTable(model,this);
		for (int k = 0; k < chTableData.getColumnCount(); k++)
		{
			DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
			renderer.setHorizontalAlignment(chTableData.getAlignmentAt(k));
			TableColumn col = new TableColumn(k,chTableData.getWidthAt(k),renderer,null);
			col.setHeaderValue(chTableData.m_columns[k].title);
			channelTable.addColumn(col);
		}
		channelTable.addMouseListener(joiner);
		JTableHeader header = channelTable.getTableHeader();
		header.setUpdateTableInRealTime(false);
		JScrollPane paneTable = new JScrollPane();
		paneTable.getViewport().setBackground(channelTable.getBackground());
		paneTable.getViewport().add(channelTable);
		return paneTable;
	}
	
	private GridBagConstraints initGBC()
	{
		
		GBC = null;
		GridBagConstraints GBC0 = new GridBagConstraints();
		return GBC0;
	}
	private JPanel initChannelTab()
	{
	
		JPanel channelTab = new JPanel();
	//	GridBagLayout GBLch = new GridBagLayout();
		//	GridBagConstraints GBCch = new GridBagConstraints();
		channelTab.setPreferredSize(new Dimension(460,340));
		channelTab.setBorder(new EmptyBorder(new Insets(0,0,0,10)));
		JLabel chLabel = new JLabel("Nome canale:");
		channelToFind = new JTextField(20);
		channelTab.setLayout(new FlowLayout());
		
	/*	GBCch.gridx = 0;
		GBCch.gridy = 0;
		GBCch.gridheight = 1;
		GBCch.gridwidth = 1;
		GBCch.anchor = GridBagConstraints.NORTHWEST;
		GBCch.fill = GridBagConstraints.BOTH;
		GBLch.setConstraints(chLabel, GBCch);
		GBCch.gridx = 1;
		GBCch.gridy = 0;
		GBCch.gridheight = 1;
		GBCch.gridwidth = 1;
		GBCch.anchor = GridBagConstraints.NORTHWEST;
		GBCch.fill = GridBagConstraints.BOTH;
		GBLch.setConstraints(channelToFind, GBCch);
		*/
		JScrollPane pane = initChannelTable(); 
	/*	GBCch.gridx = 1;
		GBCch.gridy = 0;
		GBCch.gridheight = 1;
		GBCch.gridwidth = 1;
		GBCch.anchor = GridBagConstraints.NORTHWEST;
		GBCch.fill = GridBagConstraints.BOTH;
		GBLch.setConstraints(pane, GBCch);
		*/
		channelTab.add(chLabel);
		channelTab.add(channelToFind);
		channelTab.add(pane);
		findTab.setBorder(new EmptyBorder(new Insets(0,0,0,10)));
		findTab.addTab("Cerca canale", channelTab);
		return channelTab;
	}

	private JPanel initUserTab()
	{
		JPanel userTab = new JPanel();
		userTab.setPreferredSize(new Dimension(460,380));
		userTab.setBorder(new EmptyBorder(new Insets(0,0,0,10)));
		//GridBagLayout GBLus = new GridBagLayout();
		//GridBagConstraints GBCus = new GridBagConstraints();
		JLabel uLabel = new JLabel("Email Utente:");
		userToFind = new JTextField(20);
	/*	userTab.setLayout(GBLus);
		
		GBCus.gridx = 0;
		GBCus.gridy = 0;
		GBCus.gridheight = 1;
		GBCus.gridwidth = 1;
		GBCus.anchor = GridBagConstraints.NORTHWEST;
		GBCus.fill = GridBagConstraints.BOTH;
		GBLus.setConstraints(uLabel, GBCus);
		GBCus.gridx = 1;
		GBCus.gridy = 0;
		GBCus.gridheight = 1;
		GBCus.gridwidth = 1;
		GBCus.anchor = GridBagConstraints.NORTHWEST;
		GBCus.fill = GridBagConstraints.BOTH;
		GBLus.setConstraints(userToFind, GBCus);
		*/
		//JLabel userLabel = new JLabel("Nome Utente:");
		//JTextField mytext = new JTextField(20);
		userTab.add(uLabel);
		userTab.add(userToFind);
		
		JScrollPane utable = initUserTable();
//		utable.setVisible(true);
		/*GBCus.gridx = 0;
		GBCus.gridy = 1;
		GBCus.gridheight = 2;
		GBCus.gridwidth = 2;
		GBCus.anchor = GridBagConstraints.NORTHWEST;
		GBCus.fill = GridBagConstraints.BOTH;
		GBLus.setConstraints(utable, GBCus);
		*/
		userTab.add(utable);
		
		findTab.addTab("Cerca Utente", userTab);
		return userTab;
	}
	private void initGUI(){
		
		
		   setLayout(GBL);
		
		   setPreferredSize(new Dimension(640,480));
		   //findTab.setPreferredSize(new Dimension(480,340));
		   findTab.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		   
		   initUserTab();
		   initChannelTab();
		   GBC = initGBC();
		   GBC.gridx = 0;
		   GBC.gridy = 0;
		   //GBC.ipadx = 300;
		  // GBC.ipady = 340;
		   GBC.gridheight = 3;
		   GBC.gridwidth = 3;
		   GBC.anchor = GridBagConstraints.NORTHWEST;
		   GBC.fill = GridBagConstraints.BOTH;
		   GBL.setConstraints(findTab, GBC);
		   GBC = initGBC();
		   stopButton = new JButton("Termina");
		   ActionListener stopAction = new ActionListener()
		   {

			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
			   
		   };
		   stopButton.addActionListener(stopAction);
		   searchButton = new JButton("Cerca");
		   ActionListener searchAction = new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				String user = userToFind.getText();
				int index = findTab.getSelectedIndex();
				String title = findTab.getTitleAt(index);
				if (title.equals("Cerca Utente"))
				{
					if (user == null)
						user ="*";
					uTableData.clear();
					userTable.addNotify();
					searched = user;
					model.searchUser(user);
					statusbar.setText("Searching info for user: "+user);
				} else  {
					
					String chName = channelToFind.getText();
					if (chName == null)
							chName = "*";
					searched = chName;
					model.searchChannel(searched);
				}
			
				
				
			}
		   };
		   searchButton.addActionListener(searchAction);
		   
		   closeButton = new JButton("Chiudi");
		   ActionListener closeAction = new ActionListener(){

				public void actionPerformed(ActionEvent e) {
					int index = findTab.getSelectedIndex();
					String title = findTab.getTitleAt(index);
					if (title.equals("Cerca Utente"))
					{
					
					}
					else 
					{
					
					}
					model.deleteObserver(instance);
					instance.dispose();
				}
			   };
		   closeButton.addActionListener(closeAction);
		   GBC.gridx = 5;
		   GBC.gridy = 0;
		   GBC.gridheight = 1;
		   GBC.gridwidth = 1;
		   GBC.anchor = GridBagConstraints.NORTHEAST;
		   GBC.fill = GridBagConstraints.BOTH;
		  
		   GBL.setConstraints(searchButton, GBC);
		   GBC = initGBC();
		   GBC.gridx = 5;
		   GBC.gridy = 1;
		   GBC.gridheight = 1;
		   GBC.gridwidth = 1;
		   GBC.fill = GridBagConstraints.BOTH;
		   GBL.setConstraints(stopButton, GBC);
		   GBC = initGBC();
		   GBC.gridx = 5;
		   GBC.gridy = 2;
		   GBC.gridheight = 1;
		   GBC.gridwidth = 1;
		   GBL.setConstraints(closeButton, GBC);
		   
		   add(findTab);
		   add(searchButton);
		   add(closeButton);
		   add(stopButton);
		   GBC = initGBC();
		   GBC.anchor = GridBagConstraints.SOUTHWEST;
		   GBC.gridx = 0;
		   GBC.gridy = 4;
		   GBC.fill = GridBagConstraints.BOTH;
		   GBC.gridheight = 1;
		   GBC.gridwidth = 5;
		   GBL.setConstraints(statusbar, GBC);
		   add(statusbar);
	}
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		if (arg instanceof SearchEvent)
		{
			SearchEvent ev = (SearchEvent) arg;
			System.out.println(ev.reason);
			String reason = new String(ev.reason);
			setStatusBar("Searching Event: " + reason);
		}
		if (arg instanceof ChannelDiscoveryEvent)
		{
			ChannelDiscoveryEvent ev2 = (ChannelDiscoveryEvent) arg;
			if (!ev2.found())
			{
				JOptionPane.showInputDialog(this,"Canale non trovato", "Canale non trovato", JOptionPane.ERROR_MESSAGE);
			} else {
				ChannelInfo mt0 = ev2.getMetaData();
				ChannelData chData = new ChannelData(mt0.getName(),mt0.getTopic(), mt0.getFounderEmailAddress(),mt0.getFounderName());
				chTableData.addChannel(chData);
				channelTable.addNotify();
			}
			
		}
		if (arg instanceof PresenceAdvertisement)
		{
			PresenceAdvertisement ev = (PresenceAdvertisement) arg;
			System.out.println("Entry found\n: "+ev.getName()+"-"+ev.getPresenceStatus());
			UserData dt = new UserData(ev.getName(),ev.getEmailAddress(),ev.getPresenceStatus());
			try {
			if (ev.getEmailAddress().matches(searched))
			{
			uTableData.addElement(dt);
			//uTableData.fireTableDataChanged();
			//userTable.setModel(uTableData);
			userTable.addNotify();
			}
			} catch (PatternSyntaxException e)
			{
			}
			//userTable.repaint();
		}
	}
}

class ColumnChannelData {
	public String title = null;
	public int width = 0;
	public int alignment = JLabel.CENTER;
	
	public ColumnChannelData(String title, int w, int a)
	{
		this.title = title;
		this.width = w;
		this.alignment = a;
	}
}

class ColumnUserData {
	public String title = null;
	public int width = 0;
	public int alignment = JLabel.CENTER;
	
	public ColumnUserData(String title, int w, int a)
	{
		this.title = title;
		this.width = w;
		this.alignment = a;
	}
}
class ChannelData {
private String channelName;
private String channelTopic;
private String channelFounderMail;
private String channelFounderName;
public ChannelData(String channelName, String channelTopic, String channelFounder, String channelFounderName)
{
	this.channelName = channelName;
	this.channelTopic = channelTopic;
	this.channelFounderMail = channelFounder;
	this.channelFounderName = channelFounderName;
}
public void setChannelName(String chName) { this.channelName = chName;}
public void setChannelFounder(String found) { this.channelFounderName = found; }
public void setChannelFounderMail(String mail) { this.channelFounderMail = mail;}
public void setChannelTopic(String topic) {this.channelTopic = topic;}
public String getChannelName() { return this.channelName; }
public String getChannelFounder() { return this.channelFounderName;}
public String getChannelFounderMail() { return this.channelFounderMail; }
public String getChannelTopic() { return this.channelTopic;}

}
class UserData {
	private String user;
	private String email;
	private int status;
	private String statusArray[] = {"OFFLINE","ONLINE","AWAY","BUSY"};
	public UserData(String user, String email, int status)
	{
		this.user = user;
		this.email = email;
		this.status = status;
	}
	public void setUser(String u) { user = u;}
	public void setMail(String m) { email = m; }
	public void setStatus (int status) {this.status = status; }
	public String getUser()
	{return user;}
	public String getMail()
	{return email;}
	public String getStatus()
	{return statusArray[status];}
}
class UserTableData extends  AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2217502120468446625L;
	private int numRow = 0;
	static final public ColumnUserData m_columns[] = {
		new ColumnUserData("Nome",100,JLabel.LEFT),
		new ColumnUserData("Email",160,JLabel.LEFT),
		new ColumnUserData("Stato",50,JLabel.CENTER)
	} ;
	protected Vector m_vector;
	
	
	public UserTableData()
	{
		m_vector = new Vector();
	}
	public void clear()
	{
		m_vector.clear();
		
	}
	public void addElement(UserData dt)
	{
		if (!m_vector.contains(dt))
			m_vector.addElement(dt);
		numRow = m_vector.size();
	}
	public int getWidthAt(int k)
	{
		return m_columns[k].width;
	}
	public int getAlignmentAt(int k)
	{
		return m_columns[k].alignment;
	}
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return m_columns.length;
	}

	public int getRowCount() {
		// TODO Auto-generated method stub
		return m_vector.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		if (rowIndex < 0 || rowIndex >=m_vector.size())
								return "";
		UserData data = (UserData) m_vector.elementAt(rowIndex);
		switch (columnIndex)
		{
		case 0: return data.getUser();
		case 1: return data.getMail();
		case 2: return data.getStatus();
		}
		return "";
	}
}
class ChannelTableData extends  AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2217502120468446625L;
	private int numRow = 0;
	static final public ColumnChannelData m_columns[] = {
		new ColumnChannelData("Nome",100,JLabel.LEFT),
		new ColumnChannelData("Topic",160,JLabel.LEFT),
		new ColumnChannelData("Nome Fondatore",100,JLabel.LEFT),
		new ColumnChannelData("Email Fondatore",100,JLabel.LEFT)
	} ;
	protected Vector m_vector;
	
	
	public ChannelTableData()
	{
		m_vector = new Vector();
	}
	public void addChannel(ChannelData chd)
	{
		m_vector.addElement(chd);
		numRow = m_vector.size();
	}
	public int getWidthAt(int k)
	{
		return m_columns[k].width;
	}
	public int getAlignmentAt(int k)
	{
		return m_columns[k].alignment;
	}
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return m_columns.length;
	}

	public int getRowCount() {
		// TODO Auto-generated method stub
		return numRow;
	}
	public Object getRow(int rowIndex)
	{
		if (numRow < 0 || rowIndex >=numRow)
			return "";
		ChannelData data = (ChannelData) m_vector.elementAt(rowIndex);
		return data;
	}
	public Object getValueAt(int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		if (numRow < 0 || rowIndex >=numRow)
								return "";
		ChannelData data = (ChannelData) m_vector.elementAt(rowIndex);
		switch (columnIndex)
		{
		case 0: return data.getChannelName();
		case 1: return data.getChannelTopic();
		case 2: return data.getChannelFounder();
		case 3: return data.getChannelFounderMail();
		}
		return "";
	}
}