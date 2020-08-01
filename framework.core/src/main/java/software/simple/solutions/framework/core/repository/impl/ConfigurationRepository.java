package software.simple.solutions.framework.core.repository.impl;

import java.util.List;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Repository;

import software.simple.solutions.framework.core.entities.Configuration;
import software.simple.solutions.framework.core.exceptions.FrameworkException;
import software.simple.solutions.framework.core.pojo.PagingSetting;
import software.simple.solutions.framework.core.repository.IConfigurationRepository;
import software.simple.solutions.framework.core.valueobjects.ConfigurationVO;

@Repository
public class ConfigurationRepository extends GenericRepository implements IConfigurationRepository {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8977568420353260423L;

	@Override
	public List<Configuration> getConfigurations(List<String> codes) throws FrameworkException {
		String query = "from Configuration where code in :codes";
		ConcurrentMap<String, Object> paramMap = createParamMap();
		paramMap.put("codes", codes);
		return createListQuery(query, paramMap);
	}

	@Override
	public String createSearchQuery(Object o, ConcurrentMap<String, Object> paramMap, PagingSetting pagingSetting)
			throws FrameworkException {
		ConfigurationVO readVO = (ConfigurationVO) o;
		String query = "from Configuration where 1=1 ";
		if (readVO.getCodes() != null) {
			query += "and code in :codes";
			paramMap.put("codes", readVO.getCodes());
		}
		return query;
	}

}
