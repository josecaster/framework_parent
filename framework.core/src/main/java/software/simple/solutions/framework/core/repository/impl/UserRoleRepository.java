package software.simple.solutions.framework.core.repository.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Repository;

import software.simple.solutions.framework.core.entities.Role;
import software.simple.solutions.framework.core.entities.UserRole;
import software.simple.solutions.framework.core.exceptions.FrameworkException;
import software.simple.solutions.framework.core.repository.IUserRoleRepository;

@Repository
public class UserRoleRepository extends GenericRepository implements IUserRoleRepository {

	public static final String FIND_ROLES_BY_USER = "findRolesByUser";

	@Override
	public List<Role> findRolesByUser(Long id) throws FrameworkException {
		if (id == null) {
			return new ArrayList<Role>();
		}
		ConcurrentMap<String, Object> paramMap = createParamMap();
		String query = "select ur.role from UserRole ur where ur.applicationUser.id=:userId ";
		paramMap.put("userId", id);
		query += " order by ur.applicationUser.username ";

		return createListQuery(query, paramMap);
	}

	@Override
	public UserRole getByUserAndRole(Long userId, Long roleId) throws FrameworkException {
		if (userId == null || roleId == null) {
			return null;
		}
		ConcurrentMap<String, Object> paramMap = createParamMap();
		String query = "select ur from UserRole ur where ur.applicationUser.id=:userId and ur.role.id=:roleId order by ur.role.roleCategory.id";
		paramMap.put("userId", userId);
		paramMap.put("roleId", roleId);
		return getByQuery(query, paramMap);
	}

}
