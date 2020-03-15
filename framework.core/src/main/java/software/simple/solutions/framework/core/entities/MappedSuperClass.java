package software.simple.solutions.framework.core.entities;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;

@MappedSuperclass
public abstract class MappedSuperClass implements Serializable, IMappedSuperClass {

	private static final long serialVersionUID = 3254472694918646702L;
	public static final String ID_ = "ID_";
	public static final String ACTIVE_ = "ACTIVE_";
	public static final String UPDATED_DATE_ = "UPDATED_DATE_";
	public static final String UPDATED_BY_ = "UPDATED_BY_";
	public static final String KEY_ = "KEY_";

	public static final String CODE_ = "CODE_";
	public static final String NAME_ = "NAME_";
	public static final String DESCRIPTION_ = "DESCRIPTION_";

	public static final String VAR_ID = "id";

	protected MappedSuperClass() {
		uuId = UUID.randomUUID().toString();
	}

	@Transient
	protected String uuId;

	// @Column(name = UPDATED_DATE_)
	@Transient
	private LocalDateTime updatedDate;

	// @ManyToOne
	// @JoinColumn(name = UPDATED_BY_, referencedColumnName = ID_)
	@Transient
	private ApplicationUser updatedByUser;

	// @Column(name = KEY_)
	// private String key;

	public LocalDateTime getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(LocalDateTime updatedDate) {
		this.updatedDate = updatedDate;
	}

	public ApplicationUser getUpdatedByUser() {
		return updatedByUser;
	}

	public void setUpdatedByUser(ApplicationUser updatedByUser) {
		this.updatedByUser = updatedByUser;
	}

	public String getTableName(Object entity) {
		Class<?> cl = entity.getClass();
		return getTableName(cl);
	}

	public String getTableName(Class<?> cl) {
		Table table = cl.getAnnotation(Table.class);
		String tableName = table.name();
		return tableName.toLowerCase().substring(0, tableName.length() - 1).replace('_', '.');
	}

	public String getSqlTableName(Class<?> cl) {
		Table table = cl.getAnnotation(Table.class);
		return table.name();
	}

	public String getCaption() {
		return null;
	}

	@PrePersist
	public void prePersist() {
		Boolean active = getActive();
		if (active == null) {
			setActive(true);
		}
		setUpdatedDate(LocalDateTime.now());
		customPrePersist();
	}

	protected void customPrePersist() {
		return;
	}

	@PreUpdate
	public void preUpdate() {
		Boolean active = getActive();
		if (active == null) {
			setActive(true);
		}
		setUpdatedDate(LocalDateTime.now());
		customPreUpdate();
	}

	protected void customPreUpdate() {
		return;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uuId == null) ? 0 : uuId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MappedSuperClass other = (MappedSuperClass) obj;
		if (uuId == null) {
			if (other.uuId != null)
				return false;
		} else if (!uuId.equals(other.uuId))
			return false;
		return true;
	}

	public String getUuId() {
		return uuId;
	}

	public void setUuId(String uuId) {
		this.uuId = uuId;
	}
	
	

}
