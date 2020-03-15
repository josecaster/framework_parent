package software.simple.solutions.framework.core.web.flow;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.github.appreciated.app.layout.addons.notification.DefaultNotificationHolder;
import com.github.appreciated.app.layout.component.appbar.AppBarBuilder;
import com.github.appreciated.app.layout.component.applayout.LeftLayouts;
import com.github.appreciated.app.layout.component.applayout.LeftLayouts.Left;
import com.github.appreciated.app.layout.component.builder.AppLayoutBuilder;
import com.github.appreciated.app.layout.component.menu.left.LeftMenuComponentWrapper;
import com.github.appreciated.app.layout.component.menu.left.builder.LeftAppMenuBuilder;
import com.github.appreciated.app.layout.component.router.AppLayoutRouterLayout;
import com.github.appreciated.app.layout.entity.DefaultBadgeHolder;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.ErrorEvent;
import com.vaadin.flow.server.ErrorHandler;
import com.vaadin.flow.server.StreamRegistration;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;

import io.reactivex.functions.Consumer;
import io.reactivex.subjects.BehaviorSubject;
import software.simple.solutions.framework.core.constants.Constants;
import software.simple.solutions.framework.core.constants.MenuType;
import software.simple.solutions.framework.core.constants.ReferenceKey;
import software.simple.solutions.framework.core.entities.Configuration;
import software.simple.solutions.framework.core.entities.Menu;
import software.simple.solutions.framework.core.exceptions.FrameworkException;
import software.simple.solutions.framework.core.properties.ConfigurationProperty;
import software.simple.solutions.framework.core.properties.SystemProperty;
import software.simple.solutions.framework.core.service.IConfigurationService;
import software.simple.solutions.framework.core.service.facade.MenuServiceFacade;
import software.simple.solutions.framework.core.util.ContextProvider;
import software.simple.solutions.framework.core.util.PropertyResolver;
import software.simple.solutions.framework.core.util.SessionHolder;
import software.simple.solutions.framework.core.web.flow.applayout.CustomLeftClickableItem;
import software.simple.solutions.framework.core.web.flow.applayout.CustomLeftSubMenuBuilder;
import software.simple.solutions.framework.core.web.flow.applayout.CustomLeftSubmenu;

<<<<<<< HEAD
@Push
@PreserveOnRefresh
=======
//@Push
//@PreserveOnRefresh
//@CssImport(value = "styles/paging-combobox.css", themeFor = "vaadin-combo-box")
>>>>>>> master
public class MainView extends AppLayoutRouterLayout
// implements BeforeEnterObserver
{
	private static final long serialVersionUID = -7975431980465425663L;

	private static final Logger logger = LogManager.getLogger(MainView.class);

	private DefaultNotificationHolder notifications = new DefaultNotificationHolder();
	private DefaultBadgeHolder badge = new DefaultBadgeHolder(5);
	private UI ui;
	private boolean open = false;
	private Left leftResponsive;
	private SessionHolder sessionHolder;
	private List<Menu> menus;

	// @formatter:off
	private String styles = ".applayout-profile-image { "
	        + "width: 50px;"
	        + "height: 50px;"
	        + "border-radius: 50%;"
	        + "object-fit: cover;"
	        + "border: 2px solid #ffc13f;"
	        + " }"
	        + ""
	        + ".user-menu {"
	        + "position: relative;"
	        + "display: inline-block;"
	        + "font-size: 1rem;"
	        + "z-index: 25;"
	        + "margin-top: -3px;"
	        + "}"
	        + ""
	        + ".user-menu__inner {"
	        + "position: relative;"
	        + "}"
	        + ""
	        + ".user-menu__profile {"
	        + "position: relative;"
	        + "z-index: 20;"
	        + "cursor: pointer;"
	        + "}"
	        + ""
	        + ".user-menu__profile-img {"
	        + "width: 3rem;"
	        + "height: 3rem;"
	        + "border-radius: 50%;"
	        + "object-fit: cover;"
	        + "border: 2px solid #ffc13f;"
	        + "}"
	        + ""
	        + ".user-menu__pop-out.open {"
	        + "opacity: 1;"
	        + "pointer-events: auto;"
	        + "transform: none;"
	        + "}"
	        + ""
	        + ".user-menu__pop-out {"
	        + "z-index: 19;"
	        + "opacity: 0;"
	        + "pointer-events: none;"
	        + "transition: all 150ms ease-in-out;"
	        + "position: absolute;"
	        + "top: -.6em;"
	        + "right: -.9em;"
	        + "min-width: 250px;"
	        + "border-radius: 5px;"
	        + "box-shadow: 0 0.5em 1em rgba(0, 0, 0, 0.2);"
	        + "vertical-align: middle;"
	        + "background: #fafbfc;"
	        + "transform: scale(0.96, 0.96);"
	        + "}"
	        + ""
	        + ".user-menu__user {"
	        + "display: block;"
	        + "padding: .7rem 1.5rem;"
	        + "}"
	        + ""
	        + ".user-menu__name {"
	        + "font-family: \"Montserrat\", Helvetica, sans-serif;"
	        + "font-weight: 400;"
	        + "line-height: 1.6;"
	        + "color: #36393d;"
	        + "font-size: 1.0625rem;"
	        + "display: block;"
	        + "color: #2d3033;"
	        + "font-weight: 700;"
	        + "white-space: nowrap;"
	        + "overflow: hidden;"
	        + "width: 150px;"
	        + "text-overflow: ellipsis;"
	        + "}"
	        + ""
	        + ".user-menu__edit-profile {"
	        + "font-weight: 500;"
	        + "line-height: 1.6;"
	        + "color: #36393d;"
	        + "font-size: .875rem;"
	        + "display: block;"
	        + "border: 0;"
	        + "color: #00b4f0;"
	        + "}"
	        + ""
	        + ".user-menu__link-list {"
	        + "padding: .8em 0 0;"
	        + "background: #fafbfc;"
	        + "}"
	        + ""
	        + ".user-menu__link {"
	        + "font-family: \"Montserrat\", Helvetica, sans-serif;"
	        + "font-weight: 400;"
	        + "line-height: 1.6;"
	        + "color: #36393d;"
	        + "font-size: 1.0625rem;"
	        + "display: block;"
	        + "padding: .45rem 1.5rem;"
	        + "color: #5F6970;"
	        + "line-height: 1.4;"
	        + "white-space: nowrap;"
	        + "font-size: .875rem;"
	        + "font-weight: 500;"
	        + "transition: all .1s ease-in-out;"
	        + "}"
	        + ""
	        + ".user-menu__link.logout {"
	        + "padding: 1rem 1.5rem .5rem;"
	        + "margin-bottom: .5rem;"
	        + "color: #829191;"
	        + "transition: all ease-in-out .1s;"
	        + "font-weight: 500;"
	        + "}"
	        + ""
	        + ".user-menu__link.logout iron-icon {"
	        + "width: 14px;"
	        + "height: 14px;"
	        + "margin-right: 6px;"
	        + "color: #5F6970;"
	        + "}"
	        + ""
	        + "a:hover {"
	        + "text-decoration: none;"
	        + "color: #00A1E5;"
	        + "}"
	        ;
	// @formatter:on

	public MainView() {
		ui = UI.getCurrent();
		VaadinSession.getCurrent().setErrorHandler(new ErrorHandler() {

			private static final long serialVersionUID = 5321686442197175973L;

			@Override
			public void error(ErrorEvent event) {
				TextArea errorMessageFld = new TextArea();
				errorMessageFld.setWidth("1200px");
				errorMessageFld.setMaxHeight("400px");

				Details errorDetailsFld = new Details();
				errorDetailsFld.setSummaryText("Error Details");
				errorDetailsFld.addContent(errorMessageFld);
				errorDetailsFld.setVisible(false);

				Dialog dialog = new Dialog();
				dialog.setCloseOnEsc(false);
				dialog.setCloseOnOutsideClick(false);

				dialog.add(errorDetailsFld);

				Button confirmButton = new Button("Confirm");
				confirmButton.addClickListener(new ComponentEventListener<ClickEvent<Button>>() {

					private static final long serialVersionUID = -8011280206656252197L;

					@Override
					public void onComponentEvent(ClickEvent<Button> event) {
						dialog.close();
					}
				});
				dialog.add(confirmButton);

			}
		});

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

		notifications.addClickListener(notification -> {
			/* ... */});

		MenuBar menuBar = new MenuBar();
		Icon icon = new Icon(VaadinIcon.USER);
		MenuItem userProfileMenu = menuBar.addItem(icon);

		Button logoutBtn = new Button(
				PropertyResolver.getPropertyValueByLocale(SystemProperty.LOGIN_BUTTON_LOGIN, ui.getLocale()));
		userProfileMenu.getSubMenu().add(logoutBtn);
		logoutBtn.addClickListener(new ComponentEventListener<ClickEvent<Button>>() {

			private static final long serialVersionUID = -5897232278349546260L;

			@Override
			public void onComponentEvent(ClickEvent<Button> event) {
				// UI.getCurrent().getPage().executeJs("window.location.href=''");
				UI.getCurrent().getPage().reload();
				VaadinSession.getCurrent().close();
			}
		});

		Label title = new Label();
		try {
			IConfigurationService configurationService = ContextProvider.getBean(IConfigurationService.class);
			Configuration configuration = configurationService.getByCode(ConfigurationProperty.APPLICATION_NAME);
			if (configuration != null) {
				title.setText(configuration.getValue());
				title.setSizeUndefined();
			}
		} catch (FrameworkException e) {
			logger.error(e.getMessage(), e);
		}

		sessionHolder = (SessionHolder) VaadinSession.getCurrent().getAttribute(Constants.SESSION_HOLDER);
		if (sessionHolder == null) {
			sessionHolder = new SessionHolder();
			VaadinSession.getCurrent().setAttribute(Constants.SESSION_HOLDER, sessionHolder);
		}
		BehaviorSubject<Boolean> loginSuccessfullObserver = BehaviorSubject.create();
		sessionHolder.addReferenceKey(ReferenceKey.LOGIN_SUCCESSFULL, loginSuccessfullObserver);

		FlexLayout flexLayout = AppBarBuilder.get().add(menuBar).build();

		LeftMenuComponentWrapper leftAppMenuBuilder = (LeftMenuComponentWrapper) LeftAppMenuBuilder.get().build();
		leftResponsive = AppLayoutBuilder.get(LeftLayouts.Left.class).withTitle(title).withAppBar(flexLayout)
				.withAppMenu(leftAppMenuBuilder).build();
		init(leftResponsive);

		loginSuccessfullObserver.subscribe(new Consumer<Boolean>() {

			@Override
			public void accept(Boolean t) throws Exception {
				sessionHolder = (SessionHolder) VaadinSession.getCurrent().getAttribute(Constants.SESSION_HOLDER);

				MenuServiceFacade menuServiceFacade = MenuServiceFacade.get(UI.getCurrent());
				try {
					menus = menuServiceFacade.findAuthorizedMenusByType(sessionHolder.getSelectedRole().getId(),
							Arrays.asList(MenuType.HEAD_MENU, MenuType.CHILD));
				} catch (FrameworkException e) {
					logger.error(e.getMessage(), e);
				}
				if (menus != null) {
					for (Menu menu : menus) {
						if (menu.getParentMenu() == null && menu.getView() == null) {
							com.flowingcode.vaadin.addons.fontawesome.FontAwesome.Solid.Icon icon = null;
							if (menu.getIcon() != null) {
								icon = FontAwesome.Solid.valueOf(menu.getIcon()).create();
							}
							CustomLeftSubmenu customLeftSubmenu = CustomLeftSubMenuBuilder.get(menu.getName(), icon)
									.build();
							createSubMenus(menu, customLeftSubmenu);
							leftAppMenuBuilder.add(customLeftSubmenu);
						}
					}
				}
			}
		});
	}

	private CustomLeftSubmenu createSubMenus(Menu parent, CustomLeftSubmenu customLeftSubmenu) {
		return getChildrens(parent, customLeftSubmenu);
	}

	private CustomLeftSubmenu getChildrens(Menu parent, CustomLeftSubmenu customLeftSubmenu) {
		for (Menu menu : menus) {
			if (menu.getParentMenu() != null && menu.getParentMenu().getId().compareTo(parent.getId()) == 0) {
				if (menu.getView() == null) {
					com.flowingcode.vaadin.addons.fontawesome.FontAwesome.Solid.Icon icon = null;
					if (menu.getIcon() != null) {
						icon = FontAwesome.Solid.valueOf(menu.getIcon()).create();
					}
					CustomLeftSubmenu customLeftSubmenu2 = CustomLeftSubMenuBuilder.get(menu.getName(), icon).build();
					getChildrens(menu, customLeftSubmenu2);
					customLeftSubmenu.getSubmenuContainer().add(customLeftSubmenu2);
				} else {
					String viewClassName = menu.getView().getViewClassName();
					Class<? extends Component> forName = null;
					try {
						forName = (Class<? extends Component>) Class.forName(viewClassName);
						Route route = forName.getAnnotation(Route.class);
						if (route != null) {
							String value = route.value();
							sessionHolder.addRouteMenu(value, menu.getId());
							// RouteConfiguration.forSessionScope().setRoute(value,
							// forName, MainView.class);
						}
					} catch (ClassNotFoundException e) {
						logger.error(e.getMessage());
					}
					com.flowingcode.vaadin.addons.fontawesome.FontAwesome.Solid.Icon icon = null;
					if (menu.getIcon() != null) {
						icon = FontAwesome.Solid.valueOf(menu.getIcon()).create();
					}
					CustomLeftClickableItem leftClickableItem = menuClicked(menu, forName, icon);
					customLeftSubmenu.getSubmenuContainer().add(leftClickableItem);
				}
			}
		}
		return customLeftSubmenu;
	}

	private CustomLeftClickableItem menuClicked(Menu menu, Class<? extends Component> forName,
			com.flowingcode.vaadin.addons.fontawesome.FontAwesome.Solid.Icon icon) {
		CustomLeftClickableItem leftClickableItem = new CustomLeftClickableItem(menu.getName(), icon,
				new ComponentEventListener<ClickEvent<?>>() {

					private static final long serialVersionUID = -8387329904132123511L;

					@Override
					public void onComponentEvent(ClickEvent<?> event) {
						if (forName != null) {
							Route route = forName.getAnnotation(Route.class);
							if (route != null) {
								String value = route.value();
								ui.navigate(value);
							}
							// RouteConfiguration.forSessionScope().setRoute(menu.getName(),
							// forName, MainView.class);
							// sessionHolder.addRouteMenu(menu.getName(),
							// menu.getId());
							// List<RouteData> availableRoutes =
							// RouteConfiguration.forSessionScope().getAvailableRoutes();
							// for (RouteData routeData : availableRoutes) {
							// System.out.println(routeData.getUrl() + " - " +
							// routeData.getNavigationTarget());
							// }

						}
					}
				});
		return leftClickableItem;
	}

	public DefaultNotificationHolder getNotifications() {
		return notifications;
	}

	public DefaultBadgeHolder getBadge() {
		return badge;
	}

	private Div createUserProfileItem() {
		Div userMenuDiv = new Div();
		userMenuDiv.addClassName("user-menu");

		Div userMenuInnerDiv = new Div();
		userMenuInnerDiv.addClassName("user-menu__inner");
		userMenuDiv.add(userMenuInnerDiv);

		Div userMenuProfileDiv = new Div();
		userMenuProfileDiv.addClassName("user-menu__profile");
		userMenuDiv.add(userMenuProfileDiv);

		Image image = new Image("images/profile-pic-300px.jpg", "A flower");
		image.addClassName("user-menu__profile-img");
		userMenuProfileDiv.add(image);

		Div userMenuPopOutDiv = new Div();
		userMenuPopOutDiv.setId("user-menu");
		userMenuPopOutDiv.addClassName("user-menu__pop-out");
		userMenuInnerDiv.add(userMenuPopOutDiv);

		Div clickDiv = new Div();
		clickDiv.setWidth("100%");
		clickDiv.setHeight("100%");
		clickDiv.setId("clickDiv");
		ui.add(clickDiv);
		clickDiv.addClickListener(new ComponentEventListener<ClickEvent<Div>>() {

			private static final long serialVersionUID = 1207620668236881906L;

			@Override
			public void onComponentEvent(ClickEvent<Div> event) {
				open = !open;
				if (open) {
					userMenuPopOutDiv.addClassName("open");
				} else {
					userMenuPopOutDiv.removeClassName("open");
				}
				Notification.show("test").open();
			}
		});

		image.addClickListener(new ComponentEventListener<ClickEvent<Image>>() {

			private static final long serialVersionUID = -3257481767745777407L;

			@Override
			public void onComponentEvent(ClickEvent<Image> event) {
				open = !open;
				if (open) {
					userMenuPopOutDiv.addClassName("open");
				} else {
					userMenuPopOutDiv.removeClassName("open");
				}
			}
		});

		Div userMenuUserDiv = new Div();
		userMenuUserDiv.addClassName("user-menu__user");
		userMenuPopOutDiv.add(userMenuUserDiv);

		Anchor editProfileAnchor = new Anchor();
		editProfileAnchor.addClassName("user-menu__profile-link");
		userMenuUserDiv.add(editProfileAnchor);

		Span userMenuNameSpan = new Span();
		userMenuNameSpan.addClassName("user-menu__name");
		userMenuNameSpan.setText("Yusuf");
		editProfileAnchor.add(userMenuNameSpan);

		Span userMenuEditProfileSpan = new Span();
		userMenuEditProfileSpan.addClassName("user-menu__edit-profile");
		userMenuEditProfileSpan.setText("Edit profile");
		editProfileAnchor.add(userMenuEditProfileSpan);

		Div userMenuLinkListDiv = new Div();
		userMenuLinkListDiv.addClassName("user-menu__link-list");
		userMenuPopOutDiv.add(userMenuLinkListDiv);

		// Anchor myFeedAnchor = new Anchor();
		// myFeedAnchor.addClassName("user-menu__link");
		// userMenuLinkListDiv.add(myFeedAnchor);

		Anchor logoutAnchor = new Anchor();
		logoutAnchor.addClassName("user-menu__link");
		logoutAnchor.addClassName("logout");
		userMenuLinkListDiv.add(logoutAnchor);

		Icon logoutIcon = new Icon(VaadinIcon.UNLOCK);
		logoutAnchor.add(logoutIcon);

		Span logoutSpan = new Span();
		logoutSpan.addClassName("user-menu__link-text");
		logoutSpan.setText("Log out");
		logoutAnchor.add(logoutSpan);

		return userMenuDiv;
	}

	@Override
	public void showRouterLayoutContent(HasElement content) {
		SessionHolder sessionHolder = (SessionHolder) VaadinSession.getCurrent().getAttribute(Constants.SESSION_HOLDER);
		if (sessionHolder == null || sessionHolder.getApplicationUser() == null) {
			getContent().getElement().appendChild(content.getElement());
			leftResponsive.setVisible(false);
		} else {
			leftResponsive.setVisible(true);
			super.showRouterLayoutContent(content);
		}
	}

}