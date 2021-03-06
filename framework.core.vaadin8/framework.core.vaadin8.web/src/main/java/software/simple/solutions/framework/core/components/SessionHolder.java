package software.simple.solutions.framework.core.components;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import com.google.common.eventbus.EventBus;
import com.vaadin.ui.Component;

import software.simple.solutions.framework.core.entities.ApplicationUser;
import software.simple.solutions.framework.core.entities.Role;
import software.simple.solutions.framework.core.web.SimpleSolutionsMenuItem;

public class SessionHolder implements Serializable {

	private static final long serialVersionUID = -5932213039342157837L;

	private ApplicationUser applicationUser;
	private EventBus eventBus;
	private String logoutPageLocation;
	private ViewManager viewManager;
	private List<Role> roles;
	private Role selectedRole;
	private List<SimpleSolutionsMenuItem> authorizedViews;
	private SimpleSolutionsMenuItem simpleSolutionsMenuItem;
	private Locale locale;
	private ConcurrentMap<String, Object> referenceKeys;
	private String password;
	private Map<String, Component> generatedMenus;

	public SessionHolder() {
		generatedMenus = new HashMap<String, Component>();
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

	public ViewManager getViewManager() {
		return viewManager;
	}

	public void setViewManager(ViewManager viewManager) {
		this.viewManager = viewManager;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public void setAuthorizedViews(List<SimpleSolutionsMenuItem> authorizedViews) {
		this.authorizedViews = authorizedViews;
	}

	public List<SimpleSolutionsMenuItem> getAuthorizedViews() {
		return authorizedViews;
	}

	public SimpleSolutionsMenuItem getSimpleSolutionsMenuItem() {
		return simpleSolutionsMenuItem;
	}

	public void setSimpleSolutionsMenuItem(SimpleSolutionsMenuItem simpleSolutionsMenuItem) {
		this.simpleSolutionsMenuItem = simpleSolutionsMenuItem;
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

	public Map<String, Component> getGeneratedMenus() {
		return generatedMenus;
	}

	public void setGeneratedMenus(Map<String, Component> generatedMenus) {
		this.generatedMenus = generatedMenus;
	}

	public void addGeneratedMenu(String uuid, Component component) {
		generatedMenus.put(uuid, component);
	}

	public void removeGeneratedMenu(String uuid) {
		generatedMenus.remove(uuid);
	}

	public boolean uuidExists(String uuid) {
		return generatedMenus.containsKey(uuid);
	}

	public Component getGeneratedMenu(String uuid) {
		return generatedMenus.get(uuid);
	}

}
