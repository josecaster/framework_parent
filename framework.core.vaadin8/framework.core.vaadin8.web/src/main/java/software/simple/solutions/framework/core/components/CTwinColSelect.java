package software.simple.solutions.framework.core.components;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.vaadin.data.provider.Query;
import com.vaadin.ui.TwinColSelect;

import software.simple.solutions.framework.core.entities.Privilege;
import software.simple.solutions.framework.core.pojo.ComboItem;
import software.simple.solutions.framework.core.util.PropertyResolver;

public class CTwinColSelect extends TwinColSelect<ComboItem> {

	private static final long serialVersionUID = -4573724116863038490L;

	public void setLongValues(Set<Long> values) {
		if (values != null) {
			Set<ComboItem> toBeSet = new HashSet<ComboItem>();
			for (Long value : values) {
				Optional<ComboItem> optional = getDataProvider().fetch(new Query<>()).collect(Collectors.toList())
						.stream().filter(p -> ((Comparable) p.getId()).compareTo((Comparable) value) == 0).findFirst();
				if (optional.isPresent()) {
					toBeSet.add(optional.get());
				}
			}
			setValue(toBeSet);
		}
	}

	public void setValues(List<Privilege> values) {
		if (values != null) {
			List<ComboItem> items = values.stream().map(
					p -> new ComboItem(p.getId(), p.getCode(), PropertyResolver.getPropertyValueByLocale(p.getKey())))
					.collect(Collectors.toList());
			setItems(items);
		}
	}

}
