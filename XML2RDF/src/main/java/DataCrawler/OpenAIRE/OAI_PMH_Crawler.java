package DataCrawler.OpenAIRE;

import java.io.IOException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

public class OAI_PMH_Crawler {
	
	
	public static void main(String[] args) {
		
		if(args.length < 3 ) {
			System.out.println("Comamnd args:" + "OAI_PMH_Carwler <set_name> <number of data records> <output file name>");
			System.out.println("-opetions" );
			System.out.println("<set_name>: can be \"publication\", \"project\",\"organization\" and so on.");
			System.out.println("<number of data records>: integer. Set 0 as unlimited.");
			System.out.println("<output file name>: output file path");
			return ;
		}
		// TODO Auto-generated method stub
		OAI_PMH_SaxHandler.factory = SAXParserFactory.newInstance();
		SAXParser saxParser = null;
		try {
			saxParser = OAI_PMH_SaxHandler.factory.newSAXParser();
		} catch (ParserConfigurationException | SAXException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		OAI_PMH_SaxHandler handler  = new OAI_PMH_SaxHandler();
		handler.setAsFirstPage(true);
		if(args.length == 0) {
			handler.setOutputPath("output.xml");
		} else if (args.length >=3){
			handler.setOutputPath(args[2]);
		}

		OAI_PMH_URLGenerator myURL = new OAI_PMH_URLGenerator();
		myURL.AddParameters("verb", "ListRecords");
		if(args.length >=3) {
			myURL.AddParameters("set", args[0]);
		} else {
			myURL.AddParameters("set", "publications");
		}
		
		if(args.length >=3) {
			handler.setMaxRuns(Integer.valueOf(args[1]));
		} 
		myURL.AddParameters("metadataPrefix", "oaf");
		
		// Debug 
		/*
		OAI_PMH_SaxHandler handler  = new OAI_PMH_SaxHandler();
		handler.setMaxRuns(2);
		OAI_PMH_URLGenerator myURL = new OAI_PMH_URLGenerator();
		myURL.AddParameters("verb", "ListRecords");		
		myURL.AddParameters("metadataPrefix", "oaf");
		myURL.AddParameters("set", "publications");
		handler.setOutputPath("test_output.xml");
		
		OAI_PMH_SaxHandler.factory = SAXParserFactory.newInstance();
		SAXParser saxParser = null;
		try {
			saxParser = OAI_PMH_SaxHandler.factory.newSAXParser();
		} catch (ParserConfigurationException | SAXException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} */
		// End of Debug
		try {
			// Debug
			// String path = "http://api.openaire.eu/oai_pmh?resumptionToken=11409833%7Coaf%7Cresulttypeid%3D%26quot%3Bpublication%26quot%3B%7C2200%7C5538bf49ecd290ec6ac1de8d%7Cfalse%7Cpublications&verb=ListRecords";
			String path = myURL.GenerateURL();
			System.out.println(path + " > Parsing"); 
			saxParser.parse(new URL(myURL.GenerateURL()).openStream(), handler);
			System.out.println(path + " > OK");
			while(!OAI_PMH_SaxHandler.NextURL.isEmpty() && ((OAI_PMH_SaxHandler.maxRuns == 0) || (OAI_PMH_SaxHandler.currentRuns + 1) <= OAI_PMH_SaxHandler.maxRuns)) {
				// SAXParser      saxParser = factory.newSAXParser();
				// OAI_PMH_SaxHandler handler  = new OAI_PMH_SaxHandler();
				if(OAI_PMH_SaxHandler.isReady) {
					handler.setAsFirstPage(false);
					path = OAI_PMH_SaxHandler.NextURL;
					System.out.println(path + " > Parsing"); 
					saxParser.parse(new URL(OAI_PMH_SaxHandler.NextURL).openStream(), handler);
					System.out.println(path + " > OK");
				}			
			}
			
		} catch (IOException e) {
			System.out.println("Cannot Open URL:" + myURL.GenerateURL());
			System.out.println("Message:" + e.getMessage());
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		} finally {
			
		}
		String item = OAI_PMH_SaxHandler.endElements.pop();
		try {
			while(!item.isEmpty()) {
				OAI_PMH_SaxHandler.outputStream.write("</" + item + ">");
				if(OAI_PMH_SaxHandler.endElements.size() != 0) {
					item = OAI_PMH_SaxHandler.endElements.pop();
				} else {
					break;
				}
			}
			OAI_PMH_SaxHandler.outputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
