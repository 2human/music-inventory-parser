package objects;

import java.io.File;

/**
 * 
 * @author Andrew
 * Collection file containing raw data which will be parsed.
 *
 */

public class CollectionFile {

	File file;
	boolean preParsed;	
	
	/**
	 * 
	 * @param file Source collection file.
	 * @param preParsed Collection entries contain special delimiters to optimize parsing process.
	 */
	public CollectionFile(String filePath, boolean preParsed) {
		this.file = new File(filePath);
		this.preParsed = preParsed;
	}
	
	public File get() {
		return file;
	}

	public boolean isPreParsed() {
		return preParsed;
	}

}
