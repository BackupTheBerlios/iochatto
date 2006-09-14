/* FileName: it/di/unipi/iochatto/gui/ChannelListDialog.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.gui;

import it.di.unipi.iochatto.channel.ChannelDiscoveryEvent;
import it.di.unipi.iochatto.channel.ChannelInfo;
import it.di.unipi.iochatto.core.ChatModel;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

public class ChannelListDialog extends JDialog implements Observer{
	private ChannelTableData chTableData = null;
	private ChatModel model;
	private JTable channelTable;
	private JButton listChannelButton = new JButton("Elenca Canali..");
	private JButton joinChannelButton = new JButton("Join..");
	
	
	public ChannelListDialog(ChatModel model)
	{
		super();
		this.model = model;
		model.addObserver(this);
		setResizable(false);
	}
	public ChannelListDialog(Frame parent, ChatModel model) throws HeadlessException
	{
		super(parent,false);
		setResizable(false);
		this.model = model;
		initGUI();
		
	}
	private void initGUI()
	{
		JLabel title = new JLabel("Tabella Canali");
		JPanel buttonGroup = new JPanel();
		buttonGroup.setLayout(new FlowLayout());
		Container c = getContentPane();
		setLayout(new BoxLayout(getContentPane(),BoxLayout.Y_AXIS));
		c.add(title);
		JScrollPane pane = initChannelTable();
		joinChannelButton.setEnabled(false);
		c.add(pane);
		ActionListener listAction = new ActionListener()
		{
			
			public void actionPerformed(ActionEvent e) {
				
				model.listChannels();		
			}
			
		};
		listChannelButton.addActionListener(listAction);
		buttonGroup.add(listChannelButton);
		
		ActionListener joinAction = new ActionListener()
		{
			
			public void actionPerformed(ActionEvent e) {
				int selectedRow = channelTable.getSelectedRow(); 
				if (selectedRow!=-1)
				{
					String name = (String) chTableData.getValueAt(selectedRow, 0);
					if ((name!=null) && (name.length()>0))
						model.joinChannel(name);
				}
				
			}
			
		};
		joinChannelButton.addActionListener(joinAction);
		buttonGroup.add(joinChannelButton);
		c.add(buttonGroup);
		
	}
	private JScrollPane initChannelTable()
	{
		chTableData = new ChannelTableData();
		channelTable = new JTable();
		channelTable.setAutoCreateColumnsFromModel(false);
		channelTable.setModel(chTableData);
		for (int k = 0; k < chTableData.getColumnCount(); k++)
		{
			DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
			renderer.setHorizontalAlignment(chTableData.getAlignmentAt(k));
			TableColumn col = new TableColumn(k,chTableData.getWidthAt(k),renderer,null);
			col.setHeaderValue(chTableData.m_columns[k].title);
			channelTable.addColumn(col);
		}
		JTableHeader header = channelTable.getTableHeader();
		header.setUpdateTableInRealTime(false);
		JScrollPane paneTable = new JScrollPane();
		paneTable.getViewport().setBackground(channelTable.getBackground());
		paneTable.getViewport().add(channelTable);
		return paneTable;
	}
	public void dispose()
	{
		if (model!=null)
			model.deleteObserver(this);
		super.dispose();
	}
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		if (arg instanceof ChannelDiscoveryEvent)
		{
			ChannelDiscoveryEvent ev0 = (ChannelDiscoveryEvent) arg;
			if (ev0.found())
			{
				ChannelInfo info = ev0.getMetaData();
				ChannelData dt0 = new ChannelData(info.getName(),info.getTopic(),info.getFounderEmailAddress(), info.getFounderName());
				joinChannelButton.setEnabled(true);
				chTableData.addChannel(dt0);
				channelTable.addNotify();
			}
			
		}
		
	}
	
}
