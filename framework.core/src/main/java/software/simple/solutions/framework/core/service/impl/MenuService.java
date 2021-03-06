package software.simple.solutions.framework.core.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import software.simple.solutions.framework.core.annotations.ServiceRepository;
import software.simple.solutions.framework.core.entities.Menu;
import software.simple.solutions.framework.core.entities.View;
import software.simple.solutions.framework.core.exceptions.FrameworkException;
import software.simple.solutions.framework.core.repository.IMenuRepository;
import software.simple.solutions.framework.core.service.IMenuService;
import software.simple.solutions.framework.core.valueobjects.MenuVO;
import software.simple.solutions.framework.core.valueobjects.SuperVO;

@Transactional(propagation=Propagation.REQUIRED, rollbackFor = Exception.class)
@Service
@ServiceRepository(claz = IMenuRepository.class)
public class MenuService extends SuperService implements IMenuService {

	@Autowired
	private IMenuRepository menuRepository;

	@Override
	public <T, R extends SuperVO> T updateSingle(R valueObject) throws FrameworkException {

		MenuVO vo = (MenuVO) valueObject;

		Menu menu = null;
		if (vo.isNew()) {
			menu = new Menu();
		} else {
			menu = get(Menu.class, vo.getId());
		}
		menu.setCode(vo.getCode().toUpperCase());
		menu.setName(vo.getName());
		menu.setActive(vo.getActive());
		menu.setType(vo.getType());
		menu.setView(get(View.class, vo.getViewId()));
		menu.setKey(vo.getKey());

		createKeyifNotExists(vo.getKey());

		return (T) saveOrUpdate(menu, vo.isNew());
	}

	@Override
	public List<Menu> listChildMenus(Long parentId) throws FrameworkException {
		return menuRepository.listChildMenus(parentId);
	}

	@Override
	public List<Menu> listParentMenus() throws FrameworkException {
		return menuRepository.listParentMenus();
	}

	@Override
	public List<Menu> findAuthorizedMenus(Long roleId) throws FrameworkException {
		return menuRepository.findAuthorizedMenus(roleId);
	}
	
	@Override
	public List<Menu> findAuthorizedMenusByUser(Long applicationUserId) throws FrameworkException{
		return menuRepository.findAuthorizedMenusByUser(applicationUserId);
	}

	@Override
	public List<Menu> findTabMenus(Long parentMenuId, Long roleId) throws FrameworkException {
		return menuRepository.findTabMenus(parentMenuId, roleId);
	}

	@Override
	public Menu getLookUpByViewClass(Long roleId, String name) throws FrameworkException {
		return menuRepository.getLookUpByViewClass(roleId, name);
	}

	@Override
	public List<Menu> findAuthorizedMenusByType(Long roleId, List<Long> types) throws FrameworkException {
		return menuRepository.findAuthorizedMenusByType(roleId, types);
	}

	@Override
	public List<Menu> getPossibleHomeViews() throws FrameworkException {
		return menuRepository.getPossibleHomeViews();
	}

	@Override
	public Boolean doesUserHaveAccess(Long applicationUserId, Long roleId, Long menuId) throws FrameworkException {
		return menuRepository.doesUserHaveAccess(applicationUserId, roleId, menuId);
	}

}
