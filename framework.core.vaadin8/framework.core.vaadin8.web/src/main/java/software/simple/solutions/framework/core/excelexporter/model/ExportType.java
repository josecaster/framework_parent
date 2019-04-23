/**
 * 
 */
package software.simple.solutions.framework.core.excelexporter.model;

/**
 * The Enum ExportType.
 *
 * @author Kartik Suba
 */
public enum ExportType {

	/** The xls. */
	XLS("xls"),

	/** The xlsx. */
	XLSX("xlsx");

	/** The id. */
	private final String extension;

	/**
	 * Instantiates a new export type.
	 *
	 * @param id
	 *            the id
	 */
	private ExportType(final String extension) {
		this.extension = extension;
	}

	public String getExtension() {
		return this.extension;
	}
}
