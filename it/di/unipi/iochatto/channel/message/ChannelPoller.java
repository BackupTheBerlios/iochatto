/* FileName: it/di/unipi/iochatto/channel/message/ChannelPoller.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.channel.message;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Logger;

import org.jdom.JDOMException;

import net.jxta.document.StructuredDocumentFactory;
import net.jxta.document.XMLDocument;
import net.jxta.endpoint.Message;
import net.jxta.endpoint.MessageElement;
import net.jxta.endpoint.StringMessageElement;
import net.jxta.endpoint.TextDocumentMessageElement;
import net.jxta.endpoint.Message.ElementIterator;
import net.jxta.pipe.InputPipe;
import it.di.unipi.iochatto.channel.Channel;
import it.di.unipi.iochatto.channel.ChannelInfo;
import it.di.unipi.iochatto.core.Status;
public class ChannelPoller implements Runnable {
	private int upperBound = 0;
	private int size = 0;
	private int pollTimeOut = 3000;
	private Channel[] elementData = null;
	private String[]  elementName = null;
	private String emptyLocker = "empty";
	private String[] validElement = new String[] {"ChannelJoinMessage","ChannelInfoMessage","ChatMessage","ChannelLeaveMessage"};
	private Logger log = Logger.getLogger(ChannelPoller.class.getName());
	//private int ArrayList<>
	public ChannelPoller()
	{
		elementData = new Channel[10];
		elementName = new String[10];
		upperBound = 10;
	}
	public synchronized void addChannel(Channel ch)
	{

		//map.put(ch.getName(), ch.createInputPipe());
		if (size < elementData.length)
		{
			elementData[size] = ch;
			elementName[size] = ch.getName();
			log.info("Adding channel = "+ch.getName());
			++size;
		}
		else {
			// resize and copy
			upperBound = (upperBound * 3)/2 + 1;


			Channel[] tmp = new Channel[upperBound];
			System.arraycopy(elementData, 0, tmp, 0, elementData.length);
			for (int k = 0; k < elementData.length; ++k)
				elementData[k] = null;
			String[] tmpName = new String[upperBound];
			System.arraycopy(elementName, 0, tmp, 0, elementName.length);
			for (int k = 0; k < elementName.length; ++k)
				elementName[k] = null;
			elementData = tmp;
			elementName = tmpName;
			elementName[size] = ch.getName();
			elementData[size++] = ch;
		}
		synchronized(emptyLocker)
		{
			emptyLocker.notifyAll();
		}
	
	}
	public synchronized void removeChannel(Channel ch)
	{
		remove(ch);
		//Channel in = ch.remove(ch.getName());
	}
	public synchronized void clear()
	{
		for (int index = 0; index < elementData.length; ++index)
		{
			if (elementData[index]!=null)
				elementData[index] = null;
		}
		for (int index = 0; index < elementName.length; ++index)
		{
			if (elementName[index]!=null)
				elementName[index] = null;
		}
	}


	private boolean remove(Channel o) {
		if (o == null) {
			return false;

		} else {
			o.cleanPollData();
			for (int index = 0; index < size; index++)
			{
				if (o.equals(elementData[index])) {
					fastRemove(index);
					return true;
				}
			}
		}
		return false;
	}

	private void fastRemove(int index) {

		int numMoved = size - index - 1;
		if (numMoved > 0){
			System.arraycopy(elementData, index+1, elementData, index, 
					numMoved);
			System.arraycopy(elementName, index+1, elementName, index, 
					numMoved);
		}
		elementName[size-1] = null; // Let gc do its work
		elementData[--size] = null; // Let gc do its work
	}

	public synchronized int alea()
	{
		int startpos = 0;
		int maxpos = size;
		Random r = new Random();
		if (maxpos>elementData.length)
			maxpos = elementData.length;
		if (maxpos > 1)
			startpos = (r.nextInt() % maxpos-1);
		return startpos;
	}
	private ChannelMessage loadMessage(String className,String element)
	{
		Constructor constructror = null;
		Class[] parameterTypes = new Class[1];
		Object[] objectInit = new Object[1];
		objectInit[0] = element;
		ChannelMessage chMsg0 = null;
		parameterTypes[0] = String.class;
		Class messageClass = null;
		final String pack = "it.di.unipi.iochatto.channel.message.";
		String myClass = pack + className;
		log.info("Loading..."+myClass);
		
		try {
			messageClass = Class.forName(myClass);
		} catch (ClassNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
			return null;
		}
		try {
			constructror  = messageClass.getConstructor(parameterTypes);
		} catch (NoSuchMethodException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}
		try {
			chMsg0 = (ChannelMessage) constructror.newInstance(objectInit);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	
		return chMsg0;
	}
	private ChannelMessage decodeMsg(Message m)
	{
		
		ChannelMessage chMsg0 = null;
		ElementIterator iter = m.getMessageElements();
		while ((iter!=null) && (iter.hasNext()))
		{
			MessageElement m1 = (MessageElement) iter.next();
			
			for (int k = 0 ; k < validElement.length;++k)
			{
				String element = m1.getElementName().trim();
				if (validElement[k].equals(element))
						{
						chMsg0 = loadMessage(element,m1.toString());
						if (chMsg0!=null)
							chMsg0.parse();
						}
						
			}
			
		}
		return  chMsg0;
	}
	private ChannelMessageEvent mkEvent(ChannelMessage m0, int index)
	{
		ChannelMessageEvent ev = new ChannelMessageEvent(this);
		ev.setChannelName(elementData[index].getName());
		ev.setMessage(m0);
		//elementData[index].getMetadata(); 
		//ChannelInfo copied = new ChannelInfo();
		//chInfo.getUsers();
		ev.setMetaData(elementData[index].getMetadata());
		return ev;
	}
	public void run() {
		int k = 0;
		while(true)
		{	synchronized(emptyLocker)
			{
			if (elementData.length == 0)
			{
			try {
				emptyLocker.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			}
			}
			try {
			    Thread.sleep(10000);
			} catch (InterruptedException e) {
				for (int h= 0; h < elementData.length; ++h)
				{
					if (elementData[h]!=null){
					elementData[h].cleanPollData();
					elementData[h] = null;
					}
				}
				System.gc();
				return;
			}
			Message m = null;

			int start = alea();
			for (k = start; k < size; ++k)
			{
				if (elementData.length > 0)
				{
					try {
						k = (k<0) ? 0 : k;
						start = k;
						if (elementData[k]!=null)
						{
							log.info("Polling channel "+ elementData[0].getName());
							m = elementData[k].Poll(pollTimeOut);
						}
					} catch (InterruptedException t0)
					{
						for (int h= 0; h < elementData.length; ++h)
						{
							if (elementData[h]!=null){
							elementData[h].cleanPollData();
							elementData[h] = null;
							}
						}
						//t0.printStackTrace();

					}
				} // endif0

				if (m!=null)
				{
					ChannelMessage mCh = decodeMsg(m);
					if (mCh != null)
					{		
					log.info("Message sent by "+mCh.senderAddress()+ " for channel: "+mCh.channelName());
					ChannelMessageEvent ev = mkEvent(mCh,k);
					if (ev!=null) {
					log.info("Firing Channel Message event "+ev.getChannelName() + " POS: "+k+ " Object "+elementData[k]);
					boolean fire = true;
				
					if (mCh instanceof ChannelLeaveMessage)
					{
						ChannelInfo info0 = new ChannelInfo();
				
						try {
							info0.parse(ev.getMetadata().toString());
						} catch (JDOMException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						info0.removeUser(mCh.senderAddress());
						info0.setChannelName(mCh.channelName());
						info0.buildXML();
						log.info("Messaggio :" + info0.toString());
						ChannelInfoMessage msg0 = new ChannelInfoMessage(info0);
						elementData[k].removeUser(mCh.senderAddress());
						ev = mkEvent(msg0,k);
					}
				
				
					if (mCh instanceof ChatMessage)
					{
						ChatMessage msg3 = (ChatMessage)  mCh;
						Status s = Status.getInstance();
						fire = !msg3.senderAddress().equals(s.getEmailAddress());
						}
						
					if (fire)
						elementData[k].fireMessageEvent(ev);
					}
					//System.out.println(m.toString());
					m = null;
					}
				} //end if2
			}// end for
		} //endwhile
	} //endproc
} // endclass