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

public class Graber2 {
	public static String openAIREAPIURL = "http://api.openaire.eu/search/publications";
		
	public static void main(String[] args) {
		String identifier = "";
		String startIdentifier = "dedup_wf_001::02ab131d1726c819371de1538c9b4c0b";
		String directory = "I:/OpenAIRE/Result3/";
		boolean start = false;
		try {	
			InputStream fis = new FileInputStream("E:/identities.txt");
			InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
			BufferedReader br = new BufferedReader(isr);
			
			PrintWriter logWriter = new PrintWriter("I:/OpenAIRE/result.log");
			
			while((identifier = br.readLine()) != null) {
				if(identifier.equals(startIdentifier)) {
					start = true;
				}
				if(start) {
					Document doc = Jsoup.connect(openAIREAPIURL + "?openairePublicationID=" + identifier ).timeout(30000).get();
					String fileName = directory + identifier.replace(":", "#") + ".xml";
					logWriter.append(fileName + " > Start" + System.lineSeparator());
					PrintWriter writer = new PrintWriter(fileName);
					writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + System.lineSeparator());
					writer.write(doc.getElementsByTag("response").first().toString());
					writer.close();
					logWriter.append(fileName + " > OK!" + System.lineSeparator());
					try {
					    Thread.sleep(300);                 //300 milliseconds is 500 milliseconds.
					} catch(InterruptedException ex) {
					    Thread.currentThread().interrupt();
					}
				}
			}
			logWriter.close();
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}			
	}

}
