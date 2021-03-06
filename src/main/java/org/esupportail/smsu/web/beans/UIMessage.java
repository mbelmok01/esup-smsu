package org.esupportail.smsu.web.beans;

import java.util.Date;
import java.util.List;

public class UIMessage {

	public Integer id;
	public Date date;
	public String content;
	public Integer nbRecipients;

	/**
	 * senderName.
	 */
	public String senderName;
	public String senderLogin;
	
	/**
	 * group names.
	 */
	public String groupSenderName;
	public String groupRecipientName;

	public String accountLabel;
	public String serviceName;
	
	public String stateMessage;
	public String type;
        
	public String stateMail;
	public List<String> supervisors;
	public List<String> recipients;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getNbRecipients() {
		return nbRecipients;
	}

	public void setNbRecipients(Integer nbRecipients) {
		this.nbRecipients = nbRecipients;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public String getGroupSenderName() {
		return groupSenderName;
	}

	public void setGroupSenderName(String groupSenderName) {
		this.groupSenderName = groupSenderName;
	}

	public String getGroupRecipientName() {
		return groupRecipientName;
	}

	public void setGroupRecipientName(String groupRecipientName) {
		this.groupRecipientName = groupRecipientName;
	}

	public String getStateMessage() {
		return stateMessage;
	}

	public void setStateMessage(String stateMessage) {
		this.stateMessage = stateMessage;
	}
        
        public String getTypeMessage() {
		return type;
	}

	public void setTypeMessage(String type) {
		this.type = type;
	}

	public String getStateMail() {
		return stateMail;
	}

	public void setStateMail(String stateMail) {
		this.stateMail = stateMail;
	}

	public List<String> getSupervisors() {
		return supervisors;
	}

	public void setSupervisors(List<String> supervisors) {
		this.supervisors = supervisors;
	}

}
