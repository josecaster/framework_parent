package software.simple.solutions.framework.core.service.facade;

import java.util.List;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.flow.component.UI;

import software.simple.solutions.framework.core.constants.Constants;
import software.simple.solutions.framework.core.entities.ApplicationUser;
import software.simple.solutions.framework.core.exceptions.FrameworkException;
import software.simple.solutions.framework.core.pojo.PagingResult;
import software.simple.solutions.framework.core.pojo.PagingSetting;
import software.simple.solutions.framework.core.service.ISuperService;
import software.simple.solutions.framework.core.util.ContextProvider;
import software.simple.solutions.framework.core.util.SessionHolder;
import software.simple.solutions.framework.core.valueobjects.SuperVO;

public class SuperServiceFacade<S extends ISuperService> implements ISuperService {

	public static final long serialVersionUID = 2921037060748131115L;

	protected S service;

	public SuperServiceFacade(UI ui, Class<? extends ISuperService> serviceClass) {
		if (ui != null) {
			if (SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken) {
				SessionHolder sessionHolder = (SessionHolder) ui.getSession().getAttribute(Constants.SESSION_HOLDER);
				if (sessionHolder != null) {
					ApplicationUser applicationUser = sessionHolder.getApplicationUser();
					if (applicationUser != null) {
						UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(
								applicationUser.getUsername(), sessionHolder.getPassword());
						AuthenticationManager authenticationManager = ContextProvider
								.getBean(AuthenticationManager.class);
						Authentication auth = authenticationManager.authenticate(authReq);
						SecurityContext sc = SecurityContextHolder.getContext();
						sc.setAuthentication(auth);
					}
				}
			}
		}
		System.out.println(serviceClass);
		service = ContextProvider.getBean(serviceClass);
	}

	public static SuperServiceFacade<? extends ISuperService> get(UI ui) {
		return new SuperServiceFacade<>(ui, ISuperService.class);
	}

	@Override
	public <T> T get(Class<T> cl, Long id) throws FrameworkException {
		return service.get(cl, id);
	}

	@Override
	public <T> T getById(Class<T> cl, Long id) throws FrameworkException {
		return service.getById(cl, id);
	}

	@Override
	public <T> T getByCode(Class<T> cl, String code) throws FrameworkException {
		return service.getByCode(cl, code);
	}

	@Override
	public <T, R> List<R> getForListing(Class<T> cl, List<Long> ids, Boolean active) throws FrameworkException {
		return service.getForListing(cl, ids, active);
	}

	@Override
	public <T, R> List<R> getForListing(Class<T> cl, Boolean active) throws FrameworkException {
		return service.getForListing(cl);
	}

	@Override
	public <T, R> List<R> getForListing(Class<T> cl) throws FrameworkException {
		return service.getForListing(cl);
	}

	@Override
	public <T> PagingResult<T> findBySearch(Object o, PagingSetting pagingSetting) throws FrameworkException {
		return service.findBySearch(o, pagingSetting);
	}

	@Override
	public <T> Boolean isCodeUnique(Class<T> cl, String code) throws FrameworkException {
		return service.isCodeUnique(cl, code);
	}

	@Override
	public <T> Boolean isCodeUnique(Class<T> cl, String code, Long id) throws FrameworkException {
		return service.isCodeUnique(cl, code, id);
	}

	@Override
	public <T, R extends SuperVO> T updateSingle(R entityVO) throws FrameworkException {
		return service.updateSingle(entityVO);
	}

	@Override
	public <T, R extends SuperVO> List<T> update(List<R> entityVOs) throws FrameworkException {
		return service.update(entityVOs);
	}

	@Override
	public <T> Integer delete(Class<T> cl, List<Long> entities) throws FrameworkException {
		return service.delete(cl, entities);
	}

	@Override
	public <T> Integer delete(Class<T> cl, Long id) throws FrameworkException {
		return service.delete(cl, id);
	}

	@Override
	public <T> Integer delete(List<T> entities) throws FrameworkException {
		return service.delete(entities);
	}

	@Override
	public Object updateContent(ContentUpdate contentUpdate) throws FrameworkException {
		return service.updateContent(contentUpdate);
	}

	@Override
	public <T> T saveOrUpdate(T entity, boolean newEntity) throws FrameworkException {
		return service.saveOrUpdate(entity, newEntity);
	}

	@Override
	public <T> T restore(Class<T> cl, Long id) throws FrameworkException {
		return service.restore(cl, id);
	}

}
