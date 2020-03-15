package software.simple.solutions.framework.core.web.flow;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import software.simple.solutions.framework.core.constants.Constants;
import software.simple.solutions.framework.core.util.SessionHolder;

//@Route(value = "", layout = MainView.class)
public class HomeView extends VerticalLayout implements BeforeEnterObserver {

	private static final long serialVersionUID = 4918285431578284653L;

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		SessionHolder sessionHolder = (SessionHolder) VaadinSession.getCurrent().getAttribute(Constants.SESSION_HOLDER);
		if (sessionHolder == null || sessionHolder.getApplicationUser() == null) {
			sessionHolder = new SessionHolder();
			VaadinSession.getCurrent().setAttribute(Constants.SESSION_HOLDER, sessionHolder);
			// UI.getCurrent().getPage().setLocation("login");
			event.forwardTo("login");
		}
	}

}
