package umeta;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import umeta.Person.PersonName;

public class BibXmlConverter {

	static Collection<Publication> getVolumes(Map<String, HashMap<Integer, Publication>> volumes)
	{
		Collection<Publication> result = new ArrayList<Publication>();
		
		for ( HashMap<Integer, Publication> vs : volumes.values()){
			result.addAll(vs.values());
		}
		return result;
	}

	static Collection<Publication> getNumbers(Map<String, HashMap<Integer, HashMap<Integer, Publication>>> numbers)
	{
		Collection<Publication> result = new ArrayList<Publication>();
		
		for (HashMap<Integer, HashMap<Integer, Publication>> vs : numbers.values() )
			for ( HashMap<Integer, Publication> ns : vs.values()){
				result.addAll(ns.values());
			}
		return result;
	}
	

	public static void main(String args[]){
		try {
		String fileName = null;
		String confFileName = null;
		if (args.length > 0)
			fileName = args[0];
		
		if (fileName == null)
			//fileName = "test//scopus.bib";
			throw new IllegalArgumentException("Please, provide bib file name (first argument)");
		
		if (args.length > 1)
			confFileName = args[1];
		
		if (confFileName == null)
	//		confFileName = "db_conf.txt";
			throw new IllegalArgumentException("Please, provide db config file name (second argument)");
		
		String resultFileName = null;
		if (args.length > 2)
			resultFileName = args[2];
		
		if (resultFileName == null)
			resultFileName = "result.xml";
		
		Map<String, String> connectionProps;
		connectionProps = ConfigManager.getConfigsFromFile(confFileName);
			
		
		DBConnectionManager.configConnection(connectionProps.get("url"), connectionProps.get("user"), connectionProps.get("password"));
	
			
		
		Integer startDescriptionIndex;
		StringBuilder currPubString = null;
		Map<String, Publication> publications = new HashMap<String, Publication>();
		Map<PersonName, Person> authors = new HashMap<PersonName, Person>();
		Map<String, Publication> journals = new HashMap<String, Publication>();
		Map<String, HashMap<Integer, Publication>> volumes = new HashMap<String, HashMap<Integer, Publication>>();
		Map<String, HashMap<Integer, HashMap<Integer, Publication>>> numbers = 
				new HashMap<String, HashMap<Integer, HashMap<Integer, Publication>>>();
		
		
		
		File input = new File(fileName);//("test//WoS.bib");
		
			BufferedReader br = new BufferedReader(new FileReader(input));
			String thisLine;
			
			int counter = 0;
			while((thisLine = br.readLine()) != null){
				startDescriptionIndex = Utils.findBibDescrioptionStart(thisLine); 
				//current publication is over
				if (startDescriptionIndex != null){  
					if (currPubString != null) {
						currPubString.append(thisLine.substring(0, startDescriptionIndex));
						BibContentExtracter.parsePublication(currPubString.toString(), publications, authors, journals, volumes, numbers);
					}
					
					currPubString = new StringBuilder();
					currPubString.append(thisLine.substring(startDescriptionIndex));
					counter++;
				}
				else 
					if (currPubString != null)
						currPubString.append(thisLine);	
					
				
			}
			
			if (currPubString != null)
				BibContentExtracter.parsePublication(currPubString.toString(), publications, authors, journals, volumes, numbers);
			System.out.println("counter : "+ counter);
			
			
			System.out.println("Journals----------------------------------");
			for (Publication j : journals.values()){
				System.out.println("---------------");
				j.print();
				
				System.out.println("Volumes-----<<<");
				if (volumes.containsKey(j.getMainTitle()))
				for (Publication v : volumes.get(j.getMainTitle()).values()){
					v.print();
					
					System.out.println("Numbers-----<<<");
					Map<Integer, HashMap<Integer, Publication>> volumeNums = numbers.get(j.getMainTitle());
					if (volumeNums != null){
						
						HashMap<Integer, Publication> nums = volumeNums.get(v.getNumber());
						for (Publication n : numbers.get(j.getMainTitle()).get(v.getNumber()).values()){
							n.print();
						}
					}
					System.out.println("Numbers----->>>");
				}
				System.out.println("Volumes----->>>>");
			}
			
			
			
			System.out.println("Publications----------------------------------");
			System.out.println(publications.values().size());
			for (Publication p : publications.values()){
				System.out.println("---------------");
				p.print();
			}
			System.out.println("Persons----------------------------------");
			for (Person p : authors.values()){
			    p.print();
			}
				
			
	
				RdfXmlGenerator.generateRdfXml(resultFileName, publications.values(), journals.values(), getVolumes(volumes), getNumbers(numbers), authors.values());
			
			
		}catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		} 
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
