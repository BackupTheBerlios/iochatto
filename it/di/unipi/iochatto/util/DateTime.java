/* FileName: it/di/unipi/iochatto/util/DateTime.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.util;
import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.Date;
//date = cipher cipher"/"cipher cipher"/"cipher cipher cipher cipher.
// datetime = number":"number":"number":"date.
public class DateTime {
private int day;
private int month;
private int year;
private int min;
private int hour;
private int second;
private Calendar cal = null;
public DateTime(String datetime) {
	year = min = hour = second = month = day = 0 ;
	cal = null;
	parseDateTime(datetime);
};
public DateTime(Date d)
{
	setDate(d);
}
public Calendar getCalendar()
{
	if (cal == null)
	{
		cal = Calendar.getInstance();
		cal.set(Calendar.YEAR,year);
		cal.set(Calendar.DAY_OF_MONTH,day);
		cal.set(Calendar.MONTH,month-1);
		cal.set(Calendar.HOUR_OF_DAY,hour);
		cal.set(Calendar.MINUTE,min);
		cal.set(Calendar.SECOND,second);
	}
	return cal;
}
public void clear()
{
	cal = null;
}
public void setDate(Date d)
{
	cal = null;
	cal = Calendar.getInstance();
	cal.setTime(d);
	year = cal.get(Calendar.YEAR);
	month = cal.get(Calendar.MONTH);
	day = cal.get(Calendar.DAY_OF_MONTH);
	hour = cal.get(Calendar.HOUR_OF_DAY);
	min = cal.get(Calendar.MINUTE);
	second = cal.get(Calendar.SECOND);
}
public void parse(String dt)
{
	this.clear();
	parseDateTime(dt);
}
private void parseDateTime(String dt)
{
int pos = 0;
String tmpValue = null;
StringTokenizer st = new StringTokenizer(dt,":");
String dateValue = null;
while (st.hasMoreTokens())
{
	tmpValue = st.nextToken();
	if (pos == 0)
	{
		hour = Integer.parseInt(tmpValue);
	}
	if (pos == 1)
	{
		min = Integer.parseInt(tmpValue);
	}
	if (pos == 2){
		second = Integer.parseInt(tmpValue);
	}
	if (pos == 3)
	{
		dateValue = tmpValue;
	}
	++pos;
}
pos  = 0 ;
st = null;
st = new StringTokenizer(dateValue,"/");
while ((dateValue!=null) && (st.hasMoreTokens()))
{
	tmpValue = st.nextToken();
	if (pos == 0)
	{
		day = Integer.parseInt(tmpValue);
		
	}
	if (pos == 1)
	{
		month = Integer.parseInt(tmpValue);
	}
	if (pos == 2){
		year = Integer.parseInt(tmpValue);
	}
	++pos;
}
}
public String toString()
{
	String date = hour+":"+min+":"+second+":"+day+"/"+month+"/"+year;
	return date;
}
}
