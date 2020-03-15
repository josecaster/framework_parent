package software.simple.solutions.framework.core.repository;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.springframework.data.repository.NoRepositoryBean;

import software.simple.solutions.framework.core.entities.Property;
import software.simple.solutions.framework.core.exceptions.FrameworkException;
import software.simple.solutions.framework.core.pojo.PagingResult;
import software.simple.solutions.framework.core.pojo.PagingSetting;

@NoRepositoryBean
public interface IGenericRepository extends Serializable {

	String createSearchQuery(Object o, ConcurrentMap<String, Object> paramMap, PagingSetting pagingSetting)
			throws FrameworkException;

	String createNativeSearchQuery(Object o, ConcurrentMap<String, Object> paramMap, PagingSetting pagingSetting)
			throws FrameworkException;

	CriteriaQuery<Object> createGenericCriteriaBuilder(Object o, CriteriaBuilder criteriaBuilder,
			JoinLeftBuilder joinLeftBuilder) throws FrameworkException;

	<T> T getById(Class<T> entityType, Long id) throws FrameworkException;

	<T> T get(Class<T> entityType, Long id) throws FrameworkException;

	/*
	 * Native list queries
	 */
	<T> List<T> createListNativeQuery(String queryString) throws FrameworkException;

	<T> List<T> createListNativeQuery(String queryString, ConcurrentMap<String, Object> paramMap)
			throws FrameworkException;

	<T> List<T> createListNativeQuery(String queryString, ConcurrentMap<String, Object> paramMap,
			PagingSetting pagingSetting) throws FrameworkException;
	/*
	 * End of Native list queries
	 */

	/*
	 * List queries
	 */
	<T> List<T> createListQuery(String queryString) throws FrameworkException;

	<T> List<T> createListQuery(String queryString, ConcurrentMap<String, Object> paramMap) throws FrameworkException;

	<T> List<T> createListQuery(String queryString, ConcurrentMap<String, Object> paramMap, PagingSetting pagingSetting)
			throws FrameworkException;
	/*
	 * End of List queries
	 */

	<T> T getByQuery(String queryString) throws FrameworkException;

	<T> T getByQuery(String queryString, String key, Object value) throws FrameworkException;

	<T> T getByQuery(String queryString, ConcurrentMap<String, Object> paramMap) throws FrameworkException;

	<T> T getByNativeQuery(String queryString) throws FrameworkException;

	<T> T getByNativeQuery(String queryString, String key, Object value) throws FrameworkException;

	<T> T getByNativeQuery(String queryString, ConcurrentMap<String, Object> paramMap) throws FrameworkException;

	Integer updateByHql(String queryString, ConcurrentMap<String, Object> paramMap) throws FrameworkException;

	Integer deleteByHql(String queryString, ConcurrentMap<String, Object> paramMap) throws FrameworkException;

	<T> PagingResult<T> findBySearch(Object o, PagingSetting pagingSetting) throws FrameworkException;

	Long countForNativeSearch(String query) throws FrameworkException;

	<T, R> List<R> getForListing(Class<T> cl) throws FrameworkException;

	<T, R> List<R> getForListing(Class<T> cl, Boolean active) throws FrameworkException;

	<T, R> List<R> getForListing(Class<T> cl, List<Long> ids, Boolean active) throws FrameworkException;

	Boolean isCodeUnique(Class<?> cl, String code) throws FrameworkException;

	Boolean isCodeUnique(Class<?> cl, String code, Long id) throws FrameworkException;

	<T> T updateSingle(T entity, boolean newEntity) throws FrameworkException;

	<T> Integer deleteAll(Class<T> cl, List<Long> ids) throws FrameworkException;

	<T> Integer deleteAll(List<T> entities) throws FrameworkException;

	<T> T getByCode(Class<T> cl, String code) throws FrameworkException;

	CriteriaQuery<Long> createGenericCountCriteriaBuilder(Object o, CriteriaBuilder criteriaBuilder,
			JoinLeftBuilder joinLeftBuilder) throws FrameworkException;

	<T> T restore(Class<T> cl, Long id) throws FrameworkException;

	Integer updateBySql(String queryString, ConcurrentMap<String, Object> paramMap) throws FrameworkException;

	Integer deleteBySql(String queryString, ConcurrentMap<String, Object> paramMap) throws FrameworkException;

	Property getBypropertyKey(Class<Property> class1, String key) throws FrameworkException;

	JoinLeftBuilder createJoinBuilder(Object o, CriteriaBuilder criteriaBuilder) throws FrameworkException;

	Long countForSearch(String query, ConcurrentHashMap<String, Object> map) throws FrameworkException;

}
