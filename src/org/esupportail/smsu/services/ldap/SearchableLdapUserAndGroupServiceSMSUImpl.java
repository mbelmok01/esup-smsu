package org.esupportail.smsu.services.ldap;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.esupportail.commons.exceptions.UserNotFoundException;
import org.esupportail.commons.services.ldap.LdapUser;
import org.esupportail.commons.services.ldap.SearchableLdapUserAndGroupServiceImpl;
import org.esupportail.commons.services.ldap.SearchableLdapUserServiceImpl;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsu.exceptions.ldap.LdapUserNotFoundException;
import org.springframework.ldap.LdapTemplate;
import org.springframework.ldap.support.filter.AndFilter;
import org.springframework.ldap.support.filter.EqualsFilter;
import org.springframework.ldap.support.filter.OrFilter;
import org.springframework.ldap.support.filter.WhitespaceWildcardsFilter;
import org.springframework.util.StringUtils;

/**
 * SMSU implementation of the LdapUserAndGroupService.
 * @author PRQD8824
 *
 */
public class SearchableLdapUserAndGroupServiceSMSUImpl extends SearchableLdapUserAndGroupServiceImpl {
	
	/**
	 * serial UID.
	 */
	private static final long serialVersionUID = -4824757654577713044L;
	
	/**
	 * Logger.
	 */
	private final Logger logger = new LoggerImpl(getClass());
	
	/**
	 * Spring template used to perform search in the ldap.
	 */
	private LdapTemplate ldapTemplate;
	
	/**
	 * The uid ldap attribute name.
	 */
	private String userIdAttribute;
	
	
	/**
	 * The display name ldap attribute name.
	 */
	private String userDisplayName;
	
	/**
	 * The attribute first name in the ldap.
	 */
	private String firstNameAttribute;

	/**
	 * The attribute last name in the ldap.
	 */
	private String lastNameAttribute;
	
	/**
	 * The email ldap attribute name.
	 */
	private String userEmailAttribute;
	
	/**
	 * The pager attributeName.
	 */
	private String userPagerAttribute;
	
	/**
	 * The attribute terms of use in the ldap.
	 */
	private String userTermsOfUseAttribute;

	/**
	 * the user object class.
	 */
	private String userObjectClass;
	
	/**
	 * The search attribute field.
	 */
	private String searchAttribute;
	
	/**
	 * constructor.
	 */
	public SearchableLdapUserAndGroupServiceSMSUImpl() {
		super();
	}
	/**
	 * Get the user display name from the ldap.
	 * Return null if no exist.
	 * @param uid
	 * @return
	 */
	public String getUserDisplayNameByUid(final String uid) throws LdapUserNotFoundException  {
		final String displayName = getUniqueLdapAttributeByUidAndName(uid, userDisplayName);
		return displayName;
	}
	
	/**
	 * Get the user first name in the ldap.
	 * @param uid
	 * @return
	 * @throws LdapUserNotFoundException
	 */
	public String getUserFirstNameByUid(final String uid) throws LdapUserNotFoundException {
		final String firstName = getUniqueLdapAttributeByUidAndName(uid, firstNameAttribute);
		return firstName;
	}

	/**
	 * Get the user last name in the ldap.
	 * @param uid
	 * @return
	 * @throws LdapUserNotFoundException
	 */
	public String getUserLastNameByUid(final String uid) throws LdapUserNotFoundException {
		final String lastName = getUniqueLdapAttributeByUidAndName(uid, lastNameAttribute);
		return lastName;
	}


	
	/**
	 * Get the user email adress from the ldap.
	 * Return null if no exist.
	 * @param uid
	 * @return
	 */
	public String getUserEmailAdressByUid(final String uid) throws LdapUserNotFoundException {
		final String email = getUniqueLdapAttributeByUidAndName(uid, userEmailAttribute);
		return email;
	}
	
	/**
	 * Get the user pager from the ldap.
	 * Return null if no exist.
	 * @param uid
	 * @return
	 */
	public String getUserPagerByUid(final String uid) throws LdapUserNotFoundException {
		final String pager = getUniqueLdapAttributeByUidAndName(uid, userPagerAttribute);
		return pager;
	}
	
	/**
	 * 
	 * @param uid
	 * @return
	 */
	public List<String> getUserTermsOfUse(final String uid) throws LdapUserNotFoundException {
		final List<String> termsOfUse = getLdapAttributesByUidAndName(uid, userTermsOfUseAttribute);
		return termsOfUse;
	}
	
	
	
	/**
	 * 
	 * @param uid
	 * @param name
	 * @return
	 */
	private String getUniqueLdapAttributeByUidAndName(final String uid, 
			final String name) throws LdapUserNotFoundException {
		String retVal = null;
		final List<String> tmp = getLdapAttributesByUidAndName(uid, name);
		
		if (tmp.size() > 0) {
			retVal = tmp.get(0);
		}
		
		return retVal;
	}
	
	
	/**
	 * 
	 * @param uid
	 * @param name
	 * @return
	 */
	public List<String> getLdapAttributesByUidAndName(final String uid, 
			final String name) throws LdapUserNotFoundException {
		List<String> retVal = null;
		
		final SearchableLdapUserServiceImpl userServiceTmp = new SearchableLdapUserServiceImpl();
		userServiceTmp.setIdAttribute(userIdAttribute);
		userServiceTmp.setObjectClass(userObjectClass);
		userServiceTmp.setLdapTemplate(ldapTemplate);
		final List<String> attrList = new LinkedList<String>();
		attrList.add(name);
		userServiceTmp.setAttributes(attrList);
		userServiceTmp.afterPropertiesSet();

		try {
			final LdapUser ldapUser = userServiceTmp.getLdapUser(uid);
			retVal = ldapUser.getAttributes(name);
		} catch (UserNotFoundException e) {
			final StringBuilder message = new StringBuilder();
			message.append("Unable to find the user with id : [");
			message.append(uid);
			message.append("]");
			final String messageStr = message.toString();
			throw new LdapUserNotFoundException(messageStr, e);
		}
		return retVal;
	}
	
	
	/**
	 * Search an user by the search attribute and a token.
	 * @param token
	 */
	public List<LdapUser> getLdapUsersFromToken(final String token) {
		final String[] tokenList = token.split("\\p{Blank}");
		
		final AndFilter filter = new AndFilter();
		for (String tok : tokenList) {
			if (tok.length() > 0) {
				filter.and(new WhitespaceWildcardsFilter(searchAttribute, tok));
			}
		}
				
		final String filterAsStr = filter.encode();
		final List<LdapUser> retVal = getLdapUsersFromFilter(filterAsStr);
		return retVal;
	}
	
	/**
	 * Search an user by the pager attribute and a token.
	 * @param token
	 */
	public List<LdapUser> getLdapUsersFromPhoneNumber(final String token) {
		final AndFilter filter = new AndFilter();
		
		//add the pager filter
		filter.and(new WhitespaceWildcardsFilter(userPagerAttribute, token));
		
				
		final String filterAsStr = filter.encode();
		final List<LdapUser> retVal = getLdapUsersFromFilter(filterAsStr);
		return retVal;
	}
	
	/**
	 * @param uid
	 * @param cgKeyName
	 * @param service
	 * @return the ldap user if he is eligible for the service
	 */
	public List<LdapUser> getConditionFriendlyLdapUsersFromUid(final List<String> uids,
			final String cgKeyName, final String service) {
		
		final AndFilter filter = new AndFilter();
		
		//add the general condition, service and pager filter
		filter.and(new WhitespaceWildcardsFilter(userPagerAttribute, " "));
		filter.and(new EqualsFilter(userTermsOfUseAttribute, cgKeyName));
		if (service != null) {
			filter.and(new EqualsFilter(userTermsOfUseAttribute, service));
		}
		
		final OrFilter orFilter = new OrFilter();
		for (String uid : uids) {
			orFilter.or(new EqualsFilter(userIdAttribute, uid));
		}
		filter.and(orFilter);		
		final String filterAsStr = filter.encode();
		if (logger.isDebugEnabled()) {
			final StringBuilder infofltr = new StringBuilder(50);
			infofltr.append("LDAP filter applied : ");
			infofltr.append(filterAsStr);
			final String messageftr = infofltr.toString();
			logger.debug(messageftr);
		}
		final List<LdapUser> retVal = getLdapUsersFromFilter(filterAsStr);
		return retVal;
	}
	
	/**
	 * @param uids
	 * @return a list of mails
	 */
	public List<LdapUser> getUsersByUids(final List<String> uids) {
		final OrFilter filter = new OrFilter();
		for (String uid : uids) {
			filter.or(new EqualsFilter(userIdAttribute, uid));
		}
		final String filterAsStr = filter.encode();
		if (logger.isDebugEnabled()) {
			final StringBuilder infofltr = new StringBuilder(50);
			infofltr.append("LDAP filter applied : ");
			infofltr.append(filterAsStr);
			final String messageftr = infofltr.toString();
			logger.debug(messageftr);
		}
		final List<LdapUser> retVal = getLdapUsersFromFilter(filterAsStr);
		return retVal;
	}
	
	/**
	 * @param uids
	 * @return a list for user mails.
	 */
	public List<String> getUserMailsByUids(final List<String> uids) {
		final List<LdapUser> ldapUsers = getUsersByUids(uids);
		final List<String> retVal = new ArrayList<String>();
		for (LdapUser ldapUser : ldapUsers) {
			if (logger.isDebugEnabled()) {
				logger.debug("mail recipient added to list :" 
						+ ldapUser.getAttribute(userEmailAttribute).trim());
			}
			retVal.add(ldapUser.getAttribute(userEmailAttribute).trim());
		}
		return retVal;
	}
	/**
	 * Search an user by the search attribute and a token.
	 * @param token
	 * @param cgKeyName 
	 * @param service 
	 * @return 
	 */
	public List<LdapUser> getConditionFriendlyLdapUsersFromToken(final String token, 
			final String cgKeyName, final String service) {
		if (logger.isDebugEnabled()) {
			final StringBuilder info = new StringBuilder(50);
			info.append("getConditionFriendlyLdapUsersFromToken : ");
			info.append(token);
			final String message = info.toString();
			logger.debug(message);
		}
		final String[] tokenList = token.split("\\p{Blank}");

		final AndFilter filter = new AndFilter();

		//add the general condition, service and pager filter
		filter.and(new WhitespaceWildcardsFilter(userPagerAttribute, " "));
		filter.and(new EqualsFilter(userTermsOfUseAttribute, cgKeyName));
		if (service != null) {
			filter.and(new EqualsFilter(userTermsOfUseAttribute, service));
		}

		for (String tok : tokenList) {
			if (tok.length() > 0) {		
				filter.and(new WhitespaceWildcardsFilter(searchAttribute, tok));
			}
		}

		final String filterAsStr = filter.encode();
		if (logger.isDebugEnabled()) {
			final StringBuilder infofltr = new StringBuilder(50);
			infofltr.append("LDAP filter applied : ");
			infofltr.append(filterAsStr);
			final String messageftr = infofltr.toString();
			logger.debug(messageftr);
		}
		final List<LdapUser> retVal = getLdapUsersFromFilter(filterAsStr);
		return retVal;
	}
	
	/**
	 * Mutator
	 */
	
	
	/**
	 * Set the ldapTemplate.
	 * @param ldapTemplate
	 */
	public void setLdapTemplate(final LdapTemplate ldapTemplate) {
		super.setLdapTemplate(ldapTemplate);
		this.ldapTemplate = ldapTemplate;		
	}
	
	/**
	 * Standard setter used by Spring.
	 * @param ldapEmailAttribute
	 */
	public void setUserEmailAttribute(final String userEmailAttribute) {
		this.userEmailAttribute = userEmailAttribute;
	}
	
	/**
	 * Standard setter used by Spring.
	 * @param attributes
	 */
	public void setUserAttributesAsString(final String attributes) {
		final List<String> list = new LinkedList<String>();
		for (String attribute : attributes.split(",")) {
			if (StringUtils.hasText(attribute)) {
				if (!list.contains(attribute)) {
					list.add(attribute);
				}
			}
		}
		setUserAttributes(list);
	}
	
	/**
	 * Standard setter used by spring.
	 */
	public void setUserIdAttribute(final String idAttribute) {
		super.setUserIdAttribute(idAttribute);
		this.userIdAttribute = idAttribute;
	}
	
	/**
	 * Standard setter used by spring.
	 * @param objectClass
	 */
	public void setUserObjectClass(final String objectClass) {
		super.setUserObjectClass(objectClass);
		this.userObjectClass = objectClass;
	}
	
	/**
	 * Standard setter used by spring.
	 * @param userPagerAttribute
	 */
	public void setUserPagerAttribute(final String userPagerAttribute) {
		this.userPagerAttribute = userPagerAttribute;
	}
	
	/**
	 * Standard setter used by spring.
	 * @param userTermsOfUseAttribute
	 */
	public void setUserTermsOfUseAttribute(final String userTermsOfUseAttribute) {
		this.userTermsOfUseAttribute = userTermsOfUseAttribute;
	}
	
	/**
	 * Standard setter used by spring.
	 * @param userDisplayName
	 */
	public void setUserDisplayName(final String userDisplayName) {
		this.userDisplayName = userDisplayName;
	}
	
	/**
	 * Standard setter used by Spring.
	 * @param attributes
	 */
	public void setGroupAttributesAsString(final String attributes) {
		final List<String> list = new LinkedList<String>();
		for (String attribute : attributes.split(",")) {
			if (StringUtils.hasText(attribute)) {
				if (!list.contains(attribute)) {
					list.add(attribute);
				}
			}
		}
		setGroupAttributes(list);
	}
	
	/**
	 * Standard setter used by spring.
	 * @param firstNameAttribute
	 */
	public void setFirstNameAttribute(final String firstNameAttribute) {
		this.firstNameAttribute = firstNameAttribute;
	}


	/**
	 * Standard setter used by spring.
	 * @param lastNameAttribute
	 */
	public void setLastNameAttribute(final String lastNameAttribute) {
		this.lastNameAttribute = lastNameAttribute;
	}
	
	
	/**
	 * Standard setter used by Spring.
	 * @param searchAttribute
	 */
	public void setUserSearchAttribute(final String searchAttribute) {
		super.setUserSearchAttribute(searchAttribute);
		this.searchAttribute = searchAttribute;
	}


}