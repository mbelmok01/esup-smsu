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
import org.esupportail.smsu.dao.beans.Message;
import org.esupportail.smsu.domain.beans.message.MessageStatus;
import org.esupportail.smsu.exceptions.CreateMessageException;
import org.esupportail.smsu.web.beans.UIMessage;
import org.esupportail.smsu.web.beans.UINewMessage;
import org.esupportail.smsuapi.exceptions.UnknownMessageIdException;
import org.esupportail.smsuapi.utils.HttpException;
import org.esupportail.ws.remote.beans.TrackInfos;

/**
 *
 * @author mohamed
 */

@Path("/notifications")
@RolesAllowed({"FCTN_SUIVI_ENVOIS_ETABL","FCTN_SUIVI_ENVOIS_UTIL"})
public class NotificationsController {
    
    private Integer notificationMaxSize;
    
    private final Logger logger = new LoggerImpl(getClass());
    
    
    @RolesAllowed(
            {"FCTN_SMS_ENVOI_ADH",
                "FCTN_SMS_ENVOI_GROUPES",
        "FCTN_SMS_ENVOI_NUM_TEL",
        "FCTN_SMS_ENVOI_LISTE_NUM_TEL",
        "FCTN_SMS_REQ_LDAP_ADH"})
    @POST
    @Produces("application/json")
    public void sendNotificationAction(UINewMessage msg, @Context HttpServletRequest request) throws CreateMessageException {
        String login = request.getRemoteUser();
        if (login == null) throw new InvalidParameterException("SERVICE.CLIENT.NOTDEFINED");
        msg.login = login;
        
        
    }
    
    @GET
    @Produces("application/json")
    public String getMessages(
            @QueryParam("sender") String senderLogin,
            @QueryParam("maxResults") @DefaultValue("0" /* no limit */) int maxResults,
            @Context HttpServletRequest request) {
        return "toto";
    }
        
    @GET
    @Produces("application/json")
    @Path("/{id:\\d+}")
    public String getMessage(
            @PathParam("id") int messageId,
            @Context HttpServletRequest request) {          
        return "tata";
    }
    
    @GET
    @Produces("application/json")
    @Path("/{id:\\d+}/statuses")
    public String getMessageStatuses(
            @PathParam("id") int messageId,
            @Context HttpServletRequest request) throws HttpException, UnknownMessageIdException {          
    
        return "test";
    }

    
}