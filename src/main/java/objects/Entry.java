/**
		Contains information about a music entry.
 * 
 */
package objects;

public class Entry {
	private boolean isSecular;			//true if entry is secular, false if not
	private String collection,			//name of collection entry is a part of
					source,				//source number that entry is a part of
					location,			//entry location within source (page number)
					title,				//title of entry
					composer,				//composer entry is accredited to
					vocalPart,			//vocal part of entry
					key,				//key entry is written in
					melodicIncipit,		//melodic incipit for entry, which contains its musical notes
					textIncipit,		//vocal text for entry
					notes;

	
	private static String[] fields = {"collection_name", "source_number", "entry_location", 	//labels for entry table columns
										"entry_title", "entry_composer", "entry_vocal_part",
										"entry_key", "entry_melodic_incipit", "entry_text_incipit",
										"entry_is_secular", "entry_notes"};
	
	/**
	 * Create music entry object
	 */	
	public Entry(){	
	}
	
	//
	public String toString() {		
		return "Entry Location: " + location +
				"\nEntry Title: "  +  title + 
				"\nSecular Entry: " + isSecular +
				"\nEntry Composer: "  +  composer +
				"\nEntry Vocal Part: " + vocalPart + 				
				"\nEntry Key: " + key +
				"\nEntry Melodic Incipit: "  +  melodicIncipit +
				"\nEntry Text Incipit: "  +  textIncipit + 
				"\n\n";
	}
	//---------------------------------------------------------------------------------------
	//return array containing parsed entries
	public String[] toArray(){
		String[] arr = {collection, source, location, title, composer, vocalPart, key, melodicIncipit, textIncipit, Boolean.toString(isSecular), notes};
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

	public void setComposer(String composer) {
		this.composer = composer;
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
	
	public void setIsSecular(boolean isSecular) {
		this.isSecular = isSecular;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public void setCollection(String collection) {
		this.collection = collection;
	}

	public void setSource(double source) {
		this.source = Double.toString(source);
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getCollection() {
		return collection;
	}

	public String getLocation() {
		return location;
	}

	public String getTitle() {
		return title;
	}

	public String getComposer() {
		return composer;
	}

	public String getVocalPart() {
		return vocalPart;
	}

	public String getKey() {
		return key;
	}

	public String getMelodicIncipit() {
		return melodicIncipit;
	}

	public String getTextIncipit() {
		return textIncipit;
	}

	public String getNotes() {
		return notes;
	}
	
	
}
