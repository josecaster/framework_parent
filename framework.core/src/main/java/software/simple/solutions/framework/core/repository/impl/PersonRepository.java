package software.simple.solutions.framework.core.repository.impl;

import java.util.List;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Repository;

import software.simple.solutions.framework.core.exceptions.FrameworkException;
import software.simple.solutions.framework.core.repository.IPersonRepository;

@Repository
public class PersonRepository extends GenericRepository implements IPersonRepository {

//	@Override
//	public String createSearchQuery(Object o, ConcurrentMap<String, Object> paramMap, PagingSetting pagingSetting)
//			throws FrameworkException {
//		PersonVO vo = (PersonVO) o;
//		String query = "select entity from Person entity where 1=1 ";
//
//		query += createAndStringCondition("entity.firstName", vo.getFirstName(), paramMap);
//		query += createAndStringCondition("entity.lastName", vo.getLastName(), paramMap);
//
//		query += getOrderBy(vo.getSortingHelper(), "entity.firstName,entity.lastName",
//				OrderMapBuilder.build("1", "entity.firstName", "2", "entity.lastName"));
//		return query;
//	}
	
	@Override
	public <T, R> List<R> getForListing(Class<T> cl, List<Long> ids, Boolean active) throws FrameworkException {
		ConcurrentMap<String, Object> paramMap = createParamMap();

		String query = "select new software.simple.solutions.framework.core.pojo.ComboItem(id,middleName,firstName) from "
				+ cl.getSimpleName() + " where 1=1 ";
		if (active != null) {
			query += " and active=:active ";
			paramMap.put("active", active);
		}

		query += " order by middleName,firstName ";

		return createListQuery(query, paramMap);
	}

}
