package umeta;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;

import umeta.Person.PersonName;


public class Utils {
	//regexps for names parsing
	static final String LAST_FULL_FIRST_SHORT_MID_SHORT = "[A-Z]{1}[a-z]+(['][A-Z]){0,1}[a-z]+[,]{1}[\\s]*[A-Z]{1}[\\.]{1}[\\s]*[A-Z]{1}[\\.]{1}"; //middle and first name shortened
	static final String LAST_FULL_FIRST_FULL_MID_SHORT  = "[A-Z]{1}[a-z]+(['][A-Z]){0,1}[a-z]+[,]{1}[\\s]*[A-Z]{1}[a-z]+[\\s]*[A-Z]{1}[\\.]{1}";	  //only middle name is shortened
	static final String LAST_FULL_FIRST_FULL_MID_FULL = "[A-Z]{1}[a-z]+(['][A-Z]){0,1}[a-z]+[,]{1}[\\s]*[A-Z]{1}[a-z]+[\\s]*[A-Z]{1}[a-z]+";	  //full name
	static final String LAST_FULL_FIRST_SHORT = "[A-Z]{1}[a-z]+(['][A-Z]){0,1}[a-z]+[,]{1}[\\s]*[A-Z]{1}[\\.]{1}"; //middle and first name shortened
	static final String LAST_FULL_FIRST_FULL  = "[A-Z]{1}[a-z]+(['][A-Z]){0,1}[a-z]+[,]{1}[\\s]*[A-Z]{1}[a-z]+";	  //only middle name is shortened
	
	static String findTypeName(String str)
	{
		
	      String pattern = "[@]{1}[^{}]+[{]{1}"; //(@)[^/{/}]+(\{)

	      // Create a Pattern object
	      Pattern r = Pattern.compile(pattern);

	      // Now create matcher object.
	      Matcher m = r.matcher(str);
	      if (m.find()) {
		      String result = m.group();
		      if ((result != null) && (result.length() >=3 ))
		    	  result = result.substring(1, result.length() -1 ).trim();
		      return result;
	      } else 
	    	  return null;
	}
    
	static String findUniqueID(String str)
	{ 
	      String pattern = "[@]{1}[^{}]+[{]{1}[^,]+[,]{1}"; //[@]{1}[^{}]+[{]{1}[^,]+[,]{1}

	      // Create a Pattern object
	      Pattern r = Pattern.compile(pattern);

	      // Now create matcher object.
	      Matcher m = r.matcher(str);
	      m.find();
	      String result = m.group();
	      if (result == null) 
	    	  return null;
	      
	      pattern = "[{]{1}[^,]+[,]{1}";
	      r = Pattern.compile(pattern);
	      m = r.matcher(str);	     
	     
	      if (m.find()){
	    	  result = m.group();
	    	  if (result != null)
	    		  result = result.substring(1, result.length() -1 ).trim();
	    	  return result;
	      }
	      else return null;
		
	}
	


	static Integer findBibDescrioptionStart(String str)
	{
		
	      String pattern = "[@]{1}[^{}]+[{]{1}"; //[@]{1}[^{}]+[{]{1}[^,]+[,]{1}

	      // Create a Pattern object
	      Pattern r = Pattern.compile(pattern);

	      // Now create matcher object.
	      Matcher m = r.matcher(str);
	      if (m.find())
	         return m.start();
	      return null;
		
	}
	
	

	
	public static Map<String, String> parseProperties(String properties)
	{
		
			Map<String, String> result = new HashMap<String, String>();
			
			String pattern = "[a-zA-Z-_]+[\\s]*[=]{1}[\\s]*[{]{1,2}[^{}]+[}]{1,2}[\\s]*[,]{1}";
			Pattern r = Pattern.compile(pattern);
			Matcher m = r.matcher(properties);
			String propStr;
			String propName;
			String propValue;
			
			while (m.find()){
				propStr = m.group();
				Integer i = propStr.indexOf("=");
				if (i != -1){
					propName = propStr.substring(0, i).toLowerCase().trim();
					propValue = propStr.substring(i+1, propStr.length() - 1).trim(); //= , -откусили
					if (propValue.startsWith("{{"))
						propValue = propValue.substring(2,propValue.length()-2);
					else if (propValue.startsWith("{"))
						propValue = propValue.substring(1,propValue.length()-1);
					
					result.put(propName, propValue);
					//System.out.println("name<" + propName + "> | " + "value<"+ propValue + ">");
				}
					
				//propName
		    }
			  
			return result;
	}
	
	public static Integer getMonth(String month)
	{
		month = month.toLowerCase().trim();
		DateFormat  formatter = new SimpleDateFormat("MMM", new Locale("eng"));
		Date d;
			try {
				d = formatter.parse(month);
				return d.getMonth();
			} catch (ParseException e) {
				e.printStackTrace();
				return null;
			}
	}
	
	static Collection<Integer> getNumbersRange(String value)
	{
		Collection<Integer> result = new ArrayList<Integer>();
		
		if (value.matches(("[0-9]+[\\s]*[-][\\s]*{1}[0-9]+"))){
			String[] parts = value.split("[\\s]*[-][\\s]*");
			Integer start = Integer.valueOf(parts[0]);
			Integer end = Integer.valueOf(parts[1]);
			
			for (Integer i = start; i <=end; i++)
				result.add(i);
				
		}
		else 
		{
			 Pattern r = Pattern.compile("[0-9]+");
			 Matcher m = r.matcher(value);
			 
			 while (m.find()){
				 result.add(Integer.valueOf(m.group()));
			 }
		}
		 
		return result;
	}
	
	static PersonName removeApostrof(PersonName name){
		
		String lastName =  name.getLastName();
		if (lastName != null){
			lastName = lastName.replaceAll("'", "i").toLowerCase();
			String a = lastName.substring(0,  1);
			String b = lastName.substring(1);
			name.setLastName(a.toUpperCase() + b);
		}
			
		String firstName =  name.getFirstName();
		if (firstName != null){
			firstName.replaceAll("'", "i").toLowerCase();
			String a = firstName.substring(0,  1);
			String b = firstName.substring(1);
			name.setFirstName(a.toUpperCase() + b);
		}
			
		String middleName =  name.getMiddleName();
		if (middleName != null){
			middleName.replaceAll("'", "i").toLowerCase();
			String a = middleName.substring(0,  1);
			String b = middleName.substring(1);
			name.setMiddleName(a.toUpperCase() + b);
			
		}
		return name;
		
	}
	
	static Collection<PersonName> getAuthorsNames(String value)
	{
		//System.out..println(value);
		 Set<PersonName> names = new HashSet<PersonName>();
		
		 Pattern r = Pattern.compile(LAST_FULL_FIRST_SHORT_MID_SHORT);
		 Matcher m = r.matcher(value);
		 
		 while (m.find()){
			 //System.out..println("1");
			 String nameStr = m.group();
			 //System.out..println(nameStr);
			 String[] parts = nameStr.split(",");
			 String lastName = parts[0].trim();
			 
			 String[] first_mid = parts[1].trim().split("\\.");
			 
			 String firstName = first_mid[0].trim()+".";
			 String middleName = first_mid[1].trim()+".";
			 
			 PersonName newName = new PersonName(lastName, firstName, middleName);
			 
			 //System.out..println(newName);
			 
			 names.add(removeApostrof(newName));
		 }
		 
		 r = Pattern.compile(LAST_FULL_FIRST_FULL_MID_SHORT);
		 m = r.matcher(value);
		 
		 while (m.find()){
			 //System.out..println("2");
			 String nameStr = m.group();
			 //System.out..println(nameStr);
			 String[] parts = nameStr.split(",");
			 String lastName = parts[0].trim();
			 
			 //System.out..println(parts[1]);
			 String[] first_mid = parts[1].trim().split("[\\s]+");
			 
			// for (int i=0 ; i < first_mid.length; i++)
				 //System.out..println(first_mid[i]);
			 
			 String firstName = first_mid[0].trim();
			 String middleName = first_mid[1].trim();
			 
			 PersonName newName = new PersonName(lastName, firstName, middleName) ;
			 
			 //System.out..println(newName);
			 names.add(removeApostrof(newName));
		 }
		 
		 r = Pattern.compile(LAST_FULL_FIRST_FULL_MID_FULL);
		 m = r.matcher(value);
		 
		 while (m.find()){
			 //System.out..println("3");
			 String nameStr = m.group();
			 //System.out..println(nameStr);
			 String[] parts = nameStr.split(",");
			 String lastName = parts[0].trim();
			 String[] first_mid = parts[1].trim().split("[\\s]+");
			 String firstName = first_mid[0].trim();
			 String middleName = first_mid[1].trim();

			 PersonName newName = new PersonName(lastName, firstName, middleName) ;
			 
			 //System.out..println(newName);
			 names.add(removeApostrof(newName));
		 }
		 
		 r = Pattern.compile(LAST_FULL_FIRST_SHORT);
		 m = r.matcher(value);
		 
		 while (m.find()){
			 //System.out..println("4");
			 String nameStr = m.group();
			 //System.out..println(nameStr);
			 String[] parts = nameStr.split(",");
			 String lastName = parts[0].trim();
			 String firstName = parts[1].trim();
			 
			 PersonName newName = new PersonName(lastName, firstName, null) ;
			 
			 //System.out..println(newName);
			 names.add(removeApostrof(newName));
		 }
		 
		 r = Pattern.compile(LAST_FULL_FIRST_FULL);
		 m = r.matcher(value);
		 
		 while (m.find()){
			 //System.out..println("5");
			 String nameStr = m.group();
			 //System.out..println(nameStr);
			 String[] parts = nameStr.split(",");
			 String lastName = parts[0].trim();
			 String firstName = parts[1].trim();

			 PersonName newName = new PersonName(lastName, firstName, null) ;
			 
			 //System.out..println(newName);
			 names.add(removeApostrof(newName));
		 }
		 return names;
	}
	
	
	
	public static void main(String args[]){/*
		//System.out..println(findTypeName("@article{{ ISI:000321262500038"));
		//System.out..println(findUniqueID("sdasdss@article{ ISI:000321262500038,"));
		
		String str = "assa";
		Integer i = findBibDescrioptionStart(str);
		//System.out..println(i);
		
	if (i != null){
		//System.out..println(str.substring(0, i));
		//System.out..println(str.substring(i));
	}
	
		String s = "qwerty";
		//System.out..println(s.substring(0, 3));
		//System.out..println(s.substring(3));*/
		
		DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
		Date d= new Date();
		d.setYear(2013 - 1900 );
		//System.out.println(df.format(d));
		
		PersonName name = new PersonName ("Melent'Ev", "P.", "N.");
		System.out.println(removeApostrof(name));
		
		
		String escapedXml = StringEscapeUtils.escapeXml("Martǿnez");
		System.out.println(escapedXml);
		
	}
	


}
