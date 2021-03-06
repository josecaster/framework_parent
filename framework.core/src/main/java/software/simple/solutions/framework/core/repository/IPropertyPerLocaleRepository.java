package software.simple.solutions.framework.core.repository;

import java.util.List;

import software.simple.solutions.framework.core.entities.PropertyPerLocale;
import software.simple.solutions.framework.core.exceptions.FrameworkException;

public interface IPropertyPerLocaleRepository extends IGenericRepository {

	public List<PropertyPerLocale> findAll() throws FrameworkException;

}
