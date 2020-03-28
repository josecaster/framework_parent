package software.simple.solutions.framework.core.web.view;

import java.io.ByteArrayInputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

import software.simple.solutions.framework.core.annotations.SupportedPrivileges;
import software.simple.solutions.framework.core.components.select.GenderSelect;
import software.simple.solutions.framework.core.constants.FileReference;
import software.simple.solutions.framework.core.constants.Privileges;
import software.simple.solutions.framework.core.constants.ReferenceKey;
import software.simple.solutions.framework.core.entities.EntityFile;
import software.simple.solutions.framework.core.entities.Person;
import software.simple.solutions.framework.core.exceptions.FrameworkException;
import software.simple.solutions.framework.core.properties.PersonProperty;
import software.simple.solutions.framework.core.service.IFileService;
import software.simple.solutions.framework.core.service.facade.PersonServiceFacade;
import software.simple.solutions.framework.core.util.ContextProvider;
import software.simple.solutions.framework.core.util.PropertyResolver;
import software.simple.solutions.framework.core.valueobjects.PersonVO;
import software.simple.solutions.framework.core.web.BasicTemplate;
import software.simple.solutions.framework.core.web.FilterView;
import software.simple.solutions.framework.core.web.components.CTextField;
import software.simple.solutions.framework.core.web.flow.MainView;
import software.simple.solutions.framework.core.web.routing.Routes;

//@Route(value = Routes.PERSON, layout = MainView.class)
@SupportedPrivileges(extraPrivileges = { Privileges.PERSON_SHOW_GENDER, Privileges.PERSON_SHOW_DOB })
public class PersonView extends BasicTemplate<Person> {

	private static final long serialVersionUID = 6503015064562511801L;

	private static final Logger logger = LogManager.getLogger(PersonView.class);

	public PersonView() {
		setEntityClass(Person.class);
		setServiceClass(PersonServiceFacade.class);
		setFilterClass(Filter.class);
		// setFormClass(Form.class);
//		setGridRowHeight(75);
		setEditRoute(Routes.PERSON_EDIT);
	}

	@Override
	public void setUpCustomColumns() {
		addComponentContainerProperty(new ValueProvider<Person, Image>() {

			private static final long serialVersionUID = 1818232591747121810L;

			@Override
			public Image apply(Person source) {
				Image image = new Image();
				image.setHeight("75px");
				image.setWidth("75px");

				Thread thread = new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							if (source != null) {
								IFileService fileService = ContextProvider.getBean(IFileService.class);
								EntityFile entityFile = fileService.findFileByEntityAndType(source.getId().toString(),
										ReferenceKey.PERSON, FileReference.USER_PROFILE_IMAGE);

								if (entityFile != null) {
									StreamResource resource = new StreamResource("profile-image.jpg",
											() -> new ByteArrayInputStream(entityFile.getFileObject()));
									image.setSrc(resource);
								}
							}
						} catch (FrameworkException e) {
							logger.error(e.getMessage(), e);
						}
					}
				});
				thread.start();

				return image;
			}
		}, PersonProperty.IMAGE);
		addContainerProperty(Person::getLastName, PersonProperty.LAST_NAME);
		addContainerProperty(Person::getFirstName, PersonProperty.FIRST_NAME);
		addContainerProperty(new ValueProvider<Person, String>() {

			private static final long serialVersionUID = 7700757210287029477L;

			@Override
			public String apply(Person source) {
				if (source.getGender() != null) {
					if (source.getGender().getKey() != null) {
						return PropertyResolver.getPropertyValueByLocale(source.getGender().getKey(),
								UI.getCurrent().getLocale());
					} else {
						return source.getGender().getName();
					}
				}
				return source.getGender() == null ? null
						: PropertyResolver.getPropertyValueByLocale(source.getGender().getKey(),
								UI.getCurrent().getLocale());
			}
		}, PersonProperty.GENDER);
		addContainerProperty(Person::getAge, PersonProperty.AGE);
	}

	/**
	 * Filter implementation
	 * 
	 * @author yusuf
	 *
	 */
	public class Filter extends FilterView {

		private static final long serialVersionUID = 117780868467918033L;

		private CTextField lastNameFld;
		private CTextField firstNameFld;
		private GenderSelect genderFld;

		@Override
		public void executeBuild() {

			firstNameFld = addField(CTextField.class, PersonProperty.FIRST_NAME, 0, 0);

			lastNameFld = addField(CTextField.class, PersonProperty.LAST_NAME, 1, 0);

			genderFld = addField(GenderSelect.class, PersonProperty.GENDER, 2, 0);
			genderFld.setVisible(false);
			if (getViewDetail().getPrivileges().contains(Privileges.PERSON_SHOW_GENDER)) {
				genderFld.setVisible(true);
			}

		}

		@Override
		public Object getCriteria() {
			PersonVO vo = new PersonVO();
			vo.setFirstName(firstNameFld.getValue());
			vo.setLastName(lastNameFld.getValue());
			vo.setGenderId(genderFld.getItemId());
			return vo;
		}
	}

	@Override
	public Icon getIcon() {
		// TODO Auto-generated method stub
		return null;
	}

	// public class Form extends FormView {
	//
	// private static final long serialVersionUID = 6109727427163734676L;
	//
	// private CGridLayout formGrid;
	// private ImageField imageField;
	// private CTextField lastNameFld;
	// private CTextField firstNameFld;
	// private CTextField middleNameFld;
	// private CPopupDateField dateOfBirthFld;
	// private GenderSelect genderFld;
	// private CCheckBox activeFld;
	// private HorizontalLayout personInfoLayout;
	//
	// private List<ComboItem> genderListing;
	// private Person person;
	//
	// @Override
	// public void executeBuild() {
	// personInfoLayout = createPersonLayout();
	// addComponent(personInfoLayout);
	// }
	//
	// private HorizontalLayout createPersonLayout() {
	// personInfoLayout = new HorizontalLayout();
	//
	// imageField = new ImageField();
	// imageField.setImageHeight("100px");
	// imageField.setImageWidth("100px");
	// personInfoLayout.addComponent(imageField);
	// imageField.setUploadHandler(new UploadImage() {
	//
	// @Override
	// public void upload(InputStream inputStream) {
	// try {
	// byte[] byteArray = IOUtils.toByteArray(inputStream);
	// IFileService fileService = ContextProvider.getBean(IFileService.class);
	// EntityFileVO entityFileVO = new EntityFileVO();
	// entityFileVO.setActive(true);
	// entityFileVO.setDatabase(true);
	// entityFileVO.setFilename(person.getCode() + ".png");
	// entityFileVO.setEntityName(ReferenceKey.PERSON);
	// entityFileVO.setEntityId(person.getId().toString());
	// entityFileVO.setTypeOfFile(FileReference.USER_PROFILE_IMAGE);
	// entityFileVO.setFileObject(byteArray);
	// fileService.upLoadFile(entityFileVO);
	// } catch (IOException | FrameworkException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// });
	//
	// formGrid = ComponentUtil.createGrid();
	// personInfoLayout.addComponent(formGrid);
	//
	// firstNameFld = formGrid.addField(CTextField.class,
	// PersonProperty.FIRST_NAME, 0, 0);
	//
	// middleNameFld = formGrid.addField(CTextField.class,
	// PersonProperty.MIDDLE_NAME, 0, 1);
	//
	// lastNameFld = formGrid.addField(CTextField.class,
	// PersonProperty.LAST_NAME, 0, 2);
	//
	// dateOfBirthFld = formGrid.addField(CPopupDateField.class,
	// PersonProperty.DATE_OF_BIRTH, 1, 0);
	// dateOfBirthFld.setRangeEnd(LocalDate.now());
	// dateOfBirthFld.setDateFormat(Constants.SIMPLE_DATE_FORMAT.toPattern());
	// dateOfBirthFld.setVisible(false);
	// if (getViewDetail().getPrivileges().contains(Privileges.PERSON_SHOW_DOB))
	// {
	// dateOfBirthFld.setVisible(true);
	// }
	//
	// genderFld = formGrid.addField(GenderSelect.class, GenderProperty.GENDER,
	// 1, 1);
	// genderFld.setVisible(false);
	// if
	// (getViewDetail().getPrivileges().contains(Privileges.PERSON_SHOW_GENDER))
	// {
	// genderFld.setVisible(true);
	// }
	//
	// activeFld = formGrid.addField(CCheckBox.class, PersonProperty.ACTIVE, 1,
	// 2);
	//
	// formGrid.setComponentAlignment(activeFld, Alignment.BOTTOM_LEFT);
	//
	// return personInfoLayout;
	// }
	//
	// @SuppressWarnings("unchecked")
	// @Override
	// public Person setFormValues(Object entity) throws FrameworkException {
	// person = (Person) entity;
	// addToReferenceKey(ReferenceKey.PERSON, person);
	//
	// firstNameFld.setValue(person.getFirstName());
	// middleNameFld.setValue(person.getMiddleName());
	// lastNameFld.setValue(person.getLastName());
	// dateOfBirthFld.setValue(person.getDateOfBirth());
	// activeFld.setValue(person.getActive());
	//
	// genderFld.setValue(person.getGender() == null ? null :
	// person.getGender().getId());
	//
	// firstNameFld.setRequiredIndicatorVisible(true);
	// lastNameFld.setRequiredIndicatorVisible(true);
	// dateOfBirthFld.setRequiredIndicatorVisible(true);
	// genderFld.setRequiredIndicatorVisible(true);
	//
	// IFileService fileService = ContextProvider.getBean(IFileService.class);
	// EntityFile entityFile =
	// fileService.findFileByEntityAndType(person.getId().toString(),
	// ReferenceKey.PERSON,
	// FileReference.USER_PROFILE_IMAGE);
	// if (entityFile != null) {
	// imageField.setSource(new StreamResource(new StreamSource() {
	//
	// private static final long serialVersionUID = -1143826625995236036L;
	//
	// @Override
	// public InputStream getStream() {
	// if (entityFile == null || entityFile.getFileObject() == null) {
	// return null;
	// }
	// return new ByteArrayInputStream(entityFile.getFileObject());
	// }
	// }, entityFile.getName()));
	// }
	//
	// return person;
	// }
	//
	// @Override
	// public Object getFormValues() throws FrameworkException {
	// PersonVO vo = new PersonVO();
	//
	// vo.setId(person == null ? null : person.getId());
	// vo.setFirstName(firstNameFld.getValue());
	// vo.setLastName(lastNameFld.getValue());
	// vo.setMiddleName(middleNameFld.getValue());
	// vo.setDateOfBirth(dateOfBirthFld.getValue());
	// vo.setGenderId(genderFld.getItemId());
	// vo.setActive(activeFld.getValue());
	// return vo;
	// }
	//
	// @Override
	// public void handleNewForm() throws FrameworkException {
	// activeFld.setValue(true);
	// firstNameFld.setRequiredIndicatorVisible(true);
	// lastNameFld.setRequiredIndicatorVisible(true);
	// dateOfBirthFld.setRequiredIndicatorVisible(true);
	// genderFld.setRequiredIndicatorVisible(true);
	// }
	//
	// }

}
