package umeta;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;


public class Publication {

	public enum publicationTypes{
		issueOfaMagazine, // Номер журнала
		conferenceDigest, // Труды конференции
		paperAtConference, //Статья в трудах конференции
		fiction, //Художественное произведение
		document, //Документ
		paperinMagazine,  //Статья в журнале
		magazine, //Журнал
		monograph, //монография
		unknown, //неизвестно
		boundVolumesOftheMagazines; //Том журнала
		
		public static publicationTypes getPublicationType(String publicationTypes)
		{
			publicationTypes = publicationTypes.toLowerCase().trim();
			if ("article".equals(publicationTypes))
				return paperinMagazine;
			if ("conference paper".equals(publicationTypes))
				return conferenceDigest;
			
			return unknown;
		}
	}
	private Integer number; // number or volume, not for xml

	private String uniqueID; //unique id, not for xml
	
	private String uri; //+
	private String fullTextUrl;//+
	private String annotation;//+
    private String mainTitle; //+
	private String subTitle; //+ short title(abbrev)
//	private String additionalTitle;
	private Date issuedDate; //+
	private String keywords;//+
	private String pages;//+
	private publicationTypes publicationType; //+	type uri +
	private String language;//+
	private Collection<String> authors = new ArrayList<String>(); //+ author uris
	private Collection<String> ispartOf = new ArrayList<String>(); //+ journal or volume uri
//	private List<String> citedReferences; //pubs uris
	private String publisher; //publisher name
	private String publisherAddress; //publisher address
    //abbrev_source_title, -> to journal
	//volume, -> to another publication
	private String issn;//+
	private String doi;//+
	
	private String rest; //+
	//timesCited ??
	//authorEmail, -> to person
	//authorKeywords, ->to person
	//keywordsPlus -> to person

	public void print(){
		
		System.out.println(" article :" + this.uniqueID);
	
		System.out.println(" annotation :" + this.annotation);
		System.out.println(" doi :" + this.doi);
		System.out.println(" fullTextUrl :" + this.fullTextUrl);
		
		System.out.println(" ispartOf :" + this.ispartOf);
		System.out.println(" authors :" + this.authors);
		System.out.println(" issn :" + this.issn);
		System.out.println(" keywords :" + this.keywords);
		System.out.println(" language :" + this.language);
		System.out.println(" mainTitle :" + this.mainTitle);
		System.out.println(" pages :" + this.pages);
		System.out.println(" publisher :" + this.publisher);
		System.out.println(" publisherAddress :" + this.publisherAddress);
	
		System.out.println(" subTitle :" + this.subTitle);
		System.out.println(" uniqueID :" + this.uniqueID);
		System.out.println(" uri :" + this.uri);
		System.out.println(" number :" + this.number);
		
		
		DateFormat df = new SimpleDateFormat("dd-MM-yy");
		if (issuedDate!=null)
			System.out.println(" issuedDate :" + df.format(this.issuedDate));
		System.out.println(" publicationType :" + this.publicationType);
		
		System.out.println(" rest :" + this.rest);
		
		System.out.println();
		
	}
	
	
	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}
	
	public void addAllIsPartOf(Collection<String> uris)
	{
		ispartOf.addAll(uris);
	}
	
	public void addIsPartOf(String uri)
	{
		ispartOf.add(uri);
			
	}
	
	public void addAllAuthors(Collection<String> uris)
	{
		authors.addAll(uris);
	}
	
	public void addAuthor(String uri)
	{
		authors.add(uri);
	}
	public String getRest() {
		return rest;
	}
	public void setRest(String rest) {
		this.rest = rest;
	}
	public String getPublisherAddress() {
		return publisherAddress;
	}
	public void setPublisherAddress(String publisherAddress) {
		this.publisherAddress = publisherAddress;
	}
	
	public String getUniqueID() {
		return uniqueID;
	}
	public void setUniqueID(String uniqueID) {
		this.uniqueID = uniqueID;
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public String getFullTextUrl() {
		return fullTextUrl;
	}
	public void setFullTextUrl(String fullTextUrl) {
		this.fullTextUrl = fullTextUrl;
	}
	public String getAnnotation() {
		return annotation;
	}
	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}
	public String getMainTitle() {
		return mainTitle;
	}
	public void setMainTitle(String mainTitle) {
		this.mainTitle = mainTitle;
	}
	public String getSubTitle() {
		return subTitle;
	}
	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

	public Date getIssuedDate() {
		return issuedDate;
	}
	public void setIssuedDate(Date issuedDate) {
		this.issuedDate = issuedDate;
	}
	public String getKeywords() {
		return keywords;
	}
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	public String getPages() {
		return pages;
	}
	public void setPages(String pages) {
		this.pages = pages;
	}
	public publicationTypes getPublicationType() {
		return publicationType;
	}
	
	public void setPublicationType(String publicationType) {
		this.publicationType = publicationTypes.getPublicationType(publicationType);
	}
	
	public void setPublicationType(publicationTypes publicationType) {
		this.publicationType = publicationType;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public Collection<String> getAuthor() {
		return authors;
	}
	public void setAuthor(List<String> author) {
		this.authors = author;
	}
	public  Collection<String> getIspartOf() {
		return ispartOf;
	}
	public void setIspartOf( Collection<String> ispartOf) {
		this.ispartOf = ispartOf;
	}
	
	public String getPublisher() {
		return publisher;
	}
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}
	public String getIssn() {
		return issn;
	}
	public void setIssn(String issn) {
		this.issn = issn;
	}
	public String getDoi() {
		return doi;
	}
	public void setDoi(String doi) {
		this.doi = doi;
	}
	
}
