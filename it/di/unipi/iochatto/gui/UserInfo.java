/* FileName: it/di/unipi/iochatto/gui/UserInfo.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.gui;

public class UserInfo {
private String userName = null;
private int status;
public UserInfo (String name, int Status)
{
	userName = name;
	status = Status;
}
public void setUser(String s)
{ userName = s; };
public String getUser() {return userName;};
public void setStatus(int s) { status = s; };
public int getStatus() { return status; };

}
