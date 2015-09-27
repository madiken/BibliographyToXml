package umeta;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
	
	public static Map<String, String> getConfigsFromFile(String fileName) throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		
		String line;
		Map<String, String> conf = new HashMap<String, String>();
		
		
		while(((line = br.readLine())!=null) && (line.length() > 0)){
		//	if (!line.matches("[a-zA-Z]+[\\s]*[=]{1}[\\s]*[\\.a-zA-Z0-9:\\/]+")){
			if (!line.matches("[a-zA-Z]+[\\s]*[=]{1}[\\s]*(.)+")){
				throw new IllegalArgumentException("invalid config :" + line);
			}
			
			String parts[] = line.split("[\\s]*[=]{1}[\\s]*");
			String prop = parts[0].trim().toLowerCase();
			if ("password".equals(prop) || "url".equals(prop) || "user".equals(prop)){
				conf.put(prop, parts[1].trim());
			}
		}
		
		if (!conf.containsKey("password")) 
			throw new IllegalArgumentException("invalid config : please, add the password field");

		if (!conf.containsKey("url")) 
			throw new IllegalArgumentException("invalid config : please, add the url field");

		if (!conf.containsKey("user")) 
			throw new IllegalArgumentException("invalid config : please, add the user field");

		return conf;
	}

}
