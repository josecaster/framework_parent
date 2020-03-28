package software.simple.solutions.framework.core.web.view.forms;

import com.vaadin.flow.router.Route;

import software.simple.solutions.framework.core.components.select.RoleCategorySelect;
import software.simple.solutions.framework.core.entities.Role;
import software.simple.solutions.framework.core.exceptions.FrameworkException;
import software.simple.solutions.framework.core.properties.RoleCategoryProperty;
import software.simple.solutions.framework.core.properties.RoleProperty;
import software.simple.solutions.framework.core.valueobjects.RoleVO;
import software.simple.solutions.framework.core.web.FormView;
import software.simple.solutions.framework.core.web.components.CCheckBox;
import software.simple.solutions.framework.core.web.components.CFormLayout;
import software.simple.solutions.framework.core.web.components.CTextArea;
import software.simple.solutions.framework.core.web.components.CTextField;
import software.simple.solutions.framework.core.web.components.Panel;
import software.simple.solutions.framework.core.web.flow.MainView;
import software.simple.solutions.framework.core.web.routing.Routes;

//@Route(value = Routes.ROLE_EDIT, layout = MainView.class)
public class RoleForm extends FormView {

	private static final long serialVersionUID = 6109727427163734676L;

	private CFormLayout formGrid;
	private CTextField codeFld;
	private CTextField nameFld;
	private CTextArea descriptionFld;
	private CCheckBox activeFld;
	private RoleCategorySelect roleCategoryFld;

	private Role role;

	public RoleForm() {
		super();
	}

	@Override
	public void executeBuild() {
		createFormGrid();
	}

	private CFormLayout createFormGrid() {
		Panel formCard = new Panel();
		add(formCard);
		formGrid = new CFormLayout();
		formCard.add(formGrid);
		formCard.setMaxWidth("400px");

		codeFld = formGrid.add(CTextField.class, RoleProperty.CODE);
		codeFld.setMaxWidth("400px");

		nameFld = formGrid.add(CTextField.class, RoleProperty.NAME);
		nameFld.setMaxWidth("400px");

		roleCategoryFld = formGrid.add(RoleCategorySelect.class, RoleCategoryProperty.ROLE_CATEGORY);
		roleCategoryFld.setMaxWidth("400px");

		descriptionFld = formGrid.add(CTextArea.class, RoleProperty.DESCRIPTION);
		descriptionFld.setMaxWidth("400px");

		activeFld = formGrid.add(CCheckBox.class, RoleProperty.ACTIVE);

		return formGrid;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Role setFormValues(Object entity) throws FrameworkException {
		role = (Role) entity;
		activeFld.setValue(role.getActive());
		codeFld.setValue(role.getCode());
		nameFld.setValue(role.getName());
		descriptionFld.setValue(role.getDescription());
		roleCategoryFld.setValue(role.getRoleCategory() == null ? null : role.getRoleCategory().getId());

		return role;
	}

	@Override
	public void handleNewForm() throws FrameworkException {
		nameFld.setRequired();
		codeFld.setRequired();
		activeFld.setValue(true);
	}

	@Override
	public Object getFormValues() throws FrameworkException {
		RoleVO vo = new RoleVO();

		vo.setId(role == null ? null : role.getId());

		vo.setActive(activeFld.getValue());
		vo.setCode(codeFld.getValue());
		vo.setName(nameFld.getValue());
		vo.setDescription(descriptionFld.getValue());
		vo.setRoleCategoryId(roleCategoryFld.getLongValue());
		return vo;
	}

}
