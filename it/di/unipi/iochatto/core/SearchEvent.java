/* FileName: it/di/unipi/iochatto/core/SearchEvent.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.core;

public class SearchEvent extends StatusEvent {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6957482695056355682L;
	public SearchEvent(Object source) {
		super(source);
	}
	public SearchEvent(Object source, String msg)
	{
		super(source,msg);
	}

}
