package objects;

/**
 * Entry in intermediary state that has yet to be fully parsed.
 * @author Andrew
 *
 */

public class RoughEntry {
	
	private String nonParsedFields;
	private boolean isSecular;
	
	public RoughEntry(StringBuilder nonParsedFields, boolean isSecular) {
		this.nonParsedFields = nonParsedFields.toString();
		this.isSecular = isSecular;
	}

	public String getNonParsedFields() {
		return nonParsedFields;
	}

	public boolean isSecular() {
		return isSecular;
	}
	
	

}
