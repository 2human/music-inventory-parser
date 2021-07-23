/**
		Parses string containing entry information into fields
 * 
 */
package parsers;

import objects.Entry;

public class ParseEntry {
	
	Entry entry;				//entry being parsed
	String[] splitEntries,
			workingEntries;
	
	//default constructor
	public ParseEntry(){		
	}
	
	public ParseEntry(String entryStr, Entry entry){
		//prepare data for entry array construction
		workingEntries = new String[7];
		recordTunePage(entryStr);		//pull tune page from entry string	
		entryStr = formatEntryStr(entryStr);			//format current entry, removing tunepage, whitespace and unwanted commas
		splitEntries = entryStr.split(", ");	//split entry into fields using ", " as delimiter
		parseAndRecordTitleCredit();	//get title and credit from first entry of split array, which
		this.entry = entry;		
	}

	//get tune page which is text that occurs before colon
	private void recordTunePage(String entryStr) {
		if(hasColon(entryStr)){
			workingEntries[0] = textPrecedingColon(entryStr);	//text preceding colon is tune page
		}
		else {	//no colon means no tune page
			workingEntries[0] = null;			
		}	
	}
	
	private boolean hasColon(String str) {
		return str.indexOf(":") != -1;
	}
	
	private String textPrecedingColon(String str) {
		return str.substring(0, str.indexOf(":"));
	}
	
	//format string of entry to format optimal for parsing
	private String formatEntryStr(String entryStr) {
		if(hasColon(entryStr)){	
			entryStr = getTextProceedingColon(entryStr);	//remove tunepage from entry string, which occur before colon
		}
		return entryStr.replace(",”", "”,")		//put commas on outside of quotes
				.replace(", so", "-*- so")			//remove common false delimiter and replace with temporary symbol
				.replace(", but", "-*- but")		//remove common false delimiter and replace with temporary symbol
				.replace(", by",  "-*- by")			//remove common false delimiter and replace with temporary symbol
				.trim().replaceAll(" +", " ")		//trim extra spaces
				.replace("i.e.,", "i.e.-*-")		//remove common false delimiter and replace with temporary symbol
				.replace("solo:", "solo**&");		//remove common false delimiter and replace with temporary symbol
	}
	
	private String getTextProceedingColon(String str) {
		int indexAdjustment = 2;			//number of indices after colon to start after colon when there is space to trim whitespace
		if(noSpaceAfterColon(str)) {
			indexAdjustment--;				//start at character right after colon when there is no space
		}
		return str.substring(str.indexOf(":") + indexAdjustment, str.length());
	}
	
	private boolean noSpaceAfterColon(String str) {
		final int SPACE_CHARACTER_INT = 32;
		return (int) str.charAt(str.indexOf(":") + 1) != SPACE_CHARACTER_INT;
	}
	
	//return array containing [0]: tune title and [1]: tune author by parsing string containing title and author
	public void parseAndRecordTitleCredit() {
		String titleCreditStr = splitEntries[0],	//first index of split entries always contains titleCredit
				title,
				credit;
		int creditIndex = getCreditIndex(titleCreditStr);	//start of credit text
		//separate tune title and author into individual strings
		if(creditFound(creditIndex)) {
			//record title credit
			title = getTitle(titleCreditStr, creditIndex);
			credit = getCredit(titleCreditStr, creditIndex);
			workingEntries[1] = getPolishedTitle(title);
			workingEntries[2] = getPolishedCredit(credit);
		} else{		
			workingEntries[1] = titleCreditStr;	//if no credit present, record all of text as title
		}		
	}	
	
	private int getCreditIndex(String str) {
		String[] creditIndicators = {"[by", "-by", " by ", "“By", "att."};	//strings that indicate presence of author
		int index = -1;						//no matches by default
		for(String indicator: creditIndicators) {	//check to see if any author indicators occur in text
			if(creditIndicatorFound(str, indicator)) {
				index = str.indexOf(indicator);		//record index of match
			}
		}
		return index;		
	}
	
	private boolean creditIndicatorFound(String str, String indicator) {
		return str.indexOf(indicator) != -1;
	}
	
	private boolean creditFound(int index) {
		return index != -1; //-1 means no index was found
	}
	
	private String getTitle(String str, int creditIndex) {	//get title after author index has been found
		return str.substring(0, creditIndex);		//title ends at author index
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
	public void parseEntry() {	
		int shifts = 0,			//number of times array fields are shifted, such as there being multipe values for same field in separate indices
			index,				//current split entry index being analyzed
			arrLimit,			//number of indices from split entries that will be added to temp entries before rest of 
								//indices from split entries are dumped into text incipit index
			indexDiscrepency;	//adjust difference between cur index being added from entries split and index of workingEntries 
								//that current value is being placed. Starts at 2 because of tunepage 
								//already being added and title/credit being in same index
		
		//convert split array to full array
		for(index = 1, arrLimit = 5, indexDiscrepency = 2; index < splitEntries.length && index < arrLimit; index++) {
			
			//if not first index (which is default vocal part index) and previous index was recorded as vocal part index
			if(lastIndexWasVocalPart(index, indexDiscrepency)) {				
				if(isVocalPart(splitEntries, index)) {	//checks to see if multiple vocal parts in split entry array
					//method for combining multiple values for vocal part, represented in index 4
					shifts--;					//record leftward shift of indices relative to workingEntries for later operations
					indexDiscrepency--;			//record leftward shift for current for loop so that next index will be checked for vocal part
					arrLimit++;					//increment limit of how high of index in entriesSplit will be added to fullEntries
					workingEntries[index + indexDiscrepency] += " " + splitEntries[index];	//combine extra vocal part value 
				}
				else {
					workingEntries[index + indexDiscrepency] = splitEntries[index];			//if no extra vocal part value detected, proceed as normal
				}
			}
			else {
				workingEntries[index + indexDiscrepency] = splitEntries[index];		//add entry to workingEntries
			}
		}
		
		//if no vocal part was entered and  tune key was placed vocal part field, shift cells right
		if(workingEntries[3] != null && workingEntries[3].length() < 4 && workingEntries[3].indexOf("TTB") == -1) {
			shiftCellsRight(workingEntries, 3);
			shifts++;
		}			
		
		//if melodic incipit index is empty or has data that is not melodic incipit, which will occur if
		//there was no vocal part or key in entry string
		if(workingEntries[5] == null || !isMelodicIncipit(workingEntries[5])) {
			//start at first position where melodic incipit could be, which is in vocal part index
			for(int i = 3; i < 5; i++) {			
				if(isMelodicIncipit(workingEntries[i])){	//if melodic incipit is in current field
					shiftCellsRight(workingEntries, i);	//shift cells right
					shifts++;						//account for shift for other operations
				}	
			}
		}
		
		//if extra information was given for incipit field, as indicated by mm., add incipit to correct field
		//by shifting left
		if(workingEntries[5] != null && workingEntries[5].indexOf("mm.") != -1 && isMelodicIncipit(workingEntries[6])) {
			workingEntries[5] += (", " + workingEntries[6]);
			workingEntries[6] = null;
			shifts--;
		}
		

		
		//add all unrecorded entries that occur after melodic incipit to text field 
		if(isMelodicIncipit(workingEntries[5])) {
			for(int i = 5 - shifts; i < splitEntries.length; i++) {
				workingEntries[6] += (", " + splitEntries[i]);
			}
		}
		
		//debugger to detect non-incipits in melodic incipit field
		else {
			for(int i = 5 - shifts; i < splitEntries.length; i++) {
				workingEntries[6] += (", " + splitEntries[i]);
			}

		}		
		
		//replace commas and colons that were substituted in source document
		for(int i = 0; i < workingEntries.length; i++) {
			if(workingEntries[i] != null) {
				//melodic incipits that contained commas had commas replaced by -*- 
				//and colons replaced by **&
				workingEntries[i] = workingEntries[i].replace("-*-", ",").replace("**&", ":");					
			}
		}
		
		if(!isMelodicIncipit(workingEntries[5])) {
			for(int i = 0; i < workingEntries.length; i++) {
//				System.out.println(fields[i] + ": " + workingEntries[i]);
			}
			ParseCollection.notIncipitCount++;
		}
		
		setVariables(workingEntries);

	}	
	
	private boolean lastIndexWasVocalPart(int index, int indexDiscrepency) {
		return index > 1 && index + indexDiscrepency == 4;
	}

	public static boolean isMelodicIncipit(String parText) {	//check if current entry is melodic incipit, indicated by more than three digits
		int totalDigitCount = 0,				//total digits in string
				greatestConsecutiveDigits = 0,	//most consecutive digits that occur in a row in given string
				curConsecutiveDigits = 0;		
		if(parText == null)		//no text, no incipit
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
					(parText.indexOf("|") != -1) || parText.indexOf("-") != -1) ||  //	and has pipes or dash is indicator of melodic incipit
				greatestConsecutiveDigits  > 4 || 						//more than 4 consecutive digits indicator of melodic incipit
				totalDigitCount >= 8;									//more than 8 digits total indicator of melodic incipit
	}
	
	private static boolean isNewGreatestConsecutiveDigits(int curCount, int curGreatestCount) {
		return curCount > curGreatestCount;
	}

	//determine if string is vocal part
	private boolean isVocalPart(String[] entriesSplit, int index) {
		String[] vocalPartKeywords = {"tenor", "counter", "bass", "treble", "cantus", "medus", "basus", "meaudus", "voice", "TCTB", "fragment", "not in score"};
		//^^terms that represent vocal part description
		if(entriesSplit[index].indexOf("“") != -1 )		//quote character that indicates vocal part
			return true;
		for(String kw: vocalPartKeywords) {				
			//if current string contains any of the keywords that represent vocal part description
			if(entriesSplit[index].toLowerCase().indexOf(kw) != -1) {
				return true;
			}
		}
		return false;				
	}

	public String[] shiftCellsRight(String[] strArr, int startIndex) {
		for(int i = strArr.length - 1; i > startIndex; i--) {
			strArr[i] = strArr[i - 1];
		}
		strArr[startIndex] = null;
		return strArr;
	}

	//finalize variables in entry object
	private void setVariables(String[] workingEntries) {
		entry.setLocation(workingEntries[0]);
		entry.setTitle(workingEntries[1]);
		entry.setCredit(workingEntries[2]);
		entry.setVocalPart(workingEntries[3]);
		entry.setKey(workingEntries[4]);
		entry.setMelodicIncipit(workingEntries[5]);
		entry.setTextIncipit(workingEntries[6]);						
	}
	
}
