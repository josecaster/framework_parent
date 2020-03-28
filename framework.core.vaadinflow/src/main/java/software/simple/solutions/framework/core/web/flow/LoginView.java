package software.simple.solutions.framework.core.web.flow;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamRegistration;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;

import io.reactivex.subjects.BehaviorSubject;
import software.simple.solutions.framework.core.constants.Constants;
import software.simple.solutions.framework.core.constants.ReferenceKey;
import software.simple.solutions.framework.core.entities.ApplicationUser;
import software.simple.solutions.framework.core.entities.Role;
import software.simple.solutions.framework.core.exceptions.FrameworkException;
import software.simple.solutions.framework.core.pojo.SecurityValidation;
import software.simple.solutions.framework.core.properties.SystemProperty;
import software.simple.solutions.framework.core.service.IApplicationUserService;
import software.simple.solutions.framework.core.service.IUserRoleService;
import software.simple.solutions.framework.core.util.ContextProvider;
import software.simple.solutions.framework.core.util.PropertyResolver;
import software.simple.solutions.framework.core.util.SessionHolder;
import software.simple.solutions.framework.core.web.routing.Routes;

/**
 * The main view contains a button and a click listener.
 */
//@Route(value = Routes.LOGIN, layout = MainView.class)
@CssImport(value = "./styles/shared-styles.css")
public class LoginView extends FlexLayout implements BeforeEnterObserver {

	private static final String CENTER_LOGIN_LAYOUT = "center-login-layout";

	private static final long serialVersionUID = -3279206519129928810L;

	private UI ui;

	public LoginView() {

		setWidth("100%");
		setHeight("100%");
		getElement().getStyle().set("background-image", "url('images/dynamic-style.png')");
		ui = UI.getCurrent();
		VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.setMaxWidth("350px");
		verticalLayout.setMinWidth("250px");
		verticalLayout.addClassName(CENTER_LOGIN_LAYOUT);
		add(verticalLayout);

		Label welcomeLbl = new Label();
		welcomeLbl.setWidth("100%");
		welcomeLbl.setText(
				PropertyResolver.getPropertyValueByLocale(SystemProperty.LOGIN_WELCOME, UI.getCurrent().getLocale()));
		verticalLayout.add(welcomeLbl);
		verticalLayout.setHorizontalComponentAlignment(Alignment.CENTER, welcomeLbl);

		HorizontalLayout errorLayout = new HorizontalLayout();
		errorLayout.setMargin(false);
		errorLayout.setSpacing(true);
		errorLayout.setVisible(false);
		verticalLayout.add(errorLayout);

		Icon errorMessageIcon = new Icon(VaadinIcon.EXCLAMATION_CIRCLE_O);
		errorMessageIcon.setColor("red");
		errorLayout.add(errorMessageIcon);

		VerticalLayout errorMessageLayout = new VerticalLayout();
		errorMessageLayout.addClassName("error-message-layout");
		errorMessageLayout.setMargin(false);
		errorMessageLayout.setSpacing(false);
		errorLayout.add(errorMessageLayout);

		Label errorFld = new Label();
		errorFld.setWidth("100%");
		errorFld.addClassName("error-message");
		errorMessageLayout.add(errorFld);

		TextField usernameFld = new TextField();
		usernameFld.setWidth("100%");
		usernameFld.setLabel(
				PropertyResolver.getPropertyValueByLocale(SystemProperty.LOGIN_USERNAME, UI.getCurrent().getLocale()));
		usernameFld.setPlaceholder(
				PropertyResolver.getPropertyValueByLocale(SystemProperty.LOGIN_USERNAME, UI.getCurrent().getLocale()));
		verticalLayout.add(usernameFld);
		verticalLayout.setHorizontalComponentAlignment(Alignment.CENTER, usernameFld);

		PasswordField passwordField = new PasswordField();
		passwordField.setWidth("100%");
		passwordField.setLabel(
				PropertyResolver.getPropertyValueByLocale(SystemProperty.LOGIN_PASSWORD, UI.getCurrent().getLocale()));
		passwordField.setPlaceholder(
				PropertyResolver.getPropertyValueByLocale(SystemProperty.LOGIN_PASSWORD, UI.getCurrent().getLocale()));
		verticalLayout.add(passwordField);
		verticalLayout.setHorizontalComponentAlignment(Alignment.CENTER, passwordField);

		Button submitFld = new Button();
		submitFld.setWidth("100%");
		submitFld.setText(PropertyResolver.getPropertyValueByLocale(SystemProperty.LOGIN_BUTTON_LOGIN,
				UI.getCurrent().getLocale()));
		submitFld.addClickShortcut(Key.ENTER);
		verticalLayout.add(submitFld);
		verticalLayout.setHorizontalComponentAlignment(Alignment.CENTER, submitFld);

		Anchor forgotPasswordFld = new Anchor();
		forgotPasswordFld.setWidth("100%");
		forgotPasswordFld.setText(PropertyResolver.getPropertyValueByLocale(SystemProperty.SYSTEM_RESET_PASSWORD,
				UI.getCurrent().getLocale()));
		verticalLayout.add(forgotPasswordFld);
		verticalLayout.setHorizontalComponentAlignment(Alignment.CENTER, forgotPasswordFld);

		submitFld.addClickListener(new ComponentEventListener<ClickEvent<Button>>() {

			private static final long serialVersionUID = 1425155324269087097L;

			@Override
			public void onComponentEvent(ClickEvent<Button> event) {
				signIn();
			}

			private void signIn() {
				try {
					errorFld.setText("");
					errorLayout.setVisible(false);
					IApplicationUserService applicationUserService = ContextProvider
							.getBean(IApplicationUserService.class);
					SecurityValidation securityValidation = applicationUserService.validateLogin(usernameFld.getValue(),
							passwordField.getValue());
					if (securityValidation.isSuccess()) {
						String applicationUsername = postProcessLdapUsername(usernameFld.getValue());
						processUserLogin(applicationUsername);
					} else {
						errorLayout.setVisible(true);
						errorFld.setText(PropertyResolver.getPropertyValueByLocale(securityValidation.getMessageKey(),
								UI.getCurrent().getLocale()));
						// NotificationWindow.notificationErrorWindow(
						// PropertyResolver.getPropertyValueByLocale(securityValidation.getMessageKey()),
						// UI.getCurrent().getLocale());
					}
				} catch (FrameworkException e) {
					// new MessageWindowHandler(e);
					// logger.error(e.getMessage(), e);
					e.printStackTrace();
				}
			}

			protected String postProcessLdapUsername(String username) {
				String applicationUserName = username;
				if (username.contains("@")) {
					applicationUserName = username.substring(0, username.indexOf('@'));
				} else if (username.contains("\\")) {
					applicationUserName = username.substring(username.indexOf('\\') + 1, username.length());
				}
				return applicationUserName;
			}

			private void processUserLogin(String userName) {
				SessionHolder sessionHolder = (SessionHolder) VaadinSession.getCurrent()
						.getAttribute(Constants.SESSION_HOLDER);
				if (sessionHolder == null) {
					sessionHolder = new SessionHolder();
					VaadinSession.getCurrent().setAttribute(Constants.SESSION_HOLDER, sessionHolder);
				}
				try {
					sessionHolder.setPassword(passwordField.getValue());
					IApplicationUserService applicationUserService = ContextProvider
							.getBean(IApplicationUserService.class);
					ApplicationUser applicationUser = applicationUserService.getByUsername(userName);

					/*
					 * Update password, because security context needs a
					 * password to validate.
					 */
					if (applicationUser.getUseLdap() != null && applicationUser.getUseLdap()) {
						applicationUserService.updatePasswordForLdapAccess(applicationUser.getId(),
								passwordField.getValue());
					}
					sessionHolder.setApplicationUser(applicationUser);
					IUserRoleService userRoleService = ContextProvider.getBean(IUserRoleService.class);
					List<Role> rolesByUser = userRoleService.findRolesByUser(applicationUser.getId());
					sessionHolder.setRoles(rolesByUser);
					if (rolesByUser != null && !rolesByUser.isEmpty()) {
						sessionHolder.setSelectedRole(rolesByUser.get(0));
					}
					VaadinSession.getCurrent().setAttribute(Constants.SESSION_HOLDER, sessionHolder);
					// if
					// (!RouteConfiguration.forSessionScope().isPathRegistered("home"))
					// {
					// RouteConfiguration.forSessionScope().setRoute("home",
					// MainView.class);
					// }
					BehaviorSubject<Boolean> loginSuccessfullObserver = (BehaviorSubject<Boolean>) sessionHolder
							.getReferenceKey(ReferenceKey.LOGIN_SUCCESSFULL);
					if (loginSuccessfullObserver != null) {
						loginSuccessfullObserver.onNext(true);
					}
					if (sessionHolder.getForwardTo() != null) {
						ui.navigate(sessionHolder.getForwardTo());
						sessionHolder.setForwardTo(null);
					} else {
						ui.navigate("");
					}
				} catch (FrameworkException e) {
					e.printStackTrace();
					// logger.error(e.getMessage(), e);
				}
			}
		});

		// @formatter:off
		String styles = "html {"
				+ "--lumo-border-radius: 0.125em;"
				+ "--lumo-size-xl: 2.5rem;"
				+ "--lumo-size-l: 2rem;"
				+ "--lumo-size-m: 1.75rem;"
				+ "--lumo-size-s: 1.5rem;"
				+ "--lumo-space-xl: 1.75rem;"
				+ "--lumo-space-l: 1.125rem;"
				+ "--lumo-space-m: 0.5rem;"
				+ "--lumo-space-s: 0.25rem;"
				+ "--lumo-space-xs: 0.125rem;"
				+ "--lumo-font-size: 1rem;"
				+ "--lumo-font-size-xxxl: 1.375rem;"
				+ "--lumo-font-size-xxl: 1.125rem;"
				+ "--lumo-font-size-xl: 1rem;"
				+ "--lumo-font-size-l: 0.875rem;"
				+ "--lumo-font-size-m: 0.75rem;"
				+ "--lumo-font-size-s: 0.6875rem;"
				+ "--lumo-font-size-xs: 0.625rem;"
				+ "--lumo-font-size-xxs: 0.625rem;"
				+ "--lumo-line-height-m: 1.4;"
				+ "--lumo-line-height-s: 1.2;"
				+ "--lumo-line-height-xs: 1.1;"
				+ "--lumo-size-xs: 1.25rem;"
				+ "--lumo-primary-text-color: rgb(243, 156, 18);"
				+ "--lumo-primary-color-50pct: rgba(243, 156, 18, 0.5);"
				+ "--lumo-primary-color-10pct: rgba(243, 156, 18, 0.1);"
				+ "--lumo-primary-color: #f39c12;"
				+ "}"
				+ ""
				+ "."+CENTER_LOGIN_LAYOUT+" { "
		        + "position: absolute;"
		        + "left: 50%;"
		        + "top: 50%;"
		        + "transform: translate(-50%, -50%);"
		        + "-ms-transform: translate(-50%, -50%); /* for IE 9 */"
		        + "-webkit-transform: translate(-50%, -50%); /* for Safari */;"
		        + "border: solid 1px #555;"
		        + "background-color: #eed;"
		        + "box-shadow: 10px 10px  5px rgba(0,0,0,0.6);"
		        + "-moz-box-shadow: 10px 10px  5px rgba(0,0,0,0.6);"
		        + "-webkit-box-shadow: 10px 10px  5px rgba(0,0,0,0.6);"
		        + "-o-box-shadow: 10px 10px  5px rgba(0,0,0,0.6);"
		        + " }"
		        + ".error-message{"
		        + "color: red;"
		        + "}"
		        + ".error-message-layout{"
		        + "padding: 0;"
		        + "}"
		        ;
		// @formatter:on

		/*
		 * The code below register the style file dynamically. Normally you
		 * use @StyleSheet annotation for the component class. This way is
		 * chosen just to show the style file source code.
		 */
		StreamRegistration resource = ui.getSession().getResourceRegistry()
				.registerResource(new StreamResource("styles.css", () -> {
					byte[] bytes = styles.getBytes(StandardCharsets.UTF_8);
					return new ByteArrayInputStream(bytes);
				}));
		ui.getPage().addStyleSheet("base://" + resource.getResourceUri().toString());
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		SessionHolder sessionHolder = (SessionHolder) VaadinSession.getCurrent().getAttribute(Constants.SESSION_HOLDER);
		if (sessionHolder != null && sessionHolder.getApplicationUser() != null) {
			event.forwardTo("");
			// UI.getCurrent().getPage().setLocation("");
		}
	}

}