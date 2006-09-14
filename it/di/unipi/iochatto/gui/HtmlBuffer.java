/* FileName: it/di/unipi/iochatto/gui/HtmlBuffer.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.gui;

public class HtmlBuffer {
private String head = "<html><head></head>";
private String body = "<body><table cellspacing=\"0\" cellspadding=\"0\" width=\"100%\">";
private String foot = "</table></html>";
private StringBuffer inBuf = new StringBuffer();
public void appendHTMLLine(String s) {
	inBuf.append(s);
}
public String toString()
{
	String buffIn ="";
	buffIn = head + body + inBuf.toString() + foot;
	return buffIn;
}
}
