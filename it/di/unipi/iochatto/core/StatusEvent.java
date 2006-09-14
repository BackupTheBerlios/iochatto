/* FileName: it/di/unipi/iochatto/core/StatusEvent.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.core;

import java.util.EventObject;

public class StatusEvent extends EventObject {
	public String sourceName = null;
	public String reason = null;
	public StatusEvent(Object source) {
		super(source);
		sourceName = source.getClass().getName();
		// TODO Auto-generated constructor stub
	}
	public StatusEvent(Object source, String evt)
	{
		super(source);
		sourceName = source.getClass().getName();
		reason = evt;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 7812576178522307387L;

}
