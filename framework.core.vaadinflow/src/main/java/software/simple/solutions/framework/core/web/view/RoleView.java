package software.simple.solutions.framework.core.web.view;

import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.router.Route;

import software.simple.solutions.framework.core.components.filter.CStringIntervalLayout;
import software.simple.solutions.framework.core.components.select.ActiveSelect;
import software.simple.solutions.framework.core.constants.ReferenceKey;
import software.simple.solutions.framework.core.entities.Role;
import software.simple.solutions.framework.core.exceptions.FrameworkException;
import software.simple.solutions.framework.core.properties.RoleProperty;
import software.simple.solutions.framework.core.service.facade.RoleServiceFacade;
import software.simple.solutions.framework.core.valueobjects.RoleVO;
import software.simple.solutions.framework.core.web.BasicTemplate;
import software.simple.solutions.framework.core.web.FilterView;
import software.simple.solutions.framework.core.web.flow.MainView;
import software.simple.solutions.framework.core.web.routing.Routes;
import software.simple.solutions.framework.core.web.view.forms.RoleForm;

//@Route(value = Routes.ROLE, layout = MainView.class)
public class RoleView extends BasicTemplate<Role> {

	private static final long serialVersionUID = 6503015064562511801L;

	public RoleView() {
		setEntityClass(Role.class);
		setServiceClass(RoleServiceFacade.class);
		setFilterClass(Filter.class);
		setFormClass(RoleForm.class);
		setParentReferenceKey(ReferenceKey.ROLE);
		setEditRoute(Routes.ROLE_EDIT);
	}

	@Override
	public void setUpCustomColumns() {
		addContainerProperty(Role::getCode, RoleProperty.CODE);
		addContainerProperty(Role::getName, RoleProperty.NAME);
		addContainerProperty(Role::getDescription, RoleProperty.DESCRIPTION);
	}

	/**
	 * Filter implementation
	 * 
	 * @author yusuf
	 *
	 */
	public class Filter extends FilterView {

		private static final long serialVersionUID = 117780868467918033L;

		private CStringIntervalLayout codeFld;
		private CStringIntervalLayout nameFld;
		private CStringIntervalLayout descriptionFld;
		private ActiveSelect activeFld;

		@Override
		public void executeBuild() throws FrameworkException {
			codeFld = addField(CStringIntervalLayout.class, RoleProperty.CODE, 0, 0);
			nameFld = addField(CStringIntervalLayout.class, RoleProperty.NAME, 0, 1);
			descriptionFld = addField(CStringIntervalLayout.class, RoleProperty.DESCRIPTION, 1, 0);
			activeFld = addField(ActiveSelect.class, RoleProperty.ACTIVE, 1, 1);
		}

		@Override
		public Object getCriteria() {
			RoleVO vo = new RoleVO();
			vo.setActive(activeFld.getItemId());
			vo.setCodeInterval(codeFld.getValue());
			vo.setNameInterval(nameFld.getValue());
			vo.setDescriptionInterval(descriptionFld.getValue());
			return vo;
		}
	}

	@Override
	public Icon getIcon() {
		// TODO Auto-generated method stub
		return null;
	}

}
