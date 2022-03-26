package parsers;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import objects.Collection;
import objects.CollectionFile;
import objects.RoughEntries;
import objects.RoughEntry;
import objects.Source;

/**
 * 
 * 
 * Parse music collection and source information for music collection file in .docx Word format
 * 
 * @author Andrew
 *
 */

//TODO write test that ensures correct number of sources and entries?
public class CollectionParser {
	private Collection collection;
	private FileInputStream fis;

	//Apache POI variables
	private XWPFDocument xdoc;							//.docx reader
	private List<XWPFParagraph> paragraphList;			//list of paragraphs in docx file obtained from .docx reader
	private XWPFParagraph paragraphObj;
	private String paragraphText;
	private List<XWPFRun> paragraphRuns;				//list of text "runs" contained within current paragraph,
														//containing information about formatting	
	private XWPFRun curRun;
	int curParIndex = 0;
	
	//tallies count of possible errors in entry parsing, as indicated by no melodic incipit being detected in melodic incipit field
	public static int notIncipitCount = 0;
		
	//determines if text begins with an indeterminate number of digits followed by period,
	private Pattern sourceNumberPattern = Pattern.compile("^[\\d]+[\\.]");
														//	which indicates source number
	private Matcher sourceNumberMatcher;								//matcher for detecting pattern occurrence above 

	private String collectionName;
	private StringBuilder collectionDescription,
							sourceDescription,
							sourceAuthor,
							sourceTitle;
	private Source source;
	
	//variables for parsing entries
	private RoughEntries roughEntries;
	private RoughEntry roughEntry;
	boolean entryIsSecular;
	private StringBuilder entryStrBuilder;
	EntryParser entryParser;
	
	boolean preParsed;	//determine if document is pre-parsed to optimize parsing operations
	
	
	public CollectionParser() {
		
	}
	
	public CollectionParser(CollectionFile collectionFile, Collection collection){
		this.collection = collection;
		this.preParsed = collectionFile.isPreParsed();
		parseAndSaveCollectionName(collectionFile.get());
		try {
			initializeInputDocument(collectionFile.get());			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void execute() {
		try {
			initializeStringBuilders();
			parseCollectionDescription();	
			parseSourcesAndEntries();		
			logParsePerformance();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		System.out.println(collectionName);
	}
	
	private void parseAndSaveCollectionName(File file){
		//collection name derived from file name
		collectionName = file.getName().substring(0, file.getName().lastIndexOf("."));
//		collectionName = "MA Worcester, American Antiquarian Society";
		collection.setName(collectionName);
	}
	
	//prepare document for parsing
	private void initializeInputDocument(File file) throws Exception {
		fis = new FileInputStream(file);				//Word file being parsed
		xdoc = new XWPFDocument(OPCPackage.open(fis));	//Apache POI .docx reader
		paragraphList = xdoc.getParagraphs();			//convert document to paragraphs		
	}
	
	//get information about collection which ends when source number occurs at beginning of a paragraph
	private void parseCollectionDescription() throws Exception{
		initializeParagraphVariables();
		//all information prior to source number being found is collection info
		while(!endOfDocumentReached() && !hasSourceNumber(paragraphObj.getText())) {
			collectionDescription.append(paragraphText + "\n");	//add current paragraph to collection description			
			curParIndex++;
			initializeParagraphVariables();	//prep for next iteration
		}
		collection.setDescription(collectionDescription);
	}	
	private boolean endOfDocumentReached() {
		return curParIndex >= paragraphList.size();
	}	
	
	//determine if source number is found in string, as indicated by a number followed by a period
	private boolean hasSourceNumber(String text) {
		sourceNumberMatcher = sourceNumberPattern.matcher(text);
		return sourceNumberMatcher.find();
	}
	

	//perform source/entries operations
	private void parseSourcesAndEntries() throws Exception{
		while(!endOfDocumentReached()) {
			initializeParagraphVariables();					//prepare variables for parsing
			if(hasSourceNumber(paragraphObj.getText())) {	//indicates end of last source and beginning of new source
				try {
					//TODO change to reccordAuthorTitleDescription, change others to same, and then create save source method
					finalizePreviousSource();
				} catch (Exception e) {
					System.out.println("No previous source exists because this is first source found.");
				}
				initializeSource();	
				initializeStringBuilders();
				//author, title, and most of description to be found in same paragraph as source number
				parseAuthorTitleDescription();
			}			
			else if(isInscription(paragraphObj)) {	
				parseAndSaveInscription();
			}			
			else if(hasCallNumber(paragraphObj)) {
				parseAndSaveCallNumber();		
			}
			else if(entryFound(paragraphObj)) {	
				sourceDescription.append(paragraphText + "\n");	//record text before entry
				parseAndSaveSourceEntries();
			}
			
			else {	//miscellaneous text added to description by default
				sourceDescription.append(paragraphText + "\n");
			}
			curParIndex++;
		}		
		finalizePreviousSource();	//final source
	}
	
	private void initializeStringBuilders() {
		collectionDescription = new StringBuilder();
		sourceDescription = new StringBuilder();
		sourceAuthor = new StringBuilder();
		sourceTitle = new StringBuilder();
	}
	
	//initialize paragraph information to prepare for analysis
	private void initializeParagraphVariables() {
		paragraphObj = paragraphList.get(curParIndex);		//get current xwpf paragraph being analyzed
		paragraphText = paragraphObj.getText();			//get current paragraph in string form
		paragraphRuns = paragraphObj.getRuns();						//get list of runs for current paragraph	
	}
	
	private int getSourceNumber(String text) {
		//extract source number from paragraph text
		return Integer.parseInt(text.substring(sourceNumberMatcher.start(), sourceNumberMatcher.end() - 1));
	}
	
	//record source information and reset string builder for next source
	private void finalizePreviousSource() {
		source.setAuthor(sourceAuthor.toString());		//text between source number and source title is author
		source.setTitle(sourceTitle.toString());
		source.setDescription(sourceDescription.toString().replace("**&", ":")		//replace temp false delimiter colon symbol with real colon
															.replace("	", "   "));	//reduce whitespace of tabs by substituting spaces for full tab
		collection.addSource(source); 
	}
	
	private void initializeSource() {
		source = new Source(collectionName, getSourceNumber(paragraphText));		
	}
	
	private void parseAuthorTitleDescription() {		
		//analyze runs of current paragraph to extract title and author
		for (int runIndex = getSourceNumberEndingIndex(); runIndex < paragraphRuns.size(); runIndex++) {					
			curRun = paragraphRuns.get(runIndex);								//get run at current index from list of runs		
			if(isDescription(curRun)) {
				sourceDescription.append(curRun.toString());
			}
			else if(isAuthor(curRun)) {				//current run contains author information
				sourceAuthor.append(curRun.toString());
			}
			else if(isTitle(curRun)){
				runIndex = parseTitle(runIndex);
			}					
		}
		//create new line in description because more misc. info may be added later
		sourceDescription.append("\n");
	}
	
	//determines where source number ends as indicated by first occurrence of period in source
	//this accounts for cases where recorder of inventory changed formatting within source number,
	//causing source number to span multiple text runs
	private int getSourceNumberEndingIndex() {
		String runText;
		for(int i = 0; i < paragraphRuns.size(); i++) {
			runText = paragraphRuns.get(i).toString();
			if(containsEndOfSourceNumber(runText)) {
				return i + 1;		//run after current will be starting index
			}
		}
		return paragraphRuns.size();	//
	}
	
	private boolean containsEndOfSourceNumber(String runText) {
		return runText.indexOf(".") != -1;
	}
	
	private boolean isAuthor(XWPFRun run) {
		return ( source.getTitle() == null 					//title yet to be recorded (author information occurs before title information)
			&& (!run.isItalic()							//text not italicized (indicator of title)							
			|| (run.toString().toLowerCase().indexOf("sic") == 0)));	//or if italicized, italicized word is sic, which can occur in title
	}
	
	private boolean isDescription(XWPFRun run) {
		//if title has been recorded, all other information in paragraph will be part of description
		return sourceTitle.length() != 0;
	}
	
	private boolean isTitle(XWPFRun run) {
		if(titleNotRecorded()) {
			if(run.isItalic() && isNotFalseTitleIndicator(run)){	//italicized text indicates title
				return true;
			}
		}
		return false;
	}
	
	private boolean titleNotRecorded() {
		return source.getTitle() == null;	//title has not been recorded if null
	}
	
	private boolean isNotFalseTitleIndicator(XWPFRun run) {
		//italicized 'sic' can be false indicator of title
		return run.toString().toLowerCase().indexOf("sic") != 0;
	}
	
	/**
	 * 
	 * @return Run index of where parsing left off
	 */
	private int parseTitle(int runIndex) {
		sourceTitle.append(curRun.toString());										//record current run as title
		while(runIndex + 1 < paragraphRuns.size() && isTitle(getNextRun(runIndex))) {	//next run is title
			sourceTitle.append(getNextRun(runIndex++).toString());					//record run as title and increment			
		}
		return runIndex;
	}
	
	private XWPFRun getNextRun(int runIndex) {
		return paragraphRuns.get(runIndex + 1);		
	}
	
	private boolean isInscription(XWPFParagraph par) {		
		return par.getText().toLowerCase().indexOf("inscription:") != -1 || //text contains one of two
				par.getText().toLowerCase().indexOf("inscriptions:") != -1;	//	indicators of inscription
	}
	
	private void parseAndSaveInscription() {
		String inscriptionText = getParsedInscription();
		source.setInscription(inscriptionText);
		
	}
	
	//pull inscription from document; invoked after inscription has already been detected
	private String getParsedInscription() {
		String inscription = paragraphText.substring(paragraphText.indexOf(":") + 2);	//inscription content starts after colon followed by space
		while(inscriptionContinues()) {
			//TODO consider initiating paragraph variables heres
			inscription += "\n" + paragraphList.get(++curParIndex).getText();
		}
		return inscription.replace("		", "   ");	//replace tabs with spaces to reduce tab width
	}
	
	private boolean inscriptionContinues() {
		//indentation indicates that inscription continues
		return paragraphList.get(curParIndex + 1).getText().startsWith("		");
	}
				
	private boolean hasCallNumber(XWPFParagraph paragraph) {
		for(XWPFRun run: paragraph.getRuns()) {
			if(run.isBold())					//call number indicated by bold text
				return true;
		}			
		return false;		
	}
	
	private void parseAndSaveCallNumber() {
		String callNumber = getParsedCallNumber(paragraphObj);
		source.setCallNumber(callNumber);
	}

	//search current run for call number and return call number in string form
	private String getParsedCallNumber(XWPFParagraph paragraph) {
		String callNumStr;
		if(source.getCallNumber() == null) callNumStr = "";
		else callNumStr = source.getCallNumber() + "\n";
		for(XWPFRun run: paragraph.getRuns()) {
			if(run.isBold())	//call number indicated by bold text
				callNumStr += run.toString();
		}
		return callNumStr.length() > 0 ? callNumStr : null; 
	}

	private boolean entryFound(XWPFParagraph paragraph) {		
		String parText = paragraph.getText();
		//check for various indicators of entries
		return hasCommonEntryIndicator(parText) || hasCrypticEntryIndicator(parText);
	}	

	private boolean hasCommonEntryIndicator(String text) {
		return text.indexOf("MS. music entries:") != -1;
	}

	private boolean hasCrypticEntryIndicator(String parText) {
		if(isSecondToLastParagraph(curParIndex)) return false;	//when second to last paragraph in document, not entry indicator
		XWPFParagraph nextParagraph = paragraphList.get(curParIndex + 1);
		return (isProbableEntryIndicator(parText) && isProbableEntry(nextParagraph));
	}
	
	private boolean isSecondToLastParagraph(int index) {
		return index + 1 == paragraphList.size();	//next increment will equal size
	}

	private boolean isProbableEntryIndicator(String parText) {
		//check for various signs that indicate probable presence of entry indicator
		return (parText.indexOf("MS.") != -1 &&		//most common sign of entry indicator 
				(parText.indexOf("music") != -1 || parText.indexOf("entries") != -1) &&	//when both words present, is often sign of indicator
					parText.indexOf(":") != -1 &&			//colon is common sign of entry indicator
					parText.indexOf(":")  > parText.indexOf("MS.") &&	//colon occurs after MS. (this removes false positives)
					paragraphList.get(curParIndex + 1).getText().indexOf("MS.") == -1);	//next paragraph does not contain phrase (removes false positive)
	}

	private boolean isProbableEntry(XWPFParagraph paragraph) {
		String parText = paragraph.getText();
		//melodic incipit detected after current paragraph
		return (hasMelodicIncipit(paragraph) || 
				(parText.indexOf(":") < 60 //early colon indicates likely entry
						&& parText.indexOf(":") >= 0));	//make sure -1 is not index
		
	}
	
	private boolean hasMelodicIncipit(XWPFParagraph par) {	//loose application if isMelodicIncipit method to detect if paragraph contains incipit	
		String parText = par.getText();
		return EntryParser.isMelodicIncipit(parText) && !hasCallNumber(par);	//call numbers can be false indicator of melodic incipit
	}
	
	//parse current section of entries
	private void parseAndSaveSourceEntries() throws Exception{
		constructRoughEntries();
		parseAndSaveEntries();
	}
	
	private void constructRoughEntries() {		
		curParIndex++;						//this will make erroneous text(entry indicator) be discarded
		roughEntries = new RoughEntries();
		initializeEntry();
		//separate each entry into a text of its own in preparation for analysis	
		initializeEntryParagraph();
		while(isEntry(paragraphObj)) {		
			if(isNewEntry(paragraphText) && !isFirstSourceEntry(entryStrBuilder)) {
				saveRoughEntry();
				initializeEntry();	//for next iteration
			}		
			if(!isSecular(paragraphObj)) {
				entryIsSecular = false;
			}			
			entryStrBuilder.append(paragraphText);	//add current paragraph to current entry string						
			curParIndex++;		
			initializeEntryParagraph();
		}		
		curParIndex--;		//decrement index because it will be incremented an extra time in higher-level loop
		saveRoughEntry();	///last rough entry for source
	}	
	
	private void initializeEntry() {
		entryIsSecular = true;					//entry secular by default
		entryStrBuilder= new StringBuilder();			
	}
	
	private boolean isEntry(XWPFParagraph paragraph) {
		return curParIndex < paragraphList.size() && //end of document not reached
				!hasSourceNumber(paragraph.getText()) 			//new source not found
				&& !hasCallNumber(paragraph);		//call number not found
	}
	
	private void initializeEntryParagraph() {
		if(!endOfDocumentReached()) {
			paragraphObj = paragraphList.get(curParIndex);			//current XWPFparagraph
			//format text
			paragraphText = paragraphObj.getText()
					.replaceAll(Character.toString((char)160),"")	//remove non-whitespace space
					.replace("	", "");								//remove whitespace caused by tabbing
		}
	}
	
	private boolean isNewEntry(String paragraphText) {
		if(preParsed) {
			return paragraphText.indexOf("::") != -1; //:: indicates start of new entry within entry section when doc id pre-parsed
			
		} else {
			return paragraphText.indexOf(":") != -1; //: indicates start of new entry within entry section when doc is not pre-parsed
		}
	}
	
	//determines if first entry of current source
	private boolean isFirstSourceEntry(StringBuilder entryText) {
		return entryText.length() == 0;			//if no entry text previously recorded, it is first entry
	}	
	
	private void saveRoughEntry() {
		roughEntry = new RoughEntry(entryStrBuilder, entryIsSecular); 
		roughEntries.add(roughEntry);		
	}
	
	private void parseAndSaveEntries(){	
		for(RoughEntry roughEntry: roughEntries.toArrayList()) {
			entryParser = new EntryParser(source, roughEntry, preParsed);
			entryParser.parseEntry();	
			source.addEntry(entryParser.getParsedEntry());	//save parsed entry
		}
	}
	
	@SuppressWarnings("unused")
	private void logSourceStats() {
		System.out.println("Current source: " + source.getSourceNumber() + ", Entries: " + roughEntries.getCount());
	}

	boolean isSecular(XWPFParagraph paragraph) {
		for(XWPFRun run: paragraph.getRuns()) {	
			if(run.isSmallCaps()) 			//presence of small caps denotes nonsecular entry
				return false;
			}		
		return true;	//if no small caps found
	}
	
	private void logParsePerformance() {
		System.out.println("Total entries recorded: " + collection.getSources().getEntryCount());
		System.out.println("Total not incipit: " + notIncipitCount);
	}	
	
	@SuppressWarnings("unused")
	private boolean currentSourceEquals(int sourceNumber) {
		return source.getSourceNumber() == sourceNumber;
	}
	
	@SuppressWarnings("unused")
	private void printSourceRuns() {
		for(int i = 0; i < paragraphRuns.size(); i++) {
			System.out.println("Run index: " + i + " Run text: " + paragraphRuns.get(i).toString());
		}
	}	
}
	