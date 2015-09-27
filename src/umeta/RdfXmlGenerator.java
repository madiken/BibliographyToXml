package umeta;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import umeta.Publication.publicationTypes;

public class RdfXmlGenerator {
	
	
	private static final Namespace rdf = Namespace.getNamespace("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
	private static final Namespace core = Namespace.getNamespace("core", "http://isir.ras.ru/namespace/");
	private static final Namespace dcterms = Namespace.getNamespace("dcterms", "http://purl.org/dc/terms/");
	private static final Namespace rdfs = Namespace.getNamespace("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
	private static final Namespace pcv = Namespace.getNamespace("pcv", "http://prismstandard.org/namespaces/1.2/pcv/");
	private static final Namespace isir = Namespace.getNamespace("isir",  "http://isir.ras.ru/namespace/");
	private static final Namespace kernel = Namespace.getNamespace("kernel", "http://umeta.ru/namespaces/platform/kernel/");
	private static final Namespace cv = Namespace.getNamespace("cv", "http://umeta.ru/namespaces/blocks/auxiliary/controlled-vocabularies/");
	private static final Namespace tcv = Namespace.getNamespace("tcv", "http://umeta.ru/namespaces/blocks/auxiliary/rubricator/");
	private static final Namespace aux = Namespace.getNamespace("aux", "http://umeta.ru/namespaces/blocks/auxiliary/");
	private static final Namespace classifier = Namespace.getNamespace("classifier", "http://umeta.ru/namespaces/blocks/services/classifier/");
	private static final Namespace dc = Namespace.getNamespace("dc", "http://purl.org/dc/elements/1.1/");
	
	private static final Map<Publication.publicationTypes, String> pubTypes = new HashMap<Publication.publicationTypes, String>();
	static {
		pubTypes.put(publicationTypes.issueOfaMagazine, "http://isir.ras.ru/namespace/PublicationType#iss");		
		pubTypes.put(publicationTypes.conferenceDigest, "http://isir.ras.ru/namespace/PublicationType#prc");		
		pubTypes.put(publicationTypes.paperAtConference, "http://isir.ras.ru/namespace/PublicationType#apr");		
		pubTypes.put(publicationTypes.fiction, "http://isir.ras.ru/namespace/PublicationType#fict");
		pubTypes.put(publicationTypes.document, "http://isir.ras.ru/namespace/PublicationType#doc");
		pubTypes.put(publicationTypes.paperinMagazine, "http://isir.ras.ru/namespace/PublicationType#amg");		
		pubTypes.put(publicationTypes.magazine, "http://isir.ras.ru/namespace/PublicationType#mag");		
		pubTypes.put(publicationTypes.monograph, "http://isir.ras.ru/namespace/PublicationType#book");
		pubTypes.put(publicationTypes.unknown, "http://isir.ras.ru/namespace/PublicationType#unk");
		pubTypes.put(publicationTypes.boundVolumesOftheMagazines, "http://isir.ras.ru/namespace/PublicationType#vol");
    }
	
    static String utfString(String s) throws UnsupportedEncodingException{
    	byte ptext[] = s.getBytes("ISO-8859-1"); 
		String value = new String(ptext, "UTF-8"); 
		return value;
    }

	public static Element generatePersonRdfXml(Person author)
	{
		try{
		Element personEl = new Element("Person", isir);
		personEl.setAttribute("resource", author.getUri(), rdf);
		
		if (author.getName() != null) {
			String firstName = author.getName().getFirstName();
			String middleName = author.getName().getMiddleName();
			String lastName = author.getName().getLastName();
			
			if (((firstName != null) && (firstName.length() > 0 )) || 
					((middleName != null) && (middleName.length() > 0 )) ||
					((lastName != null) && (lastName.length() > 0 ))){
			
				Element personName = new Element("personName", core);
				personName.setAttribute("parseType", "Resource", rdf);
				
				Element lang = new Element("language", dc);
				lang.setText(toUtf(StringEscapeUtils.escapeXml(("ru"))));
				personName.addContent(lang);
				
				if ((lastName != null) && (lastName.length() > 0 )){
					Element last = new Element("last", core);		
					last.setText(toUtf(StringEscapeUtils.escapeXml((author.getName().getLastName()))));
					personName.addContent(last);
				}
				
				if ((middleName != null) && (middleName.length() > 0 )){
					Element middle = new Element("middle", core);
					middle.setText(toUtf(StringEscapeUtils.escapeXml((author.getName().getMiddleName()))));
					personName.addContent(middle);
				}
				
				if ((firstName != null) && (firstName.length() > 0 )){
					Element first = new Element("first", core);
					first.setText(toUtf(StringEscapeUtils.escapeXml((author.getName().getFirstName()))));
					personName.addContent(first);
					
				}
				
				personEl.addContent(personName);
			}
		}
		
		if ((author.getEmail() != null) && ((author.getEmail().length() > 0))){
			Element email = new Element("email", isir);
			Element emailValue = new Element("value", rdf);
			emailValue.setText(toUtf(StringEscapeUtils.escapeXml(author.getEmail())));
			email.addContent(emailValue);
			personEl.addContent(email);
		}
		
		if ((author.getKeywords() != null) && (author.getKeywords().length() > 0) ){
			Element keywords = new Element("keywords", isir);
			Element keywordsValue = new Element("value", rdf);
			keywordsValue.setText(toUtf(StringEscapeUtils.escapeXml((author.getKeywords()))));
			Element keywordslang = new Element("language", dc);
			keywordslang.setText(toUtf(StringEscapeUtils.escapeXml(("ru"))));
			keywords.addContent(Arrays.asList(keywordslang, keywordsValue));
			
			personEl.addContent(keywords);
		}	
		
		return personEl;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return null;
		
	}
	
	static boolean isEmpty(String str){
		return ((str == null) || str.isEmpty());
	}
	
	static String toUtf(String s) throws UnsupportedEncodingException{
		if (s == null)
			return "";
		byte ptext[] = s.getBytes("ISO-8859-1"); 
		String value = new String(ptext, "UTF-8"); 
		return value;
	}
	public static Element generatePublicationRdfXml(Publication publication) throws UnsupportedEncodingException
	{
		Element publicationEl = new Element("Publication", core);
		if (!isEmpty(publication.getUri()))
			publicationEl.setAttribute("resource", publication.getUri(), rdf);
		
		//<publicationType>
		if (publication.getPublicationType() != null){
			Element pubType = new Element("publicationType", core);
			pubType.setAttribute("about", pubTypes.get(publication.getPublicationType()) ,rdf);
			publicationEl.addContent(pubType);
		}//</publicationType>
		
		//<mainTitle>
		if (!isEmpty(publication.getMainTitle())){
			Element mainTitle = new Element("mainTitle", core);
			mainTitle.setAttribute("parseType", "Resource", rdf);
			
			Element mainTitleValue = new Element("value", rdf);
			mainTitleValue.setText(toUtf(StringEscapeUtils.escapeXml((publication.getMainTitle()))));
			mainTitle.addContent(mainTitleValue);
			
			Element mainTitleLang = new Element("language", dc);
			mainTitleLang.setText(toUtf(StringEscapeUtils.escapeXml(("ru"))));
			mainTitle.addContent(mainTitleLang);
			
			publicationEl.addContent(mainTitle);
		}
		//</mainTitle>

		//<subTitle>
		if (!isEmpty(publication.getSubTitle())){
			Element subTitle = new Element("subTitle", isir);
			subTitle.setAttribute("parseType", "Resource", rdf);
			
			Element subTitleValue = new Element("value", rdf);
			subTitleValue.setText(toUtf(StringEscapeUtils.escapeXml((publication.getSubTitle()))));
			subTitle.addContent(subTitleValue);
			
			Element subTitleLang = new Element("language", dc);
			subTitleLang.setText(toUtf(StringEscapeUtils.escapeXml(("ru"))));
			subTitle.addContent(subTitleLang);
			
			publicationEl.addContent(subTitle);
		}
		//</subTitle>
		
		//<isPartOf>
		if (publication.getIspartOf() != null){
			for (String uri : publication.getIspartOf() ){
				if (isEmpty(uri)) 
					continue;
				Element isPartOf = new Element("isPartOf", dcterms);
				isPartOf.setAttribute("about", uri, rdf);
				publicationEl.addContent(isPartOf);
			}
		}
		//</isPartOf>
		
		//</issued>
		if (publication.getIssuedDate() != null){
			Element issued = new Element("issued", dcterms);
			Element dateValue = new Element("dateValue", isir);			
			DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
			//System.out.println("df.format " + df.format(publication.getIssuedDate()));
			dateValue.setText(toUtf(StringEscapeUtils.escapeXml((df.format(publication.getIssuedDate())))));
			issued.addContent(dateValue);
			
			publicationEl.addContent(issued);
		}
		//</issued>
		
		//<publicationLanguage>
		if (!isEmpty(publication.getLanguage())){
			Element language = new Element("publicationLanguage", isir);
			
			Element name = new Element("name", isir);
			Element full = new Element("full", isir);
			full.setText(toUtf(StringEscapeUtils.escapeXml((publication.getLanguage()))));
			name.addContent(full);
			
			Element lang = new Element("language", dc);
			lang.setText(toUtf(StringEscapeUtils.escapeXml(("ru"))));
			name.addContent(lang);
			
			language.addContent(name);			
		}
		//</publicationLanguage>
		
		//<pages>
		if (!isEmpty(publication.getPages())){

			Element pages = new Element("pages", isir);			
			pages.setText(toUtf(StringEscapeUtils.escapeXml((publication.getPages()))));
			publicationEl.addContent(pages);
		}
		//</pages>
		
		//<fullText>
		if ( !isEmpty(publication.getFullTextUrl()) || !isEmpty(publication.getRest()) ){
			Element fullText = new Element("fullText", isir);
			
			if (!isEmpty(publication.getFullTextUrl())){
				String escapedXml = toUtf(StringEscapeUtils.escapeXml(publication.getFullTextUrl()));
	
				Element fileName = new Element("fileName", aux);
				fileName.setText(toUtf(StringEscapeUtils.escapeXml((escapedXml))));
				fullText.addContent(fileName);
				
				Element href = new Element("href", aux);
				href.setText(toUtf(StringEscapeUtils.escapeXml((escapedXml))));
				fullText.addContent(href);
			}
			Element lang = new Element("language", dc);
			lang.setText(toUtf(StringEscapeUtils.escapeXml(("ru"))));
			fullText.addContent(lang);
			
			//<rest>
			if (!isEmpty(publication.getRest())){
				Element textContent = new Element("textContent", isir);			
				
				 
				textContent.setText(toUtf(StringEscapeUtils.escapeXml(( toUtf(StringEscapeUtils.escapeXml(publication.getRest()))))));
				fullText.addContent(textContent);
			}
			//</rest>
			
			publicationEl.addContent(fullText);
		}
		//</fullText>
		
		
		//<author>
		if (publication.getAuthor() != null){
			for (String uri : publication.getAuthor()){
				if (isEmpty(uri)) 
					continue;
				Element author = new Element("author", core);
				author.setAttribute("resource", uri, rdf);
				publicationEl.addContent(author);
			}
		}		
		//</author>
		
		//<abstract>
		if (!isEmpty(publication.getAnnotation())){
			Element annotation = new Element("abstract", dcterms);

			Element textContent = new Element("textContent", isir);
			textContent.setText(toUtf(StringEscapeUtils.escapeXml((publication.getAnnotation()))));
			annotation.addContent(textContent);
			
			Element lang = new Element("language", dc);
			lang.setText(toUtf(StringEscapeUtils.escapeXml(("ru"))));
			annotation.addContent(lang);
			
			publicationEl.addContent(annotation);		
		}
		//</abstract>
		
		//<identifier> issn
		if (!isEmpty(publication.getIssn())){
			Element identifier = new Element("identifier", dc);
			
			Element type = new Element("type", rdf);
			type.setText(toUtf(StringEscapeUtils.escapeXml(("http://isir.ras.ru/namespace/ISSN"))));
			identifier.addContent(type);	
			
			Element value = new Element("value", rdf);
			value.setText(toUtf(StringEscapeUtils.escapeXml((publication.getIssn()))));
			identifier.addContent(value);	
			
			publicationEl.addContent(identifier);		
		}
		//</identifier> issn
		
		//<identifier> doi
		if (!isEmpty(publication.getDoi())){
			Element identifier = new Element("identifier", dc);
			
			Element type = new Element("type", rdf);
			type.setText(toUtf(StringEscapeUtils.escapeXml(("http://isir.ras.ru/namespace/DOI"))));
			identifier.addContent(type);	
			
			Element value = new Element("value", rdf);
			value.setText(toUtf(StringEscapeUtils.escapeXml((publication.getDoi()))));
			identifier.addContent(value);	
			
			publicationEl.addContent(identifier);		
		}
		//</identifier> doi
				
		//<keywords>
		if (!isEmpty(publication.getKeywords())){
			Element keywords = new Element("keywords", isir);
			
			Element lang = new Element("language", dc);
			lang.setText(toUtf(StringEscapeUtils.escapeXml(("ru"))));
			keywords.addContent(lang);
			
			Element value = new Element("value", rdf);
			value.setText(toUtf(StringEscapeUtils.escapeXml((publication.getKeywords()))));
			keywords.addContent(value);	
			
			publicationEl.addContent(keywords);		
		}
		//</keywords>
	
		//<publishedPlace>
		if (!isEmpty(publication.getPublisher())){
			Element publishedPlace = new Element("publishedPlace", isir);
			
			Element lang = new Element("language", dc);
			lang.setText(toUtf(StringEscapeUtils.escapeXml(("ru"))));
			publishedPlace.addContent(lang);
			
			Element value = new Element("value", rdf);
			value.setText(toUtf(StringEscapeUtils.escapeXml((publication.getPublisher()))));
			publishedPlace.addContent(value);	
			
			publicationEl.addContent(publishedPlace);		
		
		}
		//</publishedPlace>
		
		//<publishingHouse>
		if (!isEmpty(publication.getPublisherAddress())){
			Element publishingHouse = new Element("publishingHouse", isir);
			
			Element lang = new Element("language", dc);
			lang.setText(toUtf(StringEscapeUtils.escapeXml(("ru"))));
			publishingHouse.addContent(lang);
			
			Element value = new Element("value", rdf);
			value.setText(toUtf(StringEscapeUtils.escapeXml((publication.getPublisherAddress()))));
			publishingHouse.addContent(value);	
			
			publicationEl.addContent(publishingHouse);		
		
		}
		//</publishingHouse>
		
		return publicationEl;
	
	}
	
	
	
	public static void generateRdfXml(
									   String fileName,
									   Collection<Publication> publications, 
									   Collection<Publication> journals, 
									   Collection<Publication> volumes,
									   Collection<Publication> numbers,
									   Collection<Person> authors
									) throws ParserConfigurationException, TransformerException, IOException
	{
		Element rootElement = new Element("RDF", rdf);
		Document doc = new Document(rootElement);
		
		rootElement.addNamespaceDeclaration(rdf);
		rootElement.addNamespaceDeclaration(core);
		rootElement.addNamespaceDeclaration(dcterms);
		rootElement.addNamespaceDeclaration(rdfs);
		rootElement.addNamespaceDeclaration(pcv);
		rootElement.addNamespaceDeclaration(isir);		
		rootElement.addNamespaceDeclaration(kernel);
		rootElement.addNamespaceDeclaration(cv);
		rootElement.addNamespaceDeclaration(tcv);
		rootElement.addNamespaceDeclaration(aux);
		rootElement.addNamespaceDeclaration(classifier);
		rootElement.addNamespaceDeclaration(dc);
		
		if (authors != null)
		for (Person p : authors)
			rootElement.addContent(generatePersonRdfXml(p));
		
		if (journals != null)
			for (Publication p : journals)
				rootElement.addContent(generatePublicationRdfXml(p));
		
		if (volumes != null)
			for (Publication p : volumes)
				rootElement.addContent(generatePublicationRdfXml(p));
		
		if (numbers != null)
			for (Publication p : numbers)
				rootElement.addContent(generatePublicationRdfXml(p));
		
		if (publications != null)
			for (Publication p : publications)
				rootElement.addContent(generatePublicationRdfXml(p));
		
		
        //System.out.println(doc.toString());
		XMLOutputter xmlOutput = new XMLOutputter();
		xmlOutput.setFormat(Format.getPrettyFormat());
		System.out.println("result : " + fileName);
		xmlOutput.output(doc, new FileWriter(fileName));
	}
	
	static public void main(String[] args) 
	{
		
			
				try {
					BufferedReader bf = new BufferedReader(new FileReader("toutf.xml"));
					
					String line;
					FileWriter fr = new FileWriter("utf.xml");
					while ((line = bf.readLine())!=null)
					//	fr.write(toUtf(StringEscapeUtils.escapeXml(line + "\n")));
					
					fr.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			
	
	}
	

}
