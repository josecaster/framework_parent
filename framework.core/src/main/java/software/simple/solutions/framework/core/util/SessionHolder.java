package software.simple.solutions.framework.core.util;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.google.common.eventbus.EventBus;

import software.simple.solutions.framework.core.entities.ApplicationUser;
import software.simple.solutions.framework.core.entities.Role;

public class SessionHolder implements Serializable {

	private static final long serialVersionUID = -5932213039342157837L;

	private ApplicationUser applicationUser;
	private EventBus eventBus;
	private String logoutPageLocation;
	private List<Role> roles;
	private Role selectedRole;
	private Locale locale;
	private ConcurrentMap<String, Object> referenceKeys;
	private String password;
	private ConcurrentMap<String, Long> routesMenus;
	private String forwardTo;
	private boolean claimToBeAuthorized = false;

	public SessionHolder() {
		super();
		referenceKeys = new ConcurrentHashMap<String, Object>();
		routesMenus = new ConcurrentHashMap<String, Long>();
	}

	public ApplicationUser getApplicationUser() {
		return applicationUser;
	}

	public void setApplicationUser(ApplicationUser thisUser) {
		this.applicationUser = thisUser;
	}

	public EventBus getEventBus() {
		return eventBus;
	}

	public void setEventBus(EventBus eventBus) {
		this.eventBus = eventBus;
	}

	public String getLogoutPageLocation() {
		return logoutPageLocation;
	}

	public void setLogoutPageLocation(String logoutPageLocation) {
		this.logoutPageLocation = logoutPageLocation;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public Role getSelectedRole() {
		return selectedRole;
	}

	public void setSelectedRole(Role selectedRole) {
		this.selectedRole = selectedRole;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public ConcurrentMap<String, Object> getReferenceKeys() {
		return referenceKeys;
	}

	public Object getReferenceKey(String key) {
		return referenceKeys.getOrDefault(key, null);
	}

	public void setReferenceKeys(ConcurrentMap<String, Object> referenceKeys) {
		this.referenceKeys = referenceKeys;
	}

	public void addReferenceKey(String key, Object value) {
		referenceKeys.put(key, value);
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public ConcurrentMap<String, Long> getRoutesMenus() {
		return routesMenus;
	}

	public void setRoutesMenus(ConcurrentMap<String, Long> routesMenus) {
		this.routesMenus = routesMenus;
	}

	public void addRouteMenu(String path, Long menuId) {
		routesMenus.put(path, menuId);
	}

	public Long getRouteMenu(String path) {
		return routesMenus.get(path);
	}

	public String getForwardTo() {
		return forwardTo;
	}

	public void setForwardTo(String forwardTo) {
		this.forwardTo = forwardTo;
	}

	public boolean isClaimToBeAuthorized() {
		return claimToBeAuthorized;
	}

	public void setClaimToBeAuthorized(boolean claimToBeAuthorized) {
		this.claimToBeAuthorized = claimToBeAuthorized;
	}

}
