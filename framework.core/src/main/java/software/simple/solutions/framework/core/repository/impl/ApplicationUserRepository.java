package software.simple.solutions.framework.core.repository.impl;

import java.util.List;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import software.simple.solutions.framework.core.entities.ApplicationUser;
import software.simple.solutions.framework.core.exceptions.FrameworkException;
import software.simple.solutions.framework.core.pojo.PagingSetting;
import software.simple.solutions.framework.core.repository.IApplicationUserRepository;

@Repository
public class ApplicationUserRepository extends GenericRepository implements IApplicationUserRepository {

	// @Override
	// public String createSearchQuery(Object o, ConcurrentMap<String, Object>
	// paramMap, PagingSetting pagingSetting)
	// throws FrameworkException {
	// ApplicationUserVO vo = (ApplicationUserVO) o;
	// String query = "select entity from ApplicationUser entity "
	// + "left join Person person on person.id=entity.person.id " + "where 1=1
	// ";
	//
	// query += createAndStringCondition("entity.username", vo.getUsername(),
	// paramMap);
	//
	// query += getOrderBy(vo.getSortingHelper(), "entity.username",
	// OrderMapBuilder.build("1", "entity.username", "2", "person.lastName"));
	// return query;
	// }

	@Override
	public ApplicationUser getByUserName(String username) throws FrameworkException {
		if (StringUtils.isBlank(username)) {
			return null;
		}
		ConcurrentMap<String, Object> paramMap = createParamMap();
		String query = "from ApplicationUser where lower(username)=lower(:username) ";
		paramMap.put("username", username);
		return getByQuery(query, paramMap);
	}

	@Override
	public <T> Integer deleteAll(List<T> entities) throws FrameworkException {
		ConcurrentMap<String, Object> paramMap = createParamMap();
		for (T t : entities) {
			ApplicationUser applicationUser = (ApplicationUser) t;
			paramMap.clear();
			String deleteRoles = "delete from UserRole where applicationUser.id=:userId";
			paramMap.put("userId", applicationUser.getId());
			updateByHql(deleteRoles, paramMap);

			t = entityManager.contains(t) ? t : entityManager.merge(t);
			// ((IMappedSuperClass)
			// t).setUpdatedByUser(get(ApplicationUser.class, userId));
			entityManager.remove(t);
		}
		return entities.size();
	}
	
	@Override
	public String createSearchQuery(Object o, ConcurrentMap<String, Object> paramMap, PagingSetting pagingSetting)
			throws FrameworkException {
		return "from ApplicationUser";
	}

	@Override
	public List<ApplicationUser> findAll() throws FrameworkException {
		String query = "from ApplicationUser";
		return createListQuery(query);
	}

	@Override
	public void removeOauthAccessToken(String authenticationId) throws FrameworkException {
		String delete = "delete from oauth_access_token where authentication_id = :authentication_id";
		ConcurrentMap<String, Object> paramMap = createParamMap();
		paramMap.put("authentication_id", authenticationId);
		deleteBySql(delete, paramMap);
	}

}
