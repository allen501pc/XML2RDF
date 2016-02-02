package xml2rdf.example;


import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringReader;


import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import DataCrawler.OpenAIRE.SequenceFileReader;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;

import xml2rdf.util.rdf.AbstractTemplateParser;
import xml2rdf.util.rdf.DefaultTemplateParser;
import xml2rdf.util.rdf.TemplateRDF;
import xml2rdf.util.xml.CustomizedSAXYFilterHandler;

public class CustomizedSaxYFilterExample5 {
	
	public static void main(String[] args) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		
		try {
						
			if(args.length < 6) {
				System.out.println("command sourceFile RDF_FILE log_file num_of_records useRDF_Store(1=true,0=false)");				
				return;
			}			
			
			File sourceFile = new File(args[0]); 
			PrintWriter logWriter = new PrintWriter(new FileOutputStream(args[2],false));
			float sumOfMappingTime = 0,sumOfOutputRDFTime = 0; 		
			int runTimes = 1;
			long trueStartTime = System.currentTimeMillis();
				for(int i = 0; i < runTimes; ++i) {	
					
					SAXParser      saxParser = factory.newSAXParser();
					
				    CustomizedSAXYFilterHandler handler   = new CustomizedSAXYFilterHandler();
				    
				    boolean useRDFStore = true;
				    if(args[4].equals("0")) {
				    	useRDFStore = false;
				    	handler.useRDFMemoryStore(useRDFStore);				    
					    handler.setOutputStream(new PrintWriter(new FileOutputStream(new File(args[1])),true));
				    }				    
				    AbstractTemplateParser resultParser = new DefaultTemplateParser();
				    resultParser.Parse(new FileReader(args[5]));
				    TemplateRDF resultTemplate = new TemplateRDF();
				    resultTemplate.setDefaultNameSpace(resultParser.GetDefaultNameSpaceURI());
				    resultTemplate.adddTriplePattern(resultParser.constructionPatternList);	
				    				    
				    /*
				    AbstractTemplateParser resultParser = new DefaultTemplateParser();
				    resultParser.Parse(new FileReader(args[5]));
				    TemplateRDF resultTemplate = new TemplateRDF();
				    resultTemplate.setDefaultNameSpace(resultParser.GetDefaultNameSpaceURI());
				    resultTemplate.adddTriplePattern(resultParser.constructionPatternList);							    
					handler.setTemplateRDF(resultTemplate);
					*/
					// saxParser.set
					
		    		// String filename = sourceFolderName + listOfFiles[index].getName();
		    		// Do NOT use it because current files have been repaired. 
		    	    // fixIncompleteTag(filename);
				   // File[] listOfFiles = sourceFolder.listFiles(); 
				   //  for ( int idx = 0; idx < listOfFiles.length; ++idx) {
				      long startTime = System.currentTimeMillis();																
						    
					  handler.setTemplateRDF(resultTemplate);
					  saxParser.parse(sourceFile, handler);
					  long endTime = System.currentTimeMillis();
					 								
					//	}						
					    
						long startTime1 = System.currentTimeMillis();
						if(useRDFStore) {
							
							handler.model.write(new FileWriter(args[1],false), "N-TRIPLES");
						}
					    
						long endTime1 = System.currentTimeMillis();
						sumOfOutputRDFTime = (endTime1 - startTime1)/1000.0f;					    
				   // }
				}
				long trueEndTime = System.currentTimeMillis();
			System.out.println("============================================");
			logWriter.write("============================================" + System.lineSeparator());
			System.out.println("Mapping Time elapsed: " + ((trueEndTime - trueStartTime)/1000.0f)/runTimes);
			logWriter.write("Mapping Time elapsed: " + ((trueEndTime - trueStartTime)/1000.0f)/runTimes + System.lineSeparator());
			System.out.println("Output RDF Time elapsed: " + sumOfOutputRDFTime/runTimes);
			logWriter.write("Output RDF Time elapsed: " + sumOfOutputRDFTime/runTimes + System.lineSeparator());
			logWriter.flush();
			logWriter.close();
		} catch (SAXException e) {		
			System.out.println(e.getMessage());			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		    e.printStackTrace ();
		}
	}
}
