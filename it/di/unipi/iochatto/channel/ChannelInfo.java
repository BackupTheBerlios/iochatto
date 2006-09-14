/* FileName: it/di/unipi/iochatto/channel/ChannelInfo.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.channel;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.jdom.*;
import org.jdom.input.SAXBuilder;
public class ChannelInfo {
private	Reader docIn = null;
private	String channelName = null;
private	String channelTopic = null;
private	long nanotime = 0L;
private	String founderName = null;
private	String founderAddress = null;
private	String founderPeerID = null;
private String pipeURI = null;
private	Document doc = null;
private	ConcurrentHashMap<String, UserInfo> chUsers = new ConcurrentHashMap<String,UserInfo>();

public void setPipeUri(String uri) { pipeURI = uri; };
public String getPipeUri() { return pipeURI; }
	public void parse(String s) throws JDOMException, IOException
	{
		docIn = new StringReader(s);
		System.out.println(s);
		SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(docIn);
        Element root = doc.getRootElement();
        // getting channel descriptor and rebuilding it
        Element chDesc = root;
        //.getChild("desc");
        // getting Channel Name
        // FIXME: there's a bug ...this a workaround.
        Element chName = chDesc.getChild("name");
        if (chName == null)
        {
        	chDesc = root.getChild("desc");
        	chName = chDesc.getChild("name");
        }
        channelName = new String(chName.getText());
        // getting channel topic
        Element chTopic = chDesc.getChild("topic");
        Element pipeUri = chDesc.getChild("pipeID");
        pipeURI = new String(pipeUri.getText());
        channelTopic = new String(chTopic.getText());
        Element chDateTime = chDesc.getChild("timestamp");
        nanotime = Long.parseLong(chDateTime.getText().trim());
        Element chFounder = chDesc.getChild("founder");
        if (chFounder==null)
        		return;
        founderName = chFounder.getChildText("name");
        founderAddress = chFounder.getChildText("email");
        founderPeerID = chFounder.getChildText("peerid");
        Element userListItem = root.getChild("userlist");
        if (userListItem==null)
        	return;
        List userList =  userListItem.getChildren();
        for (Object o : userList)
        {
        	Element e = (Element) o;
        	UserInfo infoItem = mkUserInfo(e);
        	chUsers.put(infoItem.getAddress(), infoItem);
        }
      
        }
		public void buildXML()
		{
			Element root = new Element("ChannelInfo");
			doc = new org.jdom.Document(root);
			// costructring descriptor
			Element desc = new Element("desc");
			Element name = (new Element("name")).setText(getName());
		    Element topicitem = (new Element("topic")).setText(getTopic());
		    Element pipeUri = (new Element("pipeID")).setText(getPipeUri());
		    desc.addContent(name);
		    desc.addContent(pipeUri);
		    desc.addContent(topicitem);
		    desc.addContent(mkTimeStampXML());
		    desc.addContent(mkFounderXML(founderName,founderAddress,founderPeerID));
		    root.addContent(desc);
		    // end descriptor
		    Element userList = new Element("userlist");
		    Set<Entry<String,UserInfo>> entrySet = chUsers.entrySet();
		    for (Entry<String,UserInfo> e: entrySet)
		    {
		    	UserInfo val = e.getValue();
		    	String s =  new Integer(val.getStatus()).toString();
		    	Element user = mkUserInfoXML(val.getName(),val.getAddress(),val.getPeerID(), s);
		    	userList.addContent(user);
		    }
		    root.addContent(userList);
		}
	   private Element mkFounderXML(String name, String address, String PeerID)
	   {
		   Element founder = new Element("founder");
		   Element founderName = new Element("name").setText(name);
		   Element founderAddress = new Element("email").setText(address);
		   Element founderPeerID = new Element("peerid").setText(PeerID);
		   founder.addContent(founderName);
		   founder.addContent(founderAddress);
		   founder.addContent(founderPeerID);
		   return founder;
	   }
	   private Element mkTimeStampXML()
	   {
		   nanotime = System.currentTimeMillis();
		   Element timestamp = new Element("timestamp").setText(Long.toString(nanotime));
		   return timestamp;
	   }
	 
	   public String getName()
	   {
		   return this.channelName;
	   }
	   public String getTopic()
	   {
		   return this.channelTopic;
	   }
	   public String getFounderName()
	   {
		   return this.founderName;
	   }
	   public String getFounderEmailAddress()
	   {
		   return this.founderAddress;
	   }
	   public String getFounderPeerID()
	   {
		   return this.founderPeerID;
	   }
	   public void setFounderName(String name)
	   {
		   founderName = name;
	   }
	   public void setFounderEmailAddress(String address)
	   {
		   founderAddress = address;
	   }
	   public void setFounderPeerID(String peerid)
	   {
		founderPeerID = peerid;   
	   }
	   public long getTimeStamp()
	   {
		   return nanotime;
	   }
	   private Element mkUserInfoXML(String name,String address,String PeerID, String status)
	   {
		   Element user = new Element("user");
		   Element userName = new Element("name").setText(name);
		   Element userAddress = new Element("email").setText(address);
		   Element userPeerID = new Element("peerid").setText(PeerID);
		   Element userStatus = new Element("status").setText(status);
		   user.addContent(userName);
		   user.addContent(userAddress);
		   user.addContent(userPeerID);
		   user.addContent(userStatus);
		   return user;
	   }
	   
	   private UserInfo mkUserInfo(Element user)
	   {
		   UserInfo info = new UserInfo();
		   String name = user.getChildText("name");
		   String address = user.getChildText("email");
		   String peerid = user.getChildText("peerid");
		   String status = user.getChildText("status");
		   if (status==null)
			   status = "0";
		   info.setAddress(address);
		   info.setPeerID(peerid);
		   info.setName(name);
		   info.setStatus(Integer.parseInt(status));
	      return info;
	   }
	   public UserInfo findUser(String address)
	   {
		if (chUsers.containsKey(address))
		{
			return chUsers.get(address);
		}
		return null;
	   }
	   public void setChannelName(String name) { channelName = name;}
	   public void setTopic(String topic) { this.channelTopic = topic; }
	   public void addUser(String name, String address, String peerID, int i) {
		   UserInfo info = new UserInfo();
		   info.setAddress(address.trim());
		   info.setPeerID(peerID);
		   info.setName(name);
		   info.setStatus(i);
		   if (chUsers.containsKey(address.trim()))
			   chUsers.remove(address);
		   chUsers.put(address.trim(), info);

	   }
	  

	  
	  public boolean removeUser(String emailAddress)
	  {
		  if(chUsers.containsKey(emailAddress))
		  {
			UserInfo add = chUsers.remove(emailAddress);
			add = null;
			return true;
		  }
		  return false;
		  }
	    public void visitDFS(Element current, StringBuffer b)
	    {
	    	Element item=null;
	    	if (current == null)
	    			return;
	    	String tagName = current.getName();
	    	String tmp = "<"+tagName+">" +current.getText();
	    	b.append(tmp);
	    	List children  = current.getChildren();
	    	Iterator it = children.iterator();
	    	while (it.hasNext()){
	    	item = (Element) it.next();
	    	if (item!=null) {
	    		visitDFS(item,b);
	    	}
	    	}
	    	b.append("</"+tagName+">\n");
	    }
	    public HashMap<String,UserInfo> getUsers()
	    {
	    	HashMap<String, UserInfo> m = new HashMap<String, UserInfo>();
	    	Set<String> userSet = chUsers.keySet();
			for (String key: userSet)
			{
				UserInfo info = chUsers.get(key);
				if (info!=null)
					m.put(key, info);
			}
	    	return m;
	    }
	    public String toString()
	    {
	     StringBuffer buf =new StringBuffer();
	     Element root = doc.getRootElement();
	     visitDFS(root,buf);
	     return buf.toString();
	     
	    }
		public void clear() {
			Set<String> userSet = chUsers.keySet();
			for (String key: userSet)
			{
				UserInfo info = chUsers.remove(key);
				info = null;
			}
			doc.removeContent();
			doc = null;
			// TODO Auto-generated method stub
			
		}
	  
}
