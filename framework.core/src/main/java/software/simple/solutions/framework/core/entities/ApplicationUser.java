package software.simple.solutions.framework.core.entities;

import java.time.LocalDateTime;

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
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.SelectBeforeUpdate;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import software.simple.solutions.framework.core.annotations.FilterFieldProperties;
import software.simple.solutions.framework.core.annotations.FilterFieldProperty;
import software.simple.solutions.framework.core.constants.CxodeTables;
import software.simple.solutions.framework.core.properties.ApplicationUserProperty;
import software.simple.solutions.framework.core.properties.PersonProperty;

@Audited
@AuditOverride(forClass = MappedSuperClass.class)
@Entity
@Table(name = CxodeTables.APPLICATION_USER.NAME)
@DynamicUpdate(value = true)
@SelectBeforeUpdate(value = true)
public class ApplicationUser extends MappedSuperClass {

	private static final long serialVersionUID = -7662467214498366631L;

	@Id
	@TableGenerator(name = "table", table = "sequences_", pkColumnName = "PK_NAME", valueColumnName = "PK_VALUE", initialValue = 1000000)
	@GeneratedValue(generator = "table", strategy = GenerationType.TABLE)
	@FilterFieldProperty(fieldProperty = ApplicationUserProperty.ID)
	@Column(name = ID_)
	private Long id;

	@FilterFieldProperty(fieldProperty = ApplicationUserProperty.USERNAME)
	@Column(name = CxodeTables.APPLICATION_USER.COLUMNS.USERNAME)
	public String username;

	@Column(name = CxodeTables.APPLICATION_USER.COLUMNS.PASSWORD)
	private String password;

	@Column(name = CxodeTables.APPLICATION_USER.COLUMNS.FORCE_CHANGE_PASSWORD)
	private Boolean resetPassword;

	@FilterFieldProperty(fieldProperty = ApplicationUserProperty.PASSWORD_CHANGE_DATE)
	@Column(name = CxodeTables.APPLICATION_USER.COLUMNS.PASSWORD_CHANGE_DATE)
	private LocalDateTime passwordChangeDate;

	@FilterFieldProperties(fieldProperties = { @FilterFieldProperty(fieldProperty = PersonProperty.FIRST_NAME),
			@FilterFieldProperty(fieldProperty = PersonProperty.LAST_NAME) })
	@NotFound(action=NotFoundAction.IGNORE)
	@ManyToOne
	@JoinColumn(name = CxodeTables.APPLICATION_USER.COLUMNS.PERSON_ID, referencedColumnName = CxodeTables.PERSON.COLUMNS.ID)
	private Person person;

	@Column(name = CxodeTables.APPLICATION_USER.COLUMNS.ALIAS)
	private String alias;

	@FilterFieldProperty(fieldProperty = ApplicationUserProperty.ACTIVE)
	@Column(name = CxodeTables.APPLICATION_USER.COLUMNS.ACTIVE)
	private Boolean active;

	@FilterFieldProperty(fieldProperty = ApplicationUserProperty.USE_LDAP)
	@Column(name = CxodeTables.APPLICATION_USER.COLUMNS.USE_LDAP_)
	private Boolean useLdap;

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getResetPassword() {
		return resetPassword;
	}

	public void setResetPassword(Boolean resetPassword) {
		this.resetPassword = resetPassword;
	}

	public LocalDateTime getPasswordChangeDate() {
		return passwordChangeDate;
	}

	public void setPasswordChangeDate(LocalDateTime passwordChangeDate) {
		this.passwordChangeDate = passwordChangeDate;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String getCaption() {
		return username;
	}

	public Boolean getUseLdap() {
		return useLdap;
	}

	public void setUseLdap(Boolean useLdap) {
		this.useLdap = useLdap;
	}

	@Override
	protected void customPreUpdate() {
		if (useLdap == null) {
			useLdap = true;
		}
	}

	@Override
	public void prePersist() {
		if (useLdap == null) {
			useLdap = true;
		}
	}

	@Override
	public String toString() {
		return username;
	}
	
}
