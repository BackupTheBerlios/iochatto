/* FileName: it/di/unipi/iochatto/core/Status.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.core;


public class Status {
	private static Status instance = null;
	private int status;
	private String pid;
	private String emailAddress;
	private String name;
	private Status()
	{
		
	}
	public static Status getInstance()
	{
		if (instance == null)
		{
			synchronized(Status.class)
			{
				if (instance == null)
				{
					instance = new Status();
				}
			}
		}
		return instance;
	}
	public synchronized void setStatus(int Status) { status = Status; }
	public synchronized int getStatus() { return status; }
	public synchronized void setPeerID(String Status) { pid = Status; }
	public synchronized String getPeerID() { return pid; }
	public synchronized void setName(String Status) { name = Status; }
	public synchronized String getName() { return name; }
	public synchronized void setEmailAddress(String Status) { emailAddress = Status; }
	public synchronized String getEmailAddress() { return emailAddress; }
}
