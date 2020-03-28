package software.simple.solutions.framework.core.web.view;

import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.Route;

import software.simple.solutions.framework.core.components.select.ActiveSelect;
import software.simple.solutions.framework.core.constants.ReferenceKey;
import software.simple.solutions.framework.core.entities.ApplicationUser;
import software.simple.solutions.framework.core.entities.Role;
import software.simple.solutions.framework.core.entities.UserRole;
import software.simple.solutions.framework.core.exceptions.FrameworkException;
import software.simple.solutions.framework.core.properties.UserRoleProperty;
import software.simple.solutions.framework.core.service.facade.UserRoleServiceFacade;
import software.simple.solutions.framework.core.valueobjects.UserRoleVO;
import software.simple.solutions.framework.core.web.BasicTemplate;
import software.simple.solutions.framework.core.web.FilterView;
import software.simple.solutions.framework.core.web.flow.MainView;
import software.simple.solutions.framework.core.web.lookup.ApplicationUserLookUpField;
import software.simple.solutions.framework.core.web.lookup.RoleLookUpField;
import software.simple.solutions.framework.core.web.routing.Routes;
import software.simple.solutions.framework.core.web.view.forms.UserRoleForm;

//@Route(value = Routes.USER_ROLE, layout = MainView.class)
public class UserRoleView extends BasicTemplate<UserRole> {

	private static final long serialVersionUID = 6503015064562511801L;

	public UserRoleView() {
		setEntityClass(UserRole.class);
		setServiceClass(UserRoleServiceFacade.class);
		setFilterClass(Filter.class);
		setFormClass(UserRoleForm.class);
		setParentReferenceKey(ReferenceKey.ROLE);
		setEditRoute(Routes.ROLE_EDIT);
	}

	@Override
	public void setUpCustomColumns() {
		addContainerProperty(new ValueProvider<UserRole, String>() {

			@Override
			public String apply(UserRole source) {
				return source.getApplicationUser() == null ? null : source.getApplicationUser().getCaption();
			}
		}, UserRoleProperty.USER);
		addContainerProperty(new ValueProvider<UserRole, String>() {

			@Override
			public String apply(UserRole source) {
				return source.getRole() == null ? null : source.getRole().getCaption();
			}
		}, UserRoleProperty.ROLE);
	}

	/**
	 * Filter implementation
	 * 
	 * @author yusuf
	 *
	 */
	public class Filter extends FilterView {

		private static final long serialVersionUID = 117780868467918033L;

		private ApplicationUserLookUpField applicationUserFld;
		private RoleLookUpField roleFld;
		private ActiveSelect activeFld;

		@Override
		public void executeBuild() throws FrameworkException {
			applicationUserFld = addField(ApplicationUserLookUpField.class, UserRoleProperty.USER, 0, 0);
			roleFld = addField(RoleLookUpField.class, UserRoleProperty.ROLE, 0, 1);
			activeFld = addField(ActiveSelect.class, UserRoleProperty.ACTIVE, 1, 0);

			Object parentEntity = getParentEntity();
			if (parentEntity instanceof ApplicationUser) {
				ApplicationUser applicationUser = (ApplicationUser) parentEntity;
				applicationUserFld.setValue(applicationUser);
				applicationUserFld.setReadOnly(true);
			}
			if (parentEntity instanceof Role) {
				Role role = (Role) parentEntity;
				roleFld.setValue(role);
				roleFld.setReadOnly(true);
			}
		}

		@Override
		public Object getCriteria() {
			UserRoleVO vo = new UserRoleVO();
			vo.setActive(activeFld.getItemId());
			vo.setUserId(applicationUserFld.getItemId());
			vo.setRoleId(roleFld.getItemId());
			return vo;
		}

	}

	@Override
	public Icon getIcon() {
		// TODO Auto-generated method stub
		return null;
	}
}
