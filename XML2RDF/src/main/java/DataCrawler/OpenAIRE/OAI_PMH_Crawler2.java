package DataCrawler.OpenAIRE;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import us.codecraft.xsoup.Xsoup;

public class OAI_PMH_Crawler2 {
	public static String openAIREAPIURL = "http://api.openaire.eu/search/publications";
	public String datasetDirectory = "DataSet";
		
	public static void main(String[] args) {
		if(args.length < 3) {
			System.out.println("Comamnd args:" + "OAI_PMH_Carwler <set_name> <number of data records> <directory> <resumptionToken>");
			System.out.println("-opetions" );
			System.out.println("<set_name>: can be \"publication\", \"project\",\"organization\" and so on.");
			System.out.println("<number of data records>: integer. Set 0 as unlimited.");
			System.out.println("<directory>: output directory for putting records");
		}
		
		int maxRuns = 0;
		
		String targetFilePath = "";				

		OAI_PMH_URLGenerator myURL = new OAI_PMH_URLGenerator();
		myURL.AddParameters("verb", "ListRecords");
		if(args.length >=3) {
			myURL.AddParameters("set", args[0]);
		}
		
		if(args.length >=3) {
			maxRuns = Integer.valueOf(args[1]);
		} 
		myURL.AddParameters("metadataPrefix", "oaf");		
		
		if (args.length >=3){
			targetFilePath = args[2] + "/FirstRecord.xml";
		}
		
		String sourceURL = myURL.GenerateURL();
		if(args.length >=4) {
			OAI_PMH_URLGenerator myURL2 = new OAI_PMH_URLGenerator();
			myURL2.AddParameters("verb", "ListRecords");
			myURL2.AddParameters("resumptionToken", args[3] );
			targetFilePath = args[2] + "/" + args[3].replace(":", "#") + ".xml";
			sourceURL = myURL2.GenerateURL();
		}		
		try {
			boolean shouldNext = false;
			int currentRun = 0;			
			do {
				shouldNext = false;
				Document doc = Jsoup.connect(sourceURL).timeout(30000).get();
				PrintWriter writer = new PrintWriter(targetFilePath);
				System.out.print(sourceURL + " > Start" + System.lineSeparator());
				writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + System.lineSeparator());
				writer.write(doc.getElementsByTag("oai:OAI-PMH").first().toString());
				writer.close();
				System.out.print(sourceURL + " > OK" + System.lineSeparator());
				++currentRun;
				OAI_PMH_URLGenerator myURL2 = new OAI_PMH_URLGenerator();
				myURL2.AddParameters("verb", "ListRecords");
				String resumptionToken = doc.getElementsByTag("oai:resumptionToken").first().text().trim();
				myURL2.AddParameters("resumptionToken", resumptionToken );
				targetFilePath = args[2] + "/" + resumptionToken.replace(":", "#") + ".xml";
				sourceURL = myURL2.GenerateURL();	
				if(maxRuns == 0) {
					if(!resumptionToken.isEmpty()) {
						shouldNext = true;
						Thread.sleep(500);
					}
				} else if(currentRun < maxRuns){
					if(!resumptionToken.isEmpty()) {
						shouldNext = true;
					} 
				}
			} while(shouldNext);						
			
		} catch (IOException | InterruptedException e) {
			
			e.printStackTrace();
		}	
	}

}
