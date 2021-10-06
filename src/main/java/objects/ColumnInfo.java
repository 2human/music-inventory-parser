package objects;

/**
 * Contains information about spreadsheet columns when writing information to spreadsheet
 * @author Andrew
 *
 */

public class ColumnInfo {
	
	private String[] labels;
	private int[] widths;
	
	public ColumnInfo(String[] labels, int[] widths) {
		this.labels = labels;
		this.widths = widths;
	}

	public String[] getLabels() {
		return labels;
	}

	public int[] getWidths() {
		return widths;
	}
	
	
	
}
