/**
		Parses string containing entry information into fields
 * 
 */
package parsers;

import objects.Entry;
import objects.RoughEntry;

public class EntryParser {
	
	private boolean preParsed = true;	//whether or not file is pre-optimized for parsing
	
	private Entry entry;				//entry being parsed
	private String[] splitEntry,
			workingEntry;
	private boolean isSecular;
	private String collection;
	private int source;
	String entryStr;
	
	
	int indexShift ,			//number of times array fields are shifted, such as there being multipe values for same field in separate indices
		splitArrayIndex,				//current split entry splitArrayIndex being analyzed
		arrLimit;			//number of indices from split entries that will be added to temp entries before rest of 
							//indices from split entries are dumped into text incipit splitArrayIndex
	//discrepancy between splitArrayIndex and workingEntry index
	//
	private static final int INDEX_DISCREPANCY = 2;
	
	//default constructor
	public EntryParser(){		
	}
	
	public EntryParser(String collection, int source, RoughEntry roughEntry){
		
		entry = new Entry();
		this.collection = collection;
		this.source = source;
		//prepare data for entry array construction
		workingEntry = new String[7];
		entryStr = roughEntry.getNonParsedFields();
		isSecular = roughEntry.isSecular();
	}
	
	public void parseEntry() {
		recordTunePage(entryStr);		//pull tune page from entry string	
		entryStr = formatEntryStr(entryStr);			//format current entry, removing tunepage, whitespace and unwanted commas
		splitEntry = getSplitEntry();
		parseAndRecordTitleCredit();	//get title and credit from first entry of split array, which
		parseRemainingEntryFields();		
	}

	//get tune page which is text that occurs before colon
	private void recordTunePage(String entryStr) {
		if(hasTunePage(entryStr)){
			workingEntry[0] = textPrecedingColon(entryStr);	//text preceding colon is tune page
		}
		else {	//no colon means no tune page
			workingEntry[0] = null;			
		}	
	}
	
	private boolean hasTunePage(String str) {
		if(preParsed) {
			return str.indexOf("::") != -1;		//two colons indicates presence of tune page in pre-parsed entry
		} else{
			return str.indexOf(":") != -1;		//	otherwise tune page is indicated by one colon
		}
	}
	
	private String textPrecedingColon(String str) {
		return str.substring(0, str.indexOf(":"));
	}
	
	
	
	//test to check contents of split entry array
	@SuppressWarnings("unused")
	private void printSplitEntry(int source, String tunePage) {
		if(isThisEntry(source, tunePage)) {
			for(String field: splitEntry) {
				System.out.println(field);
				System.out.println("printing field");
			}
			System.out.println("-----" + indexShift + "-------");
		}
	}
	
	//test to check contents of workingEntry array
	@SuppressWarnings("unused")
	private void printWorkingEntry(int source, String tunePage) {
		if(isThisEntry(source, tunePage)) {
			for(int i = 0; i < workingEntry.length; i++) {
				System.out.println("Index: " + i + " Content: " + workingEntry[i]);
			}
			System.out.println("-----" + indexShift + "-------");
		}
	}
	
	//format string of entry to format optimal for parsing
	private String formatEntryStr(String entryStr) {
		if(hasTunePage(entryStr)){	
			entryStr = getTextProceedingTunePage(entryStr);	//remove tunepage from entry string, which occur before colon
		}
		
		return entryStr.replace(", so", "-*- so")	//remove common false delimiter and replace with temporary symbol
				.replace(", but", "-*- but")		//remove common false delimiter and replace with temporary symbol
				.replace(", by",  "-*- by")			//remove common false delimiter and replace with temporary symbol
				.trim().replaceAll(" +", " ")		//trim extra spaces
				.replace("i.e.,", "i.e.-*-")		//remove common false delimiter and replace with temporary symbol
				.replace("solo:", "solo**&");		//remove common false delimiter and replace with temporary symbol
	}
	
	private String getTextProceedingTunePage(String str) {
		int indexAdjustment = 2;			//number of indices after colon to start after colon when there is space to trim whitespace
		if(preParsed) {	
			return str.substring(str.indexOf("::") + indexAdjustment, str.length());	//preparsed will have two colons to indicate tunepage			
		}
		else{
			if(noSpaceAfterColon(str)) {		
				indexAdjustment--;				//start at character right after colon when there is no space
			}
			return str.substring(str.indexOf(":") + indexAdjustment, str.length());
		}
	}
	
	private boolean noSpaceAfterColon(String str) {
		final int SPACE_CHARACTER_INT = 32;
		return (int) str.charAt(str.indexOf(":") + 1) != SPACE_CHARACTER_INT;
	}
	
	private String[] getSplitEntry() {
		if(preParsed) {
			return entryStr.split(",,");	//split entry into fields using ", " as delimiter
		}
		else {
			return entryStr.split(", ");	//split entry into fields using ", " as delimiter
		}
		
	}
	
	//return array containing [0]: tune title and [1]: tune author by parsing string containing title and author
	private void parseAndRecordTitleCredit() {
		String titleCreditStr = splitEntry[0],	//first splitArrayIndex of split entries always contains titleCredit
				title,
				credit;
		int creditIndex = getCreditIndex(titleCreditStr);	//start of credit text
		//separate tune title and author into individual strings
		if(creditFound(creditIndex)) {
			//record title credit
			title = getTitle(titleCreditStr, creditIndex);
			credit = getCredit(titleCreditStr, creditIndex);
			workingEntry[1] = getPolishedTitle(title);
			workingEntry[2] = getPolishedCredit(credit);
		} else{		
			workingEntry[1] = titleCreditStr;	//if no credit present, record all of text as title
		}		
	}	
	
	private int getCreditIndex(String str) {
		String[] creditIndicators = {"[by", "-by", " by ", "“By", "“by", "att.", "[originally by"};	//strings that indicate presence of author
		int splitArrayIndex = -1;						//no matches by default
		for(String indicator: creditIndicators) {	//check to see if any author indicators occur in text
			if(creditIndicatorFound(str, indicator)) {
				splitArrayIndex = str.indexOf(indicator);		//record splitArrayIndex of match
			}
		}
		return splitArrayIndex;		
	}
	
	private boolean creditIndicatorFound(String str, String indicator) {
		return str.indexOf(indicator) != -1;
	}
	
	private boolean creditFound(int splitArrayIndex) {
		return splitArrayIndex != -1; //-1 means no splitArrayIndex was found
	}
	
	private String getTitle(String str, int creditIndex) {	//get title after author splitArrayIndex has been found
		return str.substring(0, creditIndex);		//title ends at author splitArrayIndex
	}
	
	private String getCredit(String str, int creditIndex) {
		return str.substring(creditIndex, str.length());
	}

	private String getPolishedTitle(String title) {
		if(title != null) {
			title = title.trim();								//trim whitespace
			//add bracket to end of title if it starts with bracket but does not end with bracket
			if(title.startsWith("[") && !title.endsWith("]"))	
				title +=  "]";									
		}
		return title;
	}

	private String getPolishedCredit(String credit) {
		if(credit != null) {
			credit = credit.trim();				//remove whitespace
			//add bracket to beginning of credit if there is unmatched bracket at end
			if(credit.endsWith("]") && !credit.startsWith("[") && !credit.endsWith("[sic]"))
				credit = "[" + credit;
		}
		return credit;
	}

	//sort  entry so that each piece of data is in its respective field
	private void parseRemainingEntryFields() {	
		indexShift = 0;
		arrLimit = 5;		
		//convert split array to full array
		//TODO create fillWorkingArray
		for(splitArrayIndex = 1; splitArrayIndex < splitEntry.length && splitArrayIndex < arrLimit; splitArrayIndex++) {			
			if(lastIndexWasVocalPart() && isVocalPart(splitArrayIndex) && !isMelodicIncipit(splitEntry[splitArrayIndex])) {	
				recordLeftwardShift();
				appendAdditionalVocalPart();
			}			
			else {
				copySplitEntryToWorkingEntry();
			}
		}		
		
		//if no vocal part existed and  tune key was placed vocalPart field
		if(entryHadNoVocalPart()) {
			workingEntry = shiftCellsRight(workingEntry, 3);	//shift data out of vocal period
			indexShift++;	//record shift
		}	
		
		
		if(melodicIncipitIsMisplaced()) {
			findAndPlaceMelodicIncipit();
		}
		
		//if extra information was given for incipit field, as indicated by mm., add incipit to correct field
		//by shifting left
		if(additionalMelodicIncipitInfoFound()) {
			appendMelodicIncipit();
			vacateTextIncipitField();
			//TODO used to be negative shift here; find out why it had to be removed
		}
		
		appendRemainingToTextIncipit();		
		removeFalseDelimiterReplacementSymbols();
		putCommasOutsideQuotes();
		detectandTallyNotIncipit();		
		recordEntryVariables();
	}	
	
	private boolean lastIndexWasVocalPart() {
		return splitArrayIndex > 1 && 				//splitArrayIndex 1 is vocal part in splitEntry
				splitArrayIndex + INDEX_DISCREPANCY + indexShift == 4;	// and 3 in working entries
	}

	public static boolean isMelodicIncipit(String parText) {	//check if current entry is melodic incipit, indicated by more than three digits
		int totalDigitCount = 0,				//total digits in string
				greatestConsecutiveDigits = 0,	//most consecutive digits that occur in a row in given string
				curConsecutiveDigits = 0;	
		if(parText == null)		//no text, no incipit
				//TODO turn to try/catch
			return false;	
		

		for(char c: parText.toCharArray()) {	//check if each character is digit, and increment count if so			
			//TODO consider adding pipes to check
			if(Character.isDigit(c)) {
				totalDigitCount++;
				curConsecutiveDigits++;
				if(isNewGreatestConsecutiveDigits(curConsecutiveDigits, greatestConsecutiveDigits)) {
					greatestConsecutiveDigits = curConsecutiveDigits;	//record new greatest
				}
			}			
			else {	//if character not a digit
				curConsecutiveDigits = 0;	//reset count
			}
		}		
		//check for various indicators of melodic incipit according to digits contained and characters contained
		return (totalDigitCount >= 3 && 												//more than 3 digits
					((parText.indexOf("|") != -1) || parText.indexOf("-") != -1)) ||  //	and has pipes or dash is indicator of melodic incipit
				greatestConsecutiveDigits  > 4 || 						//more than 4 consecutive digits indicator of melodic incipit
				totalDigitCount >= 8;									//more than 8 digits total indicator of melodic incipit
	}
	
	private static boolean isNewGreatestConsecutiveDigits(int curCount, int curGreatestCount) {
		return curCount > curGreatestCount;
	}

	//determine if string is vocal part
	private boolean isVocalPart(int splitArrayIndex) {
		String[] vocalPartKeywords = {"tenor", "counter", "bass", "treble", "cantus", "medus", "basus",
				"meaudus", "voice", "TCTB", "fragment", "not in score", "altus", "voices", "tune A", "2 staves", "medius", "Basso"};
		//^^terms that represent vocal part description
		if(splitEntry[splitArrayIndex].indexOf("“") != -1 )		//quote character that indicates vocal part
			return true;
		for(String kw: vocalPartKeywords) {				
			//if current string contains any of the keywords that represent vocal part description
			if(splitEntry[splitArrayIndex].toLowerCase().indexOf(kw) != -1) {
				return true;
			}
		}
		return false;				
	}
	
	private void recordLeftwardShift() {
		indexShift--;	
		arrLimit++;		//leftward shift means higher amount of split entries
		//TODO record this comment
	}
	
	private void appendAdditionalVocalPart() {
		workingEntry[3] += " " + splitEntry[splitArrayIndex];	//add current split array index to vocalPart index in workingEntry		
	}
	
	private void copySplitEntryToWorkingEntry() {
		workingEntry[getWorkingEntryIndex()] = splitEntry[splitArrayIndex];		//add entry to workingEntry
	}
	
	//compare split array data with working array data for testing purposes
	@SuppressWarnings("unused")
	private void printCurrentField(int source, String tunePage) {
		if(this.source == source && workingEntry[0].indexOf(tunePage) != -1) {
			System.out.println("" + getWorkingEntryIndex() + " " + workingEntry[getWorkingEntryIndex()]
				+ splitArrayIndex + " " + splitEntry[splitArrayIndex]);			
		}
	}
	
	//index in workingEntry to which data is currently being added
	private int getWorkingEntryIndex() {
		return splitArrayIndex + INDEX_DISCREPANCY + indexShift;
	}
	
	//TODO this may be able to be eliminated by adjusting previous loop
	//determine if another field was placed into vocal part field due to no vocal part existing in entry
	private boolean entryHadNoVocalPart() {	
		return workingEntry[3] != null &&		
				workingEntry[3].length() < 4 &&		//text with less than 4 characters will not be vocal part
				workingEntry[3].indexOf("TTB") == -1;	//with exception of 'TTB'
	}
	
	public String[] shiftCellsRight(String[] strArr, int startIndex) {
		for(int i = strArr.length - 1; i > startIndex; i--) {
			strArr[i] = strArr[i - 1];
		}
		strArr[startIndex] = null;
		return strArr;
	}
	
	//determine if melodic incipit was placed in wrong field,
	// which can occur when no vocal part or key in current entry
	private boolean melodicIncipitIsMisplaced() {
		return workingEntry[5] == null || 			//no text in melodic incipit's index
				!isMelodicIncipit(workingEntry[5]);	//text in melodic incipit's index is not incipit
	}
	
	private void findAndPlaceMelodicIncipit() {
		//start at first position where melodic incipit could be (i = 3), which is in vocal part splitArrayIndex
		for(int i = 3; i < 5; i++) {			
			if(isMelodicIncipit(workingEntry[i])){	//if melodic incipit found
				workingEntry = shiftCellsRight(workingEntry, i);	//shift cells right
				indexShift++;						//record shift
			}	
		}		
	}
	
	private boolean additionalMelodicIncipitInfoFound() {
		return workingEntry[5] != null && 				//there is text in incipit field already
				workingEntry[5].indexOf("mm.") != -1 && 	//'mm.' being in melodicIncipit field often pushes rest of field information out 
				isMelodicIncipit(workingEntry[6]);			//text in field after melodic incipit field is melodic incipit
	}
	
	//add additional melodic incipit text to melodic incipit field
	private void appendMelodicIncipit() {
		workingEntry[5] += (", " + workingEntry[6]);		
	}
	
	private void vacateTextIncipitField() {
		workingEntry[6] = null;
	}
	
	private void appendRemainingToTextIncipit() {
		for(int i = 5 - indexShift; i < splitEntry.length; i++) {
			if(workingEntry[6] == null){			//will be null when a shift occurred
				workingEntry[6] = splitEntry[i];
			}
			else {
				workingEntry[6] += (", " + splitEntry[i]);
			}
		}		
	}
	
	private boolean isThisEntry(int source, String tunePage) {
		return this.source == source && workingEntry[0].indexOf(tunePage) != -1;
	}

	//replace commas and colons that were substituted in source document that were acting as false delimiters
	private void removeFalseDelimiterReplacementSymbols() {	
		for(int i = 0; i < workingEntry.length; i++) {
			if(workingEntry[i] != null) {
				//melodic incipits that contained commas had commas replaced by -*- 
				//and colons replaced by **&
				workingEntry[i] = workingEntry[i].replace("-*-", ",").replace("**&", ":");					
			}
		}		
	}
	
	//puts commas outside of quotes
	private void putCommasOutsideQuotes() {
		for(int i = 0; i < workingEntry.length; i++) {
			if(workingEntry[i] != null) {
				//melodic incipits that contained commas had commas replaced by -*- 
				//and colons replaced by **&
				workingEntry[i] = workingEntry[i].replace(",”", "”,");			
			}
		}		
	}
	
	//
	private void detectandTallyNotIncipit() {
		if(!isMelodicIncipit(workingEntry[5])) {		//most common sign is when melodic incipit field is not melodic incipit
			for(int i = 0; i < workingEntry.length; i++) {
	//			System.out.println(fields[i] + ": " + workingEntry[i]);
			}
			CollectionParser.notIncipitCount++;
		}		
	}
	
	//finalize variables in entry object
	private void recordEntryVariables() {
		entry.setLocation(workingEntry[0]);
		entry.setTitle(workingEntry[1]);
		entry.setComposer(workingEntry[2]);
		entry.setVocalPart(workingEntry[3]);
		entry.setMelodicIncipit(workingEntry[5]);
		entry.setKey(workingEntry[4]);
		entry.setTextIncipit(workingEntry[6]);	
		entry.setIsSecular(isSecular);
		entry.setCollection(collection);
		entry.setSource(source);
		entry.setNotes("");
	}
	
	/**
	 * 
	 * @return Parsed entry in array form.
	 */
	public Entry getParsedEntry() {
		return entry;
	}
}
