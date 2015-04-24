package DataCrawler.OpenAIRE;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class OAI_PMH_URLGenerator {
	public String EndPoint = "http://api.openaire.eu/oai_pmh";
	public Map<String,String> parameters = new HashMap<>();
	public String[] AllowedParameterNames = { "verb", "resumptionToken", "metadataPrefix", "set" };
	//public Set<String> AllowedParameterNames = new Set<>();
	 
	public void AddParameters(String name, String value) {
		Set<String> paras = new HashSet<String>();
		for(int i = 0; i < AllowedParameterNames.length; ++i ) {
			paras.add(AllowedParameterNames[i]);
		}
		
		if(paras.contains(name)) {
			parameters.put(name, value);
		}
	}
	
	public String GenerateURL() { 
		String outputURL = EndPoint;
		if(!EndPoint.endsWith("?")) {
			outputURL = EndPoint + "?";
		} 
		boolean isFirstTime = true;
		for(Entry<String,String> parameter: parameters.entrySet()) {
			if(isFirstTime) {
				outputURL += parameter.getKey() + "=" + parameter.getValue();
				isFirstTime = false;
			} else {
				outputURL += "&" + parameter.getKey() + "=" + parameter.getValue();
			}
		}
		return outputURL;
	}
}
