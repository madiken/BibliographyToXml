package umeta;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import umeta.Person.PersonName;
import umeta.Publication.publicationTypes;


public class BibContentExtracter {
	
	static Random random = new Random(new Date().getTime());
	
	enum tags{
		author,//->person object
	//	authorEmail,// it's not clear which of authors has this e-mail
		authorKeywords,//->person object
		keywordsPlus,//->person object
		
		journal, //->publication object
		volume, //->publication object
		number, //->publication object
		abbrevSourceTitle,//publication object
		
		//simple properties
		title, //+		
		year,  //+
		month, //+		
		pages, //+
		url,   //+
		annotation, //+
		keywords, //+
		//references, //
		publisher, //+
		correspondenceAddress,//+
		issn,//+
		doi,//+
		language,//+		
		documentType,//+		
		uniqueID //+
	}
		
    static Map<String, tags> textTags = new HashMap<String, tags>();
    
	static{
		textTags.put("author", tags.author);
		textTags.put("author_keywords", tags.authorKeywords);
		textTags.put("keywords-plus", tags.keywordsPlus);
	//	textTags.put("author-email", tags.authorEmail);
		textTags.put("keywords", tags.keywords);
		textTags.put("title", tags.title);
		textTags.put("journal", tags.journal);
		textTags.put("year", tags.year);
		textTags.put("month", tags.month);
		textTags.put("pages", tags.pages);
		textTags.put("volume", tags.volume);
		textTags.put("number", tags.number);
		textTags.put("url", tags.url);
		textTags.put("abstract", tags.annotation);
		textTags.put("publisher", tags.publisher);		
		textTags.put("correspondence_address1", tags.correspondenceAddress);
		textTags.put("address", tags.correspondenceAddress);		
		textTags.put("issn", tags.issn);
		textTags.put("doi", tags.doi);
		textTags.put("language", tags.language);		
		textTags.put("abbrev_source_title", tags.abbrevSourceTitle);
		textTags.put("journal-iso", tags.abbrevSourceTitle);		
		textTags.put("type", tags.documentType);		
		textTags.put("document_type", tags.documentType);
		textTags.put("unique-id", tags.uniqueID);
		
		//loader doesn't work correctly with this data :
		//textTags.put("cited-references", tags.references);
		//textTags.put("references", tags.references);			
	}
	
	static String generatePublicationUri(String typePrefix, String uniqueID)
	{
		DateFormat df = new SimpleDateFormat("dd-MM-yy");
		return typePrefix + random.nextInt(Integer.MAX_VALUE) + ":" + uniqueID + "/" + df.format(new Date());
	}
	
	static String generatePersonUri(String prefix, PersonName name)
	{
		DateFormat df = new SimpleDateFormat("dd-MM-yy");
		return prefix + random.nextInt(Integer.MAX_VALUE) + ":" + name.getLastName() +"/" + df.format(new Date()); 
	}
	

	
	static String generateJournalUri(String prefix)
	{
		DateFormat df = new SimpleDateFormat("dd-MM-yy");
		return prefix + random.nextInt(Integer.MAX_VALUE) + ":" + df.format(new Date());
	}

	static String generateVolumeUri(String prefix, Integer volumeNumber, Date articleDate){
		DateFormat df = new SimpleDateFormat("dd-MM-yy");
		return prefix + random.nextInt(Integer.MAX_VALUE) + ":" + df.format(new Date()) + "/" + "volume" + 
					volumeNumber +"/year" + articleDate.getYear() ;
	}
	
	static String generateNumberUri(String prefix, Integer volumeNumber, Integer number, Date articleDate){
		DateFormat df = new SimpleDateFormat("dd-MM-yy");
		return prefix + random.nextInt(Integer.MAX_VALUE) + ":" + df.format(new Date()) + "/" + "volume" + 
					volumeNumber + "/number"+ number  +"/year" + articleDate.getYear();
	}
	
	static Date getDateFromMonthField(Date d, String dateStr)
	{
		System.out.println("getDateFromMonthField " + dateStr);
		dateStr = dateStr.toLowerCase().trim();
		DateFormat monthdate = new SimpleDateFormat("MMM dd", new Locale("en"));
		DateFormat month = new SimpleDateFormat("MMM", new Locale("en"));
	
		try {
			Date dd = null;
			if (dateStr.matches("[a-z]{3}[\\s]+[0-9]{1,2}")){
				dd = monthdate.parse(dateStr);
				d.setMonth(dd.getMonth());
				d.setDate(dd.getDate());				
			}
			else if (dateStr.matches("[a-z]{3}")){
				dd = month.parse(dateStr);
				d.setMonth(dd.getMonth());
			}	
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return d;
	}
	
	static String getPersonUriFromDataBase(PersonName name)
	{
		return null;
	}
	
	public static String getJournalUriFromDataBase(String title)
	{
		return null;
	}

	static String getVolumeUriFromDataBase(String journalUri, Integer volumeNumber){
		return null;
	}
	
	static String getNumberUriFromDataBase( Integer volumeUri, String number){
		return null;
	}
	
    static Collection<String> updateAuthors(Collection<PersonName> names, Map<PersonName, Person> authors)
    {
    	
    	Collection<String> uris = new ArrayList<String>();
    	for (PersonName name : names){
    		
    		if (authors.containsKey(name)){
    			uris.add(authors.get(name).getUri());
    			continue;
    		}	
    		String uri = DAO.getPersonUriFromDataBase(name);
    		//System.out.println("updateAuthors " + name + " uri " + uri);
    		if (uri == null)
    			uri = generatePersonUri("person", name);
    		Person newPerson = new Person();
    		newPerson.setName(name);
    		newPerson.setUri(uri);
    		authors.put(newPerson.getName(), newPerson);
    		uris.add(newPerson.getUri());
    	}
    	
    	return uris;
    }
	
    static String updateJournals(String title, String subTitle, Map<String, Publication> journals)
    {
    	if (title == null)
    		throw new RuntimeException("updateJournals : JOURNAL TITLE IS NULL");
    	System.out.println("updateJournals : " + title + " " + subTitle +" "+  journals.size());
    	title = title.trim();
    	if (journals.containsKey(title))
    		return journals.get(title).getUri();
    	
    	String uri = DAO.getPublicationUriFromDataBase(title, publicationTypes.magazine);
    	if (uri == null)
    		uri = generateJournalUri("journal");
    	
    	Publication newJournal = new Publication();
    	newJournal.setMainTitle(title);
    	newJournal.setUri(uri);
    	newJournal.setPublicationType(publicationTypes.magazine);
    	if (subTitle != null){
    		subTitle = subTitle.trim();
    		newJournal.setSubTitle(subTitle);
    	}
    	journals.put(title, newJournal);
    	return newJournal.getUri();
    }
    
    //returns volume uri
    static String updateVolumes(Integer volumeNum, String journalTitle,  String journalUri, Date articleDate, 
    		Map<String, HashMap<Integer, Publication>> volumes)
    {

       	
    	if ((journalTitle == null) || (journalUri == null) || (volumeNum == null)) 
    		throw new RuntimeException("updateVolumes : journalTitle " + journalTitle +" journalUri "+ journalUri+ " volumeNum "+ volumeNum);
    	
    	if (!volumes.containsKey(journalTitle))
    		volumes.put(journalTitle, new HashMap<Integer, Publication>());
    	
   
    	//assumption : journal for this volume should exist already
    	if (volumes.get(journalTitle).containsKey(volumeNum))
    		return volumes.get(journalTitle).get(volumeNum).getUri();
    	
    	String volumeTitle = "volume " + volumeNum;
    	String uri = DAO.getPublicationUriFromDataBaseByParentUri(volumeTitle, publicationTypes.boundVolumesOftheMagazines, journalUri);
    	if (uri == null)
    		uri = generateVolumeUri("volume", volumeNum, articleDate);
    	
    	Publication newVolume = new Publication();
    	
    	newVolume.setMainTitle(volumeTitle);
    	newVolume.setUri(uri);
    	newVolume.setPublicationType(publicationTypes.boundVolumesOftheMagazines);
    	newVolume.setNumber(volumeNum);
    	if (articleDate!=null){
    		Date date = new Date(1, 0 ,1);
    		date.setYear(articleDate.getYear());
    	
    		newVolume.setIssuedDate(date);
    	}
    	newVolume.addIsPartOf(journalUri);
    	
    	volumes.get(journalTitle).put(volumeNum, newVolume);
    	return newVolume.getUri();
    		
    }
    
   
    
    static Collection<String> updateNumbers(String numbersFieldValue, String journalTitle, Integer volumeNumber, String volumeUri, Date articleDate, 
    		Map<String, HashMap<Integer, HashMap<Integer, Publication>>> numbers){
    	
    	Collection<String> uris = new ArrayList<String>();
    	if (numbersFieldValue == null || volumeNumber == null || volumeUri == null) 
    		//return null;
    		throw new RuntimeException("updateNumbers : numbersFieldValue " + numbersFieldValue +" volumeNumber "+ volumeNumber+ " volumeUri "+ volumeUri);
    	
    	Collection<Integer> numbersRange = Utils.getNumbersRange(numbersFieldValue);
    	
    	if (!numbers.containsKey(journalTitle))
    		numbers.put(journalTitle, new HashMap<Integer, HashMap<Integer, Publication>>());
    	
    	if (!numbers.get(journalTitle).containsKey(volumeNumber))
    		numbers.get(journalTitle).put(volumeNumber, new HashMap<Integer, Publication>());
    	
    	//assumption : journal  and volume for these numbers should exist already
    	Map<Integer, Publication> journalVolNumbers = numbers.get(journalTitle).get(volumeNumber);
    	Date date = new Date(1, 0 ,1);
    	
    	for (Integer number : numbersRange){
    		
    		if (journalVolNumbers.containsKey(number)){
    			uris.add(journalVolNumbers.get(number).getUri());
    			continue;
    		}
    		
    		String numberTitle = "number " + number;
    		String uri = DAO.getPublicationUriFromDataBaseByParentUri(numberTitle, publicationTypes.issueOfaMagazine, volumeUri);
    		if (uri == null)
    			uri = generateNumberUri("number", volumeNumber, number, articleDate);
    		
    		Publication newNumber = new Publication();
    		newNumber.setUri(uri);
    		newNumber.setPublicationType(publicationTypes.issueOfaMagazine);
    		newNumber.setMainTitle(numberTitle);
    		newNumber.setNumber(number);
    		if (articleDate!=null){        		
        		date.setYear(articleDate.getYear());        	
        		newNumber.setIssuedDate(date);
        	}
    		newNumber.addIsPartOf(volumeUri);
    		
    		journalVolNumbers.put(number, newNumber);
    		uris.add(uri);
    	}
    	
    	return uris;
    }
    
    
	static void parsePublication (  String currPubString, 
									Map<String, Publication> publications, 
									Map<PersonName, Person> authors,
									Map<String, Publication> journals,
									Map<String, HashMap<Integer, Publication>> volumes,
									Map<String, HashMap<Integer, HashMap<Integer, Publication>>> numbers
									)
	{
		System.out.println(currPubString);
		currPubString.trim();
		if (!currPubString.matches("[@]{1}[^{}]+[{]{1}[^,]+[,]{1}.+[}]{1}"))
			return;
		
		String uniqueId = Utils.findUniqueID(currPubString);
		if (publications.containsKey(uniqueId)) 
			return; //already present
		
		Publication newPub = new Publication();
		newPub.setUniqueID(uniqueId);
		//System.out.println(uniqueId);

		String properties = currPubString.substring(currPubString.indexOf(uniqueId) + 1 + uniqueId.length(), currPubString.length() - 1);
		
		Map<String, String> propMap = Utils.parseProperties(properties);
		
		//for properties that are out of model
		StringBuilder rest = new StringBuilder();
		
		
		String journalTitle = null;
		String journalShortTitle = null;
		String volume = null;
		String number = null;
		String authorKeywords = null; 
		String keywordsPlus = null;
		Collection<PersonName> names = null;
		for (String key : propMap.keySet()){
			
			if (textTags.containsKey(key)){
				
				String value = propMap.get(key);
				
				tags tag = textTags.get(key);
				switch (tag) {
					case title : 
			          newPub.setMainTitle(value); break;
			          
					case year : 
					  if (newPub.getIssuedDate() == null){
						  newPub.setIssuedDate(new Date(1, 0, 1));
					  }
					  Date d = newPub.getIssuedDate();
					  System.out.println("value year " + value);
				      d.setYear(Integer.valueOf(value) - 1900);
				      
				      DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
				      System.out.println("value year df" + df.format(d) + " get Year" + d.getYear());
				      newPub.setIssuedDate(d);
				     
					  break;
					  
					case month : 
						if (newPub.getIssuedDate() == null){
							newPub.setIssuedDate(new Date(1, 0, 1));
						}
					    d =  newPub.getIssuedDate();
					    System.out.println("value month" + value);
					    newPub.setIssuedDate(getDateFromMonthField(d, value));
					    
					    
					    break;
					case url :     
						newPub.setFullTextUrl(value);
						break;
					case pages :     
						newPub.setPages(value);
						break;
					case annotation :
						newPub.setAnnotation(value);
						break;
					case keywords :
						newPub.setKeywords(value);
						break;
					case publisher :
						newPub.setPublisher(value);
						break;	
					case correspondenceAddress:
						newPub.setPublisherAddress(value);
						break;
					case issn :
						newPub.setIssn(value);
						break;		
					case doi :
						newPub.setDoi(value);
						break;	
					case language :
						newPub.setLanguage(value);
						break;	
					case documentType :
						newPub.setPublicationType(value);
						break;							
					case uniqueID :
						newPub.setUniqueID(value);
						break;	
						
					case author :
						names = Utils.getAuthorsNames(value);
						
						
						//newPub.setUniqueID(value);
						break;	
					case journal :
						journalTitle = value;
						break;
					case volume :
						volume = value;
						break;
					case number :
						System.out.println("number " + key + " " + value);
						number = value;
						break;
					case abbrevSourceTitle : 
						journalShortTitle = value;
						break;					
					case authorKeywords :
						authorKeywords = value;
						break;					
					case keywordsPlus : 
						keywordsPlus = value;
						break;
					default : 
						rest.append(key + "=" + "{" + propMap.get(key) + "},\n");
						break;
				}
			} 
			else {
				rest.append(key + "=" + "{" + propMap.get(key) + "},\n");
			}
		}
		
		
		
		Collection<String> uris = updateAuthors(names, authors);
		
		newPub.addAllAuthors(uris);
		
		//publication->isPartOf->number(s)->isPartOf->volume->isPartOf->magazine
		String journalUri = null ;
		String volumeUri = null;
		Integer volumeNumber = null;
		Collection<String> numbersUris = null;
	
		/*
		 * 		textTags.put("abbrev_source_title", tags.abbrevSourceTitle);
		textTags.put("journal-iso", tags.abbrevSourceTitle);	
		 * */
		System.out.println("journalTitle " + journalTitle);
		System.out.println("journalShortTitle " + journalShortTitle);
		
		if (journalTitle != null){
			journalUri = updateJournals(journalTitle, journalShortTitle, journals);
		}
		
    	if (journalUri != null && volume != null){
			if (volume.matches("[0-9]+")){
				volumeNumber = Integer.valueOf(volume);
				volumeUri = updateVolumes(volumeNumber, journalTitle, journalUri, newPub.getIssuedDate(), volumes);		
			}
		}
		
		
		//assumption : if an article has field "number" it always has field "volume" 
		if (volumeUri != null && number != null)
			numbersUris = updateNumbers(number, journalTitle, volumeNumber, volumeUri, newPub.getIssuedDate(), numbers);
		
		if ((numbersUris!=null)&&(numbersUris.size() > 0)){
			newPub.addAllIsPartOf(numbersUris);
		}
		else if (volumeUri!=null){
			newPub.addIsPartOf(volumeUri);
		}
		else if (journalUri != null){
			newPub.addIsPartOf(journalUri);
		}
		
		//append keywords to author's keywords
		
		
		System.out.println("keywordsPlus " + keywordsPlus);
		System.out.println("authorKeywords " + authorKeywords);
		if (names != null)
		for (PersonName name : names){			
			authors.get(name).appendKeywords(keywordsPlus);
			authors.get(name).appendKeywords(authorKeywords);
			
		}
		newPub.setRest(rest.toString());
		String uri = DAO.getPublicationUriFromDataBase(newPub.getMainTitle(),newPub.getPublicationType());
		if (uri  == null)
			uri = generatePublicationUri("article", uniqueId);
		newPub.setUri(uri);
		publications.put(uniqueId, newPub);		
		
	}

}
