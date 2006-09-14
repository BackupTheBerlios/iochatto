/* FileName: it/di/unipi/iochatto/core/MainRun.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.core;

public class MainRun implements Runnable {

	private String[] args;
	private mainApp application;
	public void setArgs(String[] arg)
	{ args = arg;}
	public mainApp getApp()
	{
		return application;
	}
	public void run() {
		application = new mainApp(args);
		application.getJFrame().setVisible(true);

	}

}
