/* FileName: it/di/unipi/iochatto/channel/UserInfo.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.channel;

public class UserInfo {
  private String PeerID = null;
  private String name= null;
  private String email = null;
  private int status = 0;
  public void setPeerID(String s)
  {
	  PeerID = new String(s);
  }
  public void setName(String s)
  {
	  name = new String(s);
  }
  public void setAddress(String s)
  {
	  email = new String(s);
  }
  public int getStatus()
  {
	  
	  return status;
  }
  public void setStatus(int s)
  {
	  status = s;
  }
  public String getPeerID() { return PeerID; }
  public String getName() { return name;}
  public String getAddress() { return email;}
  
}
