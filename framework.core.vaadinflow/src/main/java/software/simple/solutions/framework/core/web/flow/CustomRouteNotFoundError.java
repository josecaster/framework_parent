package software.simple.solutions.framework.core.web.flow;

import javax.servlet.http.HttpServletResponse;

import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.RouteNotFoundError;

//@ParentLayout(MainView.class)
public class CustomRouteNotFoundError  {

	private static final long serialVersionUID = -2987012211920485332L;

//	@Override
//	public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<NotFoundException> parameter) {
//		getElement().setText("My custom not found class!");
//		return HttpServletResponse.SC_NOT_FOUND;
//	}
}
