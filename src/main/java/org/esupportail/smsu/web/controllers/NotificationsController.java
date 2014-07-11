/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.esupportail.smsu.web.controllers;

import java.util.Date;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsu.business.MessageManager;
import org.esupportail.smsu.business.SendNotificationManager;
import org.esupportail.smsu.business.SendSmsManager;
import org.esupportail.smsu.exceptions.CreateMessageException;
import org.esupportail.smsu.services.ldap.beans.UserGroup;
import org.esupportail.smsu.services.smtp.SmtpServiceUtils;
import org.esupportail.smsu.web.beans.MailToSend;
import org.esupportail.smsu.web.beans.UIMessage;
import org.esupportail.smsu.web.beans.UINewMessage;
import org.esupportail.smsuapi.exceptions.UnknownMessageIdException;
import org.esupportail.smsuapi.utils.HttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestOperations;
/**
 *
 * @author mohamed
 */

@Path("/notifications")
@RolesAllowed({"FCTN_SUIVI_ENVOIS_ETABL","FCTN_SUIVI_ENVOIS_UTIL"})
public class NotificationsController {
    
    private Integer notificationMaxSize;
    private RestOperations restTemplate;
    private final Logger logger = new LoggerImpl(getClass());
    
    @Autowired private SmtpServiceUtils smtpServiceUtils;
    @Autowired private SendSmsManager sendSmsManager;
    @Autowired private MessageManager messageManager;
    @Autowired private SendNotificationManager sendNotificationManager;

    
    
    @RolesAllowed(
            {"FCTN_SMS_ENVOI_ADH",
                "FCTN_SMS_ENVOI_GROUPES",
                "FCTN_SMS_ENVOI_NUM_TEL",
                "FCTN_SMS_ENVOI_LISTE_NUM_TEL",
                "FCTN_SMS_REQ_LDAP_ADH"})
    @POST
    @Produces("application/json")
    public UIMessage sendNotificationAction(UINewMessage msg, @Context HttpServletRequest request) throws CreateMessageException {
        String login = request.getRemoteUser();
		if (login == null) throw new InvalidParameterException("SERVICE.CLIENT.NOTDEFINED");
		msg.login = login;

		recipientsValidation(msg, request, login);
		userGroupValidation(msg.senderGroup, login);
		contentValidation(msg.content);
		if (msg.mailToSend != null) {
			if (!request.isUserInRole("FCTN_SMS_AJOUT_MAIL"))
				throw new InvalidParameterException("user " + login + " is not allowed to send mails");
			mailsValidation(msg.mailToSend);
		}
                int messageId = sendNotificationManager.sendMessage(msg, request);
		return messageManager.getUIMessage(messageId, null);
    }
    
    @GET
	@Produces("application/json")
	public List<UIMessage> getMessages(
			@QueryParam("sender") String senderLogin,
			@QueryParam("maxResults") @DefaultValue("0" /* no limit */) int maxResults,
			@Context HttpServletRequest request) {
		senderLogin = allowedSender(request, senderLogin);
		Date beginDate = null;
		Date endDate = null;
                String type = "push";
		List list = messageManager.getMessages(null, null, null, null, senderLogin, beginDate, endDate, maxResults, type);
                
                return list;
	}
        
    @GET
    @Produces("application/json")
    @Path("/{id:\\d+}")
    public String getMessage( @PathParam("id") int messageId, @Context HttpServletRequest request) {          
        return "tata";
    }
    
    
    @GET
    @Produces("application/json")
    @Path("/push")
    public UIMessage getMessageType(@Context HttpServletRequest request) {          
        return messageManager.getUIMessage("push", allowedSender(request));
    }
    
    @GET
    @Produces("application/json")
    @Path("/{id:\\d+}/statuses")
    public String getMessageStatuses( @PathParam("id") int messageId, @Context HttpServletRequest request) throws HttpException, UnknownMessageIdException {          
        return "test";
    }
    
    private void recipientsValidation(UINewMessage msg, HttpServletRequest request, String login) {
        if (msg.recipientLogins != null)
            if (!request.isUserInRole("FCTN_PUSH_ENVOI_LOGIN"))
                throw new InvalidParameterException("user " + login + " is not allowed to send Push notification");
        if (!StringUtils.isEmpty(msg.recipientGroup))
            if (!request.isUserInRole("FCTN_PUSH_ENVOI_GROUPES"))
                throw new InvalidParameterException("user " + login + " is not allowed to send Push notification to groups");
    }
    
    private void userGroupValidation(String userGroup, String login) {
        for (UserGroup g : sendNotificationManager.getUserGroupLeaves(login))
            if (g.id.equals(userGroup)) return;
        throw new InvalidParameterException("user " + login + " is not in group " + userGroup);
    }
    
    private void contentValidation(final String content) {
        Integer contentSize = content == null ? 0: content.length();
        logger.debug("taille de message : " + contentSize.toString());
        logger.debug("message : " + content);
        if (contentSize == 0) {
            throw new InvalidParameterException("SENDNOTIFICATION.MESSAGE.EMPTYMESSAGE");
        } else if (contentSize > notificationMaxSize) {
            throw new InvalidParameterException("SENDNOTIFICATION.MESSAGE.MESSAGETOOLONG");
        }
    }
    
    private void mailsValidation(MailToSend mailToSend) {
        List<String> mails = mailToSend.getMailOtherRecipients();
        if (mails.isEmpty() && !mailToSend.getIsMailToRecipients()) {
            throw new InvalidParameterException("SENDNOTIFICATION.MESSAGE.RECIPIENTSMANDATORY");
        }
        for (String mail : mails) {
            if (logger.isDebugEnabled()) logger.debug("mail validateOthersMails is :" + mail);
            if (!smtpServiceUtils.checkInternetAdresses(mail)) {
                logger.info("validateOthersMails: " + mail + " is invalid");
                throw new InvalidParameterException("SERVICE.FORMATMAIL.WRONG:" + mail);
            }
        }
    }
    
    	@Required
	public void setNotificationMaxSize(final Integer notificationMaxSize) {
		this.notificationMaxSize = notificationMaxSize;
	}
        
        private String allowedSender(HttpServletRequest request, String wanted) {
		String allowedSender = allowedSender(request);
		
		if (allowedSender == null) {
			return wanted;
		} else if (wanted == null || wanted.equals(allowedSender)) {
			return allowedSender;
		} else {
			throw new InvalidParameterException(allowedSender + " is not allowed to view messages sent by " + wanted);
		}
	}

    private String allowedSender(HttpServletRequest request) {
		if (request.isUserInRole("FCTN_SUIVI_ENVOIS_ETABL")) {
			return null;
		} else {
			// only return the messages sent by this user
			return request.getRemoteUser();				
		} 
	}

    
}