package software.simple.solutions.framework.core.web.view;

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
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

import software.simple.solutions.framework.core.components.filter.CStringIntervalLayout;
import software.simple.solutions.framework.core.components.select.ActiveSelect;
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
import software.simple.solutions.framework.core.web.BasicTemplate;
import software.simple.solutions.framework.core.web.FilterView;
import software.simple.solutions.framework.core.web.FormView;
import software.simple.solutions.framework.core.web.components.CButton;
import software.simple.solutions.framework.core.web.components.CCheckBox;
import software.simple.solutions.framework.core.web.components.CFormLayout;
import software.simple.solutions.framework.core.web.components.CPasswordField;
import software.simple.solutions.framework.core.web.components.CPopupDateField;
import software.simple.solutions.framework.core.web.components.CTextField;
import software.simple.solutions.framework.core.web.flow.MainView;
import software.simple.solutions.framework.core.web.lookup.PersonLookUpField;
import software.simple.solutions.framework.core.web.routing.Routes;
import software.simple.solutions.framework.core.web.view.forms.ApplicationUserForm;

//@Route(value = Routes.APPLICATION_USER, layout = MainView.class)
public class ApplicationUserView extends BasicTemplate<ApplicationUser> {

	private static final long serialVersionUID = 6503015064562511801L;

	private static final Logger logger = LogManager.getLogger(ApplicationUserView.class);

	public ApplicationUserView() {
		setEntityClass(ApplicationUser.class);
		setServiceClass(ApplicationUserServiceFacade.class);
		setFilterClass(Filter.class);
		setFormClass(ApplicationUserForm.class);
		setParentReferenceKey(ReferenceKey.APPLICATION_USER);
		setEditRoute(Routes.APPLICATION_USER_EDIT);
	}

	@Override
	public void setUpCustomColumns() {
		addComponentContainerProperty(new ValueProvider<ApplicationUser, Image>() {

			private static final long serialVersionUID = -2238739724212518767L;

			@Override
			public Image apply(ApplicationUser source) {
				Image image = new Image("img/profile-pic-300px.jpg", "profile-image");
				image.setHeight("75px");
				image.setWidth("75px");
				// image.addStyleName("appbar-profile-image");

				Thread thread = new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							Person person = source.getPerson();
							if (person != null) {
								IFileService fileService = ContextProvider.getBean(IFileService.class);
								EntityFile entityFile = fileService.findFileByEntityAndType(person.getId().toString(),
										ReferenceKey.PERSON, FileReference.USER_PROFILE_IMAGE);

								if (entityFile != null) {
									StreamResource resource = new StreamResource("profile-image.jpg",
											() -> new ByteArrayInputStream(entityFile.getFileObject()));
									image.setSrc(resource);
								}
							}
						} catch (FrameworkException e) {
							logger.error(e.getMessage(), e);
						}
					}
				});
				thread.start();

				return image;
			}
		}, ApplicationUserProperty.IMAGE);
		addContainerProperty(ApplicationUser::getUsername, ApplicationUserProperty.USERNAME);
		addContainerProperty(ApplicationUser::getPerson, PersonProperty.PERSON);

		setGridRowHeight(75);
	}

	/**
	 * Filter implementation
	 * 
	 * @author yusuf
	 *
	 */
	public class Filter extends FilterView {

		private static final long serialVersionUID = 117780868467918033L;

		private CStringIntervalLayout userNameFld;
		private CStringIntervalLayout personFirstNameFld;
		private CStringIntervalLayout personLastNameFld;
		private ActiveSelect activeFld;

		@Override
		public void executeBuild() {

			userNameFld = addField(CStringIntervalLayout.class, ApplicationUserProperty.USERNAME, 0, 0);
			activeFld = addField(ActiveSelect.class, ApplicationUserProperty.ACTIVE, 0, 1);
			personFirstNameFld = addField(CStringIntervalLayout.class, PersonProperty.FIRST_NAME, 1, 0);
			personLastNameFld = addField(CStringIntervalLayout.class, PersonProperty.LAST_NAME, 1, 1);
		}

		@Override
		public Object getCriteria() {
			ApplicationUserVO vo = new ApplicationUserVO();
			vo.setUsernameInterval(userNameFld.getValue());
			vo.setActive(activeFld.getItemId());
			vo.setLastNameInterval(personLastNameFld.getValue());
			vo.setFirstNameInterval(personFirstNameFld.getValue());
			return vo;
		}
	}

	public class Form extends FormView {

		private static final long serialVersionUID = 6109727427163734676L;

		private CFormLayout formGrid;
		private Card userInfoCard;
		private CFormLayout userInfoLayout;
		private Card personInfoCard;
		private HorizontalLayout personInfoLayout;
		private PersonLookUpField personLookUpFld;
		private CTextField usernameFld;
		private CCheckBox activeFld;
		private CCheckBox forceChangePasswordFld;
		private CButton managePasswordBtn;
		private CTextField emailFld;

		private Card passwordCard;
		private CFormLayout passwordLayout;
		private CPasswordField passwordFld;
		private CPasswordField confirmPasswordFld;

		private Image imageField;
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

		@Override
		public void executeBuild() throws FrameworkException {

			HorizontalLayout userMainLayout = new HorizontalLayout();
			userMainLayout.setWidth("100%");
			add(userMainLayout);

			userInfoCard = createUserInfoLayout();
			// userInfoLayout.setCaption(PropertyResolver.getPropertyValueByLocale(
			// ApplicationUserProperty.APPLICATION_USER_INFO,
			// UI.getCurrent().getLocale()));
			userMainLayout.add(userInfoCard);

			passwordCard = createPasswordLayout();
			// passwordLayout.setCaption(PropertyResolver.getPropertyValueByLocale(ApplicationUserProperty.PASSWORD_LAYOUT,
			// UI.getCurrent().getLocale()));
			userMainLayout.add(passwordCard);

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

			personInfoLayout = createPersonLayout();
			// personInfoLayout.setCaption(
			// PropertyResolver.getPropertyValueByLocale(PersonProperty.PERSON_INFO,
			// UI.getCurrent().getLocale()));
			add(personInfoLayout);

			createPersonLookUpListener();
		}

		private Card createUserInfoLayout() {
			userInfoCard = new Card();
			userInfoCard.setMaxWidth("400px");
			userInfoCard.getContent().setPadding(true);
			// common part: create layout
			userInfoLayout = new CFormLayout();
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

		private Card createPasswordLayout() {
			passwordCard = new Card();
			passwordCard.setMaxWidth("400px");
			passwordCard.getContent().setPadding(true);
			passwordLayout = new CFormLayout();
			passwordCard.add(passwordLayout);

			forceChangePasswordFld = passwordLayout.add(CCheckBox.class, ApplicationUserProperty.FORCE_CHANGE_PASSWORD);

			passwordFld = passwordLayout.add(CPasswordField.class, ApplicationUserProperty.PASSWORD);
			passwordFld.setVisible(false);

			confirmPasswordFld = passwordLayout.add(CPasswordField.class, ApplicationUserProperty.PASSWORD_CONFIRM);
			confirmPasswordFld.setVisible(false);

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

		private HorizontalLayout createPersonLayout() {
			personInfoLayout = new HorizontalLayout();
			personInfoCard = new Card();
			personInfoCard.getContent().setPadding(true);
			personInfoCard.add(personInfoLayout);

			imageField = new Image();
			personInfoLayout.add(imageField);

			formGrid = new CFormLayout();
			personInfoLayout.add(formGrid);

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

			return personInfoLayout;
		}

		private void createPersonLookUpListener() {
			personInfoLayout.setVisible(false);
			personLookUpFld.addValueChangeListener(new ValueChangeListener<ValueChangeEvent<Person>>() {

				@Override
				public void valueChanged(ValueChangeEvent<Person> event) {
					// TODO Auto-generated method stub

				}
			});
			personLookUpFld.addValueChangeListener(new ValueChangeListener<ValueChangeEvent<Person>>() {

				private static final long serialVersionUID = 3798217052004909083L;

				@Override
				public void valueChanged(ValueChangeEvent<Person> event) {
					person = event.getValue();
					personInfoLayout.setVisible(false);
					if (person != null) {
						personInfoLayout.setVisible(true);
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
			applicationUser = ApplicationUserServiceFacade.get(UI.getCurrent())
					.resetUserPassword(applicationUser.getId(), sessionHolder.getApplicationUser().getId());
			setFormValues(applicationUser);
			// NotificationWindow.notificationNormalWindow(SystemProperty.UPDATE_SUCCESSFULL);
		}

		private void setPersonInfo() throws FrameworkException {
			firstNameFld.setValue(person.getFirstName());
			middleNameFld.setValue(person.getMiddleName());
			lastNameFld.setValue(person.getLastName());
			dateOfBirthFld.setValue(person.getDateOfBirth());
			genderFld.setValue(person.getGender() == null ? null
					: PropertyResolver.getPropertyValueByLocale(person.getGender().getKey(),
							UI.getCurrent().getLocale()));
			isPersonActiveFld.setValue(person.getActive());

			IFileService fileService = ContextProvider.getBean(IFileService.class);
			EntityFile entityFile = fileService.findFileByEntityAndType(person.getId().toString(), ReferenceKey.PERSON,
					FileReference.USER_PROFILE_IMAGE);
			if (entityFile != null) {
				StreamResource resource = new StreamResource("profile-image.jpg",
						() -> new ByteArrayInputStream(entityFile.getFileObject()));
				imageField.setSrc(resource);
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

	@Override
	public Icon getIcon() {
		// TODO Auto-generated method stub
		return null;
	}

	// public class ReadonlyForm extends FormView {
	//
	// private static final long serialVersionUID = 6109727427163734676L;
	//
	// private CGridLayout formGrid;
	// private CGridLayout userInfoLayout;
	// private HorizontalLayout personInfoLayout;
	// private CLabel personLookUpFld;
	// private CLabel usernameFld;
	// private CCheckBox activeFld;
	// private CCheckBox forceChangePasswordFld;
	// private CLabel emailFld;
	//
	// private CGridLayout passwordLayout;
	//
	// private Image imageFld;
	// private CLabel lastNameFld;
	// private CLabel firstNameFld;
	// private CLabel middleNameFld;
	// private CLabel dateOfBirthFld;
	// private CLabel genderFld;
	// private CCheckBox isPersonActiveFld;
	//
	// private CGridLayout ldapLayout;
	// private CCheckBox useLdapFld;
	//
	// private ApplicationUser applicationUser;
	// private Person person;
	//
	// @Override
	// public void executeBuild() throws FrameworkException {
	//
	// HorizontalLayout userMainLayout = new HorizontalLayout();
	// addComponent(userMainLayout);
	//
	// personInfoLayout = createPersonLayout();
	// personInfoLayout.addStyleName(ValoTheme.LAYOUT_CARD);
	// personInfoLayout.setCaption(
	// PropertyResolver.getPropertyValueByLocale(PersonProperty.PERSON_INFO,
	// UI.getCurrent().getLocale()));
	// personInfoLayout.setMargin(true);
	// userMainLayout.addComponent(personInfoLayout);
	//
	// userInfoLayout = createUserInfoLayout();
	// userInfoLayout.addStyleName(ValoTheme.LAYOUT_CARD);
	// userInfoLayout.setCaption(PropertyResolver.getPropertyValueByLocale(
	// ApplicationUserProperty.APPLICATION_USER_INFO,
	// UI.getCurrent().getLocale()));
	// userMainLayout.addComponent(userInfoLayout);
	//
	// passwordLayout = createPasswordLayout();
	// passwordLayout.addStyleName(ValoTheme.LAYOUT_CARD);
	// passwordLayout.setCaption(PropertyResolver.getPropertyValueByLocale(ApplicationUserProperty.PASSWORD_LAYOUT,
	// UI.getCurrent().getLocale()));
	// userMainLayout.addComponent(passwordLayout);
	//
	// ldapLayout = createLdapLayout();
	// ldapLayout.addStyleName(ValoTheme.LAYOUT_CARD);
	// ldapLayout.setCaption(PropertyResolver.getPropertyValueByLocale(ApplicationUserProperty.LDAP_LAYOUT,
	// UI.getCurrent().getLocale()));
	// userMainLayout.addComponent(ldapLayout);
	// ldapLayout.setVisible(false);
	//
	// Configuration useLdapConfig =
	// ConfigurationServiceFacade.get(UI.getCurrent())
	// .getByCode(ConfigurationProperty.LDAP_CONFIGURATION_USE_LDAP);
	// if (useLdapConfig != null && useLdapConfig.getBoolean()) {
	// ldapLayout.setVisible(true);
	// }
	// }
	//
	// private CGridLayout createUserInfoLayout() {
	// // common part: create layout
	// userInfoLayout = ComponentUtil.createGrid();
	// userInfoLayout.setWidth("-1px");
	// userInfoLayout.setHeight("-1px");
	// userInfoLayout.setMargin(true);
	// userInfoLayout.setSpacing(true);
	//
	// personLookUpFld = userInfoLayout.addField(CLabel.class,
	// PersonProperty.PERSON, 0, 0);
	// userInfoLayout.setComponentAlignment(personLookUpFld,
	// Alignment.BOTTOM_LEFT);
	//
	// // usernameFld
	// usernameFld = userInfoLayout.addField(CLabel.class,
	// ApplicationUserProperty.USERNAME, 0, 1);
	// usernameFld.setWidth("250px");
	//
	// // emailFld
	// emailFld = userInfoLayout.addField(CLabel.class,
	// ApplicationUserProperty.EMAIL, 0, 2);
	// emailFld.setWidth("250px");
	//
	// // isActiveChkBox
	// activeFld = userInfoLayout.addField(CCheckBox.class,
	// ApplicationUserProperty.ACTIVE, 1, 0);
	// activeFld.setReadOnly(true);
	// activeFld.addStyleName(ValoTheme.CHECKBOX_LARGE);
	// userInfoLayout.setComponentAlignment(activeFld, Alignment.MIDDLE_CENTER);
	//
	// return userInfoLayout;
	// }
	//
	// private CGridLayout createPasswordLayout() {
	// passwordLayout = ComponentUtil.createGrid();
	// passwordLayout.setMargin(true);
	// passwordLayout.setSpacing(true);
	//
	// forceChangePasswordFld = passwordLayout.addField(CCheckBox.class,
	// ApplicationUserProperty.FORCE_CHANGE_PASSWORD, 0, 0);
	// forceChangePasswordFld.setReadOnly(true);
	// forceChangePasswordFld.addStyleName(ValoTheme.CHECKBOX_LARGE);
	// passwordLayout.setComponentAlignment(forceChangePasswordFld,
	// Alignment.BOTTOM_LEFT);
	//
	// return passwordLayout;
	// }
	//
	// private CGridLayout createLdapLayout() {
	// ldapLayout = ComponentUtil.createGrid();
	// ldapLayout.setMargin(true);
	// ldapLayout.setSpacing(true);
	//
	// useLdapFld = ldapLayout.addField(CCheckBox.class,
	// ApplicationUserProperty.USE_LDAP, 0, 0);
	// useLdapFld.setReadOnly(true);
	// useLdapFld.addStyleName(ValoTheme.CHECKBOX_LARGE);
	// ldapLayout.setComponentAlignment(useLdapFld, Alignment.BOTTOM_LEFT);
	//
	// return ldapLayout;
	// }
	//
	// private HorizontalLayout createPersonLayout() {
	// personInfoLayout = new HorizontalLayout();
	//
	// imageFld = new Image();
	// personInfoLayout.addComponent(imageFld);
	//
	// formGrid = ComponentUtil.createGrid();
	// personInfoLayout.addComponent(formGrid);
	//
	// firstNameFld = formGrid.addField(CLabel.class, PersonProperty.FIRST_NAME,
	// 0, 0);
	//
	// middleNameFld = formGrid.addField(CLabel.class,
	// PersonProperty.MIDDLE_NAME, 0, 1);
	//
	// lastNameFld = formGrid.addField(CLabel.class, PersonProperty.LAST_NAME,
	// 0, 2);
	//
	// dateOfBirthFld = formGrid.addField(CLabel.class,
	// PersonProperty.DATE_OF_BIRTH, 1, 0);
	//
	// genderFld = formGrid.addField(CLabel.class, GenderProperty.GENDER, 1, 1);
	//
	// isPersonActiveFld = formGrid.addField(CCheckBox.class,
	// PersonProperty.ACTIVE, 1, 2);
	// isPersonActiveFld.setReadOnly(true);
	// formGrid.setComponentAlignment(isPersonActiveFld, Alignment.BOTTOM_LEFT);
	//
	// return personInfoLayout;
	// }
	//
	// @SuppressWarnings("unchecked")
	// @Override
	// public ApplicationUser setFormValues(Object entity) throws
	// FrameworkException {
	// applicationUser = (ApplicationUser) entity;
	// person = applicationUser.getPerson();
	// addToReferenceKey(ReferenceKey.APPLICATION_USER, applicationUser);
	// addToReferenceKey(ReferenceKey.PERSON, person);
	//
	// usernameFld.setValue(applicationUser.getUsername());
	// activeFld.setValue(applicationUser.getActive());
	// forceChangePasswordFld.setValue(applicationUser.getResetPassword());
	// personLookUpFld.setValue(person == null ? null : person.getCaption());
	// useLdapFld.setValue(applicationUser.getUseLdap());
	// ThemeResource resource = new ThemeResource("img/profile-pic-300px.jpg");
	// imageFld.setSource(resource);
	// imageFld.setHeight("120px");
	//
	// if (person != null) {
	// String email =
	// PersonInformationServiceFacade.get(UI.getCurrent()).getEmail(person.getId());
	// emailFld.setValue(email);
	//
	// IFileService fileService = ContextProvider.getBean(IFileService.class);
	// EntityFile entityFile =
	// fileService.findFileByEntityAndType(person.getId().toString(),
	// ReferenceKey.PERSON, FileReference.USER_PROFILE_IMAGE);
	// if (entityFile != null) {
	// imageFld.setSource(new StreamResource(new StreamSource() {
	//
	// private static final long serialVersionUID = -1143826625995236036L;
	//
	// @Override
	// public InputStream getStream() {
	// if (entityFile == null || entityFile.getFileObject() == null) {
	// return null;
	// }
	// return new ByteArrayInputStream(entityFile.getFileObject());
	// }
	// }, entityFile.getName()));
	// }
	// }
	//
	// setPersonInfo();
	//
	// return applicationUser;
	// }
	//
	// private void setPersonInfo() throws FrameworkException {
	// personInfoLayout.setVisible(false);
	// if (person != null) {
	// personInfoLayout.setVisible(true);
	// firstNameFld.setValue(person.getFirstName());
	// middleNameFld.setValue(person.getMiddleName());
	// lastNameFld.setValue(person.getLastName());
	//
	// dateOfBirthFld.setValue(person.getDateOfBirth() == null ? null
	// : person.getDateOfBirth().format(Constants.DATE_FORMATTER));
	// genderFld.setValue(person.getGender() == null ? null
	// : PropertyResolver.getPropertyValueByLocale(person.getGender().getKey(),
	// UI.getCurrent().getLocale()));
	// isPersonActiveFld.setValue(person.getActive());
	//
	// IFileService fileService = ContextProvider.getBean(IFileService.class);
	// EntityFile entityFile =
	// fileService.findFileByEntityAndType(person.getId().toString(),
	// ReferenceKey.PERSON, FileReference.USER_PROFILE_IMAGE);
	// if (entityFile != null) {
	// imageFld.setSource(new StreamResource(new StreamSource() {
	//
	// private static final long serialVersionUID = -1143826625995236036L;
	//
	// @Override
	// public InputStream getStream() {
	// if (entityFile == null || entityFile.getFileObject() == null) {
	// return null;
	// }
	// return new ByteArrayInputStream(entityFile.getFileObject());
	// }
	// }, entityFile.getName()));
	// }
	// }
	// }
	// }

}
