package xml2rdf.example;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import com.hp.hpl.jena.rdf.model.Model;

import xml2rdf.util.xml.SAXYFilterHandler;
import xml2rdf.util.xml.SaxHandler;
import xml2rdf.util.xml.YFilter;

public class SaxYFilterExample {
	
	public static void main(String[] args) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			long SumTime = 0;			
			int runTimes = 10;
			for(int i = 0; i < runTimes; ++i) {
				long startTime = System.currentTimeMillis();
				SAXParser      saxParser = factory.newSAXParser();
			    SAXYFilterHandler handler   = new SAXYFilterHandler();
			    YFilter myFilter = new YFilter();
			    //InputStream    xmlInput  = new FileInputStream("publications.xml");
			    //InputStream    xmlInput  = new FileInputStream("STELL-I_3.rtml");
				InputStream xmlInput = new FileInputStream("dblp_small.xml");			    
			    //myFilter.AppendXPath("/RTML/Telescope/Camera//*");
			    //myFilter.AppendXPath("/RTML/Telescope/Camera/");
			    // myFilter.AppendXPath("/response/header/page");
			    
			    // For STELL-I_3.rtml.
			    //myFilter.AppendXPath("//Camera/FilterWheel");
			    //myFilter.AppendXPath("//Filter");
			    // For publicaitons.xml	
			    /*
			    myFilter.AppendXPath("//metadata");
			    myFilter.AppendXPath("//publisher");
			    myFilter.AppendXPath("//journal");
			    myFilter.AppendXPath("//title");
			    myFilter.AppendXPath("//originalId");
			    myFilter.AppendXPath("//dateofacceptance");
			    
			    */
			    // For dblp_small.xml 
			    myFilter.AppendXPath("//author");			    
			    myFilter.AppendXPath("//title");
			    myFilter.AppendXPath("//journal");
			    myFilter.AppendXPath("//booktitle");
			    myFilter.AppendXPath("//volume");
			    myFilter.AppendXPath("//year");
			    myFilter.AppendXPath("//month");
			    myFilter.AppendXPath("//pages");
			    
			    
			    
			    handler.setFilter(myFilter);
			    //long startTime = System.currentTimeMillis();
			    saxParser.parse(xmlInput, handler);
			    long stopTime = System.currentTimeMillis();
				long elapsedTime = stopTime - startTime;
				SumTime += elapsedTime;
			    //handler.model.write(new FileWriter("dblp_small_customized.rdf"), "RDF/XML-ABBREV");
				//handler.model.write(System.out, "RDF/XML-ABBREV");
			    // System.out.println(SAXYFilterHandler.outputStream);
				TimeUnit.MILLISECONDS.sleep(2000);
			}
			System.out.println("Time elapsed: " + SumTime/runTimes);
		} catch (SAXException e) {		
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		    e.printStackTrace ();
		}
	}
}
