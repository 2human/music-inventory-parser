/**
		Parses string containing entry information into fields
 * 
 */
package objects;

import parsers.EntryParser;

public class Entry {
	private boolean isSecular;			//true if entry is secular, false if not
	private String collection,			//name of collection entry is a part of
					source,				//source number that entry is a part of
					location,			//entry location within source (page number)
					title,				//title of entry
					credit,				//composer entry is accredited to
					vocalPart,			//vocal part of entry
					key,				//key entry is written in
					melodicIncipit,		//melodic incipit for entry, which contains its musical notes
					textIncipit;		//vocal text for entry

	private static String[] fields = {"collection_name", "source_number", "entry_location", 	//labels for entry table columns
			"entry_title", "entry_credit", "entry_vocal_part",
			"entry_key", "entry_melodic_incipit", "entry_text_incipit", "entry_is_secular"};
	
	/**
	 * Create music entry object
	 */	
	public Entry(){		
	}
	
	/**
	 * Create music entry object
	 * @param collection	name of collection entry is contained within
	 * @param source		name of source entry is contained within
	 * @param entryStr		non-parsed string with entry information 
	 * @param isSecular		is music entry secular?
	 */	
	public Entry(String collection, int source, RoughEntry roughEntry){
		//prepare data for entry array construction
		this.collection = collection;
		this.source =  Integer.toString(source);
		this.isSecular = roughEntry.isSecular();
		EntryParser pe = new EntryParser(roughEntry.getNonParsedFields(), this);
		pe.parseFormattedEntry();
	}
	
	//
	public String toString() {		
		return "Entry Location: " + location +
				"\nEntry Title: "  +  title + 
				"\nSecular Entry: " + isSecular +
				"\nEntry Credit: "  +  credit +
				"\nEntry Vocal Part: " + vocalPart + 				
				"\nEntry Key: " + key +
				"\nEntry Melodic Incipit: "  +  melodicIncipit +
				"\nEntry Text Incipit: "  +  textIncipit + 
				"\n\n";
	}
	//---------------------------------------------------------------------------------------
	//return array containing parsed entries
	public String[] toArray(){
		String[] arr = {collection, source, location, title, credit, vocalPart, key, melodicIncipit, textIncipit, Boolean.toString(isSecular)};
		return arr;
	}
	//---------------------------------------------------------------------------------------
	//return whether entry is secular
	public boolean isSecular() {
		return isSecular;
	}
	//field labels for each music entry
	public static String[] getFields() {
		return fields;
	}
	
	public void setSecular(boolean isSecular) {
		this.isSecular = isSecular;
	}


	public void setTitle(String title) {
		this.title = title;
	}

	public void setCredit(String credit) {
		this.credit = credit;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void setVocalPart(String vocalPart) {
		this.vocalPart = vocalPart;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setMelodicIncipit(String melodicIncipit) {
		this.melodicIncipit = melodicIncipit;
	}

	public void setTextIncipit(String textIncipit) {
		this.textIncipit = textIncipit;
	}

	public static void setFields(String[] fields) {
		Entry.fields = fields;
	}
}
