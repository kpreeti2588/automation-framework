package org.iomedia.galen.common;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.SearchTerm;

import org.iomedia.common.BaseUtil;
import org.testng.SkipException;

public class RecieveMail {
	BaseUtil base;
     
	public RecieveMail(BaseUtil base ) {
		this.base = base;
	}
	
	public Store connect(String emailAddress, String password) throws MessagingException{
		Properties props = new Properties();
        props.put("mail.store.protocol", "imaps");
        props.put("mail.imaps.ssl.trust", "*");
        Session session = Session.getInstance(props, null);
        Store store = session.getStore();
        store.connect("outlook.office365.com", 993, emailAddress, password);
        return store;
	}
	
	public  void close(Store store) throws MessagingException{
		if(store != null)
			store.close();
	}
	
	public Folder getFolder(Store store, String folderName) throws Exception{
		Folder folder = store.getFolder(folderName);
		folder.open(Folder.READ_WRITE);
		return folder;
	}
	
	public  Message[] getMessages(Folder folder) throws Exception{
		return folder.getMessages();
	}
	
	public  Message[] waitForMessages(Folder folder, int actualCount) throws Exception{
        Folder request = null;
        try {     
            request = folder;
            long time = System.currentTimeMillis();
            long end = time + 30000;
            while(System.currentTimeMillis() < end) {
            	if(request.getMessages().length <= actualCount) {
            		Thread.sleep(100);
            	}
            	else {
            		break;
            	}
            }          
            if(request.getMessages().length <= actualCount)
            	throw new SkipException("No new email found");
            return request.getMessages();
        } catch (Exception mex) {
            throw mex;
        }
	}
	
	public void copyMessages(Folder from, Folder to, Message[] messages) throws MessagingException{
		from.copyMessages(messages, to);
	}
	
	public void deleteMessage(Message msg) throws MessagingException{
		msg.setFlag(Flags.Flag.DELETED, true);
	}
	
	public String SearchMail(Message  msg) throws Exception {
         String subject = msg.getSubject();
         return subject;
	}
	
	public String SearchLinkContent(Folder folder, int actualCount) throws Exception {
		Message[] msg = waitForMessages(folder, actualCount);
//		Flags seen = new Flags(Flags.Flag.SEEN);
//		FlagTerm unseenFlagTerm = new FlagTerm(seen, true);
//		msg = folder.search(unseenFlagTerm);
		@SuppressWarnings("serial")
		SearchTerm term = new SearchTerm() {
		    public boolean match(Message message) {
		        try {
		            if (message.getSubject().contains("Password Reset Link")) {
		                return true;
		            }
		        } catch (MessagingException ex) {
		            ex.printStackTrace();
		        } catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        return false;
		    }
		};
		
		Message[] foundMessages = folder.search(term, msg);
		if(foundMessages.length == 0)
			throw new SkipException("No email found");
		String htmlcontent = "";
		String link = "";
		Message message = null;
		for(int i = foundMessages.length - 1; i >= 0; i--) {
			htmlcontent = "";
			message = foundMessages[i];
			Object content= message.getContent();
			if (content instanceof Multipart) {
				Multipart mp = (Multipart) content;
				for (int j = 0; j < mp.getCount(); j++) {
					BodyPart bp = mp.getBodyPart(j);
					if (Pattern.compile(Pattern.quote("text/html"), Pattern.CASE_INSENSITIVE).matcher(bp.getContentType()).find()) {
						// found html part
						htmlcontent=(String) bp.getContent();
						break;
					} else {
						// some other bodypart...
					}
				}
				if(!htmlcontent.trim().equalsIgnoreCase("") && getResetPasswordlink(htmlcontent).contains(base.Environment.get("TM_HOST"))){
					link= getResetPasswordlink(htmlcontent);
					break;
				}
			}
		}
		if(link.trim().equalsIgnoreCase(""))
			throw new SkipException("Forot password link not found");
		if(message != null)
			deleteMessage(message);
		folder.close(true);
		return link;
	}
	
	public String getResetPasswordlink(String htmlcontent) throws Exception {		
		Pattern p = Pattern.compile("href=\"(.*?)\"", Pattern.DOTALL);
		Matcher m = p.matcher(htmlcontent);

		String link = "";
		while (m.find()) {
			link = m.group(1);
		}
		
		return link;
	}
}