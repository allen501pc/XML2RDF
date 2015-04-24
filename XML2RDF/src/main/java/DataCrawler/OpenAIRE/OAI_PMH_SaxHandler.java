package DataCrawler.OpenAIRE;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.lang.StringEscapeUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class OAI_PMH_SaxHandler extends DefaultHandler {
	
	public boolean isFirstPage = true, enterConsumptionElement = false, shouldOutputContent = false ;
	public static String NextURL = "";
	public static long currentRuns = 0;
	public static long maxRuns = 10; 
	public static FileWriter outputStream ;
	public static String outputFilePath = "", consumptionTag = "oai:resumptionToken";
	public static String[] ElementNamesOfFirstPage = {"oai:OAI-PMH", "oai:responseDate", "oai:request", "oai:ListRecords"};
	public static boolean[] shouldOutputInTheEndOfDocuments = {true, false, false, true};
	public static Stack<String> endElements = new Stack<String>();
	public static SAXParserFactory factory ;	
	
	public void setAsFirstPage(boolean isFirst) {
		isFirstPage = isFirst;
	}
	public void setMaxRuns(long maxNum) {
		maxRuns = maxNum;
	}
	
	public void setOutputPath(String path) { 		
		outputFilePath = path;
	}
	
	public void startDocument() throws SAXException {
		NextURL = "";
		++currentRuns;
		if(maxRuns > 0 && currentRuns > maxRuns) {
			throw new SAXException("end of runs:" + maxRuns);
		}
		if(isFirstPage) {
			try {
				outputStream = new FileWriter(outputFilePath, true);
				outputStream.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + System.lineSeparator());
				if(ElementNamesOfFirstPage.length == shouldOutputInTheEndOfDocuments.length) {
					for(int i = 0 ; i < ElementNamesOfFirstPage.length; ++i) {
						if(shouldOutputInTheEndOfDocuments[i])
							endElements.push(ElementNamesOfFirstPage[i]);
					}
				}
			} catch (IOException e) {
				System.out.println("Cannot output data: " + e.getMessage());
			}
		}
	}
	
	public void endDocument() throws SAXException { 
		/*
		if(isFirstPage) {
			try {
				// outputStream.close();
			} catch (IOException e) {
				System.out.println("Cannot close output data: " + e.getMessage());
			}
		} */
	}
	
	public void startElement(String uri, String localName,
            String qName, Attributes attributes) {
		shouldOutputContent = false;
		if(qName.equals(consumptionTag)) {
			// Enter conSumption tag.
			enterConsumptionElement = true;			    
		} else {
			if(isFirstPage) {
				try {
					shouldOutputContent = true;
					outputStream.write("<" + qName );
					for(int i = 0; i < attributes.getLength(); ++i) {
						String myQName = attributes.getQName(i);
						String myAttributeValue = attributes.getValue(i);
						outputStream.write( " " + myQName + "=\"" + StringEscapeUtils.escapeXml(myAttributeValue) + "\"");					 
					}
					outputStream.write(">" + System.lineSeparator());
					outputStream.flush();
				} catch (IOException e) {					
					e.printStackTrace();
				}						
			} else {
				boolean hasElementsOfFirstPage = false;
				for(int i = 0; i < ElementNamesOfFirstPage.length; ++i) {
					if(qName.equals(ElementNamesOfFirstPage[i])) {
						hasElementsOfFirstPage = true;
						break;
					}
				}
				if(!hasElementsOfFirstPage) {
					try {
						shouldOutputContent = true;
						outputStream.write("<" + qName );
						for(int i = 0; i < attributes.getLength(); ++i) {
							String myQName = attributes.getQName(i);
							String myAttributeValue = attributes.getValue(i);
							outputStream.write( " " + myQName + "=\"" + StringEscapeUtils.escapeXml(myAttributeValue) + "\"");					 
						}
						outputStream.write(">" + System.lineSeparator());
						outputStream.flush();
					} catch (IOException e) {					
						e.printStackTrace();
					}
				} 
			}
		}				
	}
	
	public void endElement(String uri, String localName,
            String qName) {		
		if(!qName.equals(consumptionTag)) {
			if(isFirstPage) {
				boolean hasElementsOfFirstPage = false;
				for(int i = 0; i < ElementNamesOfFirstPage.length; ++i) {
					if(qName.equals(ElementNamesOfFirstPage[i]) && shouldOutputInTheEndOfDocuments[i] ) {
						hasElementsOfFirstPage = true;
						break;
					}
				}
				if(!hasElementsOfFirstPage) {
					try {
						outputStream.write("</" + qName + ">" + System.lineSeparator() );					
						outputStream.flush();
					} catch (IOException e) {					
						e.printStackTrace();
					}
				}
			} else {
				boolean hasElementsOfFirstPage = false;
				for(int i = 0; i < ElementNamesOfFirstPage.length; ++i) {
					if(qName.equals(ElementNamesOfFirstPage[i])) {
						hasElementsOfFirstPage = true;
						break;
					}
				}
				if(!hasElementsOfFirstPage) {
					try {
						outputStream.write("</" + qName + ">" + System.lineSeparator() );						
						outputStream.flush();
					} catch (IOException e) {					
						e.printStackTrace();
					}
				} 
			}	    
		}
	}
	
	public void characters(char ch[], int start, int length) throws SAXException {
    	String value = StringEscapeUtils.escapeXml(new String(ch, start, length).trim()); 
    	if(value.isEmpty()) {
    		return;
    	}
    	
    	if(enterConsumptionElement) {
   					OAI_PMH_URLGenerator myURL = new OAI_PMH_URLGenerator();
					myURL.AddParameters("verb", "ListRecords");
					myURL.AddParameters("resumptionToken", value);
					NextURL = myURL.GenerateURL();	
    	} else {
    		try {
    			if(shouldOutputContent) {
					outputStream.write(value);
					outputStream.flush();
    			}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
	}
}