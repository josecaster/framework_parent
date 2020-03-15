package software.simple.solutions.framework.core.repository.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.envers.AuditReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import software.simple.solutions.framework.core.constants.Operator;
import software.simple.solutions.framework.core.entities.IMappedSuperClass;
import software.simple.solutions.framework.core.entities.Property;
import software.simple.solutions.framework.core.exceptions.FrameworkException;
import software.simple.solutions.framework.core.pojo.DateInterval;
import software.simple.solutions.framework.core.pojo.LongInterval;
import software.simple.solutions.framework.core.pojo.PagingResult;
import software.simple.solutions.framework.core.pojo.PagingSetting;
import software.simple.solutions.framework.core.pojo.StringInterval;
import software.simple.solutions.framework.core.properties.SystemMessageProperty;
import software.simple.solutions.framework.core.repository.GenericCriteriaBuilder;
import software.simple.solutions.framework.core.repository.IGenericRepository;
import software.simple.solutions.framework.core.repository.JoinLeftBuilder;
import software.simple.solutions.framework.core.util.ColumnSort;
import software.simple.solutions.framework.core.util.SortingHelper;
import software.simple.solutions.framework.core.valueobjects.SuperVO;

@Repository
public class GenericRepository implements IGenericRepository {

	private static final long serialVersionUID = -7098826691200575726L;

	public static Map<Class<?>, Long> toDeleteEntityMap;

	public static final Logger logger = LogManager.getLogger(GenericRepository.class);

	public static final String FIND_BY_SEARCH = "findBySearch";
	public static final String COUNT_FOR_SEARCH = "countForSearch";
	public static final String GET_FOR_LISTING = "getForListing";
	public static final String GET_BY_ID = "getById";
	public static final String GET_BY_CODE = "getByCode";
	public static final String UPDATE_SINGLE = "updateSingle";
	public static final String UPDATE = "update";
	public static final String UPDATE_AFTER_VALIDATE = "updateAfterValidate";
	public static final String DELETEALL = "deleteAll";
	public static final String IS_CODE_UNIQUE = "isCodeUnique";

	@PersistenceContext
	protected EntityManager entityManager;

	@Autowired
	protected AuditReader auditReader;

	public GenericRepository() {
		toDeleteEntityMap = new HashMap<Class<?>, Long>();
	}

	private long atomicColumnAlias = 0L;

	private String createAtomicColumnAlias() {
		if (atomicColumnAlias == Integer.MAX_VALUE) {
			atomicColumnAlias = 0L;
		}
		return "column_" + Math.abs(atomicColumnAlias++);
	}

	private javax.persistence.Query setPagingValues(javax.persistence.Query query, PagingSetting pagingSetting) {
		if (pagingSetting != null) {
			if (pagingSetting.getMaxResult() != 0) {
				query.setMaxResults(pagingSetting.getMaxResult());
				query.setFirstResult(pagingSetting.getStartPosition());
			}
		}
		return query;
	}

	@Override
	public <T> T getById(Class<T> entityType, Long id) throws FrameworkException {
		if (entityType == null || id == null) {
			return null;
		}
		return entityManager.find(entityType, id);
	}

	@Override
	public <T> List<T> createListNativeQuery(String queryString) throws FrameworkException {
		return createListNativeQuery(queryString, null);
	}

	/**
	 * Use this to create a sql criteria query which returns a list of results.
	 * 
	 * @param queryString
	 * @param paramMap
	 * @return
	 * @throws FrameworkException
	 */
	@Override
	public <T> List<T> createListNativeQuery(String queryString, ConcurrentMap<String, Object> paramMap)
			throws FrameworkException {
		return createListNativeQuery(queryString, paramMap, null);
	}

	@Override
	public <T> List<T> createListNativeQuery(String queryString, ConcurrentMap<String, Object> paramMap,
			PagingSetting pagingSetting) throws FrameworkException {
		if (StringUtils.isBlank(queryString)) {
			return null;
		}
		javax.persistence.Query nativeQuery = entityManager.createNativeQuery(queryString);
		if ((paramMap != null) && (!paramMap.isEmpty())) {
			Set<Map.Entry<String, Object>> entrySet = paramMap.entrySet();
			for (Map.Entry<String, Object> entry : entrySet) {
				nativeQuery.setParameter((String) entry.getKey(), entry.getValue());
			}
		}
		setPagingValues(nativeQuery, pagingSetting);
		return nativeQuery.getResultList();
	}

	/**
	 * Use this to create a hql criteria query which returns a list of results.
	 * 
	 * @param queryString
	 * @return
	 * @throws FrameworkException
	 */
	@Override
	public <T> List<T> createListQuery(String queryString) throws FrameworkException {
		return createListQuery(queryString, null);
	}

	@Override
	public <T> List<T> createListQuery(String queryString, ConcurrentMap<String, Object> paramMap)
			throws FrameworkException {
		return createListQuery(queryString, paramMap, null);
	}

	/**
	 * Use this to create a hql criteria query which returns a list of results.
	 * 
	 * @param queryString
	 * @param paramMap
	 * @return
	 * @throws FrameworkException
	 */
	@Override
	public <T> List<T> createListQuery(String queryString, ConcurrentMap<String, Object> paramMap,
			PagingSetting pagingSetting) throws FrameworkException {
		if (StringUtils.isBlank(queryString)) {
			return null;
		}
		javax.persistence.Query query = entityManager.createQuery(queryString);
		if ((paramMap != null) && (!paramMap.isEmpty())) {
			Set<Map.Entry<String, Object>> entrySet = paramMap.entrySet();
			for (Map.Entry<String, Object> entry : entrySet) {
				query.setParameter((String) entry.getKey(), entry.getValue());
			}
		}
		setPagingValues(query, pagingSetting);
		return query.getResultList();
	}

	/**
	 * Use this to create a hql query which returns a single result
	 * 
	 * @param queryString
	 * @return
	 * @return
	 * @throws FrameworkException
	 */
	@Override
	public Object getByQuery(String queryString) throws FrameworkException {
		if (StringUtils.isBlank(queryString)) {
			return null;
		}
		javax.persistence.Query query = entityManager.createQuery(queryString);
		try {
			return query.getSingleResult();
		} catch (NoResultException e) {
			logger.error("No results found. [" + e.getMessage() + "] for query [" + queryString + "]");
		} catch (NonUniqueResultException e) {
			throw new FrameworkException(SystemMessageProperty.NON_UNIQUE_RESULT_FOUND, e);
		}
		return null;
	}

	/**
	 * Use this to create a hql query which returns a single result
	 * 
	 * @param queryString
	 * @param key
	 * @param value
	 * @return
	 * @throws FrameworkException
	 */
	@Override
	public Object getByQuery(String queryString, String key, Object value) throws FrameworkException {
		if (StringUtils.isBlank(queryString) || StringUtils.isBlank(key) || value == null) {
			return null;
		}
		javax.persistence.Query query = entityManager.createQuery(queryString);
		query.setParameter(key, value);
		try {
			return query.getSingleResult();
		} catch (NoResultException e) {
			logger.error("No results found. [" + e.getMessage() + "] for query [" + queryString + "]");
		} catch (NonUniqueResultException e) {
			throw new FrameworkException(SystemMessageProperty.NON_UNIQUE_RESULT_FOUND, e);
		}
		return null;
	}

	/**
	 * Use this to create a hql query which returns a single result
	 * 
	 * @param queryString
	 * @param paramMap
	 * @return
	 * @throws FrameworkException
	 */
	@Override
	public <T> T getByQuery(String queryString, ConcurrentMap<String, Object> paramMap) throws FrameworkException {
		if (StringUtils.isBlank(queryString)) {
			return null;
		}
		javax.persistence.Query query = entityManager.createQuery(queryString);
		if ((paramMap != null) && (!paramMap.isEmpty())) {
			Set<Map.Entry<String, Object>> entrySet = paramMap.entrySet();
			for (Map.Entry<String, Object> entry : entrySet) {
				query.setParameter((String) entry.getKey(), entry.getValue());
			}
		}
		try {
			return (T) query.getSingleResult();
		} catch (NoResultException e) {
			logger.info("No results found. [" + e.getMessage() + "] for query [" + queryString + "]");
		} catch (NonUniqueResultException e) {
			throw new FrameworkException(SystemMessageProperty.NON_UNIQUE_RESULT_FOUND, e);
		}
		return null;
	}

	/**
	 * Use this to create a sql query which returns a single result
	 * 
	 * @param queryString
	 * @return
	 * @throws FrameworkException
	 */
	@Override
	public Object getByNativeQuery(String queryString) throws FrameworkException {
		if (StringUtils.isBlank(queryString)) {
			return null;
		}
		javax.persistence.Query nativeQuery = entityManager.createNativeQuery(queryString);
		try {
			return nativeQuery.getSingleResult();
		} catch (NoResultException e) {
			logger.error("No results found. [" + e.getMessage() + "] for query [" + queryString + "]");
		} catch (NonUniqueResultException e) {
			throw new FrameworkException(SystemMessageProperty.NON_UNIQUE_RESULT_FOUND, e);
		}
		return null;
	}

	/**
	 * Use this to create a sql query which returns a single result
	 * 
	 * @param queryString
	 * @param key
	 * @param value
	 * @return
	 * @throws FrameworkException
	 */
	@Override
	public Object getByNativeQuery(String queryString, String key, Object value) throws FrameworkException {
		if (StringUtils.isBlank(queryString) || StringUtils.isBlank(key) || value == null) {
			return null;
		}
		javax.persistence.Query nativeQuery = entityManager.createNativeQuery(queryString);
		nativeQuery.setParameter(key, value);
		try {
			return nativeQuery.getSingleResult();
		} catch (NoResultException e) {
			logger.error("No results found. [" + e.getMessage() + "] for query [" + queryString + "]");
		} catch (NonUniqueResultException e) {
			throw new FrameworkException(SystemMessageProperty.NON_UNIQUE_RESULT_FOUND, e);
		}
		return null;
	}

	/**
	 * Use this to create a sql query which returns a single result
	 * 
	 * @param queryString
	 * @param paramMap
	 * @return
	 * @throws FrameworkException
	 */
	@Override
	public <T> T getByNativeQuery(String queryString, ConcurrentMap<String, Object> paramMap)
			throws FrameworkException {
		if (StringUtils.isBlank(queryString)) {
			return null;
		}
		javax.persistence.Query nativeQuery = entityManager.createNativeQuery(queryString);
		if ((paramMap != null) && (!paramMap.isEmpty())) {
			Set<Map.Entry<String, Object>> entrySet = paramMap.entrySet();
			for (Map.Entry<String, Object> entry : entrySet) {
				nativeQuery.setParameter((String) entry.getKey(), entry.getValue());
			}
		}
		try {
			return (T) nativeQuery.getSingleResult();
		} catch (NoResultException e) {
			logger.error("No results found. [" + e.getMessage() + "] for query [" + queryString + "]");
		} catch (NonUniqueResultException e) {
			throw new FrameworkException(SystemMessageProperty.NON_UNIQUE_RESULT_FOUND, e);
		}
		return null;
	}

	@Override
	public Integer updateByHql(String queryString, ConcurrentMap<String, Object> paramMap) throws FrameworkException {
		if (StringUtils.isBlank(queryString)) {
			return null;
		}
		javax.persistence.Query query = entityManager.createQuery(queryString);
		if ((paramMap != null) && (!paramMap.isEmpty())) {
			Set<Map.Entry<String, Object>> entrySet = paramMap.entrySet();
			for (Map.Entry<String, Object> entry : entrySet) {
				query.setParameter((String) entry.getKey(), entry.getValue());
			}
		}
		return query.executeUpdate();
	}

	@Override
	public Integer updateBySql(String queryString, ConcurrentMap<String, Object> paramMap) throws FrameworkException {
		if (StringUtils.isBlank(queryString)) {
			return null;
		}
		javax.persistence.Query query = entityManager.createNativeQuery(queryString);
		if ((paramMap != null) && (!paramMap.isEmpty())) {
			Set<Map.Entry<String, Object>> entrySet = paramMap.entrySet();
			for (Map.Entry<String, Object> entry : entrySet) {
				query.setParameter((String) entry.getKey(), entry.getValue());
			}
		}
		return query.executeUpdate();
	}

	@Override
	public Integer deleteByHql(String queryString, ConcurrentMap<String, Object> paramMap) throws FrameworkException {
		return updateByHql(queryString, paramMap);
		// return null;
	}

	@Override
	public Integer deleteBySql(String queryString, ConcurrentMap<String, Object> paramMap) throws FrameworkException {
		return updateBySql(queryString, paramMap);
		// return null;
	}

	/**
	 * Creates a query for dates using sql operators.
	 * 
	 * @param paramMap
	 * @param from
	 * @param to
	 * @param operator
	 * @param column
	 * @param paramPrefix
	 * @return
	 */
	public String buildDateFragment(ConcurrentMap<String, Object> paramMap, LocalDate from, LocalDate to,
			String operator, String column) {
		if (StringUtils.isBlank(column)) {
			return "";
		}
		if (operator == null) {
			operator = Operator.EQ;

		}
		if (Operator.NE.equalsIgnoreCase(operator)) {
			operator = Operator.UE;
		}
		if (from != null) {
			String paramPrefix = createAtomicColumnAlias();
			if ((Operator.BE.equalsIgnoreCase(operator)) && (to != null)) {
				paramMap.put(paramPrefix + "_from", from);
				paramMap.put(paramPrefix + "_to", to);
				return " and " + column + " between :" + paramPrefix + "_from and :" + paramPrefix + "_to ";
			} else {
				paramMap.put(paramPrefix + "_from", from);
				return " and " + column + " " + operator + " :" + paramPrefix + "_from ";
			}
		}
		return "";
	}

	public String buildDateFragment(ConcurrentMap<String, Object> paramMap, DateInterval dateInterval, String column) {
		return buildDateFragment(paramMap, dateInterval.getFrom(), dateInterval.getTo(), dateInterval.getOperator(),
				column);
	}

	public String buildStringFragment(ConcurrentMap<String, Object> paramMap, StringInterval interval, String column) {
		if (interval == null) {
			return "";
		}
		return buildStringFragment(paramMap, interval.getFrom(), interval.getOperator(), column);
	}

	public String buildStringFragment(ConcurrentMap<String, Object> paramMap, String from, String operator,
			String column) {
		if (StringUtils.isBlank(from) || StringUtils.isBlank(column)) {
			return "";
		}
		if (operator == null) {
			operator = Operator.CT;
		}
		if (Operator.NE.equalsIgnoreCase(operator)) {
			operator = Operator.UE;
		}
		if (StringUtils.isNotBlank(from)) {
			String paramPrefix = createAtomicColumnAlias();
			if (Operator.EQ.equalsIgnoreCase(operator)) {
				paramMap.put(paramPrefix + "_from", from);
				return " and lower(" + column + ") = lower(:" + paramPrefix + "_from) ";
			} else if (Operator.CT.equalsIgnoreCase(operator)) {
				paramMap.put(paramPrefix + "_from", ilike(from));
				return " and lower(" + column + ") like :" + paramPrefix + "_from ";
			} else if (Operator.SW.equalsIgnoreCase(operator)) {
				paramMap.put(paramPrefix + "_from", startsWith(from));
				return " and lower(" + column + ") like :" + paramPrefix + "_from ";
			} else if (Operator.EW.equalsIgnoreCase(operator)) {
				paramMap.put(paramPrefix + "_from", endsWith(from));
				return " and lower(" + column + ") like :" + paramPrefix + "_from ";
			} else if (Operator.UE.equalsIgnoreCase(operator)) {
				paramMap.put(paramPrefix + "_from", from);
				return " and lower(" + column + ") != lower(:" + paramPrefix + "_from) ";
			} else if (Operator.NCT.equalsIgnoreCase(operator)) {
				paramMap.put(paramPrefix + "_from", ilike(from));
				return " and lower(" + column + ") not like lower(:" + paramPrefix + "_from) ";
			}
		}
		return "";
	}

	public String buildNumberFragment(ConcurrentMap<String, Object> paramMap, LongInterval interval, String column) {
		return buildNumberFragment(paramMap, interval.getFrom(), interval.getTo(), interval.getOperator(), column);
	}

	/**
	 * Creates a query for numbers using sql operators.
	 * 
	 * @param paramMap
	 * @param from
	 * @param to
	 * @param operator
	 * @param column
	 * @param paramPrefix
	 * @return
	 */
	public String buildNumberFragment(ConcurrentMap<String, Object> paramMap, Number from, Number to, String operator,
			String column) {
		if (StringUtils.isBlank(column)) {
			return "";
		}

		if (operator == null) {
			operator = Operator.EQ;

		}
		if (Operator.NE.equalsIgnoreCase(operator)) {
			operator = Operator.UE;
		}
		if (from != null) {
			String paramPrefix = createAtomicColumnAlias();
			if ((Operator.BE.equalsIgnoreCase(operator)) && (to != null)) {
				paramMap.put(paramPrefix + "_from", from);
				paramMap.put(paramPrefix + "_to", to);
				return " and " + column + " between :" + paramPrefix + "_from and :" + paramPrefix + "_to ";
			} else {
				paramMap.put(paramPrefix + "_from", from);
				return " and " + column + " " + operator + " :" + paramPrefix + "_from ";
			}
		}
		return "";
	}

	public String buildBooleanFragment(ConcurrentMap<String, Object> paramMap, Boolean active, String column) {
		if (active == null) {
			return "";
		}
		String paramPrefix = createAtomicColumnAlias();
		paramMap.put(paramPrefix, active);
		return " and " + column + " =:" + paramPrefix + " ";
	}

	public String buildIdFragment(ConcurrentMap<String, Object> paramMap, Long id, String column) {
		if (id == null) {
			return "";
		}
		String paramPrefix = createAtomicColumnAlias();
		paramMap.put(paramPrefix, id);
		return " and " + column + " =:" + paramPrefix + " ";
	}

	public String buildActiveFragment(String column, Boolean active, ConcurrentMap<String, Object> paramMap) {
		if (active == null) {
			return "";
		}
		String paramPrefix = createAtomicColumnAlias();
		paramMap.put(paramPrefix, active);
		return " and " + column + " =:" + paramPrefix + " ";
	}

	/**
	 * Creates a case insensitive query variable.
	 * 
	 * @param value
	 * @return
	 */
	public String ilike(String value) {
		if (StringUtils.isBlank(value)) {
			return null;
		}
		return "%" + value.trim().toLowerCase() + "%";
	}

	public String startsWith(String value) {
		if (StringUtils.isBlank(value)) {
			return null;
		}
		return value.trim().toLowerCase() + "%";
	}

	public String endsWith(String value) {
		if (StringUtils.isBlank(value)) {
			return null;
		}
		return "%" + value.trim().toLowerCase();
	}

	public String createAndStringCondition(String column, String value, ConcurrentMap<String, Object> paramMap) {
		if (StringUtils.isBlank(value)) {
			return "";
		}
		String paramPrefix = createAtomicColumnAlias();
		String query = " and lower(" + column + ") like :" + paramPrefix;
		paramMap.put(paramPrefix, ilike(value));
		return query;
	}

	public String createOrStringCondition(String column, String value, ConcurrentMap<String, Object> paramMap) {
		if (StringUtils.isBlank(value)) {
			return "";
		}
		String paramPrefix = createAtomicColumnAlias();
		String query = " or lower(" + column + ") like :" + paramPrefix;
		paramMap.put(paramPrefix, ilike(value));
		return query;
	}

	/**
	 * Interface implemented to assist in building jpa criteria queries.
	 * 
	 * @author yusuf
	 *
	 */
	public static abstract interface CriteriaQueryBuilder {
		public abstract void execute(CriteriaQuery<?> paramCriteriaQuery,
				ConcurrentMap<String, Object> paramConcurrentMap);
	}

	/**
	 * This is a tricky method. When using hql make sure to start with an empty
	 * space.
	 * 
	 * @param o
	 * @param paramMap
	 * @return
	 * @throws FrameworkException
	 */
	@Override
	public String createSearchQuery(Object o, ConcurrentMap<String, Object> paramMap, PagingSetting pagingSetting)
			throws FrameworkException {
		return null;
	}

	@Override
	public String createNativeSearchQuery(Object o, ConcurrentMap<String, Object> paramMap, PagingSetting pagingSetting)
			throws FrameworkException {
		return null;
	}

	@Override
	public CriteriaQuery<Object> createGenericCriteriaBuilder(Object o, CriteriaBuilder criteriaBuilder,
			JoinLeftBuilder joinLeftBuilder) throws FrameworkException {
		GenericCriteriaBuilder genericCriteriaBuilder = new GenericCriteriaBuilder();
		genericCriteriaBuilder.setJoinLeftBuilder(joinLeftBuilder);
		SuperVO vo = (SuperVO) o;
		return genericCriteriaBuilder.build(vo.getEntityClass(), o, criteriaBuilder);
	}

	@Override
	public CriteriaQuery<Long> createGenericCountCriteriaBuilder(Object o, CriteriaBuilder criteriaBuilder,
			JoinLeftBuilder joinLeftBuilder) throws FrameworkException {
		GenericCriteriaBuilder genericCriteriaBuilder = new GenericCriteriaBuilder();
		genericCriteriaBuilder.setJoinLeftBuilder(joinLeftBuilder);
		SuperVO vo = (SuperVO) o;
		return genericCriteriaBuilder.buildCount(vo.getEntityClass(), o, criteriaBuilder);
	}

	@Override
	public <T> PagingResult<T> findBySearch(Object o, PagingSetting pagingSetting) throws FrameworkException {

		if (o == null) {
			return null;
		}

		ConcurrentHashMap<String, Object> paramMap = new ConcurrentHashMap<String, Object>();
		String query = createSearchQuery(o, paramMap, pagingSetting);

		PagingResult<T> pagingResult = new PagingResult<T>();
		pagingResult.setPagingSetting(pagingSetting);

		if (query != null) {
			pagingSetting.setQuery(query);

			// if (pagingSetting.isDoCount()) {
			Long count = countForSearch(query, paramMap);
			pagingResult.setCount(count);
			// }

			List<T> result = createListQuery(query, paramMap, pagingSetting);
			pagingResult.setResult(result);

			return pagingResult;
		} else {
			query = createNativeSearchQuery(o, paramMap, pagingSetting);
			if (query != null) {
				pagingSetting.setQuery(query);

				// if (pagingSetting.isDoCount()) {
				Long count = countForNativeSearch(query);
				pagingResult.setCount(count);
				// }

				List<T> result = createListNativeQuery(query, paramMap, pagingSetting);
				pagingResult.setResult(result);

				return pagingResult;
			} else {
				CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
				JoinLeftBuilder joinLeftBuilder = createJoinBuilder(o, criteriaBuilder);
				CriteriaQuery<Object> criteriaQuery = createGenericCriteriaBuilder(o, criteriaBuilder, joinLeftBuilder);
				if (criteriaQuery != null) {
					CriteriaQuery<Long> countQuery = createGenericCountCriteriaBuilder(o, criteriaBuilder,
							joinLeftBuilder);
					Long count = entityManager.createQuery(countQuery).getSingleResult();
					pagingResult.setCount(count);
					TypedQuery<Object> typedQuery = entityManager.createQuery(criteriaQuery);
					setPagingValues(typedQuery, pagingSetting);
					List<T> result = (List<T>) typedQuery.getResultList();
					pagingResult.setResult(result);
					return pagingResult;
				}
			}
		}

		return null;

	}

	@Override
	public JoinLeftBuilder createJoinBuilder(Object o, CriteriaBuilder criteriaBuilder) {
		return null;
	}

	@Override
	public Long countForSearch(String query, ConcurrentHashMap<String, Object> paramMap) throws FrameworkException {
		if (query == null || query.trim().isEmpty()) {
			return null;
		}
		Pattern pattern = Pattern.compile("(?i)\\bfrom\\b");
		Matcher matcher = pattern.matcher(query);
		if (matcher.find()) {
			int start = matcher.start();
			query = query.substring(start);
			query = "select count(*) " + query;
			Long count = getByQuery(query, paramMap);
			return count == null ? 0 : count;
		}
		return null;
	}

	@Override
	public Long countForNativeSearch(String query) throws FrameworkException {
		if (query == null || query.trim().isEmpty()) {
			return null;
		}
		ConcurrentMap<String, Object> paramMap = new ConcurrentHashMap<String, Object>();
		Pattern pattern = Pattern.compile("\\{(.*?)\\}");
		Matcher matcher = pattern.matcher(query);
		while (matcher.find()) {
			String group = matcher.group(1);
			int indexOf = group.indexOf(".");
			if (indexOf > -1) {
				group = group.substring(0, indexOf);
				paramMap.remove(group);
			}
		}

		query = "select count(*) from (" + query.replaceAll("\\{", "").replaceAll("\\}", "") + ")";
		Long count = getByNativeQuery(query, paramMap);
		return (count == null ? 0 : count);
	}

	public String getOrderBy(SortingHelper sortingHelper, String defaultOrder, Map<String, String> orders) {
		if (sortingHelper != null && sortingHelper.getColumnsSort() != null && orders != null && !orders.isEmpty()
				&& sortingHelper.getColumnsSort() != null && !sortingHelper.getColumnsSort().isEmpty()) {
			List<ColumnSort> columnsSort = sortingHelper.getColumnsSort();
			String orderBy = " order by ";
			for (int i = 0; i < columnsSort.size(); i++) {
				orderBy += columnsSort.get(i).getColumnId() + " " + columnsSort.get(i).getSortDirection();
				if (i != (columnsSort.size() - 1)) {
					orderBy += ", ";
				}
			}
			return orderBy;
		} else {
			String sort = " desc";
			if (defaultOrder.endsWith(" desc") || defaultOrder.endsWith(" asc")) {
				sort = "";
			}
			return " order by " + defaultOrder + sort;
		}
	}

	@Override
	public <T, R> List<R> getForListing(Class<T> cl) throws FrameworkException {
		return getForListing(cl, null);
	}

	@Override
	public <T, R> List<R> getForListing(Class<T> cl, Boolean active) throws FrameworkException {
		return getForListing(cl, null, active);
	}

	@Override
	public <T, R> List<R> getForListing(Class<T> cl, List<Long> ids, Boolean active) throws FrameworkException {
		ConcurrentMap<String, Object> paramMap = createParamMap();

		String query = "select new software.simple.solutions.framework.core.pojo.ComboItem(id,code,name) from "
				+ cl.getSimpleName() + " where 1=1 ";
		if (ids != null && !ids.isEmpty()) {
			query += " or id in (:ids) ";
			paramMap.put("ids", ids);
		}
		if (active != null) {
			query += " and active=:active ";
			paramMap.put("active", active);
		}

		query += " order by code ";

		return createListQuery(query, paramMap);
	}

	@Override
	public Boolean isCodeUnique(Class<?> cl, String code) throws FrameworkException {
		return isCodeUnique(cl, code, null);
	}

	@Override
	public Boolean isCodeUnique(Class<?> cl, String code, Long id) throws FrameworkException {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<?> criteria = builder.createQuery(cl);
		Root<?> from = criteria.from(cl);

		Predicate equal = builder.equal(from.get("code"), code);
		Predicate notEqual = builder.notEqual(from.get("id"), id);
		List<Predicate> predicates = new ArrayList<Predicate>();
		predicates.add(equal);
		if (id != null) {
			predicates.add(notEqual);
		}

		criteria.where(predicates.toArray(new Predicate[predicates.size()]));
		criteria.select((Selection) builder.count(from));
		Long count = (Long) entityManager.createQuery(criteria).getSingleResult();
		return (count == null || count.compareTo(0L) == 0);
	}

	@Override
	public <T> T updateSingle(T entity, boolean newEntity) throws FrameworkException {
		if (entity == null) {
			return null;
		}

		try {
			if (newEntity) {
				entityManager.persist(entity);
			} else {
				entity = entityManager.merge(entity);
			}

		} catch (EntityExistsException e) {
			throw new FrameworkException(SystemMessageProperty.FAILED_TO_PERSIST, e);
		}

		return entity;
	}

	public ConcurrentMap<String, Object> createParamMap() {
		return new ConcurrentHashMap<String, Object>();
	}

	@Override
	public <T> T get(Class<T> entityType, Long id) throws FrameworkException {
		return getById(entityType, id);
	}

	@Override
	public <T> Integer deleteAll(Class<T> cl, List<Long> ids) throws FrameworkException {
		for (Long id : ids) {
			T t = get(cl, id);
			entityManager.remove(t);
		}
		return ids.size();
	}

	@Override
	public <T> Integer deleteAll(List<T> entities) throws FrameworkException {
		for (T t : entities) {
			t = entityManager.contains(t) ? t : entityManager.merge(t);
			entityManager.remove(t);
		}
		return entities.size();
	}

	@Override
	public <T> T getByCode(Class<T> cl, String code) throws FrameworkException {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<?> criteria = builder.createQuery(cl);
		Root<?> from = criteria.from(cl);
		criteria.where(builder.equal(from.get("code"), code));
		try {
			return (T) entityManager.createQuery(criteria).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	@Override
	public <T> T restore(Class<T> cl, Long id) throws FrameworkException {
		T t = get(cl, id);
		IMappedSuperClass mappedSuperClass = (IMappedSuperClass) t;
		return entityManager.merge(t);
	}

	@Override
	public Property getBypropertyKey(Class<Property> class1, String key) throws FrameworkException {
		ConcurrentMap<String, Object> paramMap = createParamMap();
		String query = "from Property where key=:key";
		paramMap.put("key", key);
		return getByQuery(query, paramMap);
	}

}
