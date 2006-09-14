/* FileName: it/di/unipi/iochatto/gui/UserListModel.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.gui;

import javax.swing.AbstractListModel;

public class UserListModel extends AbstractListModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -152556467398690482L;
	private UserInfo[] m_data; 
	public UserListModel(UserInfo[] data)
	{
		m_data = data;
	}
	public  void addUser(UserInfo data)
	{
		
	}
	public void removeUser(UserInfo data)
	{
		
	}
	
	public Object getElementAt(int index) {
		if (index< 0 || index >= getSize())
		{
			return null;
		}
		return m_data[index];
	}

	public int getSize() {
		// TODO Auto-generated method stub
		return m_data.length;
	}

}
