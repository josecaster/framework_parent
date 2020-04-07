package software.simple.solutions.framework.core.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import software.simple.solutions.framework.core.annotations.FilterFieldProperty;
import software.simple.solutions.framework.core.constants.CxodeTables;
import software.simple.solutions.framework.core.properties.RoleProperty;

@Audited
@AuditOverride(forClass = MappedSuperClass.class)
@Entity
@Table(name = CxodeTables.ROLE.NAME)
@DynamicUpdate(value = true)
@SelectBeforeUpdate(value = true)
public class Role extends MappedSuperClass {

	private static final long serialVersionUID = -1706327040874057627L;

	@Id
	@TableGenerator(name = "table", table = "sequences_", pkColumnName = "PK_NAME", valueColumnName = "PK_VALUE", initialValue = 1000000)
	@GeneratedValue(generator = "table", strategy = GenerationType.TABLE)
	@FilterFieldProperty(fieldProperty = RoleProperty.ID)
	@Column(name = ID_)
	private Long id;

	@FilterFieldProperty(fieldProperty = RoleProperty.CODE)
	@Column(name = CxodeTables.ROLE.COLUMNS.CODE)
	private String code;

	@FilterFieldProperty(fieldProperty = RoleProperty.NAME)
	@Column(name = CxodeTables.ROLE.COLUMNS.NAME)
	private String name;

	@FilterFieldProperty(fieldProperty = RoleProperty.DESCRIPTION)
	@Column(name = CxodeTables.ROLE.COLUMNS.DESCRIPTION)
	private String description;

	@FilterFieldProperty(fieldProperty = RoleProperty.ACTIVE)
	@Column(name = CxodeTables.ROLE.COLUMNS.ACTIVE)
	private Boolean active;

	@FilterFieldProperty(fieldProperty = RoleProperty.ACTIVE)
	@ManyToOne
	@JoinColumn(name = CxodeTables.ROLE.COLUMNS.ROLE_CATEGORY_ID_)
	private RoleCategory roleCategory;

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public RoleCategory getRoleCategory() {
		return roleCategory;
	}

	public void setRoleCategory(RoleCategory roleCategory) {
		this.roleCategory = roleCategory;
	}

	@Override
	public String getCaption() {
		StringBuilder caption = new StringBuilder();
		if (code != null && code.length() > 0) {
			caption.append("[").append(code).append("]");
		}
		if (name != null && name.length() > 0) {
			caption.append(" ").append(name);
		}

		return caption.toString();
	}

	@Override
	public String toString() {
		return name;
	}

}
