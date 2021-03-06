package org.esupportail.smsu.exceptions;

import org.esupportail.commons.services.i18n.I18nService;

public abstract class CreateMessageException extends Exception {

	private static final long serialVersionUID = 6792087453400066168L;
	
	abstract public String toI18nString(I18nService i18nService);

	static public class Wrapper extends CreateMessageException {

		private static final long serialVersionUID = 6792087453400066168L;
	
		private String errorMsg;
		public Exception previousException;

		public Wrapper(String errorMsg, Exception e) {
			this.errorMsg = errorMsg;
			this.previousException = e;
		}

		public String toString() {
			return errorMsg == null ? this.getClass().getName() : errorMsg;
		}

		public String toI18nString(I18nService i18nService) {
			return toString();
		}
	}

	static public abstract class WebService extends CreateMessageException.Wrapper {
		private static final long serialVersionUID = 1L;

		abstract public String i18nKey();
		
		public WebService(Exception e) { super(null, e); }

		public String toI18nString(I18nService i18nService) {
			return i18nService.getString(i18nKey(),
						     i18nService.getDefaultLocale());
		}
	}
	
	@SuppressWarnings("serial")
	static public class WebServiceUnknownApplication extends CreateMessageException.WebService {
		public WebServiceUnknownApplication(Exception e) { super(e); }
		public String i18nKey() { return "WS.ERROR.APPLICATION"; }
	}
	@SuppressWarnings("serial")
	static public class WebServiceInsufficientQuota extends CreateMessageException.WebService {
		public WebServiceInsufficientQuota(Exception e) { super(e); }
		public String i18nKey() { return "WS.ERROR.QUOTA"; }
	}
	@SuppressWarnings("serial")
	static public class BackOfficeUnreachable extends CreateMessageException.WebService {
		public BackOfficeUnreachable(Exception e) { super(e); }
		public String i18nKey() { return "WS.ERROR.MESSAGE"; }
	}

	static public class GroupQuotaException extends CreateMessageException {

		private static final long serialVersionUID = -6132084495675709441L;

		private String groupName;

		public GroupQuotaException(String groupName) {
			this.groupName = groupName;
		}

		public String toString() {
			return "CreateMessageException.GroupQuotaException(" + groupName + ")";
		}

		public String toI18nString(I18nService i18nService) {
			return i18nService.getString("SENDSMS.MESSAGE.SENDERGROUPQUOTAERROR", 
						     i18nService.getDefaultLocale(), groupName);
		}

	}

	static public class UnknownCustomizedTag extends CreateMessageException {

		private static final long serialVersionUID = 6792087453400066168L;
	
		final private String tag;
		
		public UnknownCustomizedTag(final String tag) {
			this.tag = tag;
		}
		
		public String getTag() {
			return tag;
		}
	
		public String toString() {
			return "unknown tag <" + tag + ">";
		}

		public String toI18nString(I18nService i18nService) {
			return i18nService.getString("SENDSMS.MESSAGE.UNKNOWNCUSTOMIZEDTAG",
						     i18nService.getDefaultLocale(), tag);
		}

	}

	static public class CustomizedTagValueNotFound extends CreateMessageException {

		private static final long serialVersionUID = 6792087453400066168L;
	
		final private String tag;
		
		public CustomizedTagValueNotFound(final String tag) {
			this.tag = tag;
		}
		
		public String getTag() {
			return tag;
		}
	
		public String toString() {
			return "tag <" + tag + "> has no value";
		}

		public String toI18nString(I18nService i18nService) {
			return i18nService.getString("SENDSMS.MESSAGE.CUSTOMIZEDTAGVALUENOTFOUND",
						     i18nService.getDefaultLocale(), tag);
		}

	}

	static public class EmptyGroup extends CreateMessageException {
		
		private static final long serialVersionUID = 8476880753507079446L;

		final private String groupName;
		
		public EmptyGroup(final String groupName) {
			this.groupName = groupName;
		}
	
		public String toString() {
			return "group " + groupName + " is empty";
		}

		public String toI18nString(I18nService i18nService) {
			return i18nService.getString("SENDSMS.MESSAGE.EMPTYGROUP",
						     i18nService.getDefaultLocale(), groupName);
		}

	}

}
