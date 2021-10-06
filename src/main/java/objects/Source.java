/**
 * 
 */
package objects;


/**
 * @author Andrew
 *	Contains information about source within a music collection. Each source may contain tune entries	
 *
 */
public class Source {
	//source/entry variables	
	private String collection;
	private int sourceNumber;					//current source number
	private String callNumber;					//call number for current source, indicated by bold text
	private String author;						//source author
	private String title;						//title of source
	private String inscription;					//source inscription
	private String description;					//description of current source, containing all details that cannot be parsed
	
	private static String[] fields = {"collection_name", "source_number", "source_call_number", //labels for source table columns
			"source_author", "source_title", "source_inscription", "source_description"};
	
	Entries entries;								//entries object linked to current source
	
	//constructor that sets values to null by default, which are changed if needed
	public Source(String collection, int src){
		this.collection = collection;
		sourceNumber = src;
		callNumber = null;
		author = null;						
		title = null;							
		inscription = null;
		description = null;		
		entries = new Entries();
	}

	public String[] toArray() {					//source information in array format
		//collection, 
		String[] arr = {collection, Integer.toString(sourceNumber), callNumber, author, title, inscription, description};
		return arr;
	}

	public int getSourceNumber() {
		return sourceNumber;
	}

	public void setCallNumber(String callNumber){
		this.callNumber = callNumber;		
	}

	public String getCallNumber() {
		return callNumber;
	}

	public void setAuthor(String author) {
		author = author.replace(".", "")
				.trim();
		if(author.startsWith("[") && !author.endsWith("]"))
			author +=  "]";
		this.author = author;
	}

	public String getAuthor() {
		return author;
	}

	public void setTitle(String title) {
		title = title.trim();
		if(title.endsWith("]") && !title.startsWith("[") && !title.endsWith("[sic]"))
			title = "[" + title;		
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setInscription(String inscription) {
		this.inscription = inscription;
	}

	public String getInscription() {
		return inscription;
	}

	public void setDescription(String description) {
		this.description = trimmedDescription(description);
	}
	
	public String getDescription() {
		return description;
	}

	public String toString() {
		return "Source: " + sourceNumber +
				"\nAuthor: "  +  author + 
				"\nTitle: "  +  title +
				"\nDescription: "  +  description +
				"\nCall number: " + callNumber + 				
				"\nInscription(s): " + inscription  +
				"\n----------------end of source-------------\n\n";
	}

	private String trimmedDescription(String description) {
		description = description.trim();
		if(description.startsWith(".")) {
			return description.substring(2).trim();
		}
		else if(description.startsWith(".]")) {
			if(title != null) {
				title += "]";
			}
			return description.substring(3).trim();			
		}
		else if (description.startsWith("?]")){
			if(title != null) {
				title += "?]";
			}
			return description.substring(3).trim();
		}
		else {	 
			return description.trim();
		}		
	}

	public void addEntry(Entry entry) {
		entries.add(entry);
	}

	public Entries getEntries() {
		return entries;
	}

	//return field labels for source
	public static String[] getFields() {
		return fields;
	}
	
	public int getEntryCount() {
		return entries.getCount();
	}
}
