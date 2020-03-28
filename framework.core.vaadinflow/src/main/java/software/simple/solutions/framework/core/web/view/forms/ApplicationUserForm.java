package software.simple.solutions.framework.core.web.view.forms;

import java.io.ByteArrayInputStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.appreciated.card.Card;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasValue.ValueChangeEvent;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

import software.simple.solutions.framework.core.constants.FileReference;
import software.simple.solutions.framework.core.constants.ReferenceKey;
import software.simple.solutions.framework.core.entities.ApplicationUser;
import software.simple.solutions.framework.core.entities.Configuration;
import software.simple.solutions.framework.core.entities.EntityFile;
import software.simple.solutions.framework.core.entities.Person;
import software.simple.solutions.framework.core.exceptions.FrameworkException;
import software.simple.solutions.framework.core.properties.ApplicationUserProperty;
import software.simple.solutions.framework.core.properties.ConfigurationProperty;
import software.simple.solutions.framework.core.properties.GenderProperty;
import software.simple.solutions.framework.core.properties.PersonProperty;
import software.simple.solutions.framework.core.properties.SystemProperty;
import software.simple.solutions.framework.core.service.IFileService;
import software.simple.solutions.framework.core.service.facade.ApplicationUserServiceFacade;
import software.simple.solutions.framework.core.service.facade.ConfigurationServiceFacade;
import software.simple.solutions.framework.core.service.facade.PersonInformationServiceFacade;
import software.simple.solutions.framework.core.util.ContextProvider;
import software.simple.solutions.framework.core.util.PropertyResolver;
import software.simple.solutions.framework.core.valueobjects.ApplicationUserVO;
import software.simple.solutions.framework.core.web.FormView;
import software.simple.solutions.framework.core.web.components.CButton;
import software.simple.solutions.framework.core.web.components.CCheckBox;
import software.simple.solutions.framework.core.web.components.CFormLayout;
import software.simple.solutions.framework.core.web.components.CPasswordField;
import software.simple.solutions.framework.core.web.components.CPopupDateField;
import software.simple.solutions.framework.core.web.components.CTextField;
import software.simple.solutions.framework.core.web.components.Panel;
import software.simple.solutions.framework.core.web.flow.MainView;
import software.simple.solutions.framework.core.web.lookup.PersonLookUpField;
import software.simple.solutions.framework.core.web.routing.Routes;

//@Route(value = Routes.APPLICATION_USER_EDIT, layout = MainView.class)
public class ApplicationUserForm extends FormView {

	private static final long serialVersionUID = 6109727427163734676L;

	private static final Logger logger = LogManager.getLogger(ApplicationUserForm.class);

	private CFormLayout formGrid;
	private Panel userInfoCard;
	private CFormLayout userInfoLayout;
	private Panel personInfoCard;
	private PersonLookUpField personLookUpFld;
	private CTextField usernameFld;
	private CCheckBox activeFld;
	private CCheckBox forceChangePasswordFld;
	private CCheckBox changePasswordFld;
	private CButton managePasswordBtn;
	private CTextField emailFld;

	private Panel passwordCard;
	private CFormLayout passwordLayout;
	private CPasswordField passwordFld;
	private CPasswordField confirmPasswordFld;

	private CTextField lastNameFld;
	private CTextField firstNameFld;
	private CTextField middleNameFld;
	private CPopupDateField dateOfBirthFld;
	private CTextField genderFld;
	private CCheckBox isPersonActiveFld;

	private Card ldapCard;
	private CFormLayout ldapLayout;
	private CCheckBox useLdapFld;

	private ApplicationUser applicationUser;
	private Person person;

	public ApplicationUserForm() {
		super();
	}

	@Override
	public void executeBuild() throws FrameworkException {

		HorizontalLayout mainLayout = new HorizontalLayout();
		mainLayout.setWidthFull();
		add(mainLayout);

		VerticalLayout navigationLayout = new VerticalLayout();
		navigationLayout.setWidth("-1px");
		mainLayout.add(navigationLayout);

		Image profileImageFld = new Image("img/profile-pic-300px.jpg", "profile-image");
		profileImageFld.setWidth("150px");
		navigationLayout.add(profileImageFld);

		// Tabs tabs = new Tabs();
		// tabs.setOrientation(Orientation.VERTICAL);
		// navigationLayout.add(tabs);
		// Tab personalInformationTab = new
		// Tab(PropertyResolver.getPropertyValueByLocale(PersonProperty.PERSONAL_INFORMATION_TAB,
		// UI.getCurrent().getLocale()));
		// tabs.add(personalInformationTab);
		// Tab contactInformationTab = new
		// Tab(PropertyResolver.getPropertyValueByLocale(PersonProperty.CONTACT_INFORMATION_TAB,
		// UI.getCurrent().getLocale()));
		// tabs.add(contactInformationTab);
		// Tab emergencyContactInformationTab = new
		// Tab(PropertyResolver.getPropertyValueByLocale(PersonProperty.EMERGENCY_CONTACT_INFORMATION_TAB,
		// UI.getCurrent().getLocale()));
		// tabs.add(emergencyContactInformationTab);

		VerticalLayout userMainLayout = new VerticalLayout();
		userMainLayout.setWidthFull();
		mainLayout.add(userMainLayout);
		mainLayout.expand(userMainLayout);

		HorizontalLayout horizontalLayout = new HorizontalLayout();
		horizontalLayout.setWidthFull();
		userMainLayout.add(horizontalLayout);

		userInfoCard = createUserInfoLayout();
		// userInfoLayout.setCaption(PropertyResolver.getPropertyValueByLocale(
		// ApplicationUserProperty.APPLICATION_USER_INFO,
		// UI.getCurrent().getLocale()));
		horizontalLayout.add(userInfoCard);

		passwordCard = createPasswordLayout();
		// passwordLayout.setCaption(PropertyResolver.getPropertyValueByLocale(ApplicationUserProperty.PASSWORD_LAYOUT,
		// UI.getCurrent().getLocale()));
		horizontalLayout.add(passwordCard);
		horizontalLayout.setFlexGrow(1, userInfoCard, passwordCard);

		ldapCard = createLdapLayout();
		// ldapLayout.addStyleName(ValoTheme.LAYOUT_CARD);
		// ldapLayout.setCaption(PropertyResolver.getPropertyValueByLocale(ApplicationUserProperty.LDAP_LAYOUT,
		// UI.getCurrent().getLocale()));
		userMainLayout.add(ldapCard);
		ldapCard.setVisible(false);

		Configuration useLdapConfig = ConfigurationServiceFacade.get(UI.getCurrent())
				.getByCode(ConfigurationProperty.LDAP_CONFIGURATION_USE_LDAP);
		if (useLdapConfig != null && useLdapConfig.getBoolean()) {
			ldapLayout.setVisible(true);
		}

		personInfoCard = createPersonLayout();
		// personInfoLayout.setCaption(
		// PropertyResolver.getPropertyValueByLocale(PersonProperty.PERSON_INFO,
		// UI.getCurrent().getLocale()));
		userMainLayout.add(personInfoCard);

		// createPersonLookUpListener();
	}

	private Panel createUserInfoLayout() {
		userInfoCard = new Panel();
		userInfoCard.setHeaderKey(ApplicationUserProperty.APPLICATION_USER_INFO);
		userInfoLayout = new CFormLayout();
		userInfoLayout.setMaxWidth("400px");
		userInfoCard.add(userInfoLayout);

		personLookUpFld = userInfoLayout.add(PersonLookUpField.class, PersonProperty.PERSON);
		// userInfoLayout.setComponentAlignment(personLookUpFld,
		// Alignment.BOTTOM_LEFT);

		// usernameFld
		usernameFld = userInfoLayout.add(CTextField.class, ApplicationUserProperty.USERNAME);
		// usernameFld.setWidth("250px");

		// emailFld
		emailFld = userInfoLayout.add(CTextField.class, ApplicationUserProperty.EMAIL);
		// emailFld.setWidth("250px");

		// isActiveChkBox
		activeFld = userInfoLayout.add(CCheckBox.class, ApplicationUserProperty.ACTIVE);

		return userInfoCard;
	}

	private Panel createPasswordLayout() {
		passwordCard = new Panel();
		passwordCard.setHeaderKey(ApplicationUserProperty.PASSWORD_LAYOUT);

		// passwordCard.setMaxWidth("400px");
		passwordLayout = new CFormLayout();
		passwordLayout.setMaxWidth("400px");
		passwordCard.add(passwordLayout);

		forceChangePasswordFld = passwordLayout.add(CCheckBox.class, ApplicationUserProperty.FORCE_CHANGE_PASSWORD);

		changePasswordFld = passwordLayout.add(CCheckBox.class, SystemProperty.SYSTEM_RESET_PASSWORD);

		passwordFld = passwordLayout.add(CPasswordField.class, ApplicationUserProperty.PASSWORD);

		confirmPasswordFld = passwordLayout.add(CPasswordField.class, ApplicationUserProperty.PASSWORD_CONFIRM);

		// managePasswordBtn
		managePasswordBtn = new CButton();
		passwordLayout.add(managePasswordBtn);
		managePasswordBtn.setText(PropertyResolver.getPropertyValueByLocale(SystemProperty.SYSTEM_RESET_PASSWORD,
				UI.getCurrent().getLocale()));

		return passwordCard;
	}

	private Card createLdapLayout() {
		ldapCard = new Card();
		ldapCard.getContent().setPadding(true);
		ldapLayout = new CFormLayout();
		ldapCard.add(ldapLayout);

		useLdapFld = ldapLayout.add(CCheckBox.class, ApplicationUserProperty.USE_LDAP);

		return ldapCard;
	}

	private Panel createPersonLayout() {
		personInfoCard = new Panel();
		personInfoCard.setHeaderKey(PersonProperty.PERSON_INFO);
		personInfoCard.setWidthFull();

		formGrid = new CFormLayout();
		formGrid.setMaxWidth("400px");
		personInfoCard.add(formGrid);

		firstNameFld = formGrid.add(CTextField.class, PersonProperty.FIRST_NAME);
		firstNameFld.setReadOnly(true);

		middleNameFld = formGrid.add(CTextField.class, PersonProperty.MIDDLE_NAME);
		middleNameFld.setReadOnly(true);

		lastNameFld = formGrid.add(CTextField.class, PersonProperty.LAST_NAME);
		lastNameFld.setReadOnly(true);

		dateOfBirthFld = formGrid.add(CPopupDateField.class, PersonProperty.DATE_OF_BIRTH);
		dateOfBirthFld.setReadOnly(true);

		genderFld = formGrid.add(CTextField.class, GenderProperty.GENDER);
		genderFld.setReadOnly(true);

		isPersonActiveFld = formGrid.add(CCheckBox.class, PersonProperty.ACTIVE);
		isPersonActiveFld.setReadOnly(true);

		return personInfoCard;
	}

	private void createPersonLookUpListener() {
		personInfoCard.setVisible(false);
		personLookUpFld.addValueChangeListener(new ValueChangeListener<ValueChangeEvent<Person>>() {

			private static final long serialVersionUID = 3798217052004909083L;

			@Override
			public void valueChanged(ValueChangeEvent<Person> event) {
				person = (Person) event.getValue();
				personInfoCard.setVisible(false);
				if (person != null) {
					personInfoCard.setVisible(true);
					try {
						setPersonInfo();
						String email = PersonInformationServiceFacade.get(UI.getCurrent()).getEmail(person.getId());
						emailFld.setValue(email);
					} catch (FrameworkException e) {
						logger.error(e.getMessage(), e);
						// new MessageWindowHandler(e);
					}
				}
			}
		});
	}

	@Override
	public void handleNewForm() throws FrameworkException {
		activeFld.setValue(true);
		forceChangePasswordFld.setValue(true);
		passwordFld.setVisible(true);
		confirmPasswordFld.setVisible(true);
		managePasswordBtn.setVisible(false);

		useLdapFld.addClickListener(new ComponentEventListener<ClickEvent<Checkbox>>() {

			private static final long serialVersionUID = 5305893476437089439L;

			@Override
			public void onComponentEvent(ClickEvent<Checkbox> event) {
				Boolean selected = event.getSource().getValue();
				if (selected) {
					passwordLayout.setVisible(false);
				} else {
					passwordLayout.setVisible(true);
				}
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public ApplicationUser setFormValues(Object entity) throws FrameworkException {
		applicationUser = (ApplicationUser) entity;
		person = applicationUser.getPerson();
		addToReferenceKey(ReferenceKey.PERSON, person);

		usernameFld.setReadOnly(true);
		usernameFld.setValue(applicationUser.getUsername());
		activeFld.setValue(applicationUser.getActive());
		forceChangePasswordFld.setValue(applicationUser.getResetPassword());
		personLookUpFld.setValue(person);

		passwordFld.setVisible(false);
		confirmPasswordFld.setVisible(false);

		managePasswordBtn.addClickListener(new ComponentEventListener<ClickEvent<Button>>() {

			private static final long serialVersionUID = 5305893476437089439L;

			@Override
			public void onComponentEvent(ClickEvent<Button> event) {
				String email = emailFld.getValue();
				try {
					boolean smtpEnabled = ConfigurationServiceFacade.get(UI.getCurrent()).isSmtpEnabled();
					if (!smtpEnabled || StringUtils.isBlank(email)) {
						// new ResetPasswordLayout(applicationUser.getId());
					} else {
						resetPasswordAndSendMail();
					}
				} catch (FrameworkException e) {
					logger.error(e.getMessage(), e);
					// new MessageWindowHandler(e);
				}
			}
		});

		useLdapFld.addClickListener(new ComponentEventListener<ClickEvent<Checkbox>>() {

			private static final long serialVersionUID = 8889434654311443194L;

			@Override
			public void onComponentEvent(ClickEvent<Checkbox> event) {
				Boolean selected = event.getSource().getValue();
				if (selected) {
					passwordLayout.setVisible(false);
				} else {
					passwordLayout.setVisible(true);
				}
			}
		});
		useLdapFld.setValue(applicationUser.getUseLdap());

		return applicationUser;
	}

	protected void resetPasswordAndSendMail() throws FrameworkException {
		applicationUser = ApplicationUserServiceFacade.get(UI.getCurrent()).resetUserPassword(applicationUser.getId(),
				sessionHolder.getApplicationUser().getId());
		setFormValues(applicationUser);
		// NotificationWindow.notificationNormalWindow(SystemProperty.UPDATE_SUCCESSFULL);
	}

	private void setPersonInfo() throws FrameworkException {
		firstNameFld.setValue(person.getFirstName());
		middleNameFld.setValue(person.getMiddleName());
		lastNameFld.setValue(person.getLastName());
		dateOfBirthFld.setValue(person.getDateOfBirth());
		genderFld.setValue(person.getGender() == null ? null
				: PropertyResolver.getPropertyValueByLocale(person.getGender().getKey(), UI.getCurrent().getLocale()));
		isPersonActiveFld.setValue(person.getActive());

		IFileService fileService = ContextProvider.getBean(IFileService.class);
		EntityFile entityFile = fileService.findFileByEntityAndType(person.getId().toString(), ReferenceKey.PERSON,
				FileReference.USER_PROFILE_IMAGE);
		if (entityFile != null) {
			StreamResource resource = new StreamResource("profile-image.jpg",
					() -> new ByteArrayInputStream(entityFile.getFileObject()));
			// imageField.setSrc(resource);
		}
	}

	@Override
	public Object getFormValues() throws FrameworkException {
		ApplicationUserVO vo = new ApplicationUserVO();
		vo.setActive(activeFld.getValue());
		vo.setPersonId(personLookUpFld.getItemId());
		vo.setResetPassword(forceChangePasswordFld.getValue());
		vo.setUsername(usernameFld.getValue());
		vo.setId(applicationUser == null ? null : applicationUser.getId());
		vo.setEmail(emailFld.getValue());
		vo.setUseLdap(useLdapFld.getValue());
		if (applicationUser == null) {
			vo.setPassword(passwordFld.getValue());
			vo.setPasswordConfirm(confirmPasswordFld.getValue());
		}
		return vo;
	}

}